import { glyphLabel, glyphMarkup } from "../glyphs";
import { placeLabel } from "../locations/model";
import { rangeBar } from "../range-bar";
import type { TodayController } from "../today/controller";
import { todayController } from "../today/controller";
import {
  MISSING,
  compassFromDeg,
  errorCopy,
  escapeHtml,
  formatAge,
  formatMm,
  formatPercent,
  formatPressure,
  formatSpeed,
  formatTempC,
  formatUpdated,
  formatVisibility,
  formatLocalHour,
  formatWeekday,
  formatWind,
  heroHighLow,
} from "../today/present";
import type { TodayState } from "../today/state";
import type { DailySummary, ForecastPoint, WeatherSnapshot } from "../weather";

function glyph(condition: WeatherSnapshot["current"]["condition"], size = "glyph--sm"): string {
  const id = condition;
  return `<span class="glyph ${size}" aria-label="${escapeHtml(glyphLabel(id))}">${glyphMarkup(id)}</span>`;
}

function metaItem(label: string, value: string): string {
  return `<div><dt>${label}</dt><dd>${escapeHtml(value)}</dd></div>`;
}

function renderHours(points: ForecastPoint[], offset: number): string {
  if (points.length === 0) {
    return `<p class="status-copy">No 3-hour points in this payload.</p>`;
  }
  return `<ul class="hours">${points
    .map((point) => {
      const precip = formatPercent(point.precipitation.probabilityPercent);
      return `
        <li class="hour">
          <span class="hour__t">${formatLocalHour(point.atMs, offset)}</span>
          ${glyph(point.condition)}
          <span class="hour__temp">${formatTempC(point.temperatureC)}</span>
          <span class="hour__precip">${precip}</span>
        </li>`;
    })
    .join("")}</ul>`;
}

function renderDays(days: DailySummary[]): string {
  if (days.length === 0) {
    return `<p class="status-copy">No daily summaries in this payload.</p>`;
  }
  const min = Math.min(...days.map((d) => d.lowC));
  const max = Math.max(...days.map((d) => d.highC));
  return `<ul class="days">${days
    .map((day) => {
      const bar = rangeBar(day.lowC, day.highC, min, max);
      const precip = formatPercent(day.precipitation.probabilityPercent);
      const partial = day.partial
        ? `<span class="day__partial" title="Incomplete forecast coverage">part.</span>`
        : `<span class="day__partial" aria-hidden="true"></span>`;
      return `
        <li class="day">
          <span class="day__name">${formatWeekday(day.localDate)}</span>
          ${glyph(day.condition)}
          <span class="day__precip">${precip}</span>
          ${partial}
          <span class="day__low">${formatTempC(day.lowC)}</span>
          <span class="range" aria-hidden="true">
            <span class="range__fill" style="left:${bar.startPercent}%;width:${bar.widthPercent}%"></span>
          </span>
          <span class="day__high">${formatTempC(day.highC)}</span>
        </li>`;
    })
    .join("")}</ul>`;
}

function windInstrument(snapshot: WeatherSnapshot): string {
  const wind = snapshot.current.wind;
  const deg = wind.directionDeg;
  const compass = compassFromDeg(deg);
  const needle =
    deg === undefined
      ? ""
      : `<g transform="rotate(${deg} 24 24)"><path d="M24 8 L24 22"/><path d="M21 12 L24 8 L27 12"/></g>`;
  return `
    <div class="wind">
      <svg class="wind__mark" viewBox="0 0 48 48" aria-hidden="true">
        <circle cx="24" cy="24" r="17"/>
        <path d="M24 4 v4"/>
        ${needle}
      </svg>
      <dl class="wind__readout">
        <div><dt>Wind</dt><dd>${escapeHtml(formatWind(wind))}</dd></div>
        <div><dt>Gust</dt><dd>${formatSpeed(wind.gustMps)}</dd></div>
        <div><dt>From</dt><dd>${compass ? compass : MISSING}</dd></div>
      </dl>
    </div>`;
}

function atmosphere(snapshot: WeatherSnapshot): string {
  const precip = snapshot.current.precipitation;
  const precipText = [
    precip.rainMm !== undefined ? `rain ${formatMm(precip.rainMm)}` : undefined,
    precip.snowMm !== undefined ? `snow ${formatMm(precip.snowMm)}` : undefined,
    precip.probabilityPercent !== undefined
      ? formatPercent(precip.probabilityPercent)
      : undefined,
  ]
    .filter(Boolean)
    .join(" · ");
  const aq = snapshot.airQuality;
  const aqPrimary = aq ? aq.category.replace("-", " ") : MISSING;
  const aqMeta = aq ? `OpenWeather ${aq.openWeatherAqi}/5` : "";
  return `
    <section class="panel panel--telemetry" aria-labelledby="atm-heading">
      <h2 id="atm-heading" class="panel__title">Atmosphere</h2>
      ${windInstrument(snapshot)}
      <dl class="telemetry">
        ${metaItem("Precip", precipText || MISSING)}
        ${metaItem("Humidity", formatPercent(snapshot.current.humidityPercent))}
        ${metaItem("Pressure", formatPressure(snapshot.current.pressureHpa))}
        ${metaItem("Visibility", formatVisibility(snapshot.current.visibilityM))}
        ${metaItem("Cloud", formatPercent(snapshot.current.cloudPercent))}
      </dl>
      <div class="aq">
        <p class="aq__label">Air quality</p>
        <p class="aq__value">${escapeHtml(aqPrimary)}</p>
        <p class="aq__meta">${escapeHtml(aqMeta)}${aqMeta ? " · not EPA/CAQI" : ""}</p>
      </div>
    </section>`;
}

