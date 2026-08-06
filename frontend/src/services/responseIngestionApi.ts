import { IngestionResponse, ResponseSubmission } from '../types/response';
import { queueOfflineResponse } from '../utils/offlineQueueSync';

const API_BASE = '/api/v1/responses';

export async function submitResponse(payload: ResponseSubmission): Promise<IngestionResponse> {
  if (typeof navigator !== 'undefined' && !navigator.onLine) {
    await queueOfflineResponse(payload);
    return {
      responseId: 'RSP-OFFLINE-QUEUED',
      status: 'QUEUED_OFFLINE',
      message: 'Response queued in IndexDB while offline. Will sync upon reconnection.',
      timestamp: new Date().toISOString(),
    };
  }

  try {
    const res = await fetch(API_BASE, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'X-Project-ID': payload.projectId,
      },
      body: JSON.stringify(payload),
    });

    const json = await res.json();
    if (!res.ok || !json.success) {
      throw new Error(json.message || 'Failed to submit survey response');
    }

    return json.data;
  } catch (err: any) {
    if (typeof navigator !== 'undefined' && !navigator.onLine) {
      await queueOfflineResponse(payload);
      return {
        responseId: 'RSP-OFFLINE-QUEUED',
        status: 'QUEUED_OFFLINE',
        message: 'Response queued in IndexDB while offline.',
        timestamp: new Date().toISOString(),
      };
    }
    throw err;
  }
}
