/** @vitest-environment node */

import { describe, expect, it } from "vitest";
import { createWeatherProvider } from "../../src/weather/provider";
import { STOCKHOLM } from "../../src/weather/models";

const key = process.env.VITE_OPENWEATHER_API_KEY ?? process.env.OPENWEATHER_API_KEY;

describe.skipIf(!key)("live OpenWeather (optional)", () => {
  it("retrieves current, forecast, and air quality for Stockholm", async () => {
    const provider = createWeatherProvider({
      fetch,
      getApiKey: () => key,
    });
    const snapshot = await provider.getSnapshot(STOCKHOLM);
    expect(snapshot.current.temperatureC).toEqual(expect.any(Number));
    expect(snapshot.points.length).toBeGreaterThan(0);
    expect(snapshot.days.length).not.toBe(7);
    expect(snapshot.days.length).toBeGreaterThan(0);
  }, 20_000);
});
