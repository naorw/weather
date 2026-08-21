import { registerSW } from "virtual:pwa-register";
import { mount } from "./app";
import "./styles/tokens.css";
import "./styles/base.css";
import "./styles/layout.css";
import "./styles/today.css";

const root = document.querySelector("#app");
if (!(root instanceof HTMLElement)) {
  throw new Error("Missing #app root");
}

mount(root);

registerSW({ immediate: true });
