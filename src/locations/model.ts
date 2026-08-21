import { STOCKHOLM, type Coordinates } from "../weather/models";

export type PlaceSource = "saved" | "search" | "device" | "default";

export type Place = {
  id: string;
  cacheKey: string;
  displayName: string;
  coordinates: Coordinates;
  country?: string;
  region?: string;
  source: PlaceSource;
};

const COORD_DECIMALS = 4;

export function roundCoord(value: number): string {
  return value.toFixed(COORD_DECIMALS);
}

export function placeCacheKey(coordinates: Coordinates): string {
  return `${roundCoord(coordinates.latitude)}:${roundCoord(coordinates.longitude)}`;
}

export function placeLabel(place: Pick<Place, "displayName" | "country" | "region">): string {
  const region = place.region?.trim();
  const country = place.country?.trim();
  const extra = [region, country].filter(Boolean).join(" ");
  return extra ? `${place.displayName} ${extra}` : place.displayName;
}

export function placeFromFields(
  fields: {
    displayName: string;
    coordinates: Coordinates;
    country?: string;
    region?: string;
  },
  source: PlaceSource,
): Place {
  const cacheKey = placeCacheKey(fields.coordinates);
  return {
    id: cacheKey,
    cacheKey,
    displayName: fields.displayName.trim(),
    coordinates: {
      latitude: fields.coordinates.latitude,
      longitude: fields.coordinates.longitude,
    },
    country: fields.country,
    region: fields.region,
    source,
  };
}

export const DEFAULT_PLACE: Place = placeFromFields(
  {
    displayName: "Stockholm",
    coordinates: STOCKHOLM,
    country: "SE",
  },
  "default",
);

export function parsePlace(value: unknown): Place | undefined {
  if (!value || typeof value !== "object") return undefined;
  const row = value as Record<string, unknown>;
  const displayName = typeof row.displayName === "string" ? row.displayName.trim() : "";
  if (!displayName) return undefined;

  const nested =
    row.coordinates && typeof row.coordinates === "object"
      ? (row.coordinates as Record<string, unknown>)
      : undefined;
  const latitude = Number(nested?.latitude ?? row.latitude);
  const longitude = Number(nested?.longitude ?? row.longitude);
  if (!Number.isFinite(latitude) || !Number.isFinite(longitude)) return undefined;
  if (Math.abs(latitude) > 90 || Math.abs(longitude) > 180) return undefined;

  const coordinates = { latitude, longitude };
  const cacheKey =
    typeof row.cacheKey === "string" && row.cacheKey.includes(":")
      ? row.cacheKey
      : placeCacheKey(coordinates);
  const id = typeof row.id === "string" && row.id.length > 0 ? row.id : cacheKey;
  const country = typeof row.country === "string" && row.country.trim() ? row.country.trim() : undefined;
  const region = typeof row.region === "string" && row.region.trim() ? row.region.trim() : undefined;
  const source = parseSource(row.source);

  return {
    id,
    cacheKey,
    displayName,
    coordinates,
    country,
    region,
    source,
  };
}

function parseSource(value: unknown): PlaceSource {
  if (value === "saved" || value === "search" || value === "device" || value === "default") {
    return value;
  }
  return "saved";
}
