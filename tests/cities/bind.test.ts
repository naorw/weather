import { afterEach, describe, expect, it } from "vitest";
import { createCitiesController } from "../../src/cities/controller";
import { createLocationCatalog } from "../../src/locations/catalog";
import { resetAppStores } from "../../src/persist";
import { bindCities } from "../../src/screens/cities";

describe("cities place field", () => {
  afterEach(async () => {
    document.body.replaceChildren();
    await resetAppStores();
  });

  it("keeps the same input focused while typing", async () => {
    const root = document.createElement("div");
    document.body.appendChild(root);
    const controller = createCitiesController({
      catalog: createLocationCatalog(),
      provider: { searchPlaces: async () => [], reverseGeocode: async () => undefined },
      today: { setPlace: async () => undefined },
      permission: async () => "prompt",
      navigate: () => undefined,
    });
    const unbind = bindCities(root, controller);
    await controller.hydrate();

    const input = root.querySelector<HTMLInputElement>("#cities-query");
    expect(input).toBeInstanceOf(HTMLInputElement);
    if (!input) throw new Error("missing place field");
    input.focus();
    input.value = "Pa";
    input.dispatchEvent(new Event("input", { bubbles: true }));

    const after = root.querySelector("#cities-query");
    expect(after).toBe(input);
    expect(document.activeElement).toBe(input);
    expect(controller.getState().query).toBe("Pa");
    unbind();
  });
});
