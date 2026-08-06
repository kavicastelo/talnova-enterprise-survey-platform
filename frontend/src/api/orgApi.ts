import {
  ApiResponse,
  CreateNodeRequest,
  HierarchyAnomalyReport,
  MoveNodeRequest,
  OrgNode
} from '../types/organization';

const BASE_URL = '/api/v1/nodes';

export const orgApi = {

  async fetchNode(projectId: string, nodeId: string): Promise<ApiResponse<OrgNode>> {
    const res = await fetch(`${BASE_URL}/${nodeId}?projectId=${encodeURIComponent(projectId)}`);
    return res.json();
  },

  async fetchSubTree(projectId: string, nodeId: string): Promise<ApiResponse<OrgNode[]>> {
    const res = await fetch(`${BASE_URL}/${nodeId}/subtree?projectId=${encodeURIComponent(projectId)}`);
    return res.json();
  },

  async fetchLineage(projectId: string, nodeId: string): Promise<ApiResponse<OrgNode[]>> {
    const res = await fetch(`${BASE_URL}/${nodeId}/lineage?projectId=${encodeURIComponent(projectId)}`);
    return res.json();
  },

  async createNode(payload: CreateNodeRequest): Promise<ApiResponse<OrgNode>> {
    const res = await fetch(BASE_URL, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    });
    return res.json();
  },

  async moveNode(projectId: string, nodeId: string, newParentId?: string | null): Promise<ApiResponse<OrgNode>> {
    const payload: MoveNodeRequest = { newParentId: newParentId || null };
    const res = await fetch(`${BASE_URL}/${nodeId}/move?projectId=${encodeURIComponent(projectId)}`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    });
    return res.json();
  },

  async fetchAnomalies(projectId: string): Promise<ApiResponse<HierarchyAnomalyReport>> {
    const res = await fetch(`${BASE_URL}/anomalies?projectId=${encodeURIComponent(projectId)}`);
    return res.json();
  }
};
