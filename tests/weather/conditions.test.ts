import { describe, expect, it } from "vitest";
import { mapOpenWeatherCondition, pickRepresentativeCondition } from "../../src/weather/conditions";

describe("mapOpenWeatherCondition", () => {
  it("maps known OpenWeather ids deterministically", () => {
    expect(mapOpenWeatherCondition(800)).toBe("clear");
    expect(mapOpenWeatherCondition(801)).toBe("partly-cloudy");
    expect(mapOpenWeatherCondition(803)).toBe("cloudy");
    expect(mapOpenWeatherCondition(804)).toBe("overcast");
    expect(mapOpenWeatherCondition(300)).toBe("drizzle");
    expect(mapOpenWeatherCondition(500)).toBe("light-rain");
    expect(mapOpenWeatherCondition(501)).toBe("rain");
    expect(mapOpenWeatherCondition(502)).toBe("heavy-rain");
    expect(mapOpenWeatherCondition(202)).toBe("thunderstorm");
    expect(mapOpenWeatherCondition(600)).toBe("light-snow");
    expect(mapOpenWeatherCondition(601)).toBe("snow");
    expect(mapOpenWeatherCondition(741)).toBe("fog");
  });

  it("falls back safely for unknown codes", () => {
    expect(mapOpenWeatherCondition(9999)).toBe("unknown");
  });
});

describe("pickRepresentativeCondition", () => {
  it("selects the most severe category in the day", () => {
    expect(
      pickRepresentativeCondition(["clear", "rain", "partly-cloudy"]),
    ).toBe("rain");
  });
});
