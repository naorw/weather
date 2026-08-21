import { describe, expect, it } from "vitest";
import { fakeToday } from "../src/fake-today";

describe("fakeToday", () => {
  it("uses Stockholm as the Phase 0 static location", () => {
    expect(fakeToday.location).toBe("Stockholm");
  });

  it("does not assume a seven-day forecast", () => {
    expect(fakeToday.days.length).not.toBe(7);
    expect(fakeToday.days.length).toBeGreaterThanOrEqual(3);
  });

  it("supplies hourly points and atmospheric modules", () => {
    expect(fakeToday.hours.length).toBeGreaterThanOrEqual(6);
    expect(fakeToday.atmosphere).toMatchObject({
      wind: expect.any(String),
      precipitation: expect.any(String),
      humidity: expect.any(String),
      pressure: expect.any(String),
    });
  });
});
