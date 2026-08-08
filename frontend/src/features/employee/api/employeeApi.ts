import { apiClient } from '../../../services/api/client';
import { ApiResponse } from '../../../types/api';
import {
  CreateEmployeeRequest,
  UpdateEmployeeRequest,
  EmployeeResponse,
  BulkImportResult,
  HrisSyncRequest,
} from '../../../types/employee';

const BASE_URL = '/employees';

export const employeeApi = {
  async createEmployee(payload: CreateEmployeeRequest): Promise<EmployeeResponse> {
    const res = await apiClient.post<ApiResponse<EmployeeResponse>>(BASE_URL, payload);
    return res.data;
  },

  async getEmployee(employeeId: string, projectId?: string): Promise<EmployeeResponse> {
    const params = projectId ? { projectId } : undefined;
    const res = await apiClient.get<ApiResponse<EmployeeResponse>>(`${BASE_URL}/${employeeId}`, { params });
    return res.data;
  },

  async updateEmployee(employeeId: string, payload: UpdateEmployeeRequest, projectId?: string): Promise<EmployeeResponse> {
    const params = projectId ? { projectId } : undefined;
    const res = await apiClient.put<ApiResponse<EmployeeResponse>>(`${BASE_URL}/${employeeId}`, payload, { params });
    return res.data;
  },

  async bulkImportCsv(file: File, projectId?: string, autoTerminateMissing: boolean = false): Promise<BulkImportResult> {
    const formData = new FormData();
    formData.append('file', file);

    const params: Record<string, any> = { autoTerminateMissing };
    if (projectId) params.projectId = projectId;

    const res = await apiClient.post<ApiResponse<BulkImportResult>>(`${BASE_URL}/bulk-import`, formData, {
      params,
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    return res.data;
  },

  async syncHrisRoster(payload: HrisSyncRequest): Promise<BulkImportResult> {
    const res = await apiClient.post<ApiResponse<BulkImportResult>>(`${BASE_URL}/hris/sync`, payload);
    return res.data;
  },

  async anonymizeEmployee(employeeId: string, projectId?: string): Promise<EmployeeResponse> {
    const params = projectId ? { projectId } : undefined;
    const res = await apiClient.post<ApiResponse<EmployeeResponse>>(`${BASE_URL}/${employeeId}/gdpr-anonymize`, {}, { params });
    return res.data;
  },
};
