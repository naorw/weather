const DB_NAME = "org.radilabs.weather.phase0";
const DB_VERSION = 1;
const STORE = "kv";

function requestDb(): Promise<IDBDatabase> {
  return new Promise((resolve, reject) => {
    const request = indexedDB.open(DB_NAME, DB_VERSION);
    request.onupgradeneeded = () => {
      const db = request.result;
      if (!db.objectStoreNames.contains(STORE)) {
        db.createObjectStore(STORE);
      }
    };
    request.onsuccess = () => resolve(request.result);
    request.onerror = () => reject(request.error);
  });
}

export async function openProofDb(): Promise<IDBDatabase> {
  return requestDb();
}

function withStore<T>(
  mode: IDBTransactionMode,
  run: (store: IDBObjectStore) => IDBRequest<T>,
): Promise<T> {
  return requestDb().then(
    (db) =>
      new Promise<T>((resolve, reject) => {
        const tx = db.transaction(STORE, mode);
        const request = run(tx.objectStore(STORE));
        request.onsuccess = () => resolve(request.result);
        request.onerror = () => reject(request.error);
        tx.oncomplete = () => db.close();
      }),
  );
}

export function putValue(key: string, value: unknown): Promise<void> {
  return withStore("readwrite", (store) => store.put(value, key)).then(
    () => undefined,
  );
}

export function getValue<T>(key: string): Promise<T | undefined> {
  return withStore<T | undefined>("readonly", (store) => store.get(key));
}

export function deleteValue(key: string): Promise<void> {
  return withStore("readwrite", (store) => store.delete(key)).then(
    () => undefined,
  );
}

export function resetStore(): Promise<void> {
  return withStore("readwrite", (store) => store.clear()).then(() => undefined);
}
