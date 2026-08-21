import { afterEach, describe, expect, it } from "vitest";
import { CACHE_SCHEMA_VERSION, parseCachedWeather } from "../../src/cache/schema";
import { classifyFreshness, FRESH_MS, formatAge } from "../../src/cache/stale";
import { createWeatherCache } from "../../src/cache/store";
import { placeCacheKey } from "../../src/locations/model";
import { resetAppStores } from "../../src/persist";
import type { WeatherSnapshot } from "../../src/weather";

const stockholmKey = placeCacheKey({ latitude: 59.3293, longitude: 18.0686 });
const londonKey = placeCacheKey({ latitude: 51.5074, longitude: -0.1278 });

function snapshot(name: string, temp: number): WeatherSnapshot {
  return {
    location: {
      displayName: name,
      coordinates: { latitude: 1, longitude: 2 },
      timezoneOffsetSeconds: 0,
    },
    current: {
      observedAtMs: 1,
      temperatureC: temp,
      feelsLikeC: temp,
      condition: "clear",
      conditionText: "clear sky",
      wind: { speedMps: 1 },
      precipitation: {},
    },
    points: [],
    days: [],
    fetchedAtMs: 10,
  };
}

describe("staleness", () => {
  it("classifies missing, fresh, and stale ages", () => {
    expect(classifyFreshness(undefined, 1000)).toBe("missing");
    expect(classifyFreshness(1000, 1000 + FRESH_MS - 1)).toBe("fresh");
    expect(classifyFreshness(1000, 1000 + FRESH_MS)).toBe("stale");
  });

  it("formats cache age honestly", () => {
    expect(formatAge(0, 20_000)).toBe("JUST NOW");
    expect(formatAge(0, 47 * 60_000)).toBe("47 MIN AGO");
    expect(formatAge(0, 2 * 60 * 60_000)).toBe("2 H AGO");
  });
});

describe("weather cache store", () => {
  afterEach(async () => {
    await resetAppStores();
  });

  it("writes and reads one snapshot per location", async () => {
    const cache = createWeatherCache();
    await cache.put(stockholmKey, snapshot("Stockholm", 18));
    await cache.put(londonKey, snapshot("London", 12));
    expect((await cache.get(stockholmKey))?.snapshot.current.temperatureC).toBe(18);
    expect((await cache.get(londonKey))?.snapshot.current.temperatureC).toBe(12);

    await cache.put(stockholmKey, snapshot("Stockholm", 19));
    expect((await cache.get(stockholmKey))?.snapshot.current.temperatureC).toBe(19);
    expect((await cache.get(londonKey))?.snapshot.current.temperatureC).toBe(12);
  });

  it("drops incompatible or malformed cache rows", async () => {
    expect(
      parseCachedWeather({
        cacheKey: stockholmKey,
        schemaVersion: CACHE_SCHEMA_VERSION + 1,
        provider: "openweather",
        fetchedAtMs: 1,
        snapshot: snapshot("X", 1),
      }),
    ).toBeUndefined();

    const cache = createWeatherCache();
    const { putRecord, STORES } = await import("../../src/persist");
    await putRecord(STORES.snapshots, {
      cacheKey: stockholmKey,
      schemaVersion: CACHE_SCHEMA_VERSION,
      provider: "openweather",
      fetchedAtMs: 1,
      snapshot: { broken: true },
    });
    expect(await cache.get(stockholmKey)).toBeUndefined();
  });
});
