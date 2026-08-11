import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { actionPlanningApi } from './actionPlanningApi';
import { ActionPlanCreateRequest, ApprovalRequest, StateTransitionRequest } from '../../../types/actionPlanning';
import { useToast } from '../../../context/ToastContext';

export function useKanbanActionBoardQuery(
  projectId: string | undefined,
  nodeId?: string,
  groupId?: string,
  status?: string,
  assigneeId?: string
) {
  return useQuery({
    queryKey: ['action-kanban', projectId, nodeId, groupId, status, assigneeId],
    queryFn: () => actionPlanningApi.getKanbanBoard(projectId!, nodeId, groupId, status, assigneeId),
    enabled: !!projectId,
    staleTime: 60 * 1000,
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

export function useTransitionActionPlanMutation() {
  const queryClient = useQueryClient();
  const { showSuccess, showError } = useToast();

  return useMutation({
    mutationFn: ({ actionPlanId, payload }: { actionPlanId: string; payload: StateTransitionRequest }) =>
      actionPlanningApi.transitionState(actionPlanId, payload),
    onSuccess: (data) => {
      showSuccess(`Action plan ${data.actionPlanId} transitioned to ${data.status}`);
      queryClient.invalidateQueries({ queryKey: ['action-kanban'] });
    },
    onError: (err: any) => {
      showError(err.message || 'State transition failed.');
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

export function useRejectActionPlanMutation() {
  const queryClient = useQueryClient();
  const { showSuccess, showError } = useToast();

  return useMutation({
    mutationFn: ({ actionPlanId, payload }: { actionPlanId: string; payload: ApprovalRequest }) =>
      actionPlanningApi.rejectActionPlan(actionPlanId, payload),
    onSuccess: (data) => {
      showSuccess(`Action plan ${data.actionPlanId} rejected.`);
      queryClient.invalidateQueries({ queryKey: ['action-kanban'] });
    },
    onError: (err: any) => {
      showError(err.message || 'Failed to reject action plan.');
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
      showSuccess(`Synced to Jira (${data.externalKey || data.externalId || 'Issue Created'})!`);
      queryClient.invalidateQueries({ queryKey: ['action-kanban'] });
    },
    onError: (err: any) => {
      showError(err.message || 'Jira sync failed.');
    },
  });
}

export function useSyncPlannerMutation() {
  const queryClient = useQueryClient();
  const { showSuccess, showError } = useToast();

  return useMutation({
    mutationFn: ({ actionPlanId, planId }: { actionPlanId: string; planId?: string }) =>
      actionPlanningApi.syncToPlanner(actionPlanId, planId),
    onSuccess: (data) => {
      showSuccess(`Synced to Microsoft Planner (${data.externalKey || data.externalId || 'Task Created'})!`);
      queryClient.invalidateQueries({ queryKey: ['action-kanban'] });
    },
    onError: (err: any) => {
      showError(err.message || 'MS Planner sync failed.');
    },
  });
}

