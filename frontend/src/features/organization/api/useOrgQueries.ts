import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { orgApi } from './orgApi';
import { CreateNodeRequest } from '../../../types/organization';
import { useToast } from '../../../context/ToastContext';

export function useOrgNodeQuery(nodeId: string | undefined, projectId: string | undefined) {
  return useQuery({
    queryKey: ['org-node', projectId, nodeId],
    queryFn: () => orgApi.getNode(nodeId!, projectId),
    enabled: !!nodeId && !!projectId,
    staleTime: 5 * 60 * 1000,
  });
}

export function useSubtreeQuery(nodeId: string | undefined, projectId: string | undefined) {
  return useQuery({
    queryKey: ['org-subtree', projectId, nodeId],
    queryFn: () => orgApi.getSubtree(nodeId!, projectId),
    enabled: !!nodeId && !!projectId,
    staleTime: 5 * 60 * 1000,
  });
}

export function useLineageQuery(nodeId: string | undefined, projectId: string | undefined) {
  return useQuery({
    queryKey: ['org-lineage', projectId, nodeId],
    queryFn: () => orgApi.getLineage(nodeId!, projectId),
    enabled: !!nodeId && !!projectId,
    staleTime: 5 * 60 * 1000,
  });
}

export function useAnomaliesQuery(projectId: string | undefined) {
  return useQuery({
    queryKey: ['org-anomalies', projectId],
    queryFn: () => orgApi.inspectAnomalies(projectId),
    enabled: !!projectId,
    staleTime: 2 * 60 * 1000,
  });
}

export function useCreateNodeMutation() {
  const queryClient = useQueryClient();
  const { showSuccess, showError } = useToast();

  return useMutation({
    mutationFn: (payload: CreateNodeRequest) => orgApi.createNode(payload),
    onSuccess: (data) => {
      showSuccess(`Organization node ${data.nodeId} (${data.name}) created successfully!`);
      queryClient.invalidateQueries({ queryKey: ['org-subtree', data.projectId] });
      queryClient.invalidateQueries({ queryKey: ['org-anomalies', data.projectId] });
    },
    onError: (err: any) => {
      showError(err.message || 'Failed to create organization node.');
    },
  });
}

export function useMoveNodeMutation() {
  const queryClient = useQueryClient();
  const { showSuccess, showError } = useToast();

  return useMutation({
    mutationFn: ({ nodeId, newParentId, projectId }: { nodeId: string; newParentId?: string; projectId?: string }) =>
      orgApi.moveNode(nodeId, newParentId, projectId),
    onSuccess: (data) => {
      showSuccess(`Node ${data.nodeId} re-parented to ${data.parentId || 'ROOT'}! Materialized path updated.`);
      queryClient.invalidateQueries({ queryKey: ['org-subtree', data.projectId] });
      queryClient.invalidateQueries({ queryKey: ['org-lineage', data.projectId] });
      queryClient.invalidateQueries({ queryKey: ['org-anomalies', data.projectId] });
    },
    onError: (err: any) => {
      showError(err.message || 'Failed to re-parent organization node.');
    },
  });
}

export function useDeleteNodeMutation() {
  const queryClient = useQueryClient();
  const { showSuccess, showError } = useToast();

  return useMutation({
    mutationFn: ({ nodeId, projectId }: { nodeId: string; projectId?: string }) =>
      orgApi.deleteNode(nodeId, projectId),
    onSuccess: (_, { nodeId, projectId }) => {
      showSuccess(`Organization node ${nodeId} deleted successfully.`);
      queryClient.invalidateQueries({ queryKey: ['org-subtree', projectId] });
      queryClient.invalidateQueries({ queryKey: ['org-anomalies', projectId] });
    },
    onError: (err: any) => {
      showError(err.message || 'Failed to delete organization node.');
    },
  });
}
