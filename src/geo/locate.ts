import type { Coordinates } from "../weather/models";

export const GEO_TIMEOUT_MS = 12_000;

export type GeoFailureReason =
  | "unsupported"
  | "denied"
  | "unavailable"
  | "timeout"
  | "insecure";

export type GeoResult =
  | { ok: true; coords: Coordinates }
  | { ok: false; reason: GeoFailureReason };

export type GeoPermissionState = "granted" | "denied" | "prompt" | "unknown";

type GeoApi = Pick<Geolocation, "getCurrentPosition">;

export async function queryGeoPermission(
  permissions: Pick<Permissions, "query"> | undefined = typeof navigator !== "undefined"
    ? navigator.permissions
    : undefined,
): Promise<GeoPermissionState> {
  if (!permissions?.query) return "unknown";
  try {
    const status = await permissions.query({ name: "geolocation" });
    if (status.state === "granted" || status.state === "denied" || status.state === "prompt") {
      return status.state;
    }
    return "unknown";
  } catch {
    return "unknown";
  }
}

export async function requestDevicePosition(
  api?: GeoApi | null,
  options?: { secure?: boolean; timeoutMs?: number },
): Promise<GeoResult> {
  const secure =
    options?.secure ?? (typeof window !== "undefined" ? window.isSecureContext : true);
  if (!secure) return { ok: false, reason: "insecure" };

  const geo =
    api === undefined
      ? typeof navigator !== "undefined"
        ? navigator.geolocation
        : undefined
      : api;
  if (!geo || typeof geo.getCurrentPosition !== "function") {
    return { ok: false, reason: "unsupported" };
  }

  const timeoutMs = options?.timeoutMs ?? GEO_TIMEOUT_MS;

  return await new Promise((resolve) => {
    geo.getCurrentPosition(
      (position) => {
        resolve({
          ok: true,
          coords: {
            latitude: position.coords.latitude,
            longitude: position.coords.longitude,
          },
        });
      },
      (error) => {
        if (error.code === 1) resolve({ ok: false, reason: "denied" });
        else if (error.code === 3) resolve({ ok: false, reason: "timeout" });
        else resolve({ ok: false, reason: "unavailable" });
      },
      {
        enableHighAccuracy: false,
        timeout: timeoutMs,
        maximumAge: 0,
      },
    );
  });
}
