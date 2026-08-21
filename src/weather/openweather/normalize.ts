import { mapOpenWeatherCondition } from "../conditions";
import { WeatherError } from "../errors";
import type { AirQuality, CurrentConditions, ForecastPoint, Location, Precipitation, Wind } from "../models";
import { asFiniteNumber, probabilityToPercent } from "../units";

function readObject(value: unknown, label: string): Record<string, unknown> {
  if (value === null || typeof value !== "object" || Array.isArray(value)) {
    throw new WeatherError("malformed", `Weather payload missing ${label}.`);
  }
  return value as Record<string, unknown>;
}

function weatherInfo(payload: Record<string, unknown>): { id: number; text: string } {
  const weather = payload.weather;
  if (!Array.isArray(weather) || weather.length === 0) {
    throw new WeatherError("malformed", "Weather payload missing condition list.");
  }
  const first = readObject(weather[0], "condition");
  const id = asFiniteNumber(first.id);
  if (id === undefined) {
    throw new WeatherError("malformed", "Weather payload missing condition id.");
  }
  const text =
    typeof first.description === "string" && first.description.length > 0
      ? first.description
      : "unknown";
  return { id, text };
}

function windFrom(payload: Record<string, unknown>): Wind {
  const raw = payload.wind;
  if (raw === undefined) return { speedMps: 0 };
  const wind = readObject(raw, "wind");
  const speed = asFiniteNumber(wind.speed);
  if (speed === undefined) return { speedMps: 0 };
  const result: Wind = { speedMps: speed };
  const deg = asFiniteNumber(wind.deg);
  const gust = asFiniteNumber(wind.gust);
  if (deg !== undefined) result.directionDeg = deg;
  if (gust !== undefined) result.gustMps = gust;
  return result;
}

function precipitationFrom(
  payload: Record<string, unknown>,
  pop?: unknown,
): Precipitation {
  const result: Precipitation = {};
  const percent = probabilityToPercent(asFiniteNumber(pop));
  if (percent !== undefined) result.probabilityPercent = percent;

  const rain = payload.rain;
  if (rain && typeof rain === "object" && !Array.isArray(rain)) {
    const record = rain as Record<string, unknown>;
    const mm = asFiniteNumber(record["1h"]) ?? asFiniteNumber(record["3h"]);
    if (mm !== undefined) result.rainMm = mm;
  }
  const snow = payload.snow;
  if (snow && typeof snow === "object" && !Array.isArray(snow)) {
    const record = snow as Record<string, unknown>;
    const mm = asFiniteNumber(record["1h"]) ?? asFiniteNumber(record["3h"]);
    if (mm !== undefined) result.snowMm = mm;
  }
  return result;
}

export function normalizeLocation(payload: unknown, fallbackName: string): Location {
  const root = readObject(payload, "location");
  const coordSource =
    root.coord && typeof root.coord === "object"
      ? (root.coord as Record<string, unknown>)
      : root.city && typeof root.city === "object"
        ? ((root.city as Record<string, unknown>).coord as Record<string, unknown> | undefined)
        : undefined;
  const lat = asFiniteNumber(coordSource?.lat);
  const lon = asFiniteNumber(coordSource?.lon);
  if (lat === undefined || lon === undefined) {
    throw new WeatherError("malformed", "Weather payload missing coordinates.");
  }
  const city = root.city && typeof root.city === "object" ? (root.city as Record<string, unknown>) : root;
  const sys = root.sys && typeof root.sys === "object" ? (root.sys as Record<string, unknown>) : {};
  const name =
    (typeof city.name === "string" && city.name) ||
    (typeof root.name === "string" && root.name) ||
    fallbackName;
  const country =
    (typeof city.country === "string" && city.country) ||
    (typeof sys.country === "string" && sys.country) ||
    undefined;
  const timezoneOffsetSeconds =
    asFiniteNumber(city.timezone) ?? asFiniteNumber(root.timezone) ?? 0;
  return {
    displayName: name,
    coordinates: { latitude: lat, longitude: lon },
    country,
    timezoneOffsetSeconds,
  };
}

