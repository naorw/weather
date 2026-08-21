import { describe, expect, it, vi } from "vitest";
import { WeatherError } from "../../src/weather/errors";
import { STOCKHOLM } from "../../src/weather/models";
import { OpenWeatherClient } from "../../src/weather/openweather/client";
import current from "../fixtures/openweather/current.json";
import forecast from "../fixtures/openweather/forecast.json";
import air from "../fixtures/openweather/air.json";

function jsonResponse(body: unknown, status = 200): Response {
  return new Response(JSON.stringify(body), {
    status,
    headers: { "content-type": "application/json" },
  });
}

function client(fetchImpl: typeof fetch, key = "test-key"): OpenWeatherClient {
  return new OpenWeatherClient({ fetch: fetchImpl, getApiKey: () => key, timeoutMs: 50 });
}

describe("OpenWeatherClient errors", () => {
  it("maps HTTP 401 to auth", async () => {
    const fetchImpl = vi.fn(async () => jsonResponse({ cod: 401, message: "Invalid API key" }, 401));
    await expect(client(fetchImpl).getCurrent(STOCKHOLM)).rejects.toMatchObject({
      code: "auth",
    } satisfies Partial<WeatherError>);
    const called = fetchImpl.mock.calls.at(0)?.at(0);
    expect(called).toBeDefined();
    const url = String(called);
    expect(url).toContain("lat=59.3293");
    expect(url).toContain("appid=test-key");
  });

  it("maps HTTP 429 to rate_limit", async () => {
    const fetchImpl = vi.fn(async () => jsonResponse({ message: "limit" }, 429));
    await expect(client(fetchImpl).getCurrent(STOCKHOLM)).rejects.toMatchObject({
      code: "rate_limit",
    });
  });

  it("maps HTTP 500 to provider", async () => {
    const fetchImpl = vi.fn(async () => jsonResponse({ message: "down" }, 500));
    await expect(client(fetchImpl).getCurrent(STOCKHOLM)).rejects.toMatchObject({
      code: "provider",
    });
  });

  it("maps abort to timeout", async () => {
    const fetchImpl = vi.fn(async () => {
      const error = new Error("Aborted");
      error.name = "AbortError";
      throw error;
    });
    await expect(client(fetchImpl).getCurrent(STOCKHOLM)).rejects.toMatchObject({
      code: "timeout",
    });
  });

  it("maps fetch TypeError to network", async () => {
    const fetchImpl = vi.fn(async () => {
      throw new TypeError("Failed to fetch");
    });
    await expect(client(fetchImpl).getCurrent(STOCKHOLM)).rejects.toMatchObject({
      code: "network",
    });
  });

  it("maps invalid JSON to malformed", async () => {
    const fetchImpl = vi.fn(async () => new Response("nope", { status: 200 }));
    await expect(client(fetchImpl).getCurrent(STOCKHOLM)).rejects.toMatchObject({
      code: "malformed",
    });
  });

  it("does not put the API key in error messages", async () => {
    const fetchImpl = vi.fn(async () => jsonResponse({}, 401));
    await client(fetchImpl)
      .getCurrent(STOCKHOLM)
      .then(
        () => {
          throw new Error("expected failure");
        },
        (error: unknown) => {
          expect(String(error)).not.toContain("test-key");
        },
      );
  });
});

describe("OpenWeatherClient success", () => {
  it("parses current weather over HTTPS metric URLs", async () => {
    const fetchImpl = vi.fn(async () => jsonResponse(current));
    const result = await client(fetchImpl).getCurrent(STOCKHOLM);
    expect(result.current.temperatureC).toBe(14.2);
    const called = String(fetchImpl.mock.calls.at(0)?.at(0));
    expect(called).toMatch(/^https:\/\/api\.openweathermap\.org\/data\/2\.5\/weather/);
    expect(called).toContain("units=metric");
  });
});

describe("getSnapshot request discipline", () => {
  it("issues one current, one forecast, and one air request", async () => {
    const fetchImpl = vi.fn(async (input: RequestInfo | URL) => {
      const url = String(input);
      if (url.includes("/weather")) return jsonResponse(current);
      if (url.includes("/forecast")) return jsonResponse(forecast);
      if (url.includes("/air_pollution")) return jsonResponse(air);
      return jsonResponse({}, 404);
    });
    const { createWeatherProvider } = await import("../../src/weather/provider");
    const provider = createWeatherProvider({ fetch: fetchImpl, getApiKey: () => "test-key" });
    const snapshot = await provider.getSnapshot(STOCKHOLM);
    expect(fetchImpl).toHaveBeenCalledTimes(3);
    expect(snapshot.days.length).toBeGreaterThan(0);
    expect(snapshot.airQuality?.category).toBe("fair");
    expect(JSON.stringify(snapshot)).not.toContain("temp_min");
    expect(JSON.stringify(snapshot)).not.toMatch(/"cod"/);
  });
});
