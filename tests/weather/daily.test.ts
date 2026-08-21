import { describe, expect, it } from "vitest";
import { aggregateDaily } from "../../src/weather/daily";
import type { ForecastPoint } from "../../src/weather/models";
import { localDateKey, probabilityToPercent } from "../../src/weather/units";

function point(partial: Partial<ForecastPoint> & Pick<ForecastPoint, "atMs" | "temperatureC" | "condition">): ForecastPoint {
  return {
    conditionText: partial.condition,
    precipitation: {},
    wind: { speedMps: 1 },
    ...partial,
  };
}

describe("canonical unit helpers", () => {
  it("converts provider probability 0–1 into percent", () => {
    expect(probabilityToPercent(0.4)).toBe(40);
    expect(probabilityToPercent(0)).toBe(0);
    expect(probabilityToPercent(undefined)).toBeUndefined();
  });
});

describe("localDateKey", () => {
  it("uses the location offset for calendar-day boundaries", () => {
    const utcEvening = Date.UTC(2024, 0, 15, 23, 0, 0);
    expect(localDateKey(utcEvening, 0)).toBe("2024-01-15");
    expect(localDateKey(utcEvening, 3600)).toBe("2024-01-16");
  });
});

describe("aggregateDaily", () => {
  const offset = 3600;

  it("computes high, low, worst condition, and max pop", () => {
    const points: ForecastPoint[] = [
      point({
        atMs: Date.UTC(2024, 0, 16, 6, 0, 0) - offset * 1000,
        temperatureC: 4,
        condition: "clear",
        precipitation: { probabilityPercent: 10, rainMm: 0 },
      }),
      point({
        atMs: Date.UTC(2024, 0, 16, 12, 0, 0) - offset * 1000,
        temperatureC: 11,
        condition: "rain",
        precipitation: { probabilityPercent: 80, rainMm: 1.2 },
      }),
      point({
        atMs: Date.UTC(2024, 0, 16, 18, 0, 0) - offset * 1000,
        temperatureC: 7,
        condition: "overcast",
        precipitation: { probabilityPercent: 40, rainMm: 0.2 },
      }),
    ];

    const [day] = aggregateDaily(points, offset);
    expect(day.localDate).toBe("2024-01-16");
    expect(day.highC).toBe(11);
    expect(day.lowC).toBe(4);
    expect(day.condition).toBe("rain");
    expect(day.precipitation.probabilityPercent).toBe(80);
    expect(day.precipitation.rainMm).toBeCloseTo(1.4);
    expect(day.partial).toBe(true);
    expect(day.pointCount).toBe(3);
  });

  it("does not treat an 8-point day as partial", () => {
    const points = Array.from({ length: 8 }, (_, i) =>
      point({
        atMs: Date.UTC(2024, 0, 16, i * 3, 0, 0) - offset * 1000,
        temperatureC: 5 + i,
        condition: "clear",
      }),
    );
    const [day] = aggregateDaily(points, offset);
    expect(day.partial).toBe(false);
    expect(day.pointCount).toBe(8);
  });

  it("keeps incomplete first and last days distinguishable", () => {
    const points = [
      point({
        atMs: Date.UTC(2024, 0, 15, 21, 0, 0) - offset * 1000,
        temperatureC: 2,
        condition: "fog",
      }),
      point({
        atMs: Date.UTC(2024, 0, 16, 0, 0, 0) - offset * 1000,
        temperatureC: 3,
        condition: "clear",
      }),
    ];
    const days = aggregateDaily(points, offset);
    expect(days.map((d) => d.localDate)).toEqual(["2024-01-15", "2024-01-16"]);
    expect(days.every((d) => d.partial)).toBe(true);
  });
});
