import { afterEach, describe, expect, it } from "vitest";
import {
  deleteValue,
  getValue,
  openProofDb,
  putValue,
  resetStore,
} from "../src/storage";

describe("phase 0 IndexedDB proof store", () => {
  afterEach(async () => {
    await resetStore();
  });

  it("initializes, writes, reads, updates, and deletes a key", async () => {
    const db = await openProofDb();
    expect(db.name).toBe("org.radilabs.weather.phase0");

    await putValue("probe", { n: 1 });
    expect(await getValue("probe")).toEqual({ n: 1 });

    await putValue("probe", { n: 2 });
    expect(await getValue("probe")).toEqual({ n: 2 });

    await deleteValue("probe");
    expect(await getValue("probe")).toBeUndefined();
  });

  it("resetStore clears remaining keys", async () => {
    await putValue("keep", "x");
    await resetStore();
    expect(await getValue("keep")).toBeUndefined();
  });
});
