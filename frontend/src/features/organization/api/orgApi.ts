import { apiClient } from '../../../services/api/client';
import { API_ENDPOINTS } from '../../../core/api/endpoints';
import {
  CreateNodeRequest,
  MoveNodeRequest,
  OrgNodeResponse,
  HierarchyAnomalyReport,
} from '../../../types/organization';

export const orgApi = {
  async createNode(payload: CreateNodeRequest): Promise<OrgNodeResponse> {
    const res: any = await apiClient.post(API_ENDPOINTS.NODES, payload);
    return res.data || res;
  },

  async getNode(nodeId: string, projectId?: string): Promise<OrgNodeResponse> {
    const params = projectId ? { projectId } : undefined;
    const res: any = await apiClient.get(API_ENDPOINTS.NODE_BY_ID(nodeId), { params });
    return res.data || res;
  },

  async moveNode(nodeId: string, newParentId?: string, projectId?: string): Promise<OrgNodeResponse> {
    const params = projectId ? { projectId } : undefined;
    const payload: MoveNodeRequest = { newParentId };
    const res: any = await apiClient.post(API_ENDPOINTS.NODE_MOVE(nodeId), payload, { params });
    return res.data || res;
  },

  async getSubtree(nodeId: string, projectId?: string): Promise<OrgNodeResponse[]> {
    const params = projectId ? { projectId } : undefined;
    const res: any = await apiClient.get(API_ENDPOINTS.NODE_SUBTREE(nodeId), { params });
    return res.data || res;
  },

  async getLineage(nodeId: string, projectId?: string): Promise<OrgNodeResponse[]> {
    const params = projectId ? { projectId } : undefined;
    const res: any = await apiClient.get(API_ENDPOINTS.NODE_LINEAGE(nodeId), { params });
    return res.data || res;
  },

  async inspectAnomalies(projectId?: string): Promise<HierarchyAnomalyReport> {
    const params = projectId ? { projectId } : undefined;
    const res: any = await apiClient.get(API_ENDPOINTS.NODE_ANOMALIES, { params });
    return res.data || res;
  },

  async deleteNode(nodeId: string, projectId?: string): Promise<void> {
    const params = projectId ? { projectId } : undefined;
    await apiClient.delete(API_ENDPOINTS.NODE_BY_ID(nodeId), { params });
  },
};
