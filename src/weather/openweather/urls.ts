export const OPENWEATHER_BASE = "https://api.openweathermap.org";

export const OPENWEATHER_PATHS = {
  current: "/data/2.5/weather",
  forecast: "/data/2.5/forecast",
  air: "/data/2.5/air_pollution",
} as const;

export const REQUEST_TIMEOUT_MS = 12_000;

export type OpenWeatherEndpoint = keyof typeof OPENWEATHER_PATHS;

function isLocalViteHost(hostname: string): boolean {
  return (
    hostname === "localhost" ||
    hostname === "127.0.0.1" ||
    hostname === "[::1]" ||
    hostname.endsWith(".local") ||
    /^192\.168\./.test(hostname) ||
    /^10\./.test(hostname) ||
    /^172\.(1[6-9]|2\d|3[0-1])\./.test(hostname)
  );
}

export function usesDevProxy(): boolean {
  if (import.meta.env.MODE === "test") return false;
  if (typeof window !== "undefined" && isLocalViteHost(window.location.hostname)) {
    return true;
  }
  return Boolean(import.meta.env.DEV);
}

/** Absolute OpenWeather URL in tests/production hosts; same-origin `/ow/...` on Vite. */
export function openWeatherRequestUrl(path: string): URL {
  if (usesDevProxy()) {
    return new URL(`/ow${path}`, "http://weather.invalid");
  }
  return new URL(path, OPENWEATHER_BASE);
}

export function openWeatherFetchHref(url: URL): string {
  if (usesDevProxy()) {
    return `${url.pathname}${url.search}`;
  }
  return url.toString();
}
