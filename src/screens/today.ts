import { fakeToday } from "../fake-today";
import { glyphLabel, glyphMarkup } from "../glyphs";
import { rangeBar } from "../range-bar";

export function renderToday(): string {
  const days = fakeToday.days;
  const min = Math.min(...days.map((d) => d.lowC));
  const max = Math.max(...days.map((d) => d.highC));

  const hours = fakeToday.hours
    .map(
      (h) => `
      <li class="hour">
        <span class="hour__t">${h.hour}</span>
        <span class="glyph glyph--sm" aria-label="${glyphLabel(h.condition)}">${glyphMarkup(h.condition)}</span>
        <span class="hour__temp">${h.tempC}°</span>
        <span class="hour__precip">${h.precipChance}%</span>
      </li>`,
    )
    .join("");

  const dayRows = days
    .map((d) => {
      const bar = rangeBar(d.lowC, d.highC, min, max);
      return `
      <li class="day">
        <span class="day__name">${d.day}</span>
        <span class="glyph glyph--sm" aria-label="${glyphLabel(d.condition)}">${glyphMarkup(d.condition)}</span>
        <span class="day__precip">${d.precipChance}%</span>
        <span class="day__low">${d.lowC}°</span>
        <span class="range" aria-hidden="true">
          <span class="range__fill" style="left:${bar.startPercent}%;width:${bar.widthPercent}%"></span>
        </span>
        <span class="day__high">${d.highC}°</span>
      </li>`;
    })
    .join("");

  return `
    <article class="today">
      <header class="hero">
        <h1 class="hero__loc">${fakeToday.location}</h1>
        <p class="hero__temp">${fakeToday.temperatureC}°</p>
        <p class="hero__cond">${fakeToday.condition}</p>
        <dl class="hero__meta">
          <div><dt>High</dt><dd>${fakeToday.highC}°</dd></div>
          <div><dt>Low</dt><dd>${fakeToday.lowC}°</dd></div>
          <div><dt>Feels</dt><dd>${fakeToday.feelsLikeC}°</dd></div>
          <div><dt>Updated</dt><dd>${fakeToday.updatedAt}</dd></div>
        </dl>
      </header>

      <section class="panel" aria-labelledby="hours-heading">
        <h2 id="hours-heading" class="panel__title">Next hours</h2>
        <ul class="hours">${hours}</ul>
      </section>

      <section class="panel" aria-labelledby="days-heading">
        <h2 id="days-heading" class="panel__title">Next days</h2>
        <ul class="days">${dayRows}</ul>
      </section>

      <section class="panel panel--telemetry" aria-labelledby="atm-heading">
        <h2 id="atm-heading" class="panel__title">Atmosphere</h2>
        <dl class="telemetry">
          <div><dt>Wind</dt><dd>${fakeToday.atmosphere.wind}</dd></div>
          <div><dt>Precip</dt><dd>${fakeToday.atmosphere.precipitation}</dd></div>
          <div><dt>Humidity</dt><dd>${fakeToday.atmosphere.humidity}</dd></div>
          <div><dt>Pressure</dt><dd>${fakeToday.atmosphere.pressure}</dd></div>
        </dl>
      </section>
    </article>
  `;
}
