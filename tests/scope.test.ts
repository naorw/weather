import { readFileSync, readdirSync } from "node:fs";
import { join } from "node:path";
import { describe, expect, it } from "vitest";

const root = join(import.meta.dirname, "..");

function walk(dir: string): string[] {
  const entries = readdirSync(dir, { withFileTypes: true });
  const files: string[] = [];
  for (const entry of entries) {
    if (
      entry.name === "node_modules" ||
      entry.name === "dist" ||
      entry.name === ".git"
    ) {
      continue;
    }
    const path = join(dir, entry.name);
    if (entry.isDirectory()) {
      files.push(...walk(path));
    } else {
      files.push(path);
    }
  }
  return files;
}

function read(paths: string[]): string {
  return paths
    .filter((path) => /\.(ts|js|html|css)$/.test(path))
    .map((path) => readFileSync(path, "utf8"))
    .join("\n")
    .toLowerCase();
}

describe("phase 1 scope", () => {
  it("does not add later-phase location or paid-api features", () => {
    const source = read(walk(join(root, "src")).concat([join(root, "index.html")]));
    expect(source).not.toMatch(/geolocation/);
    expect(source).not.toMatch(/navigator\.geolocation/);
    expect(source).not.toMatch(/onecall/);
    expect(source).not.toMatch(/\/data\/3\.0\//);
    expect(source).not.toMatch(/pro\.openweathermap/);
  });

  it("keeps OpenWeather URLs inside the provider client", () => {
    const ui = read([
      join(root, "src", "app.ts"),
      join(root, "src", "main.ts"),
      join(root, "src", "screens", "today.ts"),
      join(root, "src", "screens", "placeholder.ts"),
      join(root, "src", "screens", "settings.ts"),
    ]);
    expect(ui).not.toMatch(/api\.openweathermap/);
    expect(ui).not.toMatch(/\/data\/2\.5\//);
  });
});
