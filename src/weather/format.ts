import type { WeatherSnapshot } from "./models";

export function formatSnapshotSummary(snapshot: WeatherSnapshot): string {
  const loc = snapshot.location.country
    ? `${snapshot.location.displayName} ${snapshot.location.country}`
    : snapshot.location.displayName;
  const days = snapshot.days
    .map((d) => `${d.localDate}${d.partial ? "*" : ""}`)
    .join(", ");
  const aq = snapshot.airQuality
    ? `AQ ${snapshot.airQuality.category} (OW ${snapshot.airQuality.openWeatherAqi})`
    : "AQ unavailable";
  return [
    `${loc} · ${snapshot.current.temperatureC}°C · ${snapshot.current.conditionText}`,
    `Points ${snapshot.points.length} · days ${snapshot.days.length}: ${days}`,
    aq,
  ].join("\n");
}
