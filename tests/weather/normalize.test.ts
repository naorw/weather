import { readFileSync } from "node:fs";
import { join } from "node:path";
import { describe, expect, it } from "vitest";
import {
  normalizeAirQuality,
  normalizeCurrent,
  normalizeForecastPoints,
  normalizeLocation,
} from "../../src/weather/openweather/normalize";

const dir = join(import.meta.dirname, "../fixtures/openweather");

function load(name: string): unknown {
  return JSON.parse(readFileSync(join(dir, name), "utf8"));
}

describe("normalizeCurrent", () => {
  it("maps a full current-weather payload into application models", () => {
    const current = normalizeCurrent(load("current.json"));
    const location = normalizeLocation(load("current.json"), "Fallback");
    expect(location.displayName).toBe("Stockholm");
    expect(location.country).toBe("SE");
    expect(location.coordinates).toEqual({ latitude: 59.3293, longitude: 18.0686 });
    expect(location.timezoneOffsetSeconds).toBe(7200);
    expect(current.temperatureC).toBe(14.2);
    expect(current.feelsLikeC).toBe(13.1);
    expect(current.highC).toBe(15.4);
    expect(current.lowC).toBe(12.8);
    expect(current.condition).toBe("cloudy");
    expect(current.conditionText).toBe("broken clouds");
    expect(current.humidityPercent).toBe(72);
    expect(current.pressureHpa).toBe(1014);
    expect(current.visibilityM).toBe(10000);
    expect(current.cloudPercent).toBe(75);
    expect(current.wind).toEqual({ speedMps: 4.1, directionDeg: 240, gustMps: 7.2 });
    expect(current.precipitation.rainMm).toBe(0.2);
    expect(current.precipitation.probabilityPercent).toBeUndefined();
    expect(current.observedAtMs).toBe(1724260800 * 1000);
  });

  it("keeps omitted precipitation distinct from zero", () => {
    const current = normalizeCurrent(load("current-sparse.json"));
    expect(current.precipitation.rainMm).toBeUndefined();
    expect(current.precipitation.snowMm).toBeUndefined();
    expect(current.highC).toBeUndefined();
    expect(current.lowC).toBeUndefined();
    expect(current.visibilityM).toBeUndefined();
    expect(current.wind.gustMps).toBeUndefined();
  });

  it("rejects structurally invalid current weather", () => {
    expect(() => normalizeCurrent({})).toThrowError(/missing/i);
  });
});

describe("normalizeForecastPoints", () => {
  it("skips malformed points without dropping the rest", () => {
    const points = normalizeForecastPoints(load("forecast.json"));
    expect(points).toHaveLength(3);
    expect(points[0]?.condition).toBe("light-rain");
    expect(points[0]?.precipitation.probabilityPercent).toBe(40);
    expect(points[0]?.precipitation.rainMm).toBe(0.31);
    expect(points[1]?.precipitation.rainMm).toBeUndefined();
    expect(points[2]?.precipitation.probabilityPercent).toBe(0);
  });

  it("fails when no usable points remain", () => {
    expect(() =>
      normalizeForecastPoints({ list: [{ dt: "nope" }] }),
    ).toThrowError(/no usable/i);
  });
});

describe("normalizeAirQuality", () => {
  it("preserves OpenWeather AQI rather than renaming it EPA", () => {
    const air = normalizeAirQuality(load("air.json"));
    expect(air.openWeatherAqi).toBe(2);
    expect(air.category).toBe("fair");
    expect(air.components.pm2_5).toBe(4.12);
  });
});
