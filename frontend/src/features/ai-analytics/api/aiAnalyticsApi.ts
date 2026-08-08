import { apiClient } from '../../../services/api/client';
import { ApiResponse } from '../../../types/api';
import {
  AiInsightDocument,
  SentimentOverrideRequest,
  ExecutiveSummary,
} from '../../../types/aiAnalytics';

const BASE_URL = '/ai';

export const aiAnalyticsApi = {
  async getCampaignInsights(projectId: string, campaignId: string): Promise<AiInsightDocument[]> {
    const res = await apiClient.get<AiInsightDocument[] | ApiResponse<AiInsightDocument[]>>(
      `${BASE_URL}/insights/projects/${projectId}/campaigns/${campaignId}`
    );
    return Array.isArray(res) ? res : (res as any).data || [];
  },

  async overrideSentimentTag(
    insightId: string,
    payload: SentimentOverrideRequest
  ): Promise<AiInsightDocument> {
    const res = await apiClient.put<AiInsightDocument | ApiResponse<AiInsightDocument>>(
      `${BASE_URL}/insights/${insightId}/override`,
      payload
    );
    return (res as any).data || res;
  },

  async getExecutiveSummary(
    campaignId?: string,
    nodeId: string = 'GLOBAL',
    providerName: string = 'OPENAI'
  ): Promise<ExecutiveSummary> {
    const params: Record<string, any> = { nodeId, providerName };
    if (campaignId) params.campaignId = campaignId;

    const res = await apiClient.get<ExecutiveSummary | ApiResponse<ExecutiveSummary>>(
      `${BASE_URL}/summaries`,
      { params }
    );
    return (res as any).data || res;
  },

  async generateExecutiveSummary(
    nodeScope: string = 'GLOBAL',
    providerName: string = 'OPENAI',
    rawComments?: string[]
  ): Promise<ExecutiveSummary> {
    const params = { nodeScope, providerName };
    const res = await apiClient.post<ExecutiveSummary | ApiResponse<ExecutiveSummary>>(
      `${BASE_URL}/summary`,
      rawComments || [],
      { params }
    );
    return (res as any).data || res;
  },
};
