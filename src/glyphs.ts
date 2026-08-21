import type { ConditionCategory } from "./weather/models";

export type GlyphId = ConditionCategory;

const stroke = "currentColor";

function svg(inner: string, label: string): string {
  return `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="${stroke}" stroke-width="1.25" stroke-linecap="square" stroke-linejoin="miter" aria-hidden="true" focusable="false"><title>${label}</title>${inner}</svg>`;
}

const glyphs: Record<GlyphId, { label: string; markup: string }> = {
  clear: {
    label: "Clear",
    markup: svg(
      `<circle cx="12" cy="12" r="4"/><path d="M12 3v2M12 19v2M3 12h2M19 12h2M5.6 5.6l1.4 1.4M17 17l1.4 1.4M5.6 18.4l1.4-1.4M17 7l1.4-1.4"/>`,
      "Clear",
    ),
  },
  "partly-cloudy": {
    label: "Partly cloudy",
    markup: svg(
      `<circle cx="9" cy="10" r="3"/><path d="M7 18h10a3.5 3.5 0 0 0 0-7h-.4A5 5 0 0 0 7 13.2"/>`,
      "Partly cloudy",
    ),
  },
  cloudy: {
    label: "Cloudy",
    markup: svg(
      `<path d="M6 16h11a3 3 0 0 0 0-6h-.4A5 5 0 0 0 6 13"/><path d="M8 19h8"/>`,
      "Cloudy",
    ),
  },
  overcast: {
    label: "Overcast",
    markup: svg(
      `<path d="M6 17h11a3 3 0 0 0 0-6h-.5A5 5 0 0 0 6 14"/><path d="M5 20h13"/>`,
      "Overcast",
    ),
  },
  drizzle: {
    label: "Drizzle",
    markup: svg(
      `<path d="M6 14h11a3 3 0 0 0 0-6h-.5A5 5 0 0 0 6 11"/><path d="M9 17v2M12 16v3M15 17v2"/>`,
      "Drizzle",
    ),
  },
  "light-rain": {
    label: "Light rain",
    markup: svg(
      `<path d="M6 13h11a3 3 0 0 0 0-6h-.5A5 5 0 0 0 6 10"/><path d="M9 16v3M13 16v3"/>`,
      "Light rain",
    ),
  },
  rain: {
    label: "Rain",
    markup: svg(
      `<path d="M6 13h11a3 3 0 0 0 0-6h-.5A5 5 0 0 0 6 10"/><path d="M8 16l-1 4M12 15l-1 5M16 16l-1 4"/>`,
      "Rain",
    ),
  },
  "heavy-rain": {
    label: "Heavy rain",
    markup: svg(
      `<path d="M6 12h11a3 3 0 0 0 0-6h-.5A5 5 0 0 0 6 9"/><path d="M7 15l-1.5 5M11 14l-1.5 6M15 15l-1.5 5M19 16l-1 4"/>`,
      "Heavy rain",
    ),
  },
  thunderstorm: {
    label: "Thunderstorm",
    markup: svg(
      `<path d="M6 12h10a3 3 0 0 0 0-6h-.5A5 5 0 0 0 6 9"/><path d="M11 12l-2 5h3l-2 5"/>`,
      "Thunderstorm",
    ),
  },
  "light-snow": {
    label: "Light snow",
    markup: svg(
      `<path d="M6 13h11a3 3 0 0 0 0-6h-.5A5 5 0 0 0 6 10"/><path d="M9 17h0M12 19h0M15 17h0"/>`,
      "Light snow",
    ),
  },
  snow: {
    label: "Snow",
    markup: svg(
      `<path d="M12 7v10M8 9l8 6M16 9l-8 6"/><circle cx="12" cy="12" r="1"/>`,
      "Snow",
    ),
  },
  fog: {
    label: "Fog",
    markup: svg(
      `<path d="M4 10h16M5 13h14M6 16h12"/>`,
      "Fog",
    ),
  },
  unknown: {
    label: "Unknown",
    markup: svg(`<circle cx="12" cy="12" r="7"/><path d="M12 8v5M12 16v1"/>`, "Unknown"),
  },
};

export function glyphFor(condition: ConditionCategory): GlyphId {
  return condition in glyphs ? condition : "unknown";
}

export function glyphMarkup(id: GlyphId): string {
  return glyphs[glyphFor(id)].markup;
}

export function glyphLabel(id: GlyphId): string {
  return glyphs[glyphFor(id)].label;
}
