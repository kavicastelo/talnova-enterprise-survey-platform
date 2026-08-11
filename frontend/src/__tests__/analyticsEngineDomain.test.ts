import { describe, it, expect, beforeEach, vi } from 'vitest';
import { analyticsApi } from '../features/analytics/api/analyticsApi';
import { apiClient } from '../services/api/client';

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

describe('FEAT-007 Analytics Engine Domain Integration', () => {
  beforeEach(() => {
    localStorage.clear();
    vi.restoreAllMocks();
  });

  it('getDashboardMetrics calls GET /analytics/dashboard via gateway client', async () => {
    const mockResponse = {
      campaignId: 'CMP-101',
      nodeId: 'N-ROOT',
      totalResponses: 2890,
      participationRate: 55.6,
      eNPS: 42,
      engagementIndex: 78.4,
      groupScores: [
        { groupId: 'QG-01', groupName: 'Leadership Trust', sampleSize: 1200, score: 84.5, colorIntensity: 'GREEN' as const },
        { groupId: 'QG-03', groupName: 'Career Mobility', sampleSize: 4, score: null, colorIntensity: 'GREY' as const },
      ],
    };

    const spy = vi.spyOn(apiClient, 'get').mockResolvedValue({
      success: true,
      data: mockResponse,
    });

    const result = await analyticsApi.getDashboardMetrics('CMP-101', 'N-ROOT');

    expect(spy).toHaveBeenCalledWith('/analytics/dashboard', {
      params: { campaignId: 'CMP-101', nodeId: 'N-ROOT' },
    });
    expect(result.totalResponses).toBe(2890);
    expect(result.eNPS).toBe(42);
    // Differential privacy check: sampleSize < 5 must have suppressed score (null)
    const suppressedGroup = result.groupScores.find((g) => g.groupId === 'QG-03');
    expect(suppressedGroup?.score).toBeNull();
    expect(suppressedGroup?.colorIntensity).toBe('GREY');
  });

  it('getDashboardMetrics passes demographic filter query parameters to endpoint', async () => {
    const mockResponse = {
      campaignId: 'CMP-1001',
      nodeId: 'N-201',
      totalResponses: 150,
      participationRate: 75.0,
      eNPS: 35.0,
      engagementIndex: 80.0,
      groupScores: [
        { groupId: 'QG-01', groupName: 'Leadership', sampleSize: 150, score: 80.0, colorIntensity: 'GREEN' as const },
      ],
    };

    const spy = vi.spyOn(apiClient, 'get').mockResolvedValue({
      success: true,
      data: mockResponse,
    });

    const filters = { Tenure: '1-3 Years', Gender: 'Female' };
    const result = await analyticsApi.getDashboardMetrics('CMP-1001', 'N-201', filters);

    expect(spy).toHaveBeenCalledWith('/analytics/dashboard', {
      params: {
        campaignId: 'CMP-1001',
        nodeId: 'N-201',
        Tenure: '1-3 Years',
        Gender: 'Female',
      },
    });
    expect(result.eNPS).toBe(35.0);
  });

  it('getHeatmapMatrix calls GET /analytics/heatmap via gateway client', async () => {
    const mockResponse = {
      campaignId: 'CMP-101',
      parentNodeId: 'ROOT',
      rowNodes: ['N-201', 'N-202'],
      columnThemes: ['QG-01', 'QG-02'],
      cells: [
        { nodeId: 'N-201', groupId: 'QG-01', sampleSize: 24, score: 84.5, colorIntensity: 'GREEN' as const },
        { nodeId: 'N-201', groupId: 'QG-02', sampleSize: 3, score: null, colorIntensity: 'GREY' as const },
      ],
    };

    const spy = vi.spyOn(apiClient, 'get').mockResolvedValue({
      success: true,
      data: mockResponse,
    });

    const result = await analyticsApi.getHeatmapMatrix('CMP-101', 'ROOT');

    expect(spy).toHaveBeenCalledWith('/analytics/heatmap', {
      params: { campaignId: 'CMP-101', parentNodeId: 'ROOT' },
    });
    expect(result.cells.length).toBe(2);
    expect(result.cells[1].score).toBeNull();
  });
});
