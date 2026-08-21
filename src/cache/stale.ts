/** Cached weather younger than this is current-enough to show without STALE. */
export const FRESH_MS = 30 * 60 * 1000;

export type CacheFreshness = "fresh" | "stale" | "missing";

export function classifyFreshness(fetchedAtMs: number | undefined, nowMs: number): CacheFreshness {
  if (fetchedAtMs === undefined || !Number.isFinite(fetchedAtMs)) return "missing";
  return nowMs - fetchedAtMs < FRESH_MS ? "fresh" : "stale";
}

export function formatAge(fetchedAtMs: number, nowMs: number): string {
  const delta = Math.max(0, nowMs - fetchedAtMs);
  const minutes = Math.round(delta / 60_000);
  if (minutes < 1) return "JUST NOW";
  if (minutes === 1) return "1 MIN AGO";
  if (minutes < 60) return `${minutes} MIN AGO`;
  const hours = Math.round(minutes / 60);
  if (hours === 1) return "1 H AGO";
  if (hours < 48) return `${hours} H AGO`;
  const days = Math.round(hours / 24);
  if (days === 1) return "1 D AGO";
  return `${days} D AGO`;
}
