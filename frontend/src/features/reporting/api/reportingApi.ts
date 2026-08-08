import { apiClient } from '../../../services/api/client';
import { ApiResponse } from '../../../types/api';
import { ReportRequest, ReportJobResponse } from '../../../types/reporting';

const BASE_URL = '/reports';

export const reportingApi = {
  async generateReport(payload: ReportRequest): Promise<ReportJobResponse> {
    const res = await apiClient.post<ReportJobResponse | ApiResponse<ReportJobResponse>>(
      `${BASE_URL}/generate`,
      payload
    );
    return (res as any).data || res;
  },

  async getJobStatus(jobId: string, projectId?: string): Promise<ReportJobResponse> {
    const endpoint = projectId ? `${BASE_URL}/jobs/${projectId}/${jobId}` : `${BASE_URL}/jobs/${jobId}`;
    const res = await apiClient.get<ReportJobResponse | ApiResponse<ReportJobResponse>>(endpoint);
    return (res as any).data || res;
  },
};
