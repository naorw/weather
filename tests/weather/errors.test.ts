import { describe, expect, it } from "vitest";
import { describeFetchFailure, redactSecrets } from "../../src/weather/errors";
import { WeatherError } from "../../src/weather/errors";

describe("secret redaction", () => {
  it("strips appid query values", () => {
    expect(redactSecrets("appid=super-secret&lat=1")).toBe("appid=redacted&lat=1");
  });

  it("describes failures without leaking keys", () => {
    const error = new WeatherError("network", "failed appid=super-secret");
    expect(describeFetchFailure(error)).not.toContain("super-secret");
    expect(describeFetchFailure(error)).toContain("network");
  });

  it("includes the original fetch cause when present", () => {
    const error = new WeatherError("network", "Network unavailable for weather request.", undefined, {
      cause: new TypeError("NetworkError when attempting to fetch resource."),
      requestPath: "/ow/data/2.5/weather",
    });
    const text = describeFetchFailure(error);
    expect(text).toContain("/ow/data/2.5/weather");
    expect(text).toContain("TypeError");
    expect(text).toContain("NetworkError");
  });
});
