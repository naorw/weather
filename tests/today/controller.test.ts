import { describe, expect, it } from "vitest";
import { createTodayController, type TodayControllerDeps } from "../../src/today/controller";
import { DEFAULT_PLACE, placeFromFields, type Place } from "../../src/locations/model";
import type { CachedWeather } from "../../src/cache/schema";
import type { WeatherSnapshot } from "../../src/weather";
import { WeatherError } from "../../src/weather/errors";

const stockholm = DEFAULT_PLACE;
const london = placeFromFields(
  {
    displayName: "London",
    coordinates: { latitude: 51.5074, longitude: -0.1278 },
    country: "GB",
  },
  "saved",
);

function snapshot(place: Place, temp: number, fetchedAtMs = 1): WeatherSnapshot {
  return {
    location: {
      displayName: place.displayName,
      coordinates: place.coordinates,
      country: place.country,
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
    fetchedAtMs,
  };
}

function deps(
  getSnapshot: TodayControllerDeps["getSnapshot"],
  options?: Partial<TodayControllerDeps> & { cache?: Map<string, CachedWeather> },
): TodayControllerDeps {
  const cache = options?.cache ?? new Map<string, CachedWeather>();
  return {
    getSnapshot,
    resolveActive: options?.resolveActive ?? (async () => stockholm),
    readCache: options?.readCache ?? (async (key) => cache.get(key)),
    writeCache:
      options?.writeCache ??
      (async (key, snap) => {
        cache.set(key, {
          cacheKey: key,
          schemaVersion: 1,
          provider: "openweather",
          fetchedAtMs: snap.fetchedAtMs,
          snapshot: snap,
        });
      }),
    now: options?.now,
  };
}

describe("today controller", () => {
  it("goes loading then loaded", async () => {
    const controller = createTodayController(deps(async () => snapshot(stockholm, 10)));
    expect(controller.getState().status).toBe("idle");
    const pending = controller.load();
    expect(controller.getState().status).toBe("loading");
    await pending;
    expect(controller.getState().status).toBe("loaded");
    expect(controller.getState().snapshot?.current.temperatureC).toBe(10);
    expect(controller.getState().source).toBe("live");
  });

  it("ignores concurrent refresh storms", async () => {
    let calls = 0;
    const controller = createTodayController(
      deps(async () => {
        calls += 1;
        await new Promise((resolve) => setTimeout(resolve, 20));
        return snapshot(stockholm, 10);
      }),
    );
    await Promise.all([controller.load(), controller.refresh(), controller.refresh()]);
    expect(calls).toBe(1);
  });

  it("keeps last valid snapshot when refresh fails", async () => {
    let fail = false;
    const controller = createTodayController(
      deps(async () => {
        if (fail) throw new WeatherError("network", "down");
        return snapshot(stockholm, 10);
      }),
    );
    await controller.load();
    fail = true;
    await controller.refresh();
    expect(controller.getState().status).toBe("loaded");
    expect(controller.getState().snapshot?.current.temperatureC).toBe(10);
    expect(controller.getState().error?.code).toBe("network");
  });

  it("uses error state when the first load fails", async () => {
    const controller = createTodayController(
      deps(async () => {
        throw new WeatherError("timeout", "slow");
      }),
    );
    await controller.load();
    expect(controller.getState().status).toBe("error");
    expect(controller.getState().snapshot).toBeUndefined();
  });

  it("shows cached weather immediately and does not blank on a failed refresh", async () => {
    const cache = new Map<string, CachedWeather>();
    cache.set(stockholm.cacheKey, {
      cacheKey: stockholm.cacheKey,
      schemaVersion: 1,
      provider: "openweather",
      fetchedAtMs: 1,
      snapshot: snapshot(stockholm, 18, 1),
    });
    const controller = createTodayController(
      deps(
        async () => {
          throw new WeatherError("network", "offline");
        },
        { cache, now: () => 1 + 40 * 60_000 },
      ),
    );
    await controller.load();
    expect(controller.getState().snapshot?.current.temperatureC).toBe(18);
    expect(controller.getState().freshness).toBe("stale");
    expect(controller.getState().source).toBe("cache");
    expect(cache.get(stockholm.cacheKey)?.snapshot.current.temperatureC).toBe(18);
  });

  it("replaces cache only after a successful refresh", async () => {
    const cache = new Map<string, CachedWeather>();
    const controller = createTodayController(deps(async () => snapshot(stockholm, 21, 50), { cache }));
    await controller.load();
    expect(cache.get(stockholm.cacheKey)?.snapshot.current.temperatureC).toBe(21);
    expect(controller.getState().freshness).toBe("live");
  });

  it("does not paint city A onto city B when an older request finishes late", async () => {
    let releaseA: (value: WeatherSnapshot) => void = () => undefined;
    const holdA = new Promise<WeatherSnapshot>((resolve) => {
      releaseA = resolve;
    });
    const controller = createTodayController(
      deps(async (coordinates) => {
        if (coordinates.latitude === stockholm.coordinates.latitude) return holdA;
        return snapshot(london, 12, 2);
      }),
    );

    const first = controller.setPlace(stockholm);
    await Promise.resolve();
    const second = controller.setPlace(london);
    await second;
    expect(controller.getState().place?.displayName).toBe("London");
    expect(controller.getState().snapshot?.current.temperatureC).toBe(12);

    releaseA(snapshot(stockholm, 99, 3));
    await first;
    expect(controller.getState().place?.displayName).toBe("London");
    expect(controller.getState().snapshot?.current.temperatureC).toBe(12);
  });

  it("clears stale live state after reconnecting with a successful refresh", async () => {
    let fail = true;
    const cache = new Map<string, CachedWeather>();
    cache.set(stockholm.cacheKey, {
      cacheKey: stockholm.cacheKey,
      schemaVersion: 1,
      provider: "openweather",
      fetchedAtMs: 1,
      snapshot: snapshot(stockholm, 7, 1),
    });
    const controller = createTodayController(
      deps(
        async () => {
          if (fail) throw new WeatherError("network", "offline");
          return snapshot(stockholm, 8, 90);
        },
        { cache, now: () => 1 + 40 * 60_000 },
      ),
    );
    await controller.load();
    expect(controller.getState().freshness).toBe("stale");
    fail = false;
    controller.onOnline();
    await controller.refresh();
    expect(controller.getState().freshness).toBe("live");
    expect(controller.getState().snapshot?.current.temperatureC).toBe(8);
  });
});
