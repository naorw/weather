import { describe, expect, it } from "vitest";
import { glyphFor, glyphLabel } from "../../src/glyphs";
import type { WeatherSnapshot } from "../../src/weather";
import {
  MISSING,
  compassFromDeg,
  errorCopy,
  formatMm,
  formatPercent,
  formatTempC,
  formatVisibility,
  formatLocalHour,
  formatWeekday,
  heroHighLow,
} from "../../src/today/present";
import { renderToday } from "../../src/screens/today";
import { WeatherError } from "../../src/weather/errors";

function snapshot(overrides: Partial<WeatherSnapshot> = {}): WeatherSnapshot {
  return {
    location: {
      displayName: "Stockholm",
      coordinates: { latitude: 59.33, longitude: 18.07 },
      country: "SE",
      timezoneOffsetSeconds: 7200,
    },
    current: {
      observedAtMs: Date.UTC(2026, 7, 21, 12, 0, 0),
      temperatureC: 18.11,
      feelsLikeC: 17,
      condition: "overcast",
      conditionText: "overcast clouds",
      wind: { speedMps: 4.1, directionDeg: 240 },
      precipitation: { rainMm: 0.2 },
      humidityPercent: 72,
      pressureHpa: 1014,
    },
    points: [
      {
        atMs: Date.UTC(2026, 7, 21, 15, 0, 0),
        temperatureC: 16,
        condition: "rain",
        conditionText: "rain",
        precipitation: { probabilityPercent: 40 },
        wind: { speedMps: 3 },
      },
    ],
    days: [
      {
        localDate: "2026-08-21",
        highC: 19,
        lowC: 12,
        condition: "rain",
        precipitation: { probabilityPercent: 55 },
        pointCount: 3,
        partial: true,
      },
      {
        localDate: "2026-08-22",
        highC: 22,
        lowC: 14,
        condition: "clear",
        precipitation: {},
        pointCount: 8,
        partial: false,
      },
    ],
    airQuality: {
      observedAtMs: Date.UTC(2026, 7, 21, 12, 0, 0),
      openWeatherAqi: 2,
      category: "fair",
      components: {},
    },
    fetchedAtMs: Date.UTC(2026, 7, 21, 12, 0, 0),
    ...overrides,
  };
}

describe("today presentation", () => {
  it("keeps missing optional values distinct from zero", () => {
    expect(formatPercent(undefined)).toBe(MISSING);
    expect(formatPercent(0)).toBe("0%");
    expect(formatMm(undefined)).toBe(MISSING);
    expect(formatMm(0)).toBe("0 mm");
    expect(formatTempC(undefined)).toBe(MISSING);
  });

  it("formats visibility and compass", () => {
    expect(formatVisibility(10000)).toBe("10 km");
    expect(formatVisibility(800)).toBe("800 m");
    expect(compassFromDeg(240)).toBe("SW");
  });

  it("uses local offset for forecast hours", () => {
    expect(formatLocalHour(Date.UTC(2026, 7, 21, 15, 0, 0), 7200)).toBe("17");
  });

  it("labels weekdays from local dates", () => {
    expect(formatWeekday("2026-08-21")).toBe("Fri");
  });

  it("prefers today's daily high/low over station envelope", () => {
    const range = heroHighLow(snapshot());
    expect(range.high).toBe(19);
    expect(range.low).toBe(12);
  });

  it("maps conditions to glyphs including unknown fallback", () => {
    expect(glyphLabel(glyphFor("thunderstorm"))).toBe("Thunderstorm");
    expect(glyphFor("unknown")).toBe("unknown");
  });
});

describe("renderToday states", () => {
  it("renders loading without fake temperatures", () => {
    const html = renderToday({ status: "loading", refreshing: false });
    expect(html).toContain("Acquiring weather");
    expect(html).not.toContain("18°");
  });

  it("renders empty distinctly from errors", () => {
    const html = renderToday({ status: "empty", refreshing: false });
    expect(html).toContain("No usable weather payload");
    expect(html).not.toContain("Offline");
  });

  it("renders provider errors without secrets", () => {
    const html = renderToday({
      status: "error",
      refreshing: false,
      error: new WeatherError("auth", "nope appid=secret"),
    });
    expect(html).toContain("Credentials");
    expect(html).not.toContain("secret");
    expect(errorCopy(new WeatherError("rate_limit", "x")).title).toBe("Rate limited");
  });

  it("renders variable days and partial markers", () => {
    const html = renderToday({ status: "loaded", refreshing: false, snapshot: snapshot() });
    expect(html).toContain("part.");
    expect(html).toContain("Fri");
    expect(html).toContain("Sat");
    expect(html).toContain("Stockholm SE");
    expect(html).toContain("OpenWeather");
    expect(html).toContain("not EPA/CAQI");
  });

  it("omits precipitation percent when missing", () => {
    const html = renderToday({
      status: "loaded",
      refreshing: false,
      snapshot: snapshot({
        days: [
          {
            localDate: "2026-08-22",
            highC: 10,
            lowC: 5,
            condition: "snow",
            precipitation: {},
            pointCount: 8,
            partial: false,
          },
        ],
      }),
    });
    expect(html).toContain(MISSING);
    expect(html).toContain("Snow");
  });
});
