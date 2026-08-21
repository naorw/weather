import { pickRepresentativeCondition } from "./conditions";
import type { DailySummary, ForecastPoint, Precipitation } from "./models";
import { localDateKey } from "./units";

/** A local calendar day is complete when it has a full 3-hour set (8 points). */
export const COMPLETE_DAY_POINTS = 8;

export function aggregateDaily(
  points: ForecastPoint[],
  timezoneOffsetSeconds: number,
): DailySummary[] {
  const groups = new Map<string, ForecastPoint[]>();
  for (const point of points) {
    const key = localDateKey(point.atMs, timezoneOffsetSeconds);
    const list = groups.get(key);
    if (list) list.push(point);
    else groups.set(key, [point]);
  }

  const days: DailySummary[] = [];
  for (const [localDate, dayPoints] of groups) {
    const temps = dayPoints.map((p) => p.temperatureC);
    days.push({
      localDate,
      highC: Math.max(...temps),
      lowC: Math.min(...temps),
      condition: pickRepresentativeCondition(dayPoints.map((p) => p.condition)),
      precipitation: summarizePrecipitation(dayPoints.map((p) => p.precipitation)),
      pointCount: dayPoints.length,
      partial: dayPoints.length < COMPLETE_DAY_POINTS,
    });
  }
  return days.sort((a, b) => a.localDate.localeCompare(b.localDate));
}

function summarizePrecipitation(items: Precipitation[]): Precipitation {
  const probs = items
    .map((p) => p.probabilityPercent)
    .filter((n): n is number => n !== undefined);
  const rains = items
    .map((p) => p.rainMm)
    .filter((n): n is number => n !== undefined);
  const snows = items
    .map((p) => p.snowMm)
    .filter((n): n is number => n !== undefined);

  const precipitation: Precipitation = {};
  if (probs.length > 0) precipitation.probabilityPercent = Math.max(...probs);
  if (rains.length > 0) precipitation.rainMm = rains.reduce((a, b) => a + b, 0);
  if (snows.length > 0) precipitation.snowMm = snows.reduce((a, b) => a + b, 0);
  return precipitation;
}
