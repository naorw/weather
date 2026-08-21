const DB_NAME = "org.radilabs.weather";
const DB_VERSION = 1;

export const APP_DB_NAME = DB_NAME;

export const STORES = {
  places: "places",
  snapshots: "snapshots",
  kv: "kv",
} as const;

export type StoreName = (typeof STORES)[keyof typeof STORES];

function requestDb(): Promise<IDBDatabase> {
  return new Promise((resolve, reject) => {
    const request = indexedDB.open(DB_NAME, DB_VERSION);
    request.onupgradeneeded = () => {
      const db = request.result;
      if (!db.objectStoreNames.contains(STORES.places)) {
        db.createObjectStore(STORES.places, { keyPath: "id" });
      }
      if (!db.objectStoreNames.contains(STORES.snapshots)) {
        db.createObjectStore(STORES.snapshots, { keyPath: "cacheKey" });
      }
      if (!db.objectStoreNames.contains(STORES.kv)) {
        db.createObjectStore(STORES.kv);
      }
    };
    request.onsuccess = () => resolve(request.result);
    request.onerror = () => reject(request.error);
  });
}

function withStore<T>(
  storeName: StoreName,
  mode: IDBTransactionMode,
  run: (store: IDBObjectStore) => IDBRequest<T>,
): Promise<T> {
  return requestDb().then(
    (db) =>
      new Promise<T>((resolve, reject) => {
        const tx = db.transaction(storeName, mode);
        const request = run(tx.objectStore(storeName));
        request.onsuccess = () => resolve(request.result);
        request.onerror = () => reject(request.error);
        tx.oncomplete = () => db.close();
      }),
  );
}

export function putRecord<T>(storeName: StoreName, value: T): Promise<void> {
  return withStore(storeName, "readwrite", (store) => store.put(value)).then(() => undefined);
}

export function getRecord<T>(storeName: StoreName, key: IDBValidKey): Promise<T | undefined> {
  return withStore<T | undefined>(storeName, "readonly", (store) => store.get(key));
}

export function deleteRecord(storeName: StoreName, key: IDBValidKey): Promise<void> {
  return withStore(storeName, "readwrite", (store) => store.delete(key)).then(() => undefined);
}

export function getAllRecords<T>(storeName: StoreName): Promise<T[]> {
  return withStore<T[]>(storeName, "readonly", (store) => store.getAll());
}

export function putKv(key: string, value: unknown): Promise<void> {
  return withStore(STORES.kv, "readwrite", (store) => store.put(value, key)).then(() => undefined);
}

export function getKv<T>(key: string): Promise<T | undefined> {
  return withStore<T | undefined>(STORES.kv, "readonly", (store) => store.get(key));
}

export function clearStore(storeName: StoreName): Promise<void> {
  return withStore(storeName, "readwrite", (store) => store.clear()).then(() => undefined);
}

export async function resetAppStores(): Promise<void> {
  await clearStore(STORES.places);
  await clearStore(STORES.snapshots);
  await clearStore(STORES.kv);
}
