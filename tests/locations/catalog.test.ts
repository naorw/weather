import { afterEach, describe, expect, it } from "vitest";
import { createLocationCatalog } from "../../src/locations/catalog";
import { DEFAULT_PLACE, parsePlace, placeCacheKey, placeFromFields } from "../../src/locations/model";
import { resetAppStores } from "../../src/persist";

const london = placeFromFields(
  {
    displayName: "London",
    coordinates: { latitude: 51.5074, longitude: -0.1278 },
    country: "GB",
    region: "England",
  },
  "search",
);

const londonRepeat = placeFromFields(
  {
    displayName: "London",
    coordinates: { latitude: 51.50741, longitude: -0.12779 },
    country: "GB",
    region: "England",
  },
  "search",
);

describe("location identity", () => {
  it("uses 4-decimal coordinate keys", () => {
    expect(placeCacheKey({ latitude: 59.3293, longitude: 18.0686 })).toBe("59.3293:18.0686");
    expect(london.id).toBe(londonRepeat.id);
  });

  it("drops malformed records", () => {
    expect(parsePlace({ displayName: "X" })).toBeUndefined();
    expect(parsePlace({ displayName: "X", latitude: 999, longitude: 0 })).toBeUndefined();
    expect(parsePlace({ displayName: "Oslo", latitude: 59.91, longitude: 10.75, country: "NO" })?.displayName).toBe(
      "Oslo",
    );
  });
});

describe("location catalog", () => {
  afterEach(async () => {
    await resetAppStores();
  });

  it("saves, lists, and removes cities without duplicates", async () => {
    const catalog = createLocationCatalog();
    const first = await catalog.save(london);
    const second = await catalog.save(londonRepeat);
    expect(second.id).toBe(first.id);
    expect(await catalog.listSaved()).toHaveLength(1);

    await catalog.setActive(first);
    const paris = await catalog.save(
      placeFromFields(
        { displayName: "Paris", coordinates: { latitude: 48.8566, longitude: 2.3522 }, country: "FR" },
        "search",
      ),
    );
    expect((await catalog.listSaved()).map((place) => place.displayName)).toEqual(["London", "Paris"]);

    await catalog.setActive(first);
    const next = await catalog.remove(first.id);
    expect(next.id).toBe(paris.id);
    expect(await catalog.getActive()).toMatchObject({ displayName: "Paris" });
    expect(await catalog.listSaved()).toHaveLength(1);
  });

  it("falls back to Stockholm when the last saved city is removed", async () => {
    const catalog = createLocationCatalog();
    const saved = await catalog.save(london);
    await catalog.setActive(saved);
    const next = await catalog.remove(saved.id);
    expect(next.id).toBe(DEFAULT_PLACE.id);
    expect(await catalog.getActive()).toMatchObject({ displayName: "Stockholm", country: "SE" });
  });

  it("recovers missing active location with the first-run default", async () => {
    const catalog = createLocationCatalog();
    expect(await catalog.getActive()).toMatchObject({ displayName: "Stockholm" });
  });

  it("skips a corrupt saved record without wiping the rest", async () => {
    const catalog = createLocationCatalog();
    await catalog.save(london);
    const { putRecord, STORES } = await import("../../src/persist");
    await putRecord(STORES.places, { id: "bad", displayName: 1, latitude: "nope" });
    const listed = await catalog.listSaved();
    expect(listed).toHaveLength(1);
    expect(listed[0]?.displayName).toBe("London");
  });
});
