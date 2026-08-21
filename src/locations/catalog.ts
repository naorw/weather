import { deleteRecord, getAllRecords, getKv, getRecord, putKv, putRecord, STORES } from "../persist";
import { DEFAULT_PLACE, parsePlace, type Place } from "./model";

const ACTIVE_KEY = "active";
const ORDER_KEY = "order";

export type LocationCatalog = {
  listSaved: () => Promise<Place[]>;
  save: (place: Place) => Promise<Place>;
  remove: (id: string) => Promise<Place>;
  getActive: () => Promise<Place>;
  setActive: (place: Place) => Promise<void>;
};

function asSaved(place: Place): Place {
  return { ...place, source: "saved" };
}

async function readOrder(): Promise<string[]> {
  const raw = await getKv<unknown>(ORDER_KEY);
  if (!Array.isArray(raw)) return [];
  return raw.filter((id): id is string => typeof id === "string" && id.length > 0);
}

async function writeOrder(ids: string[]): Promise<void> {
  await putKv(ORDER_KEY, ids);
}

export function createLocationCatalog(): LocationCatalog {
  return {
    async listSaved() {
      const raw = await getAllRecords<unknown>(STORES.places);
      const parsed: Place[] = [];
      const seen = new Set<string>();
      for (const row of raw) {
        const place = parsePlace(row);
        if (!place) {
          const id =
            row && typeof row === "object" && "id" in row && typeof (row as { id: unknown }).id === "string"
              ? (row as { id: string }).id
              : undefined;
          if (id) await deleteRecord(STORES.places, id);
          continue;
        }
        if (seen.has(place.cacheKey)) {
          if (place.id !== place.cacheKey || seen.has(place.id)) {
            await deleteRecord(STORES.places, place.id);
          }
          continue;
        }
        seen.add(place.cacheKey);
        parsed.push(place);
      }

      const order = await readOrder();
      const byId = new Map(parsed.map((place) => [place.id, place]));
      const ordered: Place[] = [];
      for (const id of order) {
        const place = byId.get(id);
        if (place) {
          ordered.push(place);
          byId.delete(id);
        }
      }
      for (const place of parsed) {
        if (byId.has(place.id)) ordered.push(place);
      }

      const nextOrder = ordered.map((place) => place.id);
      if (nextOrder.join("\0") !== order.join("\0")) await writeOrder(nextOrder);
      return ordered;
    },

    async save(place) {
      const incoming = parsePlace(place);
      if (!incoming) return DEFAULT_PLACE;
      const saved = asSaved(incoming);
      const existing = await getRecord<unknown>(STORES.places, saved.id);
      const parsedExisting = parsePlace(existing);
      if (parsedExisting && parsedExisting.cacheKey === saved.cacheKey) {
        return parsedExisting;
      }
      const duplicates = (await getAllRecords<unknown>(STORES.places))
        .map(parsePlace)
        .filter((row): row is Place => Boolean(row && row.cacheKey === saved.cacheKey));
      if (duplicates[0]) return duplicates[0];

      await putRecord(STORES.places, saved);
      const order = await readOrder();
      if (!order.includes(saved.id)) {
        order.push(saved.id);
        await writeOrder(order);
      }
      return saved;
    },

    async remove(id) {
      await deleteRecord(STORES.places, id);
      const order = (await readOrder()).filter((item) => item !== id);
      await writeOrder(order);
      const active = await this.getActive();
      if (active.id !== id && active.cacheKey !== id) return active;
      const remaining = await this.listSaved();
      const next = remaining[0] ?? DEFAULT_PLACE;
      await this.setActive(next);
      return next;
    },

    async getActive() {
      const parsed = parsePlace(await getKv(ACTIVE_KEY));
      if (parsed) return parsed;
      await putKv(ACTIVE_KEY, DEFAULT_PLACE);
      return DEFAULT_PLACE;
    },

    async setActive(place) {
      const parsed = parsePlace(place) ?? DEFAULT_PLACE;
      await putKv(ACTIVE_KEY, parsed);
    },
  };
}

let shared: LocationCatalog | undefined;

export function locationCatalog(): LocationCatalog {
  shared ??= createLocationCatalog();
  return shared;
}

export function resetLocationCatalogForTests(): void {
  shared = undefined;
}
