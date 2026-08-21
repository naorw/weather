import { describe, expect, it } from "vitest";
import { normalizePlaceCandidates } from "../../src/weather/openweather/geocode";

describe("geocode normalize", () => {
  it("keeps name, region, and country so duplicates can be distinguished", () => {
    const results = normalizePlaceCandidates([
      { name: "Springfield", lat: 39.7817, lon: -89.6501, country: "US", state: "Illinois" },
      { name: "Springfield", lat: 42.1015, lon: -72.5898, country: "US", state: "Massachusetts" },
      { name: "", lat: 1, lon: 2, country: "XX" },
      { name: "Nowhere", lat: "bad", lon: 0 },
    ]);
    expect(results).toHaveLength(2);
    expect(results[0]).toMatchObject({
      displayName: "Springfield",
      region: "Illinois",
      country: "US",
    });
    expect(results[1]?.region).toBe("Massachusetts");
  });

  it("returns an empty list for a non-array payload", () => {
    expect(normalizePlaceCandidates({ message: "bad" })).toEqual([]);
  });
});
