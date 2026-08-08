import { apiClient } from '../../../services/api/client';
import { ApiResponse } from '../../../types/api';
import {
  ProjectCreateRequest,
  ProjectResponse,
  PublicTheme,
  FeatureFlags,
  ContrastValidationRequest,
  ContrastValidationResponse,
} from '../../../types/projectConfig';

const BASE_URL = '/projects';

export const projectConfigApi = {
  async createProject(payload: ProjectCreateRequest): Promise<ProjectResponse> {
    const res = await apiClient.post<ApiResponse<ProjectResponse>>(BASE_URL, payload);
    return res.data;
  },

  async getProject(projectId: string): Promise<ProjectResponse> {
    const res = await apiClient.get<ApiResponse<ProjectResponse>>(`${BASE_URL}/${projectId}`);
    return res.data;
  },

  async updateProject(projectId: string, payload: ProjectCreateRequest): Promise<ProjectResponse> {
    const res = await apiClient.put<ApiResponse<ProjectResponse>>(`${BASE_URL}/${projectId}`, payload);
    return res.data;
  },

  async updateFeatureFlags(projectId: string, features: FeatureFlags): Promise<ProjectResponse> {
    const res = await apiClient.patch<ApiResponse<ProjectResponse>>(`${BASE_URL}/${projectId}/features`, features);
    return res.data;
  },

  async getPublicTheme(projectId: string): Promise<PublicTheme> {
    const res = await apiClient.get<ApiResponse<PublicTheme>>(`${BASE_URL}/${projectId}/public-theme`);
    return res.data;
  },

  async validateTheme(payload: ContrastValidationRequest): Promise<ContrastValidationResponse> {
    const res = await apiClient.post<ApiResponse<ContrastValidationResponse>>(`${BASE_URL}/validate-theme`, payload);
    return res.data;
  },

  async deleteProject(projectId: string): Promise<void> {
    await apiClient.delete<ApiResponse<void>>(`${BASE_URL}/${projectId}`);
  },
};
