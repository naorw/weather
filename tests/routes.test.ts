import { describe, expect, it } from "vitest";
import { parseRoute, ROUTES } from "../src/routes";

describe("parseRoute", () => {
  it("defaults to today for empty hash", () => {
    expect(parseRoute("")).toBe("today");
    expect(parseRoute("#")).toBe("today");
    expect(parseRoute("#/")).toBe("today");
  });

  it("maps known destinations", () => {
    expect(parseRoute("#/radar")).toBe("radar");
    expect(parseRoute("#/cities")).toBe("cities");
    expect(parseRoute("#/settings")).toBe("settings");
    expect(parseRoute("#/today")).toBe("today");
  });

  it("falls back to today for unknown destinations", () => {
    expect(parseRoute("#/unknown")).toBe("today");
    expect(parseRoute("#weather")).toBe("today");
  });

  it("exposes the four shell destinations", () => {
    expect([...ROUTES]).toEqual(["today", "radar", "cities", "settings"]);
  });
});
