import { CampaignCreate, CampaignResponse, CampaignStatus, OptimalDispatchPrediction } from '../types/distribution';

const API_BASE = '/api/v1';

export async function createCampaign(payload: CampaignCreate): Promise<CampaignResponse> {
  const res = await fetch(`${API_BASE}/campaigns`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  });
  const json = await res.json();
  if (!res.ok || !json.success) {
    throw new Error(json.message || 'Failed to create survey campaign');
  }
  return json.data;
}

export async function getCampaign(projectId: string, campaignId: string): Promise<CampaignResponse> {
  const res = await fetch(`${API_BASE}/campaigns/${campaignId}?projectId=${encodeURIComponent(projectId)}`);
  const json = await res.json();
  if (!res.ok || !json.success) {
    throw new Error(json.message || 'Failed to fetch campaign details');
  }
  return json.data;
}

export async function updateCampaignStatus(projectId: string, campaignId: string, status: CampaignStatus): Promise<CampaignResponse> {
  const res = await fetch(`${API_BASE}/campaigns/${campaignId}/status?projectId=${encodeURIComponent(projectId)}&status=${encodeURIComponent(status)}`, {
    method: 'PATCH',
  });
  const json = await res.json();
  if (!res.ok || !json.success) {
    throw new Error(json.message || 'Failed to update campaign status');
  }
  return json.data;
}

export async function predictOptimalDispatchTime(projectId: string, employeeId: string, department: string): Promise<OptimalDispatchPrediction> {
  const res = await fetch(`${API_BASE}/distribution/ai-optimal-time`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ projectId, employeeId, department }),
  });
  const json = await res.json();
  if (!res.ok || !json.success) {
    throw new Error(json.message || 'Failed to predict optimal dispatch time');
  }
  return json.data;
}
