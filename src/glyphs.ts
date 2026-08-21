export type GlyphId =
  | "clear"
  | "partly-cloudy"
  | "overcast"
  | "rain"
  | "drizzle";

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
  rain: {
    label: "Rain",
    markup: svg(
      `<path d="M6 13h11a3 3 0 0 0 0-6h-.5A5 5 0 0 0 6 10"/><path d="M8 16l-1 4M12 15l-1 5M16 16l-1 4"/>`,
      "Rain",
    ),
  },
};

export function glyphMarkup(id: GlyphId): string {
  return glyphs[id].markup;
}

export function glyphLabel(id: GlyphId): string {
  return glyphs[id].label;
}
