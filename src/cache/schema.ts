import type { WeatherSnapshot } from "../weather";

export const CACHE_SCHEMA_VERSION = 1;
export const CACHE_PROVIDER = "openweather";

export type CachedWeather = {
  cacheKey: string;
  schemaVersion: number;
  provider: typeof CACHE_PROVIDER;
  fetchedAtMs: number;
  snapshot: WeatherSnapshot;
};

export function isUsableSnapshot(value: unknown): value is WeatherSnapshot {
  if (!value || typeof value !== "object") return false;
  const snapshot = value as WeatherSnapshot;
  const current = snapshot.current;
  if (!current || typeof current !== "object") return false;
  if (!Number.isFinite(current.temperatureC)) return false;
  if (!snapshot.location || typeof snapshot.location.displayName !== "string") return false;
  if (!Array.isArray(snapshot.points) || !Array.isArray(snapshot.days)) return false;
  return true;
}

export function parseCachedWeather(value: unknown): CachedWeather | undefined {
  if (!value || typeof value !== "object") return undefined;
  const row = value as Record<string, unknown>;
  if (row.schemaVersion !== CACHE_SCHEMA_VERSION) return undefined;
  if (row.provider !== CACHE_PROVIDER) return undefined;
  if (typeof row.cacheKey !== "string" || row.cacheKey.length === 0) return undefined;
  if (typeof row.fetchedAtMs !== "number" || !Number.isFinite(row.fetchedAtMs)) return undefined;
  if (!isUsableSnapshot(row.snapshot)) return undefined;
  return {
    cacheKey: row.cacheKey,
    schemaVersion: CACHE_SCHEMA_VERSION,
    provider: CACHE_PROVIDER,
    fetchedAtMs: row.fetchedAtMs,
    snapshot: row.snapshot,
  };
}
