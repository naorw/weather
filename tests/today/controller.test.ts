import { describe, expect, it } from "vitest";
import { createTodayController } from "../../src/today/controller";
import type { WeatherSnapshot } from "../../src/weather";
import { WeatherError } from "../../src/weather/errors";

const ok: WeatherSnapshot = {
  location: {
    displayName: "Stockholm",
    coordinates: { latitude: 59.33, longitude: 18.07 },
    timezoneOffsetSeconds: 0,
  },
  current: {
    observedAtMs: 1,
    temperatureC: 10,
    feelsLikeC: 9,
    condition: "clear",
    conditionText: "clear sky",
    wind: { speedMps: 1 },
    precipitation: {},
  },
  points: [],
  days: [],
  fetchedAtMs: 1,
};

describe("today controller", () => {
  it("goes loading then loaded", async () => {
    const controller = createTodayController(async () => ok);
    expect(controller.getState().status).toBe("idle");
    const pending = controller.load();
    expect(controller.getState().status).toBe("loading");
    await pending;
    expect(controller.getState().status).toBe("loaded");
    expect(controller.getState().snapshot?.current.temperatureC).toBe(10);
  });

  it("ignores concurrent refresh storms", async () => {
    let calls = 0;
    const controller = createTodayController(async () => {
      calls += 1;
      await new Promise((resolve) => setTimeout(resolve, 20));
      return ok;
    });
    await Promise.all([controller.load(), controller.refresh(), controller.refresh()]);
    expect(calls).toBe(1);
  });

  it("keeps last valid snapshot when refresh fails", async () => {
    let fail = false;
    const controller = createTodayController(async () => {
      if (fail) throw new WeatherError("network", "down");
      return ok;
    });
    await controller.load();
    fail = true;
    await controller.refresh();
    expect(controller.getState().status).toBe("loaded");
    expect(controller.getState().snapshot?.current.temperatureC).toBe(10);
    expect(controller.getState().error?.code).toBe("network");
  });

  it("uses error state when the first load fails", async () => {
    const controller = createTodayController(async () => {
      throw new WeatherError("timeout", "slow");
    });
    await controller.load();
    expect(controller.getState().status).toBe("error");
    expect(controller.getState().snapshot).toBeUndefined();
  });
});
