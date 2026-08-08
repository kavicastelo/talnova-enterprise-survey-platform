import { describe, it, expect, beforeEach, vi } from 'vitest';
import { surveyApi } from '../features/survey-builder/api/surveyApi';
import { apiClient } from '../services/api/client';
import { SurveySaveDraftRequest } from '../types/survey';

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

describe('FEAT-004 Survey Builder Domain Integration', () => {
  beforeEach(() => {
    localStorage.clear();
    vi.restoreAllMocks();
  });

  it('validates survey ID regex pattern correctly', () => {
    const srvPattern = /^SRV-[A-Za-z0-9_-]{3,20}$/;
    expect(srvPattern.test('SRV-5001')).toBe(true);
    expect(srvPattern.test('SRV-ENG-2026')).toBe(true);
    expect(srvPattern.test('INVALID SURVEY')).toBe(false);
    expect(srvPattern.test('S-1')).toBe(false);
  });

  it('saveDraft calls PUT /surveys/{surveyId} via gateway client', async () => {
    const mockResponse = {
      id: '66b26d8f8a84a51e3c8b9999',
      projectId: 'PRJ-99201',
      surveyId: 'SRV-5001',
      version: 1,
      title: { 'en-US': '2026 Annual Employee Engagement Survey' },
      status: 'DRAFT' as const,
      pages: [],
    };

    const spy = vi.spyOn(apiClient, 'put').mockResolvedValue({
      success: true,
      data: mockResponse,
    });

    const payload: SurveySaveDraftRequest = {
      projectId: 'PRJ-99201',
      surveyId: 'SRV-5001',
      title: { 'en-US': '2026 Annual Employee Engagement Survey' },
      pages: [],
    };

    const result = await surveyApi.saveDraft('SRV-5001', payload);

    expect(spy).toHaveBeenCalledWith('/surveys/SRV-5001', payload);
    expect(result.surveyId).toBe('SRV-5001');
    expect(result.status).toBe('DRAFT');
  });

  it('publishSurvey calls POST /surveys/{surveyId}/publish via gateway client', async () => {
    const mockResponse = {
      projectId: 'PRJ-99201',
      surveyId: 'SRV-5001',
      version: 1,
      status: 'PUBLISHED' as const,
      publishedAt: new Date().toISOString(),
      pages: [],
    };

    const spy = vi.spyOn(apiClient, 'post').mockResolvedValue({
      success: true,
      data: mockResponse,
    });

    const result = await surveyApi.publishSurvey('SRV-5001', 'PRJ-99201');

    expect(spy).toHaveBeenCalledWith('/surveys/SRV-5001/publish', {}, { params: { projectId: 'PRJ-99201' } });
    expect(result.status).toBe('PUBLISHED');
  });

  it('createNewVersion calls POST /surveys/{surveyId}/new-version via gateway client', async () => {
    const mockResponse = {
      projectId: 'PRJ-99201',
      surveyId: 'SRV-5001',
      version: 2,
      status: 'DRAFT' as const,
      pages: [],
    };

    const spy = vi.spyOn(apiClient, 'post').mockResolvedValue({
      success: true,
      data: mockResponse,
    });

    const result = await surveyApi.createNewVersion('SRV-5001', 'PRJ-99201');

    expect(spy).toHaveBeenCalledWith('/surveys/SRV-5001/new-version', {}, { params: { projectId: 'PRJ-99201' } });
    expect(result.version).toBe(2);
    expect(result.status).toBe('DRAFT');
  });

  it('analyzeBias calls POST /surveys/ai/analyze-bias', async () => {
    const mockResponse = {
      riskScore: 85,
      biasDetected: true,
      biasCategory: 'LEADING_QUESTION',
      message: 'Question prompt contains leading language encouraging positive responses.',
      suggestedPrompts: ['How would you describe your direct manager guidance?'],
    };

    const spy = vi.spyOn(apiClient, 'post').mockResolvedValue({
      success: true,
      data: mockResponse,
    });

    const result = await surveyApi.analyzeBias({
      questionPrompt: 'Don’t you agree your manager is fantastic?',
      locale: 'en-US',
    });

    expect(spy).toHaveBeenCalledWith('/surveys/ai/analyze-bias', {
      questionPrompt: 'Don’t you agree your manager is fantastic?',
      locale: 'en-US',
    });
    expect(result.riskScore).toBe(85);
    expect(result.biasDetected).toBe(true);
  });
});
