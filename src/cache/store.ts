import { deleteRecord, getRecord, putRecord, STORES } from "../persist";
import type { WeatherSnapshot } from "../weather";
import {
  CACHE_PROVIDER,
  CACHE_SCHEMA_VERSION,
  parseCachedWeather,
  type CachedWeather,
} from "./schema";

export type WeatherCache = {
  get: (cacheKey: string) => Promise<CachedWeather | undefined>;
  put: (cacheKey: string, snapshot: WeatherSnapshot) => Promise<void>;
};

export function createWeatherCache(): WeatherCache {
  return {
    async get(cacheKey) {
      const raw = await getRecord(STORES.snapshots, cacheKey);
      if (raw === undefined) return undefined;
      const parsed = parseCachedWeather(raw);
      if (!parsed || parsed.cacheKey !== cacheKey) {
        await deleteRecord(STORES.snapshots, cacheKey);
        return undefined;
      }
      return parsed;
    },

    async put(cacheKey, snapshot) {
      const record: CachedWeather = {
        cacheKey,
        schemaVersion: CACHE_SCHEMA_VERSION,
        provider: CACHE_PROVIDER,
        fetchedAtMs: snapshot.fetchedAtMs,
        snapshot,
      };
      if (!parseCachedWeather(record)) return;
      await putRecord(STORES.snapshots, record);
    },
  };
}

let shared: WeatherCache | undefined;

export function weatherCache(): WeatherCache {
  shared ??= createWeatherCache();
  return shared;
}

export function resetWeatherCacheForTests(): void {
  shared = undefined;
}
