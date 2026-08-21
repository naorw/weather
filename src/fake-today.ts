import type { GlyphId } from "./glyphs";

export type HourPoint = {
  hour: string;
  tempC: number;
  condition: GlyphId;
  precipChance: number;
};

export type DayPoint = {
  day: string;
  condition: GlyphId;
  precipChance: number;
  lowC: number;
  highC: number;
};

export type TodaySnapshot = {
  location: string;
  temperatureC: number;
  condition: string;
  conditionId: GlyphId;
  highC: number;
  lowC: number;
  feelsLikeC: number;
  updatedAt: string;
  hours: HourPoint[];
  days: DayPoint[];
  atmosphere: {
    wind: string;
    precipitation: string;
    humidity: string;
    pressure: string;
  };
};

export const fakeToday: TodaySnapshot = {
  location: "Stockholm",
  temperatureC: 14,
  condition: "Broken cloud",
  conditionId: "partly-cloudy",
  highC: 16,
  lowC: 9,
  feelsLikeC: 12,
  updatedAt: "21:40 CEST",
  hours: [
    { hour: "22", tempC: 14, condition: "partly-cloudy", precipChance: 10 },
    { hour: "23", tempC: 13, condition: "overcast", precipChance: 20 },
    { hour: "00", tempC: 12, condition: "overcast", precipChance: 25 },
    { hour: "01", tempC: 11, condition: "drizzle", precipChance: 40 },
    { hour: "02", tempC: 11, condition: "rain", precipChance: 55 },
    { hour: "03", tempC: 10, condition: "rain", precipChance: 50 },
    { hour: "04", tempC: 10, condition: "overcast", precipChance: 30 },
    { hour: "05", tempC: 9, condition: "partly-cloudy", precipChance: 15 },
  ],
  days: [
    { day: "Fri", condition: "partly-cloudy", precipChance: 20, lowC: 9, highC: 16 },
    { day: "Sat", condition: "rain", precipChance: 70, lowC: 8, highC: 13 },
    { day: "Sun", condition: "overcast", precipChance: 40, lowC: 7, highC: 14 },
    { day: "Mon", condition: "clear", precipChance: 5, lowC: 8, highC: 18 },
    { day: "Tue", condition: "drizzle", precipChance: 35, lowC: 10, highC: 17 },
  ],
  atmosphere: {
    wind: "WSW 4 m/s",
    precipitation: "0.2 mm",
    humidity: "72%",
    pressure: "1014 hPa",
  },
};
