import React, { useState } from 'react';
import { PageHeader } from '../../../components/ui/PageHeader';
import { Tabs } from '../../../components/ui/Tabs';
import { Card } from '../../../components/ui/Card';
import { Skeleton } from '../../../components/ui/Skeleton';
import { ErrorState } from '../../../components/ui/ErrorState';
import { ExecutiveScorecardPanel } from '../../../components/analytics/ExecutiveScorecardPanel';
import { OrganizationalHeatmapGrid } from '../../../components/analytics/OrganizationalHeatmapGrid';
import { useTenant } from '../../../context/TenantContext';
import {
  useDashboardMetricsQuery,
  useHeatmapMatrixQuery,
} from '../api/useAnalyticsQueries';

export const AnalyticsDashboardPage: React.FC = () => {
  const { activeProject } = useTenant();
  const [activeTab, setActiveTab] = useState<string>('scorecard');

  const campaignId = 'CMP-101';
  const projectId = activeProject?.projectId || 'PRJ-99201';

  const {
    data: dashboardData,
    isLoading: isDashboardLoading,
    isError: isDashboardError,
    refetch: refetchDashboard,
  } = useDashboardMetricsQuery(campaignId, 'N-ROOT');

  const {
    data: heatmapData,
    isLoading: isHeatmapLoading,
    isError: isHeatmapError,
    refetch: refetchHeatmap,
  } = useHeatmapMatrixQuery(campaignId, 'N-ROOT');

  // Demo fallback metrics for interactive demonstration
  const fallbackDashboardMetrics = {
    campaignId,
    nodeId: 'N-ROOT',
    totalResponses: 2890,
    participationRate: 55.6,
    eNPS: 42,
    engagementIndex: 78.4,
    groupScores: [
      { groupId: 'QG-01', groupName: 'Leadership Trust', sampleSize: 1200, score: 84.5, colorIntensity: 'GREEN' as const },
      { groupId: 'QG-02', groupName: 'Workload Balance', sampleSize: 1180, score: 62.0, colorIntensity: 'YELLOW' as const },
      { groupId: 'QG-03', groupName: 'Career Mobility', sampleSize: 4, score: null, colorIntensity: 'GREY' as const },
      { groupId: 'QG-04', groupName: 'Compensation Fairness', sampleSize: 950, score: 48.2, colorIntensity: 'RED' as const },
    ],
  };

  const fallbackHeatmapData = {
    parentNodeId: 'ROOT',
    rowNodes: [
      { id: 'N-201', name: 'Engineering & Technology' },
      { id: 'N-202', name: 'Operations & Logistics' },
      { id: 'N-203', name: 'Human Resources' },
    ],
    columnThemes: [
      { id: 'QG-01', name: 'Leadership Trust' },
      { id: 'QG-02', name: 'Workload Balance' },
      { id: 'QG-03', name: 'Career Mobility' },
    ],
    cells: [
      { nodeId: 'N-201', groupId: 'QG-01', sampleSize: 24, score: 84.5, colorIntensity: 'GREEN' as const },
      { nodeId: 'N-201', groupId: 'QG-02', sampleSize: 22, score: 62.0, colorIntensity: 'YELLOW' as const },
      { nodeId: 'N-201', groupId: 'QG-03', sampleSize: 3, score: null, colorIntensity: 'GREY' as const },
      { nodeId: 'N-202', groupId: 'QG-01', sampleSize: 18, score: 71.2, colorIntensity: 'GREEN' as const },
      { nodeId: 'N-202', groupId: 'QG-02', sampleSize: 15, score: 48.0, colorIntensity: 'RED' as const },
      { nodeId: 'N-202', groupId: 'QG-03', sampleSize: 16, score: 65.5, colorIntensity: 'YELLOW' as const },
    ],
  };

  const tabs = [
    { id: 'scorecard', label: 'Executive Scorecard' },
    { id: 'heatmap', label: '2D Organizational Heatmap' },
  ];

  return (
    <div>
      <PageHeader
        title="Analytics Engine & Organizational Heatmap Studio"
        subtitle={`Real-time engagement scoring, 2D heatmaps, and differential privacy suppression for ${projectId}`}
      />

      <Tabs tabs={tabs} activeTab={activeTab} onChange={setActiveTab} />

      <div style={{ marginTop: '20px' }}>
        {activeTab === 'scorecard' && (
          <div>
            {isDashboardLoading ? (
              <Card variant="bordered" padding="24px">
                <Skeleton height="260px" borderRadius="12px" />
              </Card>
            ) : isDashboardError ? (
              <Card variant="bordered" padding="24px">
                <ErrorState
                  title="Dashboard Metrics Unavailable"
                  message="Could not load real-time analytics from analytics-engine-service."
                  onRetry={refetchDashboard}
                />
              </Card>
            ) : (
              <ExecutiveScorecardPanel metrics={dashboardData || fallbackDashboardMetrics} />
            )}
          </div>
        )}

        {activeTab === 'heatmap' && (
          <div>
            {isHeatmapLoading ? (
              <Card variant="bordered" padding="24px">
                <Skeleton height="400px" borderRadius="12px" />
              </Card>
            ) : isHeatmapError ? (
              <Card variant="bordered" padding="24px">
                <ErrorState
                  title="Heatmap Matrix Unavailable"
                  message="Could not load 2D heatmap matrix from analytics-engine-service."
                  onRetry={refetchHeatmap}
                />
              </Card>
            ) : (
              <OrganizationalHeatmapGrid
                data={(heatmapData as any) || fallbackHeatmapData}
              />
            )}
          </div>
        )}
      </div>
    </div>
  );
};
