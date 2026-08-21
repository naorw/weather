import { getValue, putValue, resetStore } from "../storage";
import {
  STOCKHOLM,
  describeFetchFailure,
  formatSnapshotSummary,
  weatherProvider,
} from "../weather";

export function renderSettings(): string {
  return `
    <section class="placeholder" aria-labelledby="screen-title">
      <h1 id="screen-title" class="placeholder__title">Settings</h1>
      <p class="placeholder__copy">Units, location behavior, and provider configuration belong to later phases.</p>
      <p class="placeholder__meta">Weather 0.0.0 · org.radilabs.weather</p>
      <div class="probe">
        <p class="panel__title">Local storage probe</p>
        <p id="probe-status" class="placeholder__copy">IndexedDB proof store is idle.</p>
        <button type="button" id="probe-run">Run storage probe</button>
        <button type="button" id="probe-reset">Reset proof store</button>
      </div>
      <div class="probe">
        <p class="panel__title">Weather data probe</p>
        <p id="weather-probe-status" class="placeholder__copy">Fetches Stockholm through the provider boundary. Today stays static.</p>
        <button type="button" id="weather-probe-run">Fetch Stockholm weather</button>
      </div>
    </section>
  `;
}

export function bindSettings(root: HTMLElement): void {
  const status = root.querySelector("#probe-status");
  const run = root.querySelector("#probe-run");
  const reset = root.querySelector("#probe-reset");
  if (status instanceof HTMLElement) {
    run?.addEventListener("click", async () => {
      await putValue("probe", { at: Date.now() });
      const value = await getValue<{ at: number }>("probe");
      status.textContent = value
        ? `Wrote and read probe timestamp ${value.at}.`
        : "Probe read failed.";
    });
    reset?.addEventListener("click", async () => {
      await resetStore();
      status.textContent = "Proof store reset.";
    });
  }

  const weatherStatus = root.querySelector("#weather-probe-status");
  const weatherRun = root.querySelector("#weather-probe-run");
  if (!(weatherStatus instanceof HTMLElement)) return;

  weatherRun?.addEventListener("click", async () => {
    weatherStatus.textContent = "Fetching…";
    try {
      const snapshot = await weatherProvider().getSnapshot(STOCKHOLM);
      weatherStatus.textContent = formatSnapshotSummary(snapshot);
    } catch (error) {
      weatherStatus.textContent = `Weather probe failed (${describeFetchFailure(error)}).`;
    }
  });
}
