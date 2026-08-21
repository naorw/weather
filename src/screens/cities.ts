import { citiesController, placeLabel, type CitiesController } from "../cities/controller";
import type { CitiesState } from "../cities/controller";
import { escapeHtml } from "../today/present";
import type { Place } from "../locations/model";

function searchCopy(state: CitiesState): string {
  switch (state.search) {
    case "loading":
      return "Searching.";
    case "empty":
      return "No matching places.";
    case "error":
      return state.searchError ?? "Search failed.";
    case "results":
      return `${state.results.length} result${state.results.length === 1 ? "" : "s"}.`;
    default:
      return "Enter a place name, then search.";
  }
}

function geoCopy(state: CitiesState): string {
  switch (state.geo) {
    case "locating":
      return "Requesting device location.";
    case "denied":
      return "Location permission denied. Weather still works with search.";
    case "unsupported":
      return "This browser does not provide device location.";
    case "insecure":
      return "Device location needs a secure context (HTTPS or localhost).";
    case "timeout":
      return "Device location timed out.";
    case "unavailable":
      return "Device location is unavailable.";
    case "error":
      return "Device location failed.";
    default:
      return "Optional. Never requested on startup.";
  }
}

function placeRow(place: Place, activeId: string, kind: "result" | "saved"): string {
  const active = place.id === activeId;
  const remove =
    kind === "saved"
      ? `<button type="button" class="cities-row__remove" data-remove="${escapeHtml(place.id)}">Remove</button>`
      : "";
  const action = kind === "result" ? "select" : "switch";
  return `
    <li class="cities-row ${active ? "is-active" : ""}">
      <button type="button" class="cities-row__main" data-${action}="${escapeHtml(place.id)}">
        <span class="cities-row__name">${escapeHtml(placeLabel(place))}</span>
        <span class="cities-row__meta">${active ? "Active" : kind === "result" ? "Use" : "Switch"}</span>
      </button>
      ${remove}
    </li>`;
}

export function renderCities(state: CitiesState): string {
  const savedEmpty = state.saved.length === 0;
  const activeSaved = state.saved.some((place) => place.id === state.active.id);
  return `
    <section class="cities" aria-labelledby="screen-title">
      <h1 id="screen-title" class="cities__title">Cities</h1>
      <form class="cities-search" id="cities-search">
        <label class="cities-search__field">
          <span>Place</span>
          <input id="cities-query" name="q" type="search" autocomplete="off" value="${escapeHtml(state.query)}" placeholder="Stockholm" />
        </label>
        <button type="submit"${state.search === "loading" ? " disabled" : ""}>
          ${state.search === "loading" ? "Searching" : "Search"}
        </button>
      </form>
      <p class="cities-status" role="status">${escapeHtml(searchCopy(state))}</p>
      ${
        state.results.length > 0
          ? `<ul class="cities-list">${state.results.map((place) => placeRow(place, state.active.id, "result")).join("")}</ul>`
          : ""
      }
      <div class="cities-geo">
        <button type="button" id="cities-geo" ${
          state.geoDeniedSticky || state.geo === "locating" || state.geoUnsupported ? "disabled" : ""
        }>
          ${state.geo === "locating" ? "Locating" : "Use device location"}
        </button>
        <p class="cities-status">${escapeHtml(geoCopy(state))}</p>
      </div>
      <section class="cities-block" aria-labelledby="active-heading">
        <h2 id="active-heading" class="cities-block__title">Active</h2>
        <p class="cities-active">${escapeHtml(placeLabel(state.active))}</p>
        ${
          activeSaved
            ? ""
            : `<button type="button" id="cities-save-active">Save this location</button>`
        }
      </section>
      <section class="cities-block" aria-labelledby="saved-heading">
        <h2 id="saved-heading" class="cities-block__title">Saved</h2>
        ${
          savedEmpty
            ? `<p class="cities-status">None yet. Search or use device location.</p>`
            : `<ul class="cities-list">${state.saved.map((place) => placeRow(place, state.active.id, "saved")).join("")}</ul>`
        }
      </section>
    </section>`;
}

export function bindCities(root: HTMLElement, controller: CitiesController = citiesController()): () => void {
  const paint = (): void => {
    const state = controller.getState();
    root.innerHTML = renderCities(state);

    root.querySelector("#cities-search")?.addEventListener("submit", (event) => {
      event.preventDefault();
      void controller.search();
    });
    root.querySelector("#cities-query")?.addEventListener("input", (event) => {
      if (event.target instanceof HTMLInputElement) controller.setQuery(event.target.value);
    });
    root.querySelector("#cities-geo")?.addEventListener("click", () => {
      void controller.useDevice();
    });
    root.querySelector("#cities-save-active")?.addEventListener("click", () => {
      void controller.saveActive();
    });
    for (const button of root.querySelectorAll<HTMLButtonElement>("[data-select]")) {
      button.addEventListener("click", () => {
        const id = button.dataset.select;
        const place = controller.getState().results.find((item) => item.id === id);
        if (place) void controller.selectResult(place);
      });
    }
    for (const button of root.querySelectorAll<HTMLButtonElement>("[data-switch]")) {
      button.addEventListener("click", () => {
        const id = button.dataset.switch;
        const place = controller.getState().saved.find((item) => item.id === id);
        if (place) void controller.switchTo(place);
      });
    }
    for (const button of root.querySelectorAll<HTMLButtonElement>("[data-remove]")) {
      button.addEventListener("click", () => {
        const id = button.dataset.remove;
        if (id) void controller.remove(id);
      });
    }
  };

  const unsubscribe = controller.subscribe(paint);
  paint();
  void controller.hydrate();
  return unsubscribe;
}
