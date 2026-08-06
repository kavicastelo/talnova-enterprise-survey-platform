import { SurveyResponse, SurveySaveDraft } from '../types/survey';

const BASE_URL = '/api/v1/surveys';

export const surveyBuilderApi = {
  async saveDraft(surveyId: string, payload: SurveySaveDraft): Promise<SurveyResponse> {
    const res = await fetch(`${BASE_URL}/${surveyId}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'X-Correlation-ID': `CORR-${Date.now()}`
      },
      body: JSON.stringify(payload)
    });
    const json = await res.json();
    if (!res.ok || !json.success) {
      throw new Error(json.message || 'Failed to save survey draft');
    }
    return json.data;
  },

  async getSurvey(projectId: string, surveyId: string): Promise<SurveyResponse> {
    const res = await fetch(`${BASE_URL}/${surveyId}?projectId=${encodeURIComponent(projectId)}`, {
      method: 'GET',
      headers: {
        'X-Correlation-ID': `CORR-${Date.now()}`
      }
    });
    const json = await res.json();
    if (!res.ok || !json.success) {
      throw new Error(json.message || 'Failed to fetch survey AST');
    }
    return json.data;
  },

  async publishSurvey(projectId: string, surveyId: string): Promise<SurveyResponse> {
    const res = await fetch(`${BASE_URL}/${surveyId}/publish?projectId=${encodeURIComponent(projectId)}`, {
      method: 'POST',
      headers: {
        'X-Correlation-ID': `CORR-${Date.now()}`
      }
    });
    const json = await res.json();
    if (!res.ok || !json.success) {
      throw new Error(json.message || 'Failed to publish survey');
    }
    return json.data;
  },

  async createDraftVersion(projectId: string, surveyId: string): Promise<SurveyResponse> {
    const res = await fetch(`${BASE_URL}/${surveyId}/new-version?projectId=${encodeURIComponent(projectId)}`, {
      method: 'POST',
      headers: {
        'X-Correlation-ID': `CORR-${Date.now()}`
      }
    });
    const json = await res.json();
    if (!res.ok || !json.success) {
      throw new Error(json.message || 'Failed to create new draft version');
    }
    return json.data;
  }
};
