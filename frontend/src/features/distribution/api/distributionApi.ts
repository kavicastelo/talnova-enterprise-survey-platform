import { apiClient } from '../../../services/api/client';
import { ApiResponse } from '../../../types/api';
import {
  CampaignCreateRequest,
  CampaignResponse,
  CampaignStatus,
  TokenBatchGenerationRequest,
  TokenBatchGenerationResponse,
  OptimalDispatchPredictionRequest,
  OptimalDispatchPredictionResponse,
} from '../../../types/distribution';

export const distributionApi = {
  async createCampaign(payload: CampaignCreateRequest): Promise<CampaignResponse> {
    const res = await apiClient.post<ApiResponse<CampaignResponse>>('/campaigns', payload);
    return res.data;
  },

  async getCampaign(campaignId: string, projectId?: string): Promise<CampaignResponse> {
    const params = projectId ? { projectId } : undefined;
    const res = await apiClient.get<ApiResponse<CampaignResponse>>(`/campaigns/${campaignId}`, { params });
    return res.data;
  },

  async updateCampaignStatus(
    campaignId: string,
    status: CampaignStatus,
    projectId?: string
  ): Promise<CampaignResponse> {
    const params: Record<string, any> = { status };
    if (projectId) params.projectId = projectId;

    const res = await apiClient.patch<ApiResponse<CampaignResponse>>(`/campaigns/${campaignId}/status`, {}, { params });
    return res.data;
  },

  async generateTokens(payload: TokenBatchGenerationRequest): Promise<TokenBatchGenerationResponse> {
    const res = await apiClient.post<ApiResponse<TokenBatchGenerationResponse>>('/tokens/generate', payload);
    return res.data;
  },

  async burnToken(token: string): Promise<any> {
    const res = await apiClient.post<ApiResponse<any>>('/tokens/burn', {}, { params: { token } });
    return res.data;
  },

  async predictOptimalTime(
    payload: OptimalDispatchPredictionRequest
  ): Promise<OptimalDispatchPredictionResponse> {
    const res = await apiClient.post<ApiResponse<OptimalDispatchPredictionResponse>>(
      '/distribution/ai-optimal-time',
      payload
    );
    return res.data;
  },

  async triggerReminders(campaignId: string, projectId?: string): Promise<{ remindedCount: number; status: string }> {
    const params = projectId ? { projectId } : undefined;
    const res = await apiClient.post<ApiResponse<{ remindedCount: number; status: string }>>(
      `/campaigns/${campaignId}/remind`,
      {},
      { params }
    );
    return res.data;
  },
};