export function normalizeCurrent(payload: unknown): CurrentConditions {
  const root = readObject(payload, "current weather");
  const main = readObject(root.main, "current temperatures");
  const temperatureC = asFiniteNumber(main.temp);
  const feelsLikeC = asFiniteNumber(main.feels_like);
  const observed = asFiniteNumber(root.dt);
  if (temperatureC === undefined || feelsLikeC === undefined || observed === undefined) {
    throw new WeatherError("malformed", "Current weather payload missing required fields.");
  }
  const info = weatherInfo(root);
  const clouds =
    root.clouds && typeof root.clouds === "object"
      ? asFiniteNumber((root.clouds as Record<string, unknown>).all)
      : undefined;
  return {
    observedAtMs: observed * 1000,
    temperatureC,
    feelsLikeC,
    highC: asFiniteNumber(main.temp_max),
    lowC: asFiniteNumber(main.temp_min),
    condition: mapOpenWeatherCondition(info.id),
    conditionText: info.text,
    visibilityM: asFiniteNumber(root.visibility),
    cloudPercent: clouds,
    wind: windFrom(root),
    precipitation: precipitationFrom(root),
    humidityPercent: asFiniteNumber(main.humidity),
    pressureHpa: asFiniteNumber(main.pressure),
  };
}

export function normalizeForecastPoints(payload: unknown): ForecastPoint[] {
  const root = readObject(payload, "forecast");
  if (!Array.isArray(root.list)) {
    throw new WeatherError("malformed", "Forecast payload missing list.");
  }
  const points: ForecastPoint[] = [];
  for (const entry of root.list) {
    try {
      points.push(normalizeForecastPoint(entry));
    } catch (error) {
      if (error instanceof WeatherError && error.code === "malformed") continue;
      throw error;
    }
  }
  if (points.length === 0) {
    throw new WeatherError("malformed", "Forecast contained no usable points.");
  }
  return points;
}

function normalizeForecastPoint(entry: unknown): ForecastPoint {
  const root = readObject(entry, "forecast point");
  const main = readObject(root.main, "forecast temperatures");
  const temperatureC = asFiniteNumber(main.temp);
  const at = asFiniteNumber(root.dt);
  if (temperatureC === undefined || at === undefined) {
    throw new WeatherError("malformed", "Forecast point missing required fields.");
  }
  const info = weatherInfo(root);
  return {
    atMs: at * 1000,
    temperatureC,
    feelsLikeC: asFiniteNumber(main.feels_like),
    condition: mapOpenWeatherCondition(info.id),
    conditionText: info.text,
    precipitation: precipitationFrom(root, root.pop),
    wind: windFrom(root),
    humidityPercent: asFiniteNumber(main.humidity),
    pressureHpa: asFiniteNumber(main.pressure),
  };
}

export function normalizeAirQuality(payload: unknown): AirQuality {
  const root = readObject(payload, "air quality");
  if (!Array.isArray(root.list) || root.list.length === 0) {
    throw new WeatherError("malformed", "Air quality payload missing observations.");
  }
  const first = readObject(root.list[0], "air quality observation");
  const observed = asFiniteNumber(first.dt);
  const main = first.main && typeof first.main === "object" ? (first.main as Record<string, unknown>) : {};
  const aqi = asFiniteNumber(main.aqi);
  if (observed === undefined || aqi === undefined) {
    throw new WeatherError("malformed", "Air quality payload missing required fields.");
  }
  const raw =
    first.components && typeof first.components === "object"
      ? (first.components as Record<string, unknown>)
      : {};
  return {
    observedAtMs: observed * 1000,
    openWeatherAqi: aqi,
    category: aqiCategory(aqi),
    components: {
      co: asFiniteNumber(raw.co),
      no: asFiniteNumber(raw.no),
      no2: asFiniteNumber(raw.no2),
      o3: asFiniteNumber(raw.o3),
      so2: asFiniteNumber(raw.so2),
      pm2_5: asFiniteNumber(raw.pm2_5),
      pm10: asFiniteNumber(raw.pm10),
      nh3: asFiniteNumber(raw.nh3),
    },
  };
}

function aqiCategory(aqi: number): AirQuality["category"] {
  switch (aqi) {
    case 1:
      return "good";
    case 2:
      return "fair";
    case 3:
      return "moderate";
    case 4:
      return "poor";
    case 5:
      return "very-poor";
    default:
      return "unknown";
  }
}
