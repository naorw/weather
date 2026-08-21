import { describe, expect, it } from "vitest";
import { requestDevicePosition } from "../../src/geo/locate";

function fakeGeo(
  impl: (success: PositionCallback, error?: PositionErrorCallback) => void,
): Pick<Geolocation, "getCurrentPosition"> {
  return {
    getCurrentPosition(success, error) {
      impl(success, error ?? undefined);
    },
  };
}

describe("device geolocation", () => {
  it("returns coordinates when permission is granted", async () => {
    const result = await requestDevicePosition(
      fakeGeo((success) => {
        success({
          coords: { latitude: 59.33, longitude: 18.07 },
        } as GeolocationPosition);
      }),
      { secure: true },
    );
    expect(result).toEqual({ ok: true, coords: { latitude: 59.33, longitude: 18.07 } });
  });

  it("maps permission denial", async () => {
    const result = await requestDevicePosition(
      fakeGeo((_success, error) => {
        error?.({ code: 1, message: "denied" } as GeolocationPositionError);
      }),
      { secure: true },
    );
    expect(result).toEqual({ ok: false, reason: "denied" });
  });

  it("maps timeout and unavailable", async () => {
    await expect(
      requestDevicePosition(
        fakeGeo((_success, error) => {
          error?.({ code: 3, message: "timeout" } as GeolocationPositionError);
        }),
        { secure: true },
      ),
    ).resolves.toEqual({ ok: false, reason: "timeout" });
    await expect(
      requestDevicePosition(
        fakeGeo((_success, error) => {
          error?.({ code: 2, message: "missing" } as GeolocationPositionError);
        }),
        { secure: true },
      ),
    ).resolves.toEqual({ ok: false, reason: "unavailable" });
  });

  it("treats a missing API as unsupported", async () => {
    await expect(requestDevicePosition(null, { secure: true })).resolves.toEqual({
      ok: false,
      reason: "unsupported",
    });
  });

  it("rejects insecure contexts", async () => {
    await expect(
      requestDevicePosition(
        fakeGeo((success) => {
          success({ coords: { latitude: 1, longitude: 2 } } as GeolocationPosition);
        }),
        { secure: false },
      ),
    ).resolves.toEqual({ ok: false, reason: "insecure" });
  });
});
