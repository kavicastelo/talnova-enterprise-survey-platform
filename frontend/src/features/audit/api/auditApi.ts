import { apiClient } from '../../../services/api/client';
import { ApiResponse } from '../../../types/api';
import { AuditLogRequest, AuditLogResponse } from '../../../types/audit';

const BASE_URL = '/audit';

export const auditApi = {
  async recordAuditLog(payload: AuditLogRequest): Promise<AuditLogResponse> {
    const res = await apiClient.post<AuditLogResponse | ApiResponse<AuditLogResponse>>(
      `${BASE_URL}/logs`,
      payload
    );
    return (res as any).data || res;
  },

  async getAuditLogs(
    projectId: string,
    actorId?: string,
    action?: string,
    page: number = 0,
    size: number = 20
  ): Promise<AuditLogResponse[]> {
    const res = await apiClient.get<AuditLogResponse[] | ApiResponse<AuditLogResponse[]>>(
      `${BASE_URL}/logs`,
      { params: { projectId, actorId, action, page, size } }
    );
    return Array.isArray(res) ? res : (res as any).data || [];
  },

  async getAuditLogById(auditId: string): Promise<AuditLogResponse> {
    const res = await apiClient.get<AuditLogResponse | ApiResponse<AuditLogResponse>>(
      `${BASE_URL}/logs/${auditId}`
    );
    return (res as any).data || res;
  },
};
