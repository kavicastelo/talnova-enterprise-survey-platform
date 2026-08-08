import { useMutation } from '@tanstack/react-query';
import { responseIntakeApi } from './responseIntakeApi';
import { ResponseSubmissionRequest } from '../../../types/responseIntake';
import { useToast } from '../../../context/ToastContext';

export function useSubmitResponseMutation() {
  const { showSuccess, showError } = useToast();

  return useMutation({
    mutationFn: (payload: ResponseSubmissionRequest) => responseIntakeApi.submitResponse(payload),
    onSuccess: (data) => {
      showSuccess(`Survey response ingested successfully (ID: ${data.responseId})`);
    },
    onError: (err: any) => {
      showError(err.message || 'Failed to submit survey response.');
    },
  });
}
