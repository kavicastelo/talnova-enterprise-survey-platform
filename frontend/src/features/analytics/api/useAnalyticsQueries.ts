import { useQuery } from '@tanstack/react-query';
import { analyticsApi } from './analyticsApi';

export function useDashboardMetricsQuery(
  campaignId: string | undefined,
  nodeId?: string,
  filters?: Record<string, string>
) {
  return useQuery({
    queryKey: ['analytics-dashboard', campaignId, nodeId, filters],
    queryFn: () => analyticsApi.getDashboardMetrics(campaignId!, nodeId, filters),
    enabled: !!campaignId,
    staleTime: 5 * 60 * 1000,
  });
}

export function useHeatmapMatrixQuery(
  campaignId: string | undefined,
  parentNodeId?: string,
  filters?: Record<string, string>
) {
  return useQuery({
    queryKey: ['analytics-heatmap', campaignId, parentNodeId, filters],
    queryFn: () => analyticsApi.getHeatmapMatrix(campaignId!, parentNodeId, filters),
    enabled: !!campaignId,
    staleTime: 5 * 60 * 1000,
  });
}

export function useBaselineTrendsQuery(campaignId: string | undefined, nodeId?: string) {
  return useQuery({
    queryKey: ['analytics-trends', campaignId, nodeId],
    queryFn: () => analyticsApi.getBaselineTrends(campaignId!, nodeId),
    enabled: !!campaignId,
    staleTime: 5 * 60 * 1000,
  });
}

export function useAnalyticalSnapshotsQuery(campaignId?: string) {
  return useQuery({
    queryKey: ['analytics-snapshots', campaignId],
    queryFn: () => analyticsApi.getAnalyticalSnapshots(campaignId),
    staleTime: 5 * 60 * 1000,
  });
}

export function useAiAnalyticsInsightsQuery(campaignId: string | undefined, nodeId?: string) {
  return useQuery({
    queryKey: ['analytics-ai-insights', campaignId, nodeId],
    queryFn: () => analyticsApi.getAiInsights(campaignId!, nodeId),
    enabled: !!campaignId,
    staleTime: 5 * 60 * 1000,
  });
}
