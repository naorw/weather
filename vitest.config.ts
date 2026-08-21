import { loadEnv } from "vite";
import { defineConfig } from "vitest/config";

const env = loadEnv("test", process.cwd(), "");

export default defineConfig({
  test: {
    environment: "happy-dom",
    setupFiles: ["tests/setup.ts"],
    env,
  },
});
