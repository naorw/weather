import { STOCKHOLM, WeatherError, weatherProvider } from "../weather";
import type { WeatherSnapshot } from "../weather";
import type { TodayState } from "./state";

export type TodayController = {
  getState: () => TodayState;
  subscribe: (listener: () => void) => () => void;
  load: () => Promise<void>;
  refresh: () => Promise<void>;
};

export function createTodayController(getSnapshot: () => Promise<WeatherSnapshot>): TodayController {
  let state: TodayState = { status: "idle", refreshing: false };
  let inflight: Promise<void> | undefined;
  const listeners = new Set<() => void>();

  const emit = (): void => {
    for (const listener of listeners) listener();
  };

  const setState = (next: TodayState): void => {
    state = next;
    emit();
  };

  const run = async (): Promise<void> => {
    if (inflight) return inflight;
    const previous = state.snapshot;
    inflight = (async () => {
      setState({
        status: previous ? "loaded" : "loading",
        snapshot: previous,
        error: previous ? undefined : state.error,
        refreshing: Boolean(previous),
      });
      try {
        const snapshot = await getSnapshot();
        if (!Number.isFinite(snapshot.current.temperatureC)) {
          setState({ status: "empty", snapshot: undefined, error: undefined, refreshing: false });
          return;
        }
        setState({ status: "loaded", snapshot, error: undefined, refreshing: false });
      } catch (error) {
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
          });
          return;
        }
        setState({ status: "error", snapshot: undefined, error: weatherError, refreshing: false });
      }
    })();
    try {
      await inflight;
    } finally {
      inflight = undefined;
    }
  };

  return {
    getState: () => state,
    subscribe(listener) {
      listeners.add(listener);
      return () => listeners.delete(listener);
    },
    load: run,
    refresh: run,
  };
}

let shared: TodayController | undefined;

export function todayController(): TodayController {
  shared ??= createTodayController(() => weatherProvider().getSnapshot(STOCKHOLM));
  return shared;
}
