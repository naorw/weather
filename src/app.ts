import { bindCities, renderCities } from "./screens/cities";
import { bindSettings, renderSettings } from "./screens/settings";
import { renderPlaceholder } from "./screens/placeholder";
import { bindToday, renderToday } from "./screens/today";
import { parseRoute, routeHash, ROUTES, type Route } from "./routes";
import { citiesController } from "./cities/controller";

const LABELS: Record<Route, string> = {
  today: "Today",
  radar: "Radar",
  cities: "Cities",
  settings: "Settings",
};

function screen(route: Route): string {
  switch (route) {
    case "today":
      return renderToday({ status: "idle", refreshing: false });
    case "radar":
      return renderPlaceholder(
        "Radar",
        "Map and radar layers are deferred to a later phase.",
      );
    case "cities":
      return renderCities(citiesController().getState());
    case "settings":
      return renderSettings();
  }
}

export function mount(root: HTMLElement): void {
  root.innerHTML = `
    <div class="shell">
      <header class="topbar">
        <p class="topbar__mark">WX</p>
        <p class="topbar__name">Weather</p>
      </header>
      <main id="main" class="main" tabindex="-1"></main>
      <nav class="nav" aria-label="Primary">
        <ul>
          ${ROUTES.map(
            (route) => `
            <li>
              <a href="${routeHash(route)}" data-route="${route}">
                <span>${LABELS[route]}</span>
              </a>
            </li>`,
          ).join("")}
        </ul>
      </nav>
    </div>
  `;

  const main = root.querySelector("#main");
  if (!(main instanceof HTMLElement)) return;

  let unbind: (() => void) | undefined;

  const paint = (): void => {
    unbind?.();
    unbind = undefined;
    const route = parseRoute(window.location.hash);
    main.innerHTML = screen(route);
    if (route === "today") unbind = bindToday(main);
    if (route === "cities") unbind = bindCities(main);
    if (route === "settings") bindSettings(main);
    for (const link of root.querySelectorAll<HTMLAnchorElement>("[data-route]")) {
      const active = link.dataset.route === route;
      link.classList.toggle("is-active", active);
      if (active) link.setAttribute("aria-current", "page");
      else link.removeAttribute("aria-current");
    }
  };

  window.addEventListener("hashchange", paint);
  if (!window.location.hash) {
    window.location.hash = routeHash("today");
  }
  paint();
}
