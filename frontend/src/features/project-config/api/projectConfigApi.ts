import { apiClient } from '../../../services/api/client';
import { API_ENDPOINTS } from '../../../core/api/endpoints';
import {
  ProjectCreateRequest,
  ProjectResponse,
  PublicTheme,
  FeatureFlags,
  ContrastValidationRequest,
  ContrastValidationResponse,
} from '../../../types/projectConfig';

export const projectConfigApi = {
  async createProject(payload: ProjectCreateRequest): Promise<ProjectResponse> {
    const res: any = await apiClient.post(API_ENDPOINTS.PROJECTS, payload);
    return res.data || res;
  },

  async getProject(projectId: string): Promise<ProjectResponse> {
    const res: any = await apiClient.get(API_ENDPOINTS.PROJECT_BY_ID(projectId));
    return res.data || res;
  },

  async updateProject(projectId: string, payload: ProjectCreateRequest): Promise<ProjectResponse> {
    const res: any = await apiClient.put(API_ENDPOINTS.PROJECT_BY_ID(projectId), payload);
    return res.data || res;
  },

  async updateFeatureFlags(projectId: string, features: FeatureFlags): Promise<ProjectResponse> {
    const res: any = await apiClient.patch(`${API_ENDPOINTS.PROJECT_BY_ID(projectId)}/features`, features);
    return res.data || res;
  },

  async getPublicTheme(projectId: string): Promise<PublicTheme> {
    const res: any = await apiClient.get(API_ENDPOINTS.PROJECT_BRANDING(projectId));
    return res.data || res;
  },

  async validateTheme(payload: ContrastValidationRequest): Promise<ContrastValidationResponse> {
    const res: any = await apiClient.post(`${API_ENDPOINTS.PROJECTS}/validate-theme`, payload);
    return res.data || res;
  },

  async deleteProject(projectId: string): Promise<void> {
    await apiClient.delete(API_ENDPOINTS.PROJECT_BY_ID(projectId));
  },
};
