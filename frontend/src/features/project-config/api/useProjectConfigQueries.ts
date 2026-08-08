import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { projectConfigApi } from './projectConfigApi';
import {
  ProjectCreateRequest,
  FeatureFlags,
  ContrastValidationRequest,
} from '../../../types/projectConfig';
import { useToast } from '../../../context/ToastContext';

export function useProjectConfigQuery(projectId: string | undefined) {
  return useQuery({
    queryKey: ['project', projectId],
    queryFn: () => projectConfigApi.getProject(projectId!),
    enabled: !!projectId,
    staleTime: 5 * 60 * 1000,
  });
}

export function usePublicThemeQuery(projectId: string | undefined) {
  return useQuery({
    queryKey: ['public-theme', projectId],
    queryFn: () => projectConfigApi.getPublicTheme(projectId!),
    enabled: !!projectId,
    staleTime: 10 * 60 * 1000,
  });
}

export function useCreateProjectMutation() {
  const queryClient = useQueryClient();
  const { showSuccess, showError } = useToast();

  return useMutation({
    mutationFn: (payload: ProjectCreateRequest) => projectConfigApi.createProject(payload),
    onSuccess: (data) => {
      showSuccess(`Project workspace ${data.projectId} provisioned successfully!`);
      queryClient.invalidateQueries({ queryKey: ['projects'] });
      queryClient.setQueryData(['project', data.projectId], data);
    },
    onError: (err: any) => {
      showError(err.message || 'Failed to provision project workspace.');
    },
  });
}

export function useUpdateProjectMutation() {
  const queryClient = useQueryClient();
  const { showSuccess, showError } = useToast();

  return useMutation({
    mutationFn: ({ projectId, payload }: { projectId: string; payload: ProjectCreateRequest }) =>
      projectConfigApi.updateProject(projectId, payload),
    onSuccess: (data) => {
      showSuccess(`Project workspace ${data.projectId} configuration updated.`);
      queryClient.invalidateQueries({ queryKey: ['project', data.projectId] });
    },
    onError: (err: any) => {
      showError(err.message || 'Failed to update project configuration.');
    },
  });
}

export function useUpdateFeatureFlagsMutation() {
  const queryClient = useQueryClient();
  const { showSuccess, showError } = useToast();

  return useMutation({
    mutationFn: ({ projectId, features }: { projectId: string; features: FeatureFlags }) =>
      projectConfigApi.updateFeatureFlags(projectId, features),
    onSuccess: (data) => {
      showSuccess(`Feature flags updated for project ${data.projectId}`);
      queryClient.invalidateQueries({ queryKey: ['project', data.projectId] });
    },
    onError: (err: any) => {
      showError(err.message || 'Failed to update feature flags.');
    },
  });
}

export function useValidateThemeMutation() {
  const { showError } = useToast();

  return useMutation({
    mutationFn: (payload: ContrastValidationRequest) => projectConfigApi.validateTheme(payload),
    onError: (err: any) => {
      showError(err.message || 'Theme accessibility validation failed.');
    },
  });
}

export function useDeleteProjectMutation() {
  const queryClient = useQueryClient();
  const { showSuccess, showError } = useToast();

  return useMutation({
    mutationFn: (projectId: string) => projectConfigApi.deleteProject(projectId),
    onSuccess: (_, projectId) => {
      showSuccess(`Project ${projectId} soft deleted.`);
      queryClient.invalidateQueries({ queryKey: ['projects'] });
      queryClient.removeQueries({ queryKey: ['project', projectId] });
    },
    onError: (err: any) => {
      showError(err.message || 'Failed to delete project workspace.');
    },
  });
}
