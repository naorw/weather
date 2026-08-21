import type { ConditionCategory } from "./models";

const SEVERITY: Record<ConditionCategory, number> = {
  thunderstorm: 100,
  "heavy-rain": 90,
  snow: 85,
  rain: 80,
  "light-snow": 75,
  "light-rain": 70,
  drizzle: 60,
  fog: 40,
  overcast: 30,
  cloudy: 25,
  "partly-cloudy": 20,
  clear: 10,
  unknown: 0,
};

export function mapOpenWeatherCondition(id: number): ConditionCategory {
  if (id >= 200 && id < 300) return "thunderstorm";
  if (id >= 300 && id < 400) return "drizzle";
  if (id === 500 || id === 520) return "light-rain";
  if (id === 502 || id === 503 || id === 504 || id === 522 || id === 531) {
    return "heavy-rain";
  }
  if (id >= 500 && id < 600) return "rain";
  if (id === 600 || id === 612 || id === 620) return "light-snow";
  if (id >= 600 && id < 700) return "snow";
  if (id >= 700 && id < 800) return "fog";
  if (id === 800) return "clear";
  if (id === 801) return "partly-cloudy";
  if (id === 802 || id === 803) return "cloudy";
  if (id === 804) return "overcast";
  return "unknown";
}

export function conditionSeverity(category: ConditionCategory): number {
  return SEVERITY[category];
}

export function pickRepresentativeCondition(
  categories: ConditionCategory[],
): ConditionCategory {
  if (categories.length === 0) return "unknown";
  return categories.reduce((worst, next) =>
    conditionSeverity(next) >= conditionSeverity(worst) ? next : worst,
  );
}
