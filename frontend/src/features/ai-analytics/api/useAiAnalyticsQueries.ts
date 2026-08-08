import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { aiAnalyticsApi } from './aiAnalyticsApi';
import { SentimentOverrideRequest } from '../../../types/aiAnalytics';
import { useToast } from '../../../context/ToastContext';

export function useCampaignAiInsightsQuery(projectId: string | undefined, campaignId: string | undefined) {
  return useQuery({
    queryKey: ['ai-insights', projectId, campaignId],
    queryFn: () => aiAnalyticsApi.getCampaignInsights(projectId!, campaignId!),
    enabled: !!projectId && !!campaignId,
    staleTime: 5 * 60 * 1000,
  });
}

export function useExecutiveSummaryQuery(campaignId?: string, nodeId?: string) {
  return useQuery({
    queryKey: ['ai-summary', campaignId, nodeId],
    queryFn: () => aiAnalyticsApi.getExecutiveSummary(campaignId, nodeId),
    staleTime: 5 * 60 * 1000,
  });
}

export function useOverrideSentimentMutation() {
  const queryClient = useQueryClient();
  const { showSuccess, showError } = useToast();

  return useMutation({
    mutationFn: ({ insightId, payload }: { insightId: string; payload: SentimentOverrideRequest; projectId?: string; campaignId?: string }) =>
      aiAnalyticsApi.overrideSentimentTag(insightId, payload),
    onSuccess: (data) => {
      showSuccess(`Sentiment tag updated to ${data.sentimentLabel}!`);
      queryClient.invalidateQueries({ queryKey: ['ai-insights'] });
    },
    onError: (err: any) => {
      showError(err.message || 'Failed to update sentiment tag.');
    },
  });
}

export function useGenerateSummaryMutation() {
  const queryClient = useQueryClient();
  const { showSuccess, showError } = useToast();

  return useMutation({
    mutationFn: ({ nodeScope, providerName, rawComments }: { nodeScope?: string; providerName?: string; rawComments?: string[] }) =>
      aiAnalyticsApi.generateExecutiveSummary(nodeScope, providerName, rawComments),
    onSuccess: (data) => {
      showSuccess(`Generated AI Executive Summary: "${data.summaryTitle}"`);
      queryClient.invalidateQueries({ queryKey: ['ai-summary'] });
    },
    onError: (err: any) => {
      showError(err.message || 'AI Executive Summary generation failed.');
    },
  });
}