function locationTitle(state: TodayState, snapshot?: WeatherSnapshot): string {
  if (state.place) return placeLabel(state.place);
  if (!snapshot) return "Weather";
  return snapshot.location.country
    ? `${snapshot.location.displayName} ${snapshot.location.country}`
    : snapshot.location.displayName;
}

function freshnessBanner(state: TodayState, nowMs = Date.now()): string {
  if (!state.snapshot || !state.fetchedAtMs) return "";
  if (state.freshness !== "fresh" && state.freshness !== "stale") return "";
  const mark = state.freshness === "stale" ? "STALE" : "CACHED";
  return `<p class="freshness freshness--${state.freshness}" role="status"><span>${mark}</span> UPDATED ${formatAge(state.fetchedAtMs, nowMs)}</p>`;
}

function renderLoaded(state: TodayState, snapshot: WeatherSnapshot, refreshHint?: string): string {
  const loc = locationTitle(state, snapshot);
  const range = heroHighLow(snapshot);
  const offset = snapshot.location.timezoneOffsetSeconds;
  return `
    <article class="today">
      ${freshnessBanner(state)}
      ${refreshHint ? `<p class="refresh-note" role="status">${escapeHtml(refreshHint)}</p>` : ""}
      <header class="hero">
        <div class="hero__text">
          <h1 class="hero__loc">${escapeHtml(loc)}</h1>
          <p class="hero__temp">${formatTempC(snapshot.current.temperatureC)}</p>
          <p class="hero__cond">${glyph(snapshot.current.condition, "glyph--lg")} ${escapeHtml(snapshot.current.conditionText)}</p>
          <dl class="hero__meta">
            ${metaItem("High", formatTempC(range.high))}
            ${metaItem("Low", formatTempC(range.low))}
            ${metaItem("Feels", formatTempC(snapshot.current.feelsLikeC))}
            ${metaItem("Updated", formatUpdated(snapshot.current.observedAtMs, offset))}
          </dl>
        </div>
        <div class="hero__instrument" aria-hidden="true">
          <span class="hero__reticle"></span>
          ${glyph(snapshot.current.condition, "glyph--hero")}
        </div>
      </header>
      <section class="panel" aria-labelledby="hours-heading">
        <h2 id="hours-heading" class="panel__title">Next 3 hours</h2>
        ${renderHours(snapshot.points, offset)}
      </section>
      <section class="panel" aria-labelledby="days-heading">
        <h2 id="days-heading" class="panel__title">Next days</h2>
        ${renderDays(snapshot.days)}
      </section>
      ${atmosphere(snapshot)}
    </article>`;
}

function renderStatus(kind: "loading" | "empty" | "error", title: string, detail: string): string {
  return `
    <article class="today today--${kind}">
      <header class="hero hero--status">
        <h1 class="hero__loc">${escapeHtml(title)}</h1>
        <p class="hero__temp hero__temp--idle">WX</p>
        <p class="hero__cond">${escapeHtml(detail)}</p>
        <div class="hero__instrument" aria-hidden="true"><span class="hero__reticle"></span></div>
      </header>
    </article>`;
}

export function renderToday(state: TodayState): string {
  if (state.status === "loading" || state.status === "idle") {
    return renderStatus("loading", locationTitle(state), "Acquiring weather.");
  }
  if (state.status === "empty") {
    return renderStatus("empty", "No weather", "No usable weather payload.");
  }
  if (state.status === "error" && state.error) {
    const copy = errorCopy(state.error);
    return renderStatus("error", copy.title, copy.detail);
  }
  if (state.snapshot) {
    const hint = state.error
      ? `${errorCopy(state.error).title}. Last valid reading kept.`
      : state.refreshing
        ? "Refreshing…"
        : undefined;
    return renderLoaded(state, state.snapshot, hint);
  }
  return renderStatus("empty", "No weather", "No usable weather payload.");
}

export function bindToday(root: HTMLElement, controller: TodayController = todayController()): () => void {
  const paint = (): void => {
    const state = controller.getState();
    root.innerHTML = `
      <div class="today-wrap">
        <div class="today-toolbar">
          <button type="button" id="today-refresh" ${state.refreshing ? "disabled" : ""}>
            ${state.refreshing ? "Refreshing" : "Refresh"}
          </button>
        </div>
        ${renderToday(state)}
      </div>
    `;
    root.querySelector("#today-refresh")?.addEventListener("click", () => {
      void controller.refresh();
    });
  };

  const unsubscribe = controller.subscribe(paint);
  const onOnline = (): void => controller.onOnline();
  window.addEventListener("online", onOnline);
  paint();
  if (controller.getState().status === "idle") {
    void controller.load();
  } else {
    void controller.refresh();
  }
  return () => {
    unsubscribe();
    window.removeEventListener("online", onOnline);
  };
}
