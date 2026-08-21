import type { DailySummary, WeatherSnapshot, Wind } from "../weather";
import type { WeatherError } from "../weather/errors";
import { localDateKey } from "../weather/units";

export const MISSING = "—";

export function escapeHtml(text: string): string {
  return text
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;");
}

export function formatTempC(value: number | undefined): string {
  if (value === undefined) return MISSING;
  return `${Math.round(value)}°`;
}

export function formatPercent(value: number | undefined): string {
  if (value === undefined) return MISSING;
  return `${Math.round(value)}%`;
}

export function formatPressure(hpa: number | undefined): string {
  if (hpa === undefined) return MISSING;
  return `${Math.round(hpa)} hPa`;
}

export function formatVisibility(meters: number | undefined): string {
  if (meters === undefined) return MISSING;
  if (meters >= 1000) return `${(meters / 1000).toFixed(meters % 1000 === 0 ? 0 : 1)} km`;
  return `${Math.round(meters)} m`;
}

export function formatMm(value: number | undefined): string {
  if (value === undefined) return MISSING;
  return `${value} mm`;
}

export function formatSpeed(mps: number | undefined): string {
  if (mps === undefined) return MISSING;
  return `${mps.toFixed(mps % 1 === 0 ? 0 : 1)} m/s`;
}

const COMPASS = ["N", "NE", "E", "SE", "S", "SW", "W", "NW"] as const;

export function compassFromDeg(deg: number | undefined): string | undefined {
  if (deg === undefined) return undefined;
  const index = Math.round((((deg % 360) + 360) % 360) / 45) % 8;
  return COMPASS[index];
}

export function formatWind(wind: Wind): string {
  const dir = compassFromDeg(wind.directionDeg);
  const speed = formatSpeed(wind.speedMps);
  return dir ? `${dir} ${speed}` : speed;
}

export function formatLocalHour(epochMs: number, offsetSeconds: number): string {
  const shifted = new Date(epochMs + offsetSeconds * 1000);
  return String(shifted.getUTCHours()).padStart(2, "0");
}

export function formatWeekday(localDate: string): string {
  const [year, month, day] = localDate.split("-").map(Number);
  if (!year || !month || !day) return localDate;
  const utc = Date.UTC(year, month - 1, day);
  return ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"][new Date(utc).getUTCDay()] ?? localDate;
}

export function formatUpdated(epochMs: number, offsetSeconds: number): string {
  const hour = formatLocalHour(epochMs, offsetSeconds);
  const shifted = new Date(epochMs + offsetSeconds * 1000);
  const minute = String(shifted.getUTCMinutes()).padStart(2, "0");
  return `${hour}:${minute}`;
}

export function heroHighLow(snapshot: WeatherSnapshot): { high?: number; low?: number } {
  const todayKey = localDateKey(
    snapshot.current.observedAtMs,
    snapshot.location.timezoneOffsetSeconds,
  );
  const today = snapshot.days.find((day) => day.localDate === todayKey);
  return {
    high: today?.highC ?? snapshot.current.highC,
    low: today?.lowC ?? snapshot.current.lowC,
  };
}

export function precipSummary(day: DailySummary): string {
  return formatPercent(day.precipitation.probabilityPercent);
}

export function errorCopy(error: WeatherError): { title: string; detail: string } {
  switch (error.code) {
    case "auth":
      return {
        title: "Credentials",
        detail: "Weather provider credentials are missing or rejected.",
      };
    case "rate_limit":
      return { title: "Rate limited", detail: "The weather provider asked us to wait." };
    case "not_found":
      return { title: "Not found", detail: "The weather provider did not recognise that location." };
    case "network":
      return { title: "Offline", detail: "Network unavailable for weather request." };
    case "timeout":
      return { title: "Timeout", detail: "The weather provider did not answer in time." };
    case "provider":
      return { title: "Provider fault", detail: "The weather provider failed." };
    case "malformed":
      return { title: "Bad payload", detail: "Weather data was not usable." };
    default:
      return { title: "Weather fault", detail: "Weather request failed." };
  }
}
