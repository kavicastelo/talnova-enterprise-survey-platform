import { SurveyResponse, SurveySaveDraftRequest } from '../types/survey';
import { surveyApi } from '../features/survey-builder/api/surveyApi';

export const surveyBuilderApi = {
  async saveDraft(surveyId: string, payload: SurveySaveDraftRequest): Promise<SurveyResponse> {
    return surveyApi.saveDraft(surveyId, payload);
  },

  async getSurvey(projectId: string, surveyId: string): Promise<SurveyResponse> {
    return surveyApi.getSurvey(surveyId, projectId);
  },

  async publishSurvey(projectId: string, surveyId: string): Promise<SurveyResponse> {
    return surveyApi.publishSurvey(surveyId, projectId);
  },

  async createDraftVersion(projectId: string, surveyId: string): Promise<SurveyResponse> {
    return surveyApi.createNewVersion(surveyId, projectId);
  },
};
