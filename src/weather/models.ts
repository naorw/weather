export type ConditionCategory =
  | "clear"
  | "partly-cloudy"
  | "cloudy"
  | "overcast"
  | "drizzle"
  | "light-rain"
  | "rain"
  | "heavy-rain"
  | "thunderstorm"
  | "light-snow"
  | "snow"
  | "fog"
  | "unknown";

export type Coordinates = {
  latitude: number;
  longitude: number;
};

export type Location = {
  displayName: string;
  coordinates: Coordinates;
  country?: string;
  /** IANA name is not supplied by the free 2.5 APIs. Offset is seconds east of UTC. */
  timezoneOffsetSeconds: number;
};

export type Wind = {
  speedMps: number;
  directionDeg?: number;
  gustMps?: number;
};

export type Precipitation = {
  /** 0–100. Undefined when the provider omitted probability. */
  probabilityPercent?: number;
  /** Undefined when omitted; 0 means a measured zero. */
  rainMm?: number;
  snowMm?: number;
};

export type CurrentConditions = {
  observedAtMs: number;
  temperatureC: number;
  feelsLikeC: number;
  /** Station envelope from current weather, not a daily summary. */
  highC?: number;
  lowC?: number;
  condition: ConditionCategory;
  conditionText: string;
  visibilityM?: number;
  cloudPercent?: number;
  wind: Wind;
  precipitation: Precipitation;
  humidityPercent?: number;
  pressureHpa?: number;
};

export type ForecastPoint = {
  atMs: number;
  temperatureC: number;
  feelsLikeC?: number;
  condition: ConditionCategory;
  conditionText: string;
  precipitation: Precipitation;
  wind: Wind;
  humidityPercent?: number;
  pressureHpa?: number;
};

export type DailySummary = {
  /** Calendar date in the location offset, YYYY-MM-DD. */
  localDate: string;
  highC: number;
  lowC: number;
  condition: ConditionCategory;
  precipitation: Precipitation;
  pointCount: number;
  partial: boolean;
};

export type AirQuality = {
  observedAtMs: number;
  /** OpenWeather 1–5 index, not EPA/CAQI. */
  openWeatherAqi: number;
  category: "good" | "fair" | "moderate" | "poor" | "very-poor" | "unknown";
  components: {
    co?: number;
    no?: number;
    no2?: number;
    o3?: number;
    so2?: number;
    pm2_5?: number;
    pm10?: number;
    nh3?: number;
  };
};

export type WeatherSnapshot = {
  location: Location;
  current: CurrentConditions;
  points: ForecastPoint[];
  days: DailySummary[];
  airQuality?: AirQuality;
  fetchedAtMs: number;
};

export const STOCKHOLM: Coordinates = {
  latitude: 59.3293,
  longitude: 18.0686,
};
