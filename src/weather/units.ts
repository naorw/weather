/** Calendar date YYYY-MM-DD in the location's UTC offset. */
export function localDateKey(epochMs: number, timezoneOffsetSeconds: number): string {
  const shifted = new Date(epochMs + timezoneOffsetSeconds * 1000);
  const year = shifted.getUTCFullYear();
  const month = String(shifted.getUTCMonth() + 1).padStart(2, "0");
  const day = String(shifted.getUTCDate()).padStart(2, "0");
  return `${year}-${month}-${day}`;
}

export function probabilityToPercent(pop: number | undefined): number | undefined {
  if (typeof pop !== "number" || Number.isNaN(pop)) return undefined;
  return Math.round(Math.min(1, Math.max(0, pop)) * 100);
}

export function asFiniteNumber(value: unknown): number | undefined {
  return typeof value === "number" && Number.isFinite(value) ? value : undefined;
}
