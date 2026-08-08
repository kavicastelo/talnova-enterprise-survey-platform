import { describe, it, expect, beforeEach, vi } from 'vitest';
import { responseIntakeApi } from '../features/response-intake/api/responseIntakeApi';
import { apiClient } from '../services/api/client';
import { ResponseSubmissionRequest } from '../types/responseIntake';

const mockLocalStorage = (() => {
  let store: Record<string, string> = {};
  return {
    getItem: (key: string) => store[key] || null,
    setItem: (key: string, value: string) => {
      store[key] = value.toString();
    },
    removeItem: (key: string) => {
      delete store[key];
    },
    clear: () => {
      store = {};
    },
  };
})();

Object.defineProperty(global, 'localStorage', {
  value: mockLocalStorage,
  writable: true,
});

describe('FEAT-006 Response Intake Domain Integration', () => {
  beforeEach(() => {
    localStorage.clear();
    vi.restoreAllMocks();
  });

  it('submitResponse calls POST /responses via gateway client', async () => {
    const mockIngestionResponse = {
      responseId: 'RSP-99018273',
      status: 'ACCEPTED',
      message: 'Survey response ingested successfully',
      timestamp: new Date().toISOString(),
    };

    const spy = vi.spyOn(apiClient, 'post').mockResolvedValue({
      success: true,
      data: mockIngestionResponse,
    });

    const payload: ResponseSubmissionRequest = {
      projectId: 'PRJ-99201',
      campaignId: 'CMP-101',
      surveyId: 'SRV-5001',
      surveyVersion: 1,
      respondentType: 'SEMI_ANONYMOUS',
      responseToken: 'TOKEN-HMAC-123',
      nodeId: 'N-201',
      answers: [
        {
          questionId: 'Q-101',
          questionType: 'LIKERT',
          numericValue: 5,
        },
        {
          questionId: 'Q-201',
          questionType: 'SHORT_TEXT',
          textValue: 'Great managerial direction provided.',
        },
      ],
    };

    const result = await responseIntakeApi.submitResponse(payload);

    expect(spy).toHaveBeenCalledWith('/responses', payload);
    expect(result.responseId).toBe('RSP-99018273');
    expect(result.status).toBe('ACCEPTED');
  });

  it('handles multi-choice answer submission payload', async () => {
    const mockIngestionResponse = {
      responseId: 'RSP-99018274',
      status: 'ACCEPTED',
      message: 'Survey response ingested successfully',
      timestamp: new Date().toISOString(),
    };

    const spy = vi.spyOn(apiClient, 'post').mockResolvedValue({
      success: true,
      data: mockIngestionResponse,
    });

    const payload: ResponseSubmissionRequest = {
      projectId: 'PRJ-99201',
      campaignId: 'CMP-101',
      surveyId: 'SRV-5001',
      surveyVersion: 1,
      respondentType: 'KIOSK',
      answers: [
        {
          questionId: 'Q-301',
          questionType: 'MULTIPLE_CHOICE',
          selectedOptions: ['Option A', 'Option C'],
        },
      ],
    };

    const result = await responseIntakeApi.submitResponse(payload);

    expect(spy).toHaveBeenCalledWith('/responses', payload);
    expect(result.status).toBe('ACCEPTED');
  });
});
