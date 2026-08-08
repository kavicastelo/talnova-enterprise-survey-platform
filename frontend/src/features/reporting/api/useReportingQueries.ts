import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { reportingApi } from './reportingApi';
import { ReportRequest } from '../../../types/reporting';
import { useToast } from '../../../context/ToastContext';

export function useGenerateReportMutation() {
  const queryClient = useQueryClient();
  const { showSuccess, showError } = useToast();

  return useMutation({
    mutationFn: (payload: ReportRequest) => reportingApi.generateReport(payload),
    onSuccess: (data) => {
      showSuccess(`Report job queued successfully (ID: ${data.jobId})`);
      queryClient.invalidateQueries({ queryKey: ['report-jobs'] });
    },
    onError: (err: any) => {
      showError(err.message || 'Failed to submit report generation request.');
    },
  });
}

export function useReportJobStatusQuery(jobId: string | undefined, projectId?: string) {
  return useQuery({
    queryKey: ['report-job-status', jobId],
    queryFn: () => reportingApi.getJobStatus(jobId!, projectId),
    enabled: !!jobId,
    refetchInterval: (query) => {
      const status = query.state.data?.status;
      if (status === 'QUEUED' || status === 'PROCESSING') {
        return 3000;
      }
      return false;
    },
  });
}
