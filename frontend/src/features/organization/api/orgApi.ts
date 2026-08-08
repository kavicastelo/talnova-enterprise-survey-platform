import { apiClient } from '../../../services/api/client';
import { ApiResponse } from '../../../types/api';
import {
  CreateNodeRequest,
  MoveNodeRequest,
  OrgNodeResponse,
  HierarchyAnomalyReport,
} from '../../../types/organization';

const BASE_URL = '/nodes';

export const orgApi = {
  async createNode(payload: CreateNodeRequest): Promise<OrgNodeResponse> {
    const res = await apiClient.post<ApiResponse<OrgNodeResponse>>(BASE_URL, payload);
    return res.data;
  },

  async getNode(nodeId: string, projectId?: string): Promise<OrgNodeResponse> {
    const params = projectId ? { projectId } : undefined;
    const res = await apiClient.get<ApiResponse<OrgNodeResponse>>(`${BASE_URL}/${nodeId}`, { params });
    return res.data;
  },

  async moveNode(nodeId: string, newParentId?: string, projectId?: string): Promise<OrgNodeResponse> {
    const params = projectId ? { projectId } : undefined;
    const payload: MoveNodeRequest = { newParentId };
    const res = await apiClient.post<ApiResponse<OrgNodeResponse>>(`${BASE_URL}/${nodeId}/move`, payload, { params });
    return res.data;
  },

  async getSubtree(nodeId: string, projectId?: string): Promise<OrgNodeResponse[]> {
    const params = projectId ? { projectId } : undefined;
    const res = await apiClient.get<ApiResponse<OrgNodeResponse[]>>(`${BASE_URL}/${nodeId}/subtree`, { params });
    return res.data;
  },

  async getLineage(nodeId: string, projectId?: string): Promise<OrgNodeResponse[]> {
    const params = projectId ? { projectId } : undefined;
    const res = await apiClient.get<ApiResponse<OrgNodeResponse[]>>(`${BASE_URL}/${nodeId}/lineage`, { params });
    return res.data;
  },

  async inspectAnomalies(projectId?: string): Promise<HierarchyAnomalyReport> {
    const params = projectId ? { projectId } : undefined;
    const res = await apiClient.get<ApiResponse<HierarchyAnomalyReport>>(`${BASE_URL}/anomalies`, { params });
    return res.data;
  },
};
