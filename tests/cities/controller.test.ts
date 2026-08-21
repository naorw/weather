import { afterEach, describe, expect, it, vi } from "vitest";
import { createCitiesController } from "../../src/cities/controller";
import { createLocationCatalog } from "../../src/locations/catalog";
import { placeFromFields } from "../../src/locations/model";
import { resetAppStores } from "../../src/persist";
import type { PlaceCandidate } from "../../src/weather";

const paris: PlaceCandidate = {
  displayName: "Paris",
  coordinates: { latitude: 48.8566, longitude: 2.3522 },
  country: "FR",
};

describe("cities controller geolocation", () => {
  afterEach(async () => {
    await resetAppStores();
  });
  it("activates reverse-geocoded coordinates when permission is granted", async () => {
    const catalog = createLocationCatalog();
    const setPlace = vi.fn(async () => undefined);
    const controller = createCitiesController({
      catalog,
      provider: {
        searchPlaces: async () => [],
        reverseGeocode: async () => paris,
      },
      today: { setPlace },
      locate: async () => ({ ok: true, coords: paris.coordinates }),
      permission: async () => "prompt",
      navigate: () => undefined,
    });

    await controller.useDevice();
    expect(setPlace).toHaveBeenCalledWith(
      expect.objectContaining({ displayName: "Paris", country: "FR", source: "device" }),
    );
    expect((await catalog.getActive()).displayName).toBe("Paris");
  });

  it("keeps a fallback name when reverse geocoding fails", async () => {
    const catalog = createLocationCatalog();
    const controller = createCitiesController({
      catalog,
      provider: {
        searchPlaces: async () => [],
        reverseGeocode: async () => {
          throw new Error("down");
        },
      },
      today: { setPlace: async () => undefined },
      locate: async () => ({ ok: true, coords: { latitude: 10, longitude: 20 } }),
      permission: async () => "prompt",
      navigate: () => undefined,
    });

    await controller.useDevice();
    expect((await catalog.getActive()).displayName).toBe("Device location");
  });

  it("does not nag after denial", async () => {
    const locate = vi.fn(async () => ({ ok: false as const, reason: "denied" as const }));
    const controller = createCitiesController({
      catalog: createLocationCatalog(),
      provider: { searchPlaces: async () => [], reverseGeocode: async () => undefined },
      today: { setPlace: async () => undefined },
      locate,
      permission: async () => "denied",
      navigate: () => undefined,
    });

    await controller.hydrate();
    await controller.useDevice();
    expect(locate).not.toHaveBeenCalled();
    expect(controller.getState().geoDeniedSticky).toBe(true);
  });

  it("searches and can save a result without multiplying it", async () => {
    const catalog = createLocationCatalog();
    const controller = createCitiesController({
      catalog,
      provider: {
        searchPlaces: async () => [paris],
        reverseGeocode: async () => undefined,
      },
      today: { setPlace: async () => undefined },
      permission: async () => "prompt",
      navigate: () => undefined,
    });

    controller.setQuery("Paris");
    await controller.search();
    expect(controller.getState().results).toHaveLength(1);
    const result = controller.getState().results[0];
    expect(result).toBeDefined();
    if (!result) throw new Error("missing result");
    await controller.selectResult(result);
    await controller.selectResult(placeFromFields(paris, "search"));
    expect(await catalog.listSaved()).toHaveLength(1);
  });
});
