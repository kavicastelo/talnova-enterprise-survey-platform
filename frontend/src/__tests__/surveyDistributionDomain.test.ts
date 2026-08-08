import { describe, it, expect, beforeEach, vi } from 'vitest';
import { distributionApi } from '../features/distribution/api/distributionApi';
import { apiClient } from '../services/api/client';
import { CampaignCreateRequest, TokenBatchGenerationRequest } from '../types/distribution';

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

describe('FEAT-005 Survey Distribution Domain Integration', () => {
  beforeEach(() => {
    localStorage.clear();
    vi.restoreAllMocks();
  });

  it('validates campaign ID regex pattern correctly', () => {
    const cmpPattern = /^CMP-[A-Za-z0-9_-]{3,20}$/;
    expect(cmpPattern.test('CMP-1001')).toBe(true);
    expect(cmpPattern.test('CMP-Q3-PULSE')).toBe(true);
    expect(cmpPattern.test('INVALID CAMPAIGN')).toBe(false);
    expect(cmpPattern.test('C-1')).toBe(false);
  });

  it('createCampaign calls POST /campaigns via gateway client', async () => {
    const mockResponse = {
      id: '66b26d8f8a84a51e3c8b7777',
      projectId: 'PRJ-99201',
      campaignId: 'CMP-1001',
      surveyId: 'SRV-5001',
      surveyVersion: 1,
      title: 'Q3 Employee Engagement Campaign',
      anonymityLevel: 'SEMI_ANONYMOUS' as const,
      status: 'ACTIVE' as const,
      channels: ['EMAIL' as const, 'TEAMS' as const],
      startDate: new Date().toISOString(),
      expirationDate: new Date().toISOString(),
    };

    const spy = vi.spyOn(apiClient, 'post').mockResolvedValue({
      success: true,
      data: mockResponse,
    });

    const payload: CampaignCreateRequest = {
      projectId: 'PRJ-99201',
      campaignId: 'CMP-1001',
      surveyId: 'SRV-5001',
      surveyVersion: 1,
      title: 'Q3 Employee Engagement Campaign',
      anonymityLevel: 'SEMI_ANONYMOUS',
      channels: ['EMAIL', 'TEAMS'],
      startDate: new Date().toISOString(),
      expirationDate: new Date().toISOString(),
    };

    const result = await distributionApi.createCampaign(payload);

    expect(spy).toHaveBeenCalledWith('/campaigns', payload);
    expect(result.campaignId).toBe('CMP-1001');
    expect(result.status).toBe('ACTIVE');
  });

  it('updateCampaignStatus calls PATCH /campaigns/{campaignId}/status via gateway client', async () => {
    const mockResponse = {
      projectId: 'PRJ-99201',
      campaignId: 'CMP-1001',
      status: 'PAUSED' as const,
    };

    const spy = vi.spyOn(apiClient, 'patch').mockResolvedValue({
      success: true,
      data: mockResponse,
    });

    const result = await distributionApi.updateCampaignStatus('CMP-1001', 'PAUSED', 'PRJ-99201');

    expect(spy).toHaveBeenCalledWith('/campaigns/CMP-1001/status', {}, { params: { status: 'PAUSED', projectId: 'PRJ-99201' } });
    expect(result.status).toBe('PAUSED');
  });

  it('generateTokens calls POST /tokens/generate via gateway client', async () => {
    const mockResponse = {
      campaignId: 'CMP-1001',
      generatedCount: 100,
      tokens: ['HMAC-TOKEN-001', 'HMAC-TOKEN-002'],
      kioskPin: '882019',
    };

    const spy = vi.spyOn(apiClient, 'post').mockResolvedValue({
      success: true,
      data: mockResponse,
    });

    const payload: TokenBatchGenerationRequest = {
      projectId: 'PRJ-99201',
      campaignId: 'CMP-1001',
      surveyId: 'SRV-5001',
      anonymityLevel: 'SEMI_ANONYMOUS',
      count: 100,
      generateKioskPin: true,
    };

    const result = await distributionApi.generateTokens(payload);

    expect(spy).toHaveBeenCalledWith('/tokens/generate', payload);
    expect(result.generatedCount).toBe(100);
    expect(result.kioskPin).toBe('882019');
  });

  it('predictOptimalTime calls POST /distribution/ai-optimal-time', async () => {
    const mockResponse = {
      recommendedHourUtc: 9,
      predictedOpenRateImprovementPercent: 18.5,
      rationale: 'Shift analysis indicates highest desktop activity at 9:00 AM UTC.',
    };

    const spy = vi.spyOn(apiClient, 'post').mockResolvedValue({
      success: true,
      data: mockResponse,
    });

    const result = await distributionApi.predictOptimalTime({
      projectId: 'PRJ-99201',
      channel: 'EMAIL',
      targetAudienceCount: 1200,
    });

    expect(spy).toHaveBeenCalledWith('/distribution/ai-optimal-time', {
      projectId: 'PRJ-99201',
      channel: 'EMAIL',
      targetAudienceCount: 1200,
    });
    expect(result.recommendedHourUtc).toBe(9);
    expect(result.predictedOpenRateImprovementPercent).toBe(18.5);
  });
});
