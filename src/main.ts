import { mount } from "./app";
import "./styles/tokens.css";
import "./styles/base.css";
import "./styles/layout.css";
import "./styles/today.css";

const root = document.querySelector("#app");
if (!(root instanceof HTMLElement)) {
  throw new Error("Missing #app root");
}
const appRoot = root;

async function start(): Promise<void> {
  if (import.meta.env.DEV && "serviceWorker" in navigator) {
    const registrations = await navigator.serviceWorker.getRegistrations();
    await Promise.all(registrations.map((registration) => registration.unregister()));
  }

  mount(appRoot);

  if (!import.meta.env.DEV) {
    const { registerSW } = await import("virtual:pwa-register");
    registerSW({ immediate: true });
  }
}

void start();
