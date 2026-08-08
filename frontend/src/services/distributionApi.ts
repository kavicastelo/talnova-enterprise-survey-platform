import {
  CampaignCreateRequest,
  CampaignResponse,
  CampaignStatus,
  OptimalDispatchPredictionResponse,
} from '../types/distribution';
import { distributionApi } from '../features/distribution/api/distributionApi';

export async function createCampaign(payload: CampaignCreateRequest): Promise<CampaignResponse> {
  return distributionApi.createCampaign(payload);
}

export async function getCampaign(projectId: string, campaignId: string): Promise<CampaignResponse> {
  return distributionApi.getCampaign(campaignId, projectId);
}

export async function updateCampaignStatus(projectId: string, campaignId: string, status: CampaignStatus): Promise<CampaignResponse> {
  return distributionApi.updateCampaignStatus(campaignId, status, projectId);
}

export async function predictOptimalDispatchTime(projectId: string): Promise<OptimalDispatchPredictionResponse> {
  return distributionApi.predictOptimalTime({
    projectId,
    channel: 'EMAIL',
    targetAudienceCount: 1000,
  });
}
