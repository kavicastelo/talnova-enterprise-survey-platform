import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { distributionApi } from './distributionApi';
import {
  CampaignCreateRequest,
  CampaignStatus,
  TokenBatchGenerationRequest,
  OptimalDispatchPredictionRequest,
} from '../../../types/distribution';
import { useToast } from '../../../context/ToastContext';

export function useCampaignQuery(campaignId: string | undefined, projectId: string | undefined) {
  return useQuery({
    queryKey: ['campaign', projectId, campaignId],
    queryFn: () => distributionApi.getCampaign(campaignId!, projectId),
    enabled: !!campaignId && !!projectId,
    staleTime: 5 * 60 * 1000,
  });
}

export function useCreateCampaignMutation() {
  const queryClient = useQueryClient();
  const { showSuccess, showError } = useToast();

  return useMutation({
    mutationFn: (payload: CampaignCreateRequest) => distributionApi.createCampaign(payload),
    onSuccess: (data) => {
      showSuccess(`Survey Campaign ${data.campaignId} launched successfully!`);
      queryClient.setQueryData(['campaign', data.projectId, data.campaignId], data);
    },
    onError: (err: any) => {
      showError(err.message || 'Failed to launch survey campaign.');
    },
  });
}

export function useUpdateCampaignStatusMutation() {
  const queryClient = useQueryClient();
  const { showSuccess, showError } = useToast();

  return useMutation({
    mutationFn: ({ campaignId, status, projectId }: { campaignId: string; status: CampaignStatus; projectId?: string }) =>
      distributionApi.updateCampaignStatus(campaignId, status, projectId),
    onSuccess: (data) => {
      showSuccess(`Campaign ${data.campaignId} status updated to ${data.status}!`);
      queryClient.setQueryData(['campaign', data.projectId, data.campaignId], data);
    },
    onError: (err: any) => {
      showError(err.message || 'Failed to update campaign status.');
    },
  });
}

export function useGenerateTokensMutation() {
  const { showSuccess, showError } = useToast();

  return useMutation({
    mutationFn: (payload: TokenBatchGenerationRequest) => distributionApi.generateTokens(payload),
    onSuccess: (data) => {
      showSuccess(`Generated ${data.generatedCount} single-use HMAC-SHA256 survey tokens.`);
    },
    onError: (err: any) => {
      showError(err.message || 'Failed to generate survey tokens.');
    },
  });
}

export function usePredictOptimalTimeMutation() {
  const { showError } = useToast();

  return useMutation({
    mutationFn: (payload: OptimalDispatchPredictionRequest) => distributionApi.predictOptimalTime(payload),
    onError: (err: any) => {
      showError(err.message || 'AI optimal dispatch time prediction failed.');
    },
  });
}
