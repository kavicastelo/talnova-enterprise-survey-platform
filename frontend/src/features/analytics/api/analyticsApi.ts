import { apiClient } from '../../../services/api/client';
import { ApiResponse } from '../../../types/api';
import {
  DashboardMetrics,
  HeatmapMatrix,
  AnalyticalSnapshot,
  LongitudinalDelta,
  TrendHistoryItem,
  AnomalyReport,
  KeyDriver,
} from '../../../types/analytics';

const BASE_URL = '/analytics';

export const analyticsApi = {
  async getDashboardMetrics(
    campaignId: string,
    nodeId: string = 'N-ROOT',
    filters?: Record<string, string>
  ): Promise<DashboardMetrics> {
    const params: Record<string, string> = { campaignId, nodeId, ...filters };
    const res = await apiClient.get<ApiResponse<DashboardMetrics>>(`${BASE_URL}/dashboard`, {
      params,
    });
    return res.data;
  },

  async getHeatmapMatrix(
    campaignId: string,
    parentNodeId: string = 'N-ROOT',
    filters?: Record<string, string>
  ): Promise<HeatmapMatrix> {
    const params: Record<string, string> = { campaignId, parentNodeId, ...filters };
    const res = await apiClient.get<ApiResponse<HeatmapMatrix>>(`${BASE_URL}/heatmap`, {
      params,
    });
    return res.data;
  },

  async getAnalyticalSnapshots(campaignId?: string): Promise<AnalyticalSnapshot[]> {
    const res = await apiClient.get<ApiResponse<AnalyticalSnapshot[]>>(`${BASE_URL}/snapshots`, {
      params: campaignId ? { campaignId } : undefined,
    });
    return res.data;
  },

  async getBaselineTrends(
    campaignId: string,
    nodeId: string = 'N-ROOT'
  ): Promise<{ longitudinalDelta: LongitudinalDelta; trendHistory: TrendHistoryItem[] }> {
    const res = await apiClient.get<
      ApiResponse<{ longitudinalDelta: LongitudinalDelta; trendHistory: TrendHistoryItem[] }>
    >(`${BASE_URL}/trends`, {
      params: { campaignId, nodeId },
    });
    return res.data;
  },

  async getAiInsights(
    campaignId: string,
    nodeId: string = 'N-ROOT'
  ): Promise<{ anomalies: AnomalyReport[]; keyDrivers: KeyDriver[] }> {
    const res = await apiClient.get<
      ApiResponse<{ anomalies: AnomalyReport[]; keyDrivers: KeyDriver[] }>
    >(`${BASE_URL}/ai-insights`, {
      params: { campaignId, nodeId },
    });
    return res.data;
  },
};
