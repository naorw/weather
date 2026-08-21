import { aggregateDaily } from "./daily";
import { WeatherError } from "./errors";
import type { Coordinates, WeatherSnapshot } from "./models";
import { OpenWeatherClient, type OpenWeatherDeps } from "./openweather/client";

export type WeatherProvider = {
  getCurrent: OpenWeatherClient["getCurrent"];
  getForecast: OpenWeatherClient["getForecast"];
  getAirQuality: OpenWeatherClient["getAirQuality"];
  getSnapshot(coordinates: Coordinates, signal?: AbortSignal): Promise<WeatherSnapshot>;
};

export function createWeatherProvider(deps: OpenWeatherDeps): WeatherProvider {
  const client = new OpenWeatherClient(deps);

  return {
    getCurrent: (coordinates, signal) => client.getCurrent(coordinates, signal),
    getForecast: (coordinates, signal) => client.getForecast(coordinates, signal),
    getAirQuality: (coordinates, signal) => client.getAirQuality(coordinates, signal),
    async getSnapshot(coordinates, signal) {
      const currentTask = client.getCurrent(coordinates, signal);
      const forecastTask = client.getForecast(coordinates, signal);
      const airTask = client.getAirQuality(coordinates, signal).then(
        (airQuality) => ({ airQuality }),
        (error: unknown) => {
          if (error instanceof WeatherError && error.code === "auth") throw error;
          return { airQuality: undefined };
        },
      );

      const [current, forecast, air] = await Promise.all([
        currentTask,
        forecastTask,
        airTask,
      ]);

      return {
        location: current.location,
        current: current.current,
        points: forecast.points,
        days: aggregateDaily(forecast.points, current.location.timezoneOffsetSeconds),
        airQuality: air.airQuality,
        fetchedAtMs: Date.now(),
      };
    },
  };
}

export function envApiKey(): string | undefined {
  const key = import.meta.env.VITE_OPENWEATHER_API_KEY;
  return typeof key === "string" && key.trim().length > 0 ? key.trim() : undefined;
}

let shared: WeatherProvider | undefined;

export function weatherProvider(): WeatherProvider {
  shared ??= createWeatherProvider({
    fetch: (input, init) => globalThis.fetch(input, init),
    getApiKey: envApiKey,
  });
  return shared;
}
