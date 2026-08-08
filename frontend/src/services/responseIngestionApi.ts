import { IngestionResponse, ResponseSubmissionRequest } from '../types/responseIntake';
import { responseIntakeApi } from '../features/response-intake/api/responseIntakeApi';
import { queueOfflineResponse } from '../utils/offlineQueueSync';

export async function submitResponse(payload: ResponseSubmissionRequest): Promise<IngestionResponse> {
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
    return await responseIntakeApi.submitResponse(payload);
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
