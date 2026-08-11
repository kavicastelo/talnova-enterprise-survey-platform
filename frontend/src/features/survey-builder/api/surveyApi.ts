import { apiClient } from '../../../services/api/client';
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
    const res: any = await apiClient.put(`${BASE_URL}/${surveyId}`, payload);
    return res.data || res;
  },

  async getSurvey(surveyId: string, projectId?: string): Promise<SurveyResponse> {
    const params = projectId ? { projectId } : undefined;
    const res: any = await apiClient.get(`${BASE_URL}/${surveyId}`, { params });
    return res.data || res;
  },

  async publishSurvey(surveyId: string, projectId?: string): Promise<SurveyResponse> {
    const params = projectId ? { projectId } : undefined;
    const res: any = await apiClient.post(`${BASE_URL}/${surveyId}/publish`, {}, { params });
    return res.data || res;
  },

  async createNewVersion(surveyId: string, projectId?: string): Promise<SurveyResponse> {
    const params = projectId ? { projectId } : undefined;
    const res: any = await apiClient.post(`${BASE_URL}/${surveyId}/new-version`, {}, { params });
    return res.data || res;
  },

  async analyzeBias(payload: AIBiasAnalysisRequest): Promise<AIBiasAnalysisResponse> {
    const res: any = await apiClient.post(`${BASE_URL}/ai/analyze-bias`, payload);
    return res.data || res;
  },

  async searchQuestionLibrary(category?: string, search?: string): Promise<QuestionLibraryTemplate[]> {
    const params: Record<string, any> = {};
    if (category) params.category = category;
    if (search) params.search = search;

    const res: any = await apiClient.get('/question-library', { params });
    return res.data || res;
  },
};
