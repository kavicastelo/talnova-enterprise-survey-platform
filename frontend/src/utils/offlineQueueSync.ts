import { ResponseSubmission } from '../types/response';
import { submitResponse } from '../services/responseIngestionApi';

const DB_NAME = 'tesp_offline_db';
const STORE_NAME = 'offline_responses';

function openDatabase(): Promise<IDBDatabase> {
  return new Promise((resolve, reject) => {
    const request = indexedDB.open(DB_NAME, 1);

    request.onupgradeneeded = (event) => {
      const db = (event.target as IDBOpenDBRequest).result;
      if (!db.objectStoreNames.contains(STORE_NAME)) {
        db.createObjectStore(STORE_NAME, { keyPath: 'id', autoIncrement: true });
      }
    };

    request.onsuccess = () => resolve(request.result);
    request.onerror = () => reject(request.error);
  });
}

export async function queueOfflineResponse(payload: ResponseSubmission): Promise<void> {
  const db = await openDatabase();
  return new Promise((resolve, reject) => {
    const tx = db.transaction(STORE_NAME, 'readwrite');
    const store = tx.objectStore(STORE_NAME);
    const request = store.add({
      payload,
      createdAt: new Date().toISOString(),
    });

    request.onsuccess = () => resolve();
    request.onerror = () => reject(request.error);
  });
}

export async function getQueuedOfflineResponseCount(): Promise<number> {
  const db = await openDatabase();
  return new Promise((resolve, reject) => {
    const tx = db.transaction(STORE_NAME, 'readonly');
    const store = tx.objectStore(STORE_NAME);
    const countRequest = store.count();

    countRequest.onsuccess = () => resolve(countRequest.result);
    countRequest.onerror = () => reject(countRequest.error);
  });
}

export async function flushOfflineResponseQueue(): Promise<number> {
  if (!navigator.onLine) {
    return 0;
  }

  const db = await openDatabase();
  const items: Array<{ id: number; payload: ResponseSubmission }> = await new Promise((resolve, reject) => {
    const tx = db.transaction(STORE_NAME, 'readonly');
    const store = tx.objectStore(STORE_NAME);
    const getAllReq = store.getAll();

    getAllReq.onsuccess = () => resolve(getAllReq.result);
    getAllReq.onerror = () => reject(getAllReq.error);
  });

  if (items.length === 0) {
    return 0;
  }

  let flushedCount = 0;
  for (const item of items) {
    try {
      await submitResponse(item.payload);
      const tx = db.transaction(STORE_NAME, 'readwrite');
      tx.objectStore(STORE_NAME).delete(item.id);
      flushedCount++;
    } catch {
      // Keep item in IndexDB if submission fails again
    }
  }

  return flushedCount;
}

// Automatically register window online listener for background sync
if (typeof window !== 'undefined') {
  window.addEventListener('online', () => {
    flushOfflineResponseQueue().catch(() => {});
  });
}
