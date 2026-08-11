import React, { useState } from 'react';
import { PageHeader } from '../../../components/ui/PageHeader';
import { Tabs } from '../../../components/ui/Tabs';
import { Card } from '../../../components/ui/Card';
import { Skeleton } from '../../../components/ui/Skeleton';
import { ErrorState } from '../../../components/ui/ErrorState';
import { EmptyState } from '../../../components/ui/EmptyState';
import { Alert } from '../../../components/ui/Alert';
import { SentimentAnalyticsPanel } from '../../../components/ai/SentimentAnalyticsPanel';
import { ExecutiveSummaryCard } from '../../../components/ai/ExecutiveSummaryCard';
import {
  WorkplaceRiskAlertBanner,
  RiskAlertItem,
} from '../../../components/ai/WorkplaceRiskAlertBanner';
import { useTenant } from '../../../context/TenantContext';
import { useAuth } from '../../../context/AuthContext';
import {
  useCampaignAiInsightsQuery,
  useExecutiveSummaryQuery,
  useOverrideSentimentMutation,
  useGenerateSummaryMutation,
} from '../api/useAiAnalyticsQueries';

export const AiAnalyticsPage: React.FC = () => {
  const { activeProject } = useTenant();
  const { user, hasRole } = useAuth();
  const [activeTab, setActiveTab] = useState<string>('sentiment');

  // Scope controls
  const [campaignId, setCampaignId] = useState<string>('CMP-77102');
  const [nodeScope, setNodeScope] = useState<string>('GLOBAL');
  const [providerName, setProviderName] = useState<string>('OPENAI');

  const projectId = activeProject?.projectId || localStorage.getItem('tesp_project_id') || 'PRJ-99201';

  // Permission check per PR-AI-001 to PR-AI-004
  const isAuthorized =
    !user ||
    hasRole(['EXECUTIVE', 'HR_MANAGER', 'CONSULTANT_DAASH', 'SUPER_ADMIN', 'PROJECT_ADMIN']);

  const {
    data: insightsData,
    isLoading: isInsightsLoading,
    isError: isInsightsError,
    error: insightsError,
    refetch: refetchInsights,
  } = useCampaignAiInsightsQuery(
    isAuthorized ? projectId : undefined,
    isAuthorized ? campaignId : undefined
  );

  const {
    data: summaryData,
    isLoading: isSummaryLoading,
    isError: isSummaryError,
    refetch: refetchSummary,
  } = useExecutiveSummaryQuery(
    isAuthorized ? campaignId : undefined,
    nodeScope,
    providerName
  );

  const overrideMutation = useOverrideSentimentMutation();
  const generateSummaryMutation = useGenerateSummaryMutation();

  const handleOverrideTag = (insightId: string, newLabel: 'POSITIVE' | 'NEUTRAL' | 'NEGATIVE') => {
    overrideMutation.mutate({
      insightId,
      payload: {
        overriddenBy: user?.email || 'ANALYST_USER',
        newLabel,
        reason: 'Human analyst calibration per BR-AI-003',
      },
      projectId,
      campaignId,
    });
  };

  const handleRegenerateSummary = () => {
    generateSummaryMutation.mutate({
      nodeScope,
      providerName,
    });
  };

  // Derive workplace risk alerts for PF-AI-002 from insightsData
  const extractedRiskAlerts: RiskAlertItem[] = React.useMemo(() => {
    if (!insightsData) return [];
    const items: RiskAlertItem[] = [];
    insightsData.forEach((doc: any, index: number) => {
      if (doc.riskFlags && doc.riskFlags.length > 0) {
        doc.riskFlags.forEach((flag: any, subIndex: number) => {
          items.push({
            id: `ALERT-${doc.id || index}-${subIndex}`,
            category: flag.category || doc.riskCategory || 'SAFETY',
            severity: flag.severity || doc.riskSeverity || 'HIGH',
            keyword: flag.keyword || 'risk keyword',
            sanitizedSnippet: doc.sanitizedText || 'Unspecified risk comment',
            detectedAt: doc.processedAt || new Date().toISOString(),
          });
        });
      } else if (doc.riskSeverity && doc.riskSeverity !== 'NONE') {
        items.push({
          id: `ALERT-${doc.id || index}`,
          category: doc.riskCategory || 'COMPLIANCE',
          severity: doc.riskSeverity,
          keyword: doc.riskCategory || 'workplace risk',
          sanitizedSnippet: doc.sanitizedText || 'Risk comment',
          detectedAt: doc.createdAt || new Date().toISOString(),
        });
      }
    });
    return items;
  }, [insightsData]);

  const handleInvestigateRisk = (alert: RiskAlertItem) => {
    window.location.href = `/action-planning?campaignId=${campaignId}&riskId=${alert.id}&category=${alert.category}`;
  };

  const tabs = [
    { id: 'sentiment', label: 'AI Sentiment & Topic Studio' },
    { id: 'summary', label: 'LLM Executive Summaries' },
  ];

  if (!isAuthorized) {
    return (
      <div className="space-y-6 p-6">
        <PageHeader
          title="AI Analytics &amp; Sentiment Intelligence Studio"
          subtitle="Access restricted per PR-AI-004"
        />
        <Card variant="bordered" padding="24px">
          <Alert type="error" title="Access Forbidden (403)">
            Your user role does not have authorization to access AI Sentiment Analytics endpoints or LLM Executive Summaries.
          </Alert>
        </Card>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <PageHeader
        title="AI Analytics &amp; Sentiment Intelligence Studio"
        subtitle={`NLP sentiment extraction, PII pre-sanitization, topic clustering, and LLM executive summaries for ${projectId}`}
      />

      {/* Scope Controls */}
      <Card variant="bordered" padding="16px">
        <div className="flex flex-wrap items-center justify-between gap-4">
          <div className="flex flex-wrap items-center gap-4">
            <div className="flex items-center gap-2">
              <label className="block text-[11px] font-bold text-slate-600 uppercase tracking-wider">
                Campaign ID
              </label>
              <input
                type="text"
                value={campaignId}
                onChange={(e) => setCampaignId(e.target.value)}
                placeholder="e.g. CMP-77102"
                className="bg-white border border-slate-300 rounded px-3 py-1.5 text-xs text-slate-800 font-mono focus:outline-none focus:border-indigo-600 w-36"
              />
            </div>

            <div className="flex items-center gap-2">
              <label className="block text-[11px] font-bold text-slate-600 uppercase tracking-wider">
                Node Scope (FR-AI-006)
              </label>
              <input
                type="text"
                value={nodeScope}
                onChange={(e) => setNodeScope(e.target.value)}
                placeholder="e.g. GLOBAL or N-301"
                className="bg-white border border-slate-300 rounded px-3 py-1.5 text-xs text-slate-800 font-mono focus:outline-none focus:border-indigo-600 w-36"
              />
            </div>

            <div className="flex items-center gap-2">
              <label className="block text-[11px] font-bold text-slate-600 uppercase tracking-wider">
                AI Provider (FR-AI-005)
              </label>
              <select
                value={providerName}
                onChange={(e) => setProviderName(e.target.value)}
                className="bg-white border border-slate-300 rounded px-3 py-1.5 text-xs font-semibold text-slate-800 focus:outline-none focus:border-indigo-600"
              >
                <option value="OPENAI">OpenAI GPT-4o</option>
                <option value="GEMINI">Google Gemini 1.5 Pro</option>
                <option value="VLLM">Local vLLM Llama 3</option>
              </select>
            </div>
          </div>

          <div className="text-xs text-slate-500">
            PII Pre-Sanitization Status: <span className="font-bold text-emerald-600">🛡️ Active (Regex + NER)</span>
          </div>
        </div>
      </Card>

      <Tabs tabs={tabs} activeTab={activeTab} onChange={setActiveTab} />

      <div className="mt-4">
        {activeTab === 'sentiment' && (
          <div>
            {isInsightsLoading ? (
              <Card variant="bordered" padding="24px">
                <div className="space-y-4">
                  <Skeleton height="32px" width="30%" />
                  <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
                    <Skeleton height="100px" borderRadius="12px" />
                    <Skeleton height="100px" borderRadius="12px" />
                    <Skeleton height="100px" borderRadius="12px" />
                  </div>
                  <Skeleton height="180px" borderRadius="12px" />
                </div>
              </Card>
            ) : isInsightsError ? (
              <Card variant="bordered" padding="24px">
                <ErrorState
                  title="AI Insights API Error"
                  message={
                    (insightsError as any)?.message ||
                    'Could not load NLP sentiment insights from ai-analytics-service.'
                  }
                  onRetry={refetchInsights}
                />
              </Card>
            ) : insightsData && insightsData.length === 0 ? (
              <Card variant="bordered" padding="24px">
                <EmptyState
                  title="No Ingested Qualitative Feedback"
                  description={`Campaign ${campaignId} has 0 ingested text comments. Sentiment scoring worker will process new open-ended responses asynchronously.`}
                />
              </Card>
            ) : insightsData ? (
              <div className="space-y-6">
                <WorkplaceRiskAlertBanner
                  alerts={extractedRiskAlerts}
                  onInvestigate={handleInvestigateRisk}
                />
                <SentimentAnalyticsPanel
                  insights={insightsData as any}
                  onOverrideTag={handleOverrideTag}
                />
              </div>
            ) : (
              <Card variant="bordered" padding="24px">
                <EmptyState
                  title="No AI Sentiment Data"
                  description="Please specify a valid Campaign ID to view sentiment intelligence metrics."
                />
              </Card>
            )}
          </div>
        )}

        {activeTab === 'summary' && (
          <div>
            {isSummaryLoading ? (
              <Card variant="bordered" padding="24px">
                <Skeleton height="280px" borderRadius="12px" />
              </Card>
            ) : isSummaryError ? (
              <Card variant="bordered" padding="24px">
                <ErrorState
                  title="Executive Summary API Error"
                  message="Could not load LLM executive summary report from ai-analytics-service."
                  onRetry={refetchSummary}
                />
              </Card>
            ) : summaryData ? (
              <ExecutiveSummaryCard
                summary={summaryData}
                onRegenerate={handleRegenerateSummary}
                isLoading={generateSummaryMutation.isPending}
              />
            ) : (
              <Card variant="bordered" padding="24px">
                <EmptyState
                  title="No LLM Executive Summary"
                  description="No executive summary generated for active scope. Click Regenerate to compile."
                />
              </Card>
            )}
          </div>
        )}
      </div>
    </div>
  );
};
