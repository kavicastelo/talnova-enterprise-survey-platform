import { apiClient } from '../../../services/api/client';
import { ApiResponse } from '../../../types/api';
import {
  ActionPlanResponse,
  ActionPlanCreateRequest,
  StateTransitionRequest,
  ApprovalRequest,
  ExternalSyncInfo,
  ActionTemplate,
  ActionMilestone,
} from '../../../types/actionPlanning';

const BASE_URL = '/actions';

export interface KanbanQueryParams {
  projectId: string;
  nodeId?: string;
  groupId?: string;
  status?: string;
  assigneeId?: string;
}

export const actionPlanningApi = {
  async getKanbanBoard(projectId: string, nodeId?: string, groupId?: string, status?: string, assigneeId?: string): Promise<ActionPlanResponse[]> {
    const params: KanbanQueryParams = { projectId };
    if (nodeId) params.nodeId = nodeId;
    if (groupId) params.groupId = groupId;
    if (status) params.status = status;
    if (assigneeId) params.assigneeId = assigneeId;

    const res = await apiClient.get<ActionPlanResponse[] | ApiResponse<ActionPlanResponse[]>>(
      `${BASE_URL}/kanban`,
      { params }
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

  async transitionState(actionPlanId: string, payload: StateTransitionRequest): Promise<ActionPlanResponse> {
    if (payload.targetStatus === 'APPROVED') {
      return this.approveActionPlan(actionPlanId, {
        actorId: payload.actorId || 'USR-HR-DIRECTOR',
        userRole: payload.userRole || 'HR_MANAGER',
        rationale: payload.comments || 'Approved via Kanban Board',
      });
    } else if (payload.targetStatus === 'REJECTED') {
      return this.rejectActionPlan(actionPlanId, {
        actorId: payload.actorId || 'USR-HR-DIRECTOR',
        userRole: payload.userRole || 'HR_MANAGER',
        rationale: payload.comments || 'Rejected via Kanban Board',
      });
    }

    const res = await apiClient.post<ActionPlanResponse | ApiResponse<ActionPlanResponse>>(
      `${BASE_URL}/${actionPlanId}/transition`,
      payload
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

  async updateMilestones(actionPlanId: string, milestones: ActionMilestone[]): Promise<ActionPlanResponse> {
    const res = await apiClient.put<ActionPlanResponse | ApiResponse<ActionPlanResponse>>(
      `${BASE_URL}/${actionPlanId}/milestones`,
      { milestones }
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

  async syncToPlanner(actionPlanId: string, planId: string = 'PLN-MAIN'): Promise<ExternalSyncInfo> {
    const res = await apiClient.post<ExternalSyncInfo | ApiResponse<ExternalSyncInfo>>(
      `${BASE_URL}/${actionPlanId}/sync-ms-planner`,
      {},
      { params: { planId } }
    );
    return (res as any).data || res;
  },

  async verifyActionPlan(actionPlanId: string, postActionScore?: number): Promise<ActionPlanResponse> {
    const res = await apiClient.post<ActionPlanResponse | ApiResponse<ActionPlanResponse>>(
      `${BASE_URL}/${actionPlanId}/verify`,
      { postActionScore }
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

