import type { PlaceCandidate } from "../place";

export function normalizePlaceCandidates(payload: unknown): PlaceCandidate[] {
  if (!Array.isArray(payload)) return [];
  const results: PlaceCandidate[] = [];
  for (const item of payload) {
    const candidate = normalizePlaceCandidate(item);
    if (candidate) results.push(candidate);
  }
  return results;
}

export function normalizePlaceCandidate(value: unknown): PlaceCandidate | undefined {
  if (!value || typeof value !== "object") return undefined;
  const row = value as Record<string, unknown>;
  const latitude = Number(row.lat);
  const longitude = Number(row.lon);
  if (!Number.isFinite(latitude) || !Number.isFinite(longitude)) return undefined;
  if (Math.abs(latitude) > 90 || Math.abs(longitude) > 180) return undefined;
  const name = typeof row.name === "string" ? row.name.trim() : "";
  if (!name) return undefined;
  const country = typeof row.country === "string" && row.country.trim() ? row.country.trim() : undefined;
  const region = typeof row.state === "string" && row.state.trim() ? row.state.trim() : undefined;
  return {
    displayName: name,
    coordinates: { latitude, longitude },
    country,
    region,
  };
}
