import { apiClient } from '../../../services/api/client';
import { ApiResponse } from '../../../types/api';
import {
  SurveySaveDraftRequest,
  SurveyResponse,
  AIBiasAnalysisRequest,
  AIBiasAnalysisResponse,
  QuestionLibraryTemplate,
} from '../../../types/survey';

const BASE_URL = '/surveys';

export const surveyApi = {
  async saveDraft(surveyId: string, payload: SurveySaveDraftRequest): Promise<SurveyResponse> {
    const res = await apiClient.put<ApiResponse<SurveyResponse>>(`${BASE_URL}/${surveyId}`, payload);
    return res.data;
  },

  async getSurvey(surveyId: string, projectId?: string): Promise<SurveyResponse> {
    const params = projectId ? { projectId } : undefined;
    const res = await apiClient.get<ApiResponse<SurveyResponse>>(`${BASE_URL}/${surveyId}`, { params });
    return res.data;
  },

  async publishSurvey(surveyId: string, projectId?: string): Promise<SurveyResponse> {
    const params = projectId ? { projectId } : undefined;
    const res = await apiClient.post<ApiResponse<SurveyResponse>>(`${BASE_URL}/${surveyId}/publish`, {}, { params });
    return res.data;
  },

  async createNewVersion(surveyId: string, projectId?: string): Promise<SurveyResponse> {
    const params = projectId ? { projectId } : undefined;
    const res = await apiClient.post<ApiResponse<SurveyResponse>>(`${BASE_URL}/${surveyId}/new-version`, {}, { params });
    return res.data;
  },

  async analyzeBias(payload: AIBiasAnalysisRequest): Promise<AIBiasAnalysisResponse> {
    const res = await apiClient.post<ApiResponse<AIBiasAnalysisResponse>>(`${BASE_URL}/ai/analyze-bias`, payload);
    return res.data;
  },

  async searchQuestionLibrary(category?: string, search?: string): Promise<QuestionLibraryTemplate[]> {
    const params: Record<string, any> = {};
    if (category) params.category = category;
    if (search) params.search = search;

    const res = await apiClient.get<ApiResponse<QuestionLibraryTemplate[]>>('/question-library', { params });
    return res.data;
  },
};
