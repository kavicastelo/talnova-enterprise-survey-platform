import { apiClient } from '../../../services/api/client';
import { ApiResponse } from '../../../types/api';
import {
  ResponseSubmissionRequest,
  IngestionResponse,
} from '../../../types/responseIntake';

export const responseIntakeApi = {
  async submitResponse(payload: ResponseSubmissionRequest): Promise<IngestionResponse> {
    const res = await apiClient.post<ApiResponse<IngestionResponse>>('/responses', payload);
    return res.data;
  },
};
