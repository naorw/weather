export const ROUTES = ["today", "radar", "cities", "settings"] as const;

export type Route = (typeof ROUTES)[number];

export function parseRoute(hash: string): Route {
  const path = hash.replace(/^#/, "").replace(/^\//, "");
  if ((ROUTES as readonly string[]).includes(path)) {
    return path as Route;
  }
  return "today";
}

export function routeHash(route: Route): string {
  return `#/${route}`;
}
