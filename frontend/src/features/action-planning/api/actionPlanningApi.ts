import { apiClient } from '../../../services/api/client';
import { ApiResponse } from '../../../types/api';
import {
  ActionPlanResponse,
  ActionPlanCreateRequest,
  ApprovalRequest,
  ExternalSyncInfo,
  ActionTemplate,
} from '../../../types/actionPlanning';

const BASE_URL = '/actions';

export const actionPlanningApi = {
  async getKanbanBoard(projectId: string, nodeId?: string): Promise<ActionPlanResponse[]> {
    const res = await apiClient.get<ActionPlanResponse[] | ApiResponse<ActionPlanResponse[]>>(
      `${BASE_URL}/kanban`,
      { params: { projectId, nodeId } }
    );
    return Array.isArray(res) ? res : (res as any).data || [];
  },

  async createActionPlan(payload: ActionPlanCreateRequest): Promise<ActionPlanResponse> {
    const res = await apiClient.post<ActionPlanResponse | ApiResponse<ActionPlanResponse>>(
      BASE_URL,
      payload
    );
    return (res as any).data || res;
  },

  async getActionPlan(actionPlanId: string): Promise<ActionPlanResponse> {
    const res = await apiClient.get<ActionPlanResponse | ApiResponse<ActionPlanResponse>>(
      `${BASE_URL}/${actionPlanId}`
    );
    return (res as any).data || res;
  },

  async approveActionPlan(actionPlanId: string, payload: ApprovalRequest): Promise<ActionPlanResponse> {
    const res = await apiClient.post<ActionPlanResponse | ApiResponse<ActionPlanResponse>>(
      `${BASE_URL}/${actionPlanId}/approve`,
      payload
    );
    return (res as any).data || res;
  },

  async rejectActionPlan(actionPlanId: string, payload: ApprovalRequest): Promise<ActionPlanResponse> {
    const res = await apiClient.post<ActionPlanResponse | ApiResponse<ActionPlanResponse>>(
      `${BASE_URL}/${actionPlanId}/reject`,
      payload
    );
    return (res as any).data || res;
  },

  async syncToJira(actionPlanId: string, projectKey: string = 'ENG'): Promise<ExternalSyncInfo> {
    const res = await apiClient.post<ExternalSyncInfo | ApiResponse<ExternalSyncInfo>>(
      `${BASE_URL}/${actionPlanId}/sync-jira`,
      {},
      { params: { projectKey } }
    );
    return (res as any).data || res;
  },

  async getTemplateRecommendations(groupId: string, keyword?: string): Promise<ActionTemplate[]> {
    const res = await apiClient.get<ActionTemplate[] | ApiResponse<ActionTemplate[]>>(
      `${BASE_URL}/templates/recommendations`,
      { params: { groupId, keyword } }
    );
    return Array.isArray(res) ? res : (res as any).data || [];
  },
};
