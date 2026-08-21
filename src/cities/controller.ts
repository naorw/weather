import { queryGeoPermission, requestDevicePosition, type GeoFailureReason } from "../geo/locate";
import { locationCatalog, type LocationCatalog } from "../locations/catalog";
import { DEFAULT_PLACE, placeFromFields, placeLabel, type Place } from "../locations/model";
import { todayController, type TodayController } from "../today/controller";
import { weatherProvider, type PlaceCandidate, type WeatherProvider } from "../weather";

export type SearchStatus = "idle" | "loading" | "results" | "empty" | "error";
export type GeoStatus = "idle" | "locating" | GeoFailureReason | "error";

export type CitiesState = {
  query: string;
  search: SearchStatus;
  results: Place[];
  searchError?: string;
  saved: Place[];
  active: Place;
  geo: GeoStatus;
  geoDeniedSticky: boolean;
  geoUnsupported: boolean;
};

export type CitiesControllerDeps = {
  catalog: LocationCatalog;
  provider: Pick<WeatherProvider, "searchPlaces" | "reverseGeocode">;
  today: Pick<TodayController, "setPlace">;
  locate?: typeof requestDevicePosition;
  permission?: typeof queryGeoPermission;
  navigate?: (hash: string) => void;
};

export type CitiesController = {
  getState: () => CitiesState;
  subscribe: (listener: () => void) => () => void;
  hydrate: () => Promise<void>;
  setQuery: (query: string) => void;
  search: () => Promise<void>;
  selectResult: (place: Place) => Promise<void>;
  switchTo: (place: Place) => Promise<void>;
  saveActive: () => Promise<void>;
  remove: (id: string) => Promise<void>;
  useDevice: () => Promise<void>;
};

function candidatesToPlaces(results: PlaceCandidate[]): Place[] {
  return results.map((candidate) => placeFromFields(candidate, "search"));
}

export function createCitiesController(deps: CitiesControllerDeps): CitiesController {
  let state: CitiesState = {
    query: "",
    search: "idle",
    results: [],
    saved: [],
    active: DEFAULT_PLACE,
    geo: "idle",
    geoDeniedSticky: false,
    geoUnsupported: false,
  };
  const listeners = new Set<() => void>();
  const locate = deps.locate ?? requestDevicePosition;
  const permission = deps.permission ?? queryGeoPermission;
  const navigate = deps.navigate ?? ((hash: string) => {
    window.location.hash = hash;
  });

  const emit = (): void => {
    for (const listener of listeners) listener();
  };

  const setState = (patch: Partial<CitiesState>): void => {
    state = { ...state, ...patch };
    emit();
  };

  const reloadLists = async (): Promise<void> => {
    const [saved, active] = await Promise.all([deps.catalog.listSaved(), deps.catalog.getActive()]);
    setState({ saved, active });
  };

  const activate = async (place: Place, save: boolean): Promise<void> => {
    const next = save ? await deps.catalog.save(place) : place;
    await deps.catalog.setActive(next);
    await deps.today.setPlace(next);
    await reloadLists();
    navigate("#/today");
  };

  return {
    getState: () => state,
    subscribe(listener) {
      listeners.add(listener);
      return () => listeners.delete(listener);
    },
    async hydrate() {
      await reloadLists();
      const geoPermission = await permission();
      if (geoPermission === "denied") {
        setState({ geo: "denied", geoDeniedSticky: true });
      } else if (geoPermission === "unknown" && typeof navigator !== "undefined" && !navigator.geolocation) {
        setState({ geo: "unsupported", geoUnsupported: true });
      }
    },
    setQuery(query) {
      state = { ...state, query };
    },
    async search() {
      const query = state.query.trim();
      if (!query) {
        setState({ search: "idle", results: [], searchError: undefined });
        return;
      }
      setState({ search: "loading", searchError: undefined });
      try {
        const results = candidatesToPlaces(await deps.provider.searchPlaces(query));
        setState({
          search: results.length === 0 ? "empty" : "results",
          results,
        });
      } catch (error) {
        setState({
          search: "error",
          results: [],
          searchError: error instanceof Error ? error.message : "Search failed.",
        });
      }
    },
    async selectResult(place) {
      await activate(place, true);
    },
    async switchTo(place) {
      await activate(place, false);
    },
    async saveActive() {
      await deps.catalog.save(state.active);
      await reloadLists();
    },
    async remove(id) {
      const next = await deps.catalog.remove(id);
      await deps.today.setPlace(next);
      await reloadLists();
    },
    async useDevice() {
      if (state.geoDeniedSticky) return;
      setState({ geo: "locating" });
      const result = await locate();
      if (!result.ok) {
        setState({
          geo: result.reason,
          geoDeniedSticky: result.reason === "denied",
          geoUnsupported: result.reason === "unsupported" || result.reason === "insecure",
        });
        return;
      }
      let named = placeFromFields(
        { displayName: "Device location", coordinates: result.coords },
        "device",
      );
      try {
        const reversed = await deps.provider.reverseGeocode(result.coords);
        if (reversed) named = placeFromFields(reversed, "device");
      } catch {
        /* keep coordinate fallback name */
      }
      setState({ geo: "idle" });
      await activate(named, false);
    },
  };
}

export { placeLabel };

let shared: CitiesController | undefined;

export function citiesController(): CitiesController {
  shared ??= createCitiesController({
    catalog: locationCatalog(),
    provider: weatherProvider(),
    today: todayController(),
  });
  return shared;
}

export function resetCitiesControllerForTests(): void {
  shared = undefined;
}
