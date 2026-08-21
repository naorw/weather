import { classifyFreshness } from "../cache/stale";
import type { CachedWeather } from "../cache/schema";
import { weatherCache, type WeatherCache } from "../cache/store";
import { locationCatalog, type LocationCatalog } from "../locations/catalog";
import type { Place } from "../locations/model";
import type { Coordinates, WeatherSnapshot } from "../weather";
import { WeatherError, weatherProvider } from "../weather";
import type { TodayFreshness, TodayState } from "./state";

export type TodayControllerDeps = {
  getSnapshot: (coordinates: Coordinates, signal?: AbortSignal) => Promise<WeatherSnapshot>;
  resolveActive: () => Promise<Place>;
  readCache: (cacheKey: string) => Promise<CachedWeather | undefined>;
  writeCache: (cacheKey: string, snapshot: WeatherSnapshot) => Promise<void>;
  now?: () => number;
};

export type TodayController = {
  getState: () => TodayState;
  subscribe: (listener: () => void) => () => void;
  load: () => Promise<void>;
  refresh: () => Promise<void>;
  setPlace: (place: Place) => Promise<void>;
  onOnline: () => void;
};

function freshnessFromCache(fetchedAtMs: number, nowMs: number): TodayFreshness {
  return classifyFreshness(fetchedAtMs, nowMs) === "fresh" ? "fresh" : "stale";
}

export function createTodayController(deps: TodayControllerDeps): TodayController {
  let state: TodayState = { status: "idle", refreshing: false };
  let generation = 0;
  let inflight: Promise<void> | undefined;
  let inflightGen: number | undefined;
  let fetchAbort: AbortController | undefined;
  const listeners = new Set<() => void>();
  const now = (): number => deps.now?.() ?? Date.now();

  const emit = (): void => {
    for (const listener of listeners) listener();
  };

  const setState = (next: TodayState): void => {
    state = next;
    emit();
  };

  const fetchLive = async (place: Place, gen: number): Promise<void> => {
    if (inflight && inflightGen === gen) return inflight;
    if (inflightGen !== undefined && inflightGen !== gen) {
      fetchAbort?.abort();
    }

    const abort = new AbortController();
    fetchAbort = abort;
    const previous = state.place?.id === place.id ? state.snapshot : undefined;
    const previousFetchedAt = state.place?.id === place.id ? state.fetchedAtMs : undefined;
    const previousSource = state.place?.id === place.id ? state.source : undefined;
    const previousFreshness = state.place?.id === place.id ? state.freshness : undefined;

    inflight = (async () => {
      setState({
        status: previous ? "loaded" : "loading",
        snapshot: previous,
        error: previous ? undefined : state.error,
        refreshing: Boolean(previous),
        place,
        source: previousSource,
        freshness: previousFreshness,
        fetchedAtMs: previousFetchedAt,
      });
      try {
        const snapshot = await deps.getSnapshot(place.coordinates, abort.signal);
        await deps.writeCache(place.cacheKey, snapshot);
        if (gen !== generation) return;
        if (!Number.isFinite(snapshot.current.temperatureC)) {
          setState({
            status: previous ? "loaded" : "empty",
            snapshot: previous,
            error: undefined,
            refreshing: false,
            place,
            source: previousSource,
            freshness: previousFreshness,
            fetchedAtMs: previousFetchedAt,
          });
          return;
        }
        setState({
          status: "loaded",
          snapshot,
          error: undefined,
          refreshing: false,
          place,
          source: "live",
          freshness: "live",
          fetchedAtMs: snapshot.fetchedAtMs,
        });
      } catch (error) {
        if (gen !== generation) return;
        const weatherError =
          error instanceof WeatherError
            ? error
            : new WeatherError("unknown", "Weather request failed.", undefined, { cause: error });
        if (previous) {
          setState({
            status: "loaded",
            snapshot: previous,
            error: weatherError,
            refreshing: false,
            place,
            source: previousSource ?? "cache",
            freshness: previousFreshness ?? freshnessFromCache(previousFetchedAt ?? 0, now()),
            fetchedAtMs: previousFetchedAt ?? previous.fetchedAtMs,
          });
          return;
        }
        setState({
          status: "error",
          snapshot: undefined,
          error: weatherError,
          refreshing: false,
          place,
          source: undefined,
          freshness: "missing",
          fetchedAtMs: undefined,
        });
      }
    })();
    inflightGen = gen;
    try {
      await inflight;
    } finally {
      if (inflightGen === gen) {
        inflight = undefined;
        inflightGen = undefined;
      }
    }
  };

  const showCacheAndFetch = async (place: Place, gen: number): Promise<void> => {
    const cached = await deps.readCache(place.cacheKey);
    if (gen !== generation) return;
    if (cached) {
      setState({
        status: "loaded",
        snapshot: cached.snapshot,
        error: undefined,
        refreshing: true,
        place,
        source: "cache",
        freshness: freshnessFromCache(cached.fetchedAtMs, now()),
        fetchedAtMs: cached.fetchedAtMs,
      });
    } else if (state.place?.id !== place.id || !state.snapshot) {
      setState({
        status: "loading",
        snapshot: undefined,
        error: undefined,
        refreshing: false,
        place,
        source: undefined,
        freshness: "missing",
        fetchedAtMs: undefined,
      });
    }
    await fetchLive(place, gen);
  };

  const load = async (): Promise<void> => {
    if (!state.snapshot) {
      setState({ ...state, status: "loading" });
    }
    const place = await deps.resolveActive();
    if (state.place && place.id !== state.place.id) generation += 1;
    if (generation === 0) generation = 1;
    await showCacheAndFetch(place, generation);
  };

  const refresh = async (): Promise<void> => {
    const place = state.place ?? (await deps.resolveActive());
    if (generation === 0) generation = 1;
    await fetchLive(place, generation);
  };

  return {
    getState: () => state,
    subscribe(listener) {
      listeners.add(listener);
      return () => listeners.delete(listener);
    },
    load,
    refresh,
    async setPlace(place) {
      generation += 1;
      if (state.place?.id !== place.id) {
        setState({
          status: "loading",
          snapshot: undefined,
          error: undefined,
          refreshing: false,
          place,
          source: undefined,
          freshness: "missing",
          fetchedAtMs: undefined,
        });
      }
      await showCacheAndFetch(place, generation);
    },
    onOnline() {
      if (state.status === "idle") return;
      void refresh();
    },
  };
}

function productionDeps(): TodayControllerDeps {
  const locations: LocationCatalog = locationCatalog();
  const cache: WeatherCache = weatherCache();
  return {
    getSnapshot: (coordinates, signal) => weatherProvider().getSnapshot(coordinates, signal),
    resolveActive: () => locations.getActive(),
    readCache: (cacheKey) => cache.get(cacheKey),
    writeCache: (cacheKey, snapshot) => cache.put(cacheKey, snapshot),
  };
}

let shared: TodayController | undefined;

export function todayController(): TodayController {
  shared ??= createTodayController(productionDeps());
  return shared;
}

export function resetTodayControllerForTests(): void {
  shared = undefined;
}
