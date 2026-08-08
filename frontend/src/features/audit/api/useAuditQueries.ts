import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { auditApi } from './auditApi';
import { AuditLogRequest } from '../../../types/audit';
import { useToast } from '../../../context/ToastContext';

export function useAuditLogsQuery(
  projectId: string | undefined,
  actorId?: string,
  action?: string,
  page: number = 0,
  size: number = 20
) {
  return useQuery({
    queryKey: ['audit-logs', projectId, actorId, action, page, size],
    queryFn: () => auditApi.getAuditLogs(projectId!, actorId, action, page, size),
    enabled: !!projectId,
    staleTime: 2 * 60 * 1000,
  });
}

export function useRecordAuditLogMutation() {
  const queryClient = useQueryClient();
  const { showSuccess, showError } = useToast();

  return useMutation({
    mutationFn: (payload: AuditLogRequest) => auditApi.recordAuditLog(payload),
    onSuccess: (data) => {
      showSuccess(`Audit record appended (ID: ${data.auditId})`);
      queryClient.invalidateQueries({ queryKey: ['audit-logs'] });
    },
    onError: (err: any) => {
      showError(err.message || 'Failed to record audit log.');
    },
  });
}
