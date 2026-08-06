import {
  ProjectCreateRequest,
  ProjectResponse,
  PublicTheme,
  FeatureFlags,
  ContrastValidationRequest,
  ContrastValidationResponse
} from '../types/projectConfig';
import { ApiResponse } from '../types/domain';

const BASE_URL = '/api/v1/projects';

export const projectConfigApi = {
  async createProject(payload: ProjectCreateRequest): Promise<ApiResponse<ProjectResponse>> {
    const res = await fetch(BASE_URL, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    });
    return res.json();
  },

  async getProject(projectId: string): Promise<ApiResponse<ProjectResponse>> {
    const res = await fetch(`${BASE_URL}/${projectId}`);
    return res.json();
  },

  async updateProject(projectId: string, payload: ProjectCreateRequest): Promise<ApiResponse<ProjectResponse>> {
    const res = await fetch(`${BASE_URL}/${projectId}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    });
    return res.json();
  },

  async updateFeatureFlags(projectId: string, features: FeatureFlags): Promise<ApiResponse<ProjectResponse>> {
    const res = await fetch(`${BASE_URL}/${projectId}/features`, {
      method: 'PATCH',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(features)
    });
    return res.json();
  },

  async getPublicTheme(projectId: string): Promise<ApiResponse<PublicTheme>> {
    const res = await fetch(`${BASE_URL}/${projectId}/public-theme`);
    return res.json();
  },

  async validateTheme(payload: ContrastValidationRequest): Promise<ApiResponse<ContrastValidationResponse>> {
    const res = await fetch(`${BASE_URL}/validate-theme`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    });
    return res.json();
  }
};
