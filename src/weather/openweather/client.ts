import { WeatherError, errorFromFetchFailure, errorFromHttpStatus } from "../errors";
import type { AirQuality, Coordinates, CurrentConditions, ForecastPoint, Location } from "../models";
import type { PlaceCandidate } from "../place";
import { normalizePlaceCandidates } from "./geocode";
import { normalizeAirQuality, normalizeCurrent, normalizeForecastPoints, normalizeLocation } from "./normalize";
import {
  GEO_SEARCH_LIMIT,
  OPENWEATHER_GEO_PATHS,
  OPENWEATHER_PATHS,
  REQUEST_TIMEOUT_MS,
  openWeatherFetchHref,
  openWeatherRequestUrl,
  usesDevProxy,
  type OpenWeatherEndpoint,
} from "./urls";

export type OpenWeatherDeps = {
  fetch: typeof fetch;
  getApiKey: () => string | undefined;
  timeoutMs?: number;
};

export class OpenWeatherClient {
  private readonly fetchImpl: typeof fetch;
  private readonly getApiKey: () => string | undefined;
  private readonly timeoutMs: number;

  constructor(deps: OpenWeatherDeps) {
    this.fetchImpl = deps.fetch;
    this.getApiKey = deps.getApiKey;
    this.timeoutMs = deps.timeoutMs ?? REQUEST_TIMEOUT_MS;
  }

  async getCurrent(coordinates: Coordinates, signal?: AbortSignal): Promise<{
    location: Location;
    current: CurrentConditions;
  }> {
    const payload = await this.request("current", coordinates, signal);
    return {
      location: normalizeLocation(payload, "Unknown"),
      current: normalizeCurrent(payload),
    };
  }

  async getForecast(coordinates: Coordinates, signal?: AbortSignal): Promise<{
    location: Location;
    points: ForecastPoint[];
  }> {
    const payload = await this.request("forecast", coordinates, signal);
    return {
      location: normalizeLocation(payload, "Unknown"),
      points: normalizeForecastPoints(payload),
    };
  }

  async getAirQuality(coordinates: Coordinates, signal?: AbortSignal): Promise<AirQuality> {
    const payload = await this.request("air", coordinates, signal);
    return normalizeAirQuality(payload);
  }

  async searchPlaces(query: string, signal?: AbortSignal): Promise<PlaceCandidate[]> {
    const q = query.trim();
    if (!q) return [];
    const url = this.keyedUrl(openWeatherRequestUrl(OPENWEATHER_GEO_PATHS.direct));
    url.searchParams.set("q", q);
    url.searchParams.set("limit", String(GEO_SEARCH_LIMIT));
    return normalizePlaceCandidates(await this.fetchJson(url, signal));
  }

  async reverseGeocode(
    coordinates: Coordinates,
    signal?: AbortSignal,
  ): Promise<PlaceCandidate | undefined> {
    const url = this.keyedUrl(openWeatherRequestUrl(OPENWEATHER_GEO_PATHS.reverse));
    url.searchParams.set("lat", String(coordinates.latitude));
    url.searchParams.set("lon", String(coordinates.longitude));
    url.searchParams.set("limit", "1");
    return normalizePlaceCandidates(await this.fetchJson(url, signal))[0];
  }

  private keyedUrl(url: URL): URL {
    const key = this.requireKey();
    if (!usesDevProxy()) url.searchParams.set("appid", key);
    return url;
  }

  private requireKey(): string {
    const key = this.getApiKey();
    if (!key) {
      throw new WeatherError("auth", "Weather API key is not configured.");
    }
    return key;
  }

  private async request(
    endpoint: OpenWeatherEndpoint,
    coordinates: Coordinates,
    external?: AbortSignal,
  ): Promise<unknown> {
    const url = this.keyedUrl(openWeatherRequestUrl(OPENWEATHER_PATHS[endpoint]));
    url.searchParams.set("lat", String(coordinates.latitude));
    url.searchParams.set("lon", String(coordinates.longitude));
    url.searchParams.set("units", "metric");
    return this.fetchJson(url, external);
  }

  private async fetchJson(url: URL, external?: AbortSignal): Promise<unknown> {
    const controller = new AbortController();
    const timer = setTimeout(() => controller.abort(), this.timeoutMs);
    const onExternalAbort = () => controller.abort();
    external?.addEventListener("abort", onExternalAbort, { once: true });
    if (external?.aborted) controller.abort();

    try {
      const response = await this.fetchImpl.bind(globalThis)(openWeatherFetchHref(url), {
        method: "GET",
        signal: controller.signal,
        credentials: "omit",
      });
      return await this.readBody(response, url.pathname);
    } catch (error) {
      throw errorFromFetchFailure(error, url.pathname);
    } finally {
      clearTimeout(timer);
      external?.removeEventListener("abort", onExternalAbort);
    }
  }

  private async readBody(response: Response, requestPath?: string): Promise<unknown> {
    const text = await response.text();
    let body: unknown;
    try {
      body = text.length === 0 ? {} : JSON.parse(text);
    } catch {
      if (!response.ok) throw errorFromHttpStatus(response.status, requestPath);
      throw new WeatherError("malformed", "Weather provider returned non-JSON.", response.status, {
        requestPath,
      });
    }

    const bodyCode = bodyCodeOf(body);
    if (!response.ok || (bodyCode !== undefined && bodyCode !== 200)) {
      const status = response.ok ? bodyCode ?? response.status : response.status;
      if (status === 401 || status === 403) throw errorFromHttpStatus(401, requestPath);
      if (status === 429) throw errorFromHttpStatus(429, requestPath);
      if (status === 404) throw errorFromHttpStatus(404, requestPath);
      if (!response.ok) throw errorFromHttpStatus(response.status, requestPath);
      throw new WeatherError("provider", "Weather provider returned an error payload.", status, {
        requestPath,
      });
    }
    return body;
  }
}

function bodyCodeOf(body: unknown): number | undefined {
  if (!body || typeof body !== "object") return undefined;
  const cod = (body as { cod?: unknown }).cod;
  if (typeof cod === "number") return cod;
  if (typeof cod === "string") {
    const parsed = Number(cod);
    return Number.isFinite(parsed) ? parsed : undefined;
  }
  return undefined;
}
