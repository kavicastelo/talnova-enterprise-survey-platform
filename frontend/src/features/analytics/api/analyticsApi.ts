import { apiClient } from '../../../services/api/client';
import { ApiResponse } from '../../../types/api';
import { DashboardMetrics, HeatmapMatrix } from '../../../types/analytics';

const BASE_URL = '/analytics';

export const analyticsApi = {
  async getDashboardMetrics(
    campaignId: string,
    nodeId: string = 'N-ROOT'
  ): Promise<DashboardMetrics> {
    const res = await apiClient.get<ApiResponse<DashboardMetrics>>(`${BASE_URL}/dashboard`, {
      params: { campaignId, nodeId },
    });
    return res.data;
  },

  async getHeatmapMatrix(
    campaignId: string,
    parentNodeId: string = 'N-ROOT'
  ): Promise<HeatmapMatrix> {
    const res = await apiClient.get<ApiResponse<HeatmapMatrix>>(`${BASE_URL}/heatmap`, {
      params: { campaignId, parentNodeId },
    });
    return res.data;
  },
};
