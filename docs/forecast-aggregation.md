# Forecast aggregation

Input: normalized `ForecastPoint[]` plus `timezoneOffsetSeconds` from the location.

## Day boundary

A point belongs to local calendar date `YYYY-MM-DD` computed by shifting the UTC timestamp by `timezoneOffsetSeconds`, then reading UTC Y/M/D of the shifted instant.

The free API does not provide IANA zone names.

## High / low

- High: maximum `temperatureC` among points that day
- Low: minimum `temperatureC` among points that day

These are extrema of 3-hour steps, not true instantaneous daily min/max.

## Representative condition

The day’s `ConditionCategory` with the highest severity:

thunderstorm > heavy-rain > snow > rain > light-snow > light-rain > drizzle > fog > overcast > cloudy > partly-cloudy > clear > unknown

## Precipitation

- Probability: maximum `probabilityPercent` among points that have one
- Rain/snow amounts: **sum** of supplied mm values for that day
- Omitted amounts stay omitted unless at least one point supplied a number

## Incomplete days

A day with fewer than **8** three-hour points is `partial: true`. The first and last forecast days are usually partial. Partial days are still returned; they are not treated as complete 24-hour observations.

Horizon length is whatever the free forecast list contains. There is no seven-day assumption.
