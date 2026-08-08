import { useQuery } from '@tanstack/react-query';
import { analyticsApi } from './analyticsApi';

export function useDashboardMetricsQuery(campaignId: string | undefined, nodeId?: string) {
  return useQuery({
    queryKey: ['analytics-dashboard', campaignId, nodeId],
    queryFn: () => analyticsApi.getDashboardMetrics(campaignId!, nodeId),
    enabled: !!campaignId,
    staleTime: 5 * 60 * 1000,
  });
}

export function useHeatmapMatrixQuery(campaignId: string | undefined, parentNodeId?: string) {
  return useQuery({
    queryKey: ['analytics-heatmap', campaignId, parentNodeId],
    queryFn: () => analyticsApi.getHeatmapMatrix(campaignId!, parentNodeId),
    enabled: !!campaignId,
    staleTime: 5 * 60 * 1000,
  });
}
