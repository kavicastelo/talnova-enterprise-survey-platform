import { apiClient } from '../../../services/api/client';
import {
  CreateEmployeeRequest,
  UpdateEmployeeRequest,
  EmployeeResponse,
  BulkImportResult,
  HrisSyncRequest,
} from '../../../types/employee';

const BASE_URL = '/employees';

export const employeeApi = {
  async getEmployees(projectId?: string, nodeId?: string, status?: string): Promise<EmployeeResponse[]> {
    const params: Record<string, any> = {};
    if (projectId) params.projectId = projectId;
    if (nodeId) params.nodeId = nodeId;
    if (status) params.status = status;
    const res: any = await apiClient.get(BASE_URL, { params });
    return res.data || res;
  },

  async createEmployee(payload: CreateEmployeeRequest): Promise<EmployeeResponse> {
    const res: any = await apiClient.post(BASE_URL, payload);
    return res.data || res;
  },

  async getEmployee(employeeId: string, projectId?: string): Promise<EmployeeResponse> {
    const params = projectId ? { projectId } : undefined;
    const res: any = await apiClient.get(`${BASE_URL}/${employeeId}`, { params });
    return res.data || res;
  },

  async updateEmployee(employeeId: string, payload: UpdateEmployeeRequest, projectId?: string): Promise<EmployeeResponse> {
    const params = projectId ? { projectId } : undefined;
    const res: any = await apiClient.put(`${BASE_URL}/${employeeId}`, payload, { params });
    return res.data || res;
  },

  async bulkImportCsv(file: File, projectId?: string, autoTerminateMissing: boolean = false): Promise<BulkImportResult> {
    const formData = new FormData();
    formData.append('file', file);

    const params: Record<string, any> = { autoTerminateMissing };
    if (projectId) params.projectId = projectId;

    const res: any = await apiClient.post(`${BASE_URL}/bulk-import`, formData, {
      params,
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    return res.data || res;
  },

  async syncHrisRoster(payload: HrisSyncRequest): Promise<BulkImportResult> {
    const res: any = await apiClient.post(`${BASE_URL}/hris/sync`, payload);
    return res.data || res;
  },

  async anonymizeEmployee(employeeId: string, projectId?: string): Promise<EmployeeResponse> {
    const params = projectId ? { projectId } : undefined;
    const res: any = await apiClient.post(`${BASE_URL}/${employeeId}/gdpr-anonymize`, {}, { params });
    return res.data || res;
  },
};
