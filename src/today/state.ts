import type { Place } from "../locations/model";
import type { WeatherSnapshot } from "../weather";
import type { WeatherError } from "../weather/errors";

export type TodayStatus = "idle" | "loading" | "loaded" | "empty" | "error";

export type TodaySource = "live" | "cache";

export type TodayFreshness = "live" | "fresh" | "stale" | "missing";

export type TodayState = {
  status: TodayStatus;
  snapshot?: WeatherSnapshot;
  error?: WeatherError;
  refreshing: boolean;
  place?: Place;
  source?: TodaySource;
  freshness?: TodayFreshness;
  fetchedAtMs?: number;
};
