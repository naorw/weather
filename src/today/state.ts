import type { WeatherSnapshot } from "../weather";
import type { WeatherError } from "../weather/errors";

export type TodayStatus = "idle" | "loading" | "loaded" | "empty" | "error";

export type TodayState = {
  status: TodayStatus;
  snapshot?: WeatherSnapshot;
  error?: WeatherError;
  refreshing: boolean;
};
