import { describe, expect, it } from "vitest";
import { rangeBar } from "../src/range-bar";

describe("rangeBar", () => {
  it("positions a day range within the forecast extrema", () => {
    expect(rangeBar(8, 14, 4, 20)).toEqual({
      startPercent: 25,
      widthPercent: 37.5,
    });
  });

  it("handles identical extrema without dividing by zero", () => {
    expect(rangeBar(10, 10, 10, 10)).toEqual({
      startPercent: 0,
      widthPercent: 100,
    });
  });
});
