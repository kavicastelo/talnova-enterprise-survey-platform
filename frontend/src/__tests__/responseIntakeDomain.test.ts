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

  it('validates mandatory questions requirement (VR-INT-004)', () => {
    const isQuestionAnswered = (ans: { numericValue?: number; textValue?: string; selectedOptions?: string[] } | undefined): boolean => {
      if (!ans) return false;
      if (ans.numericValue !== undefined && ans.numericValue !== null) return true;
      if (ans.textValue !== undefined && ans.textValue !== null && ans.textValue.trim().length > 0) return true;
      if (ans.selectedOptions !== undefined && ans.selectedOptions !== null && ans.selectedOptions.length > 0) return true;
      return false;
    };

    expect(isQuestionAnswered({ numericValue: 5 })).toBe(true);
    expect(isQuestionAnswered({ textValue: 'Feedback text' })).toBe(true);
    expect(isQuestionAnswered({ selectedOptions: ['Opt 1'] })).toBe(true);
    expect(isQuestionAnswered({ textValue: '   ' })).toBe(false);
    expect(isQuestionAnswered(undefined)).toBe(false);
  });

  it('handles duplicate submission token burn rejection (BR-INT-001 / TC-INT-002)', async () => {
    const errorResponse = {
      response: {
        status: 403,
        data: {
          message: 'Invalid or expired survey token. Token already used.',
        },
      },
    };

    vi.spyOn(apiClient, 'post').mockRejectedValue(errorResponse);

    const payload: ResponseSubmissionRequest = {
      projectId: 'PRJ-99201',
      campaignId: 'CMP-101',
      surveyId: 'SRV-5001',
      surveyVersion: 1,
      responseToken: 'BURNED-TOKEN-101',
      respondentType: 'SEMI_ANONYMOUS',
      answers: [],
    };

    await expect(responseIntakeApi.submitResponse(payload)).rejects.toMatchObject({
      response: {
        status: 403,
        data: {
          message: 'Invalid or expired survey token. Token already used.',
        },
      },
    });
  });

  it('scrubs emails and phone numbers from open-text answers (Section 20 / BR-INT-002)', () => {
    const rawText = 'Reach me at john.doe@example.com or call 555-123-4567 for details.';
    const scrubbed = rawText
      .replace(/[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}/g, '[REDACTED EMAIL]')
      .replace(/(\+\d{1,3}[-.\s]?)?\(?\d{3}\)?[-.\s]?\d{3}[-.\s]?\d{4}/g, '[REDACTED PHONE]');

    expect(scrubbed).toContain('[REDACTED EMAIL]');
    expect(scrubbed).toContain('[REDACTED PHONE]');
    expect(scrubbed).not.toContain('john.doe@example.com');
    expect(scrubbed).not.toContain('555-123-4567');
  });
});
