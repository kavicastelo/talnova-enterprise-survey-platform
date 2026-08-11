import React, { useState } from 'react';
import { PageHeader } from '../../../components/ui/PageHeader';
import { Tabs } from '../../../components/ui/Tabs';
import { Card } from '../../../components/ui/Card';
import { Skeleton } from '../../../components/ui/Skeleton';
import { ErrorState } from '../../../components/ui/ErrorState';
import { EmptyState } from '../../../components/ui/EmptyState';
import { Alert } from '../../../components/ui/Alert';
import { ExecutiveScorecardPanel } from '../../../components/analytics/ExecutiveScorecardPanel';
import { OrganizationalHeatmapGrid } from '../../../components/analytics/OrganizationalHeatmapGrid';
import { ExecutiveKpiScorecard } from '../../../components/analytics/ExecutiveKpiScorecard';
import { AiInsightsPanel } from '../../../components/analytics/AiInsightsPanel';
import { useTenant } from '../../../context/TenantContext';
import { useAuth } from '../../../context/AuthContext';
import {
  useDashboardMetricsQuery,
  useHeatmapMatrixQuery,
  useBaselineTrendsQuery,
  useAiAnalyticsInsightsQuery,
} from '../api/useAnalyticsQueries';

export const AnalyticsDashboardPage: React.FC = () => {
  const { activeProject } = useTenant();
  const { user, hasRole } = useAuth();
  const [activeTab, setActiveTab] = useState<string>('scorecard');

  // Dynamic campaign and node scope selection state
  const [campaignId, setCampaignId] = useState<string>('CMP-1001');
  const [nodeId, setNodeId] = useState<string>('N-ROOT');
  const [demographicFilters, setDemographicFilters] = useState<Record<string, string>>({});

  const projectId = activeProject?.projectId || localStorage.getItem('tesp_project_id') || 'PRJ-99201';

  // Permission check per PR-ANL-001 to PR-ANL-004
  const isAuthorized =
    !user ||
    hasRole(['EXECUTIVE', 'HR_MANAGER', 'DEPARTMENT_MANAGER', 'SUPER_ADMIN', 'PROJECT_ADMIN']);

  const {
    data: dashboardData,
    isLoading: isDashboardLoading,
    isError: isDashboardError,
    error: dashboardError,
    refetch: refetchDashboard,
  } = useDashboardMetricsQuery(
    isAuthorized ? campaignId : undefined,
    nodeId,
    demographicFilters
  );

  const {
    data: heatmapData,
    isLoading: isHeatmapLoading,
    isError: isHeatmapError,
    refetch: refetchHeatmap,
  } = useHeatmapMatrixQuery(
    isAuthorized ? campaignId : undefined,
    nodeId,
    demographicFilters
  );

  const {
    data: trendsData,
    isLoading: isTrendsLoading,
    isError: isTrendsError,
    refetch: refetchTrends,
  } = useBaselineTrendsQuery(isAuthorized ? campaignId : undefined, nodeId);

  const {
    data: aiInsightsData,
    isLoading: isAiInsightsLoading,
    isError: isAiInsightsError,
    refetch: refetchAiInsights,
  } = useAiAnalyticsInsightsQuery(isAuthorized ? campaignId : undefined, nodeId);

  const tabs = [
    { id: 'scorecard', label: 'Executive Scorecard' },
    { id: 'heatmap', label: '2D Organizational Heatmap' },
    { id: 'trends', label: 'Longitudinal Baseline Trends' },
    { id: 'ai-insights', label: 'AI Anomaly Alerts & Key Drivers' },
  ];

  if (!isAuthorized) {
    return (
      <div className="space-y-6 p-6">
        <PageHeader
          title="Analytics Engine &amp; Heatmap Studio"
          subtitle="Access restricted per PR-ANL-004"
        />
        <Card variant="bordered" padding="24px">
          <Alert type="error" title="Access Forbidden (403)">
            Your user role does not have authorization to access Real-Time Engagement Analytics APIs or organizational heatmaps. Please contact your system administrator.
          </Alert>
        </Card>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <PageHeader
        title="Real-Time Engagement Analytics &amp; Heatmap Engine"
        subtitle={`Quantitative metric processing, 2D heatmaps, and differential privacy ($N < 5$) for ${projectId}`}
      />

      {/* Scope Controls & Global Demographic Slicers (VR-ANL-004) */}
      <Card variant="bordered" padding="16px">
        <div className="space-y-4">
          <div className="flex flex-wrap items-center justify-between gap-4 border-b border-slate-100 pb-3">
            <div className="flex flex-wrap items-center gap-3">
              <div>
                <label className="block text-[11px] font-bold text-slate-600 uppercase tracking-wider mb-1">
                  Campaign ID (VR-ANL-001)
                </label>
                <input
                  type="text"
                  value={campaignId}
                  onChange={(e) => setCampaignId(e.target.value)}
                  placeholder="e.g. CMP-1001"
                  className="bg-white border border-slate-300 rounded px-3 py-1.5 text-xs text-slate-800 font-mono focus:outline-none focus:border-indigo-600 w-44"
                />
              </div>

              <div>
                <label className="block text-[11px] font-bold text-slate-600 uppercase tracking-wider mb-1">
                  Node Scope (VR-ANL-002)
                </label>
                <input
                  type="text"
                  value={nodeId}
                  onChange={(e) => setNodeId(e.target.value)}
                  placeholder="e.g. N-201 or N-ROOT"
                  className="bg-white border border-slate-300 rounded px-3 py-1.5 text-xs text-slate-800 font-mono focus:outline-none focus:border-indigo-600 w-44"
                />
              </div>
            </div>

            <div className="text-xs text-slate-500">
              Active Scope: <span className="font-mono text-indigo-700 font-bold">{nodeId}</span>
            </div>
          </div>

          {/* Demographic Cohort Slicers Bar (Max 5 per VR-ANL-004) */}
          <div className="flex flex-wrap items-center gap-3">
            <span className="text-xs font-bold text-slate-700">Demographic Cohort Slicers ({Object.keys(demographicFilters).length}/5):</span>
            
            <select
              className="bg-white border border-slate-300 text-xs text-slate-800 rounded px-2.5 py-1 focus:outline-none focus:border-indigo-600"
              value={demographicFilters['Tenure'] || ''}
              onChange={(e) => {
                const val = e.target.value;
                const updated = { ...demographicFilters };
                if (val) updated['Tenure'] = val; else delete updated['Tenure'];
                setDemographicFilters(updated);
              }}
            >
              <option value="">All Tenures</option>
              <option value="<1 Year">&lt; 1 Year</option>
              <option value="1-3 Years">1-3 Years</option>
              <option value="3-5 Years">3-5 Years</option>
              <option value="5+ Years">5+ Years</option>
            </select>

            <select
              className="bg-white border border-slate-300 text-xs text-slate-800 rounded px-2.5 py-1 focus:outline-none focus:border-indigo-600"
              value={demographicFilters['Gender'] || ''}
              onChange={(e) => {
                const val = e.target.value;
                const updated = { ...demographicFilters };
                if (val) updated['Gender'] = val; else delete updated['Gender'];
                setDemographicFilters(updated);
              }}
            >
              <option value="">All Genders</option>
              <option value="Female">Female</option>
              <option value="Male">Male</option>
              <option value="Non-Binary">Non-Binary</option>
            </select>

            <select
              className="bg-white border border-slate-300 text-xs text-slate-800 rounded px-2.5 py-1 focus:outline-none focus:border-indigo-600"
              value={demographicFilters['Location'] || ''}
              onChange={(e) => {
                const val = e.target.value;
                const updated = { ...demographicFilters };
                if (val) updated['Location'] = val; else delete updated['Location'];
                setDemographicFilters(updated);
              }}
            >
              <option value="">All Locations</option>
              <option value="HQ - New York">HQ - New York</option>
              <option value="Factory B - Austin">Factory B - Austin</option>
              <option value="EMEA - London">EMEA - London</option>
              <option value="APAC - Singapore">APAC - Singapore</option>
            </select>

            <select
              className="bg-white border border-slate-300 text-xs text-slate-800 rounded px-2.5 py-1 focus:outline-none focus:border-indigo-600"
              value={demographicFilters['AgeGroup'] || ''}
              onChange={(e) => {
                const val = e.target.value;
                const updated = { ...demographicFilters };
                if (val) updated['AgeGroup'] = val; else delete updated['AgeGroup'];
                setDemographicFilters(updated);
              }}
            >
              <option value="">All Age Groups</option>
              <option value="18-29">18-29 Years</option>
              <option value="30-44">30-44 Years</option>
              <option value="45+">45+ Years</option>
            </select>

            {Object.keys(demographicFilters).length > 0 && (
              <button
                onClick={() => setDemographicFilters({})}
                className="text-[11px] font-bold text-rose-600 hover:text-rose-800 underline ml-1"
              >
                Clear Slicers
              </button>
            )}
          </div>
        </div>
      </Card>

      <Tabs tabs={tabs} activeTab={activeTab} onChange={setActiveTab} />

      <div className="mt-4">
        {activeTab === 'scorecard' && (
          <div>
            {isDashboardLoading ? (
              <Card variant="bordered" padding="24px">
                <div className="space-y-4">
                  <Skeleton height="32px" width="40%" />
                  <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
                    <Skeleton height="100px" borderRadius="12px" />
                    <Skeleton height="100px" borderRadius="12px" />
                    <Skeleton height="100px" borderRadius="12px" />
                  </div>
                  <Skeleton height="140px" borderRadius="12px" />
                </div>
              </Card>
            ) : isDashboardError ? (
              <Card variant="bordered" padding="24px">
                <ErrorState
                  title="Dashboard Analytics API Error"
                  message={
                    (dashboardError as any)?.message ||
                    'Failed to communicate with analytics-engine-service GET /api/v1/analytics/dashboard'
                  }
                  onRetry={refetchDashboard}
                />
              </Card>
            ) : dashboardData && dashboardData.totalResponses === 0 ? (
              <Card variant="bordered" padding="24px">
                <EmptyState
                  title="No Survey Responses Ingested"
                  description={`Campaign ${campaignId} currently has 0 recorded responses for node ${nodeId}. Metrics will update live as responses are received.`}
                />
              </Card>
            ) : dashboardData ? (
              <ExecutiveScorecardPanel metrics={dashboardData} />
            ) : (
              <Card variant="bordered" padding="24px">
                <EmptyState
                  title="No Analytics Data Available"
                  description="Please specify a valid Campaign ID and Node Scope to view engagement metrics."
                />
              </Card>
            )}
          </div>
        )}

        {activeTab === 'heatmap' && (
          <div>
            {isHeatmapLoading ? (
              <Card variant="bordered" padding="24px">
                <Skeleton height="360px" borderRadius="12px" />
              </Card>
            ) : isHeatmapError ? (
              <Card variant="bordered" padding="24px">
                <ErrorState
                  title="Heatmap Matrix API Error"
                  message="Could not load 2D heatmap matrix from analytics-engine-service GET /api/v1/analytics/heatmap."
                  onRetry={refetchHeatmap}
                />
              </Card>
            ) : heatmapData ? (
              <OrganizationalHeatmapGrid
                data={heatmapData}
                onFilterChange={(filters) => setDemographicFilters(filters)}
              />
            ) : (
              <Card variant="bordered" padding="24px">
                <EmptyState
                  title="No Heatmap Data Available"
                  description="No organizational heatmap matrix found for the active campaign scope."
                />
              </Card>
            )}
          </div>
        )}

        {activeTab === 'trends' && (
          <div>
            {isTrendsLoading ? (
              <Card variant="bordered" padding="24px">
                <Skeleton height="360px" borderRadius="12px" />
              </Card>
            ) : isTrendsError ? (
              <Card variant="bordered" padding="24px">
                <ErrorState
                  title="Longitudinal Trends API Error"
                  message="Could not fetch baseline trend comparison from analytical_snapshots."
                  onRetry={refetchTrends}
                />
              </Card>
            ) : trendsData ? (
              <ExecutiveKpiScorecard
                data={{
                  totalResponses: dashboardData?.totalResponses || 0,
                  participationRate: dashboardData?.participationRate || 0,
                  eNPS: dashboardData?.eNPS || 0,
                  engagementIndex: dashboardData?.engagementIndex || 0,
                  longitudinalDelta: trendsData.longitudinalDelta,
                  trendHistory: trendsData.trendHistory,
                }}
              />
            ) : (
              <Card variant="bordered" padding="24px">
                <EmptyState
                  title="No Historical Baseline Data"
                  description="No prior campaign snapshot frozen in analytical_snapshots collection for comparison."
                />
              </Card>
            )}
          </div>
        )}

        {activeTab === 'ai-insights' && (
          <div>
            {isAiInsightsLoading ? (
              <Card variant="bordered" padding="24px">
                <Skeleton height="360px" borderRadius="12px" />
              </Card>
            ) : isAiInsightsError ? (
              <Card variant="bordered" padding="24px">
                <ErrorState
                  title="AI Insights API Error"
                  message="Could not load Key Driver Regression or Score Drop Anomaly alerts."
                  onRetry={refetchAiInsights}
                />
              </Card>
            ) : aiInsightsData ? (
              <AiInsightsPanel
                anomalies={aiInsightsData.anomalies || []}
                keyDrivers={aiInsightsData.keyDrivers || []}
              />
            ) : (
              <Card variant="bordered" padding="24px">
                <EmptyState
                  title="No AI Analytics Data"
                  description="AI background workers have not completed key driver regression or anomaly analysis for this scope."
                />
              </Card>
            )}
          </div>
        )}
      </div>
    </div>
  );
};
