import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { surveyApi } from './surveyApi';
import {
  SurveySaveDraftRequest,
  AIBiasAnalysisRequest,
} from '../../../types/survey';
import { useToast } from '../../../context/ToastContext';

export function useSurveyQuery(surveyId: string | undefined, projectId: string | undefined) {
  return useQuery({
    queryKey: ['survey', projectId, surveyId],
    queryFn: () => surveyApi.getSurvey(surveyId!, projectId),
    enabled: !!surveyId && !!projectId,
    staleTime: 5 * 60 * 1000,
  });
}

export function useQuestionLibraryQuery(category?: string, search?: string) {
  return useQuery({
    queryKey: ['question-library', category, search],
    queryFn: () => surveyApi.searchQuestionLibrary(category, search),
    staleTime: 10 * 60 * 1000,
  });
}

export function useSaveDraftMutation() {
  const queryClient = useQueryClient();
  const { showSuccess, showError } = useToast();

  return useMutation({
    mutationFn: ({ surveyId, payload }: { surveyId: string; payload: SurveySaveDraftRequest }) =>
      surveyApi.saveDraft(surveyId, payload),
    onSuccess: (data) => {
      showSuccess(`Survey draft version v${data.version} saved successfully!`);
      queryClient.setQueryData(['survey', data.projectId, data.surveyId], data);
    },
    onError: (err: any) => {
      showError(err.message || 'Failed to save survey draft.');
    },
  });
}

export function usePublishSurveyMutation() {
  const queryClient = useQueryClient();
  const { showSuccess, showError } = useToast();

  return useMutation({
    mutationFn: ({ surveyId, projectId }: { surveyId: string; projectId?: string }) =>
      surveyApi.publishSurvey(surveyId, projectId),
    onSuccess: (data) => {
      showSuccess(`Survey version v${data.version} published! Structure locked.`);
      queryClient.setQueryData(['survey', data.projectId, data.surveyId], data);
    },
    onError: (err: any) => {
      showError(err.message || 'Failed to publish survey version.');
    },
  });
}

export function useCreateNewVersionMutation() {
  const queryClient = useQueryClient();
  const { showSuccess, showError } = useToast();

  return useMutation({
    mutationFn: ({ surveyId, projectId }: { surveyId: string; projectId?: string }) =>
      surveyApi.createNewVersion(surveyId, projectId),
    onSuccess: (data) => {
      showSuccess(`New draft version v${data.version} created! Editing unlocked.`);
      queryClient.setQueryData(['survey', data.projectId, data.surveyId], data);
    },
    onError: (err: any) => {
      showError(err.message || 'Failed to create new draft version.');
    },
  });
}

export function useAnalyzeBiasMutation() {
  const { showError } = useToast();

  return useMutation({
    mutationFn: (payload: AIBiasAnalysisRequest) => surveyApi.analyzeBias(payload),
    onError: (err: any) => {
      showError(err.message || 'AI Question bias analysis failed.');
    },
  });
}
