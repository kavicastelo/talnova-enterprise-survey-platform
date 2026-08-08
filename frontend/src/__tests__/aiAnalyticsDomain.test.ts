import { describe, it, expect, beforeEach, vi } from 'vitest';
import { aiAnalyticsApi } from '../features/ai-analytics/api/aiAnalyticsApi';
import { apiClient } from '../services/api/client';
import { SentimentOverrideRequest } from '../types/aiAnalytics';

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

describe('FEAT-008 AI Analytics Domain Integration', () => {
  beforeEach(() => {
    localStorage.clear();
    vi.restoreAllMocks();
  });

  it('getCampaignInsights calls GET /ai/insights/projects/{projectId}/campaigns/{campaignId}', async () => {
    const mockInsights = [
      {
        id: 'INSIGHT-001',
        projectId: 'PRJ-99201',
        campaignId: 'CMP-101',
        sanitizedText: 'Great teamwork across departments',
        sentimentScore: 0.88,
        sentimentLabel: 'POSITIVE' as const,
        confidence: 0.95,
        themes: ['Teamwork'],
      },
    ];

    const spy = vi.spyOn(apiClient, 'get').mockResolvedValue(mockInsights);

    const result = await aiAnalyticsApi.getCampaignInsights('PRJ-99201', 'CMP-101');

    expect(spy).toHaveBeenCalledWith('/ai/insights/projects/PRJ-99201/campaigns/CMP-101');
    expect(result.length).toBe(1);
    expect(result[0].sentimentLabel).toBe('POSITIVE');
  });

  it('overrideSentimentTag calls PUT /ai/insights/{insightId}/override', async () => {
    const mockUpdatedInsight = {
      id: 'INSIGHT-001',
      projectId: 'PRJ-99201',
      campaignId: 'CMP-101',
      sentimentLabel: 'POSITIVE' as const,
      humanOverride: {
        overriddenBy: 'HR_ADMIN_01',
        originalLabel: 'NEGATIVE',
        newLabel: 'POSITIVE' as const,
        reason: 'Sarcasm incorrectly tagged by model',
      },
    };

    const spy = vi.spyOn(apiClient, 'put').mockResolvedValue(mockUpdatedInsight);

    const payload: SentimentOverrideRequest = {
      overriddenBy: 'HR_ADMIN_01',
      newLabel: 'POSITIVE',
      reason: 'Sarcasm incorrectly tagged by model',
    };

    const result = await aiAnalyticsApi.overrideSentimentTag('INSIGHT-001', payload);

    expect(spy).toHaveBeenCalledWith('/ai/insights/INSIGHT-001/override', payload);
    expect(result.sentimentLabel).toBe('POSITIVE');
  });

  it('getExecutiveSummary calls GET /ai/summaries via gateway client', async () => {
    const mockSummary = {
      nodeScope: 'GLOBAL',
      summaryTitle: 'Q3 Executive Summary Report',
      topStrengths: ['Leadership trust', 'Team cohesion'],
      topConcerns: ['Workload pressure'],
      recommendations: ['Balancing workshops'],
      generatedAt: new Date().toISOString(),
    };

    const spy = vi.spyOn(apiClient, 'get').mockResolvedValue(mockSummary);

    const result = await aiAnalyticsApi.getExecutiveSummary('CMP-101', 'GLOBAL', 'OPENAI');

    expect(spy).toHaveBeenCalledWith('/ai/summaries', {
      params: { campaignId: 'CMP-101', nodeId: 'GLOBAL', providerName: 'OPENAI' },
    });
    expect(result.summaryTitle).toBe('Q3 Executive Summary Report');
    expect(result.topStrengths?.length).toBe(2);
  });
});
