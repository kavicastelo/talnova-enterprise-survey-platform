import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { actionPlanningApi } from './actionPlanningApi';
import { ActionPlanCreateRequest, ApprovalRequest } from '../../../types/actionPlanning';
import { useToast } from '../../../context/ToastContext';

export function useKanbanActionBoardQuery(projectId: string | undefined, nodeId?: string) {
  return useQuery({
    queryKey: ['action-kanban', projectId, nodeId],
    queryFn: () => actionPlanningApi.getKanbanBoard(projectId!, nodeId),
    enabled: !!projectId,
    staleTime: 2 * 60 * 1000,
  });
}

export function useCreateActionPlanMutation() {
  const queryClient = useQueryClient();
  const { showSuccess, showError } = useToast();

  return useMutation({
    mutationFn: (payload: ActionPlanCreateRequest) => actionPlanningApi.createActionPlan(payload),
    onSuccess: (data) => {
      showSuccess(`Action plan created successfully (${data.actionPlanId})`);
      queryClient.invalidateQueries({ queryKey: ['action-kanban'] });
    },
    onError: (err: any) => {
      showError(err.message || 'Failed to create action plan.');
    },
  });
}

export function useApproveActionPlanMutation() {
  const queryClient = useQueryClient();
  const { showSuccess, showError } = useToast();

  return useMutation({
    mutationFn: ({ actionPlanId, payload }: { actionPlanId: string; payload: ApprovalRequest }) =>
      actionPlanningApi.approveActionPlan(actionPlanId, payload),
    onSuccess: (data) => {
      showSuccess(`Action plan ${data.actionPlanId} approved!`);
      queryClient.invalidateQueries({ queryKey: ['action-kanban'] });
    },
    onError: (err: any) => {
      showError(err.message || 'Failed to approve action plan.');
    },
  });
}

export function useSyncJiraMutation() {
  const queryClient = useQueryClient();
  const { showSuccess, showError } = useToast();

  return useMutation({
    mutationFn: ({ actionPlanId, projectKey }: { actionPlanId: string; projectKey?: string }) =>
      actionPlanningApi.syncToJira(actionPlanId, projectKey),
    onSuccess: (data) => {
      showSuccess(`Synced to Jira issue ${data.externalId}!`);
      queryClient.invalidateQueries({ queryKey: ['action-kanban'] });
    },
    onError: (err: any) => {
      showError(err.message || 'Jira sync failed.');
    },
  });
}
