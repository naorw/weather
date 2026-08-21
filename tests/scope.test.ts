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

describe("phase 0 scope", () => {
  it("does not include later-phase weather provider or location features", () => {
    const source = walk(join(root, "src"))
      .concat([join(root, "index.html")])
      .filter((path) => /\.(ts|js|html|css)$/.test(path))
      .map((path) => readFileSync(path, "utf8"))
      .join("\n")
      .toLowerCase();

    expect(source).not.toMatch(/openweather/);
    expect(source).not.toMatch(/api\.openweathermap/);
    expect(source).not.toMatch(/geolocation/);
    expect(source).not.toMatch(/navigator\.geolocation/);
  });
});
