import { defineConfig, loadEnv } from "vite";
import { VitePWA } from "vite-plugin-pwa";

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), "");
  const apiKey = env.VITE_OPENWEATHER_API_KEY ?? "";

  const openWeatherProxy = {
    "/ow": {
      target: "https://api.openweathermap.org",
      changeOrigin: true,
      rewrite(path: string) {
        const url = new URL(path.replace(/^\/ow/, "") || "/", "https://api.openweathermap.org");
        if (apiKey) url.searchParams.set("appid", apiKey);
        return `${url.pathname}${url.search}`;
      },
    },
  };

  return {
    server: { proxy: openWeatherProxy },
    preview: { proxy: openWeatherProxy },
    plugins: [
      VitePWA({
        injectRegister: false,
        registerType: "autoUpdate",
        devOptions: { enabled: false },
        includeAssets: ["icons/*.png", "icons/*.svg"],
        manifest: false,
        workbox: {
          globPatterns: ["**/*.{js,css,html,ico,png,svg,webmanifest}"],
          navigateFallback: "index.html",
        },
      }),
    ],
  };
});
