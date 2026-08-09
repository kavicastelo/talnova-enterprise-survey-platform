import React, { useState } from 'react';
import { PageHeader } from '../../../components/ui/PageHeader';
import { Tabs } from '../../../components/ui/Tabs';
import { Card } from '../../../components/ui/Card';
import { Skeleton } from '../../../components/ui/Skeleton';
import { ErrorState } from '../../../components/ui/ErrorState';
import { SentimentAnalyticsPanel } from '../../../components/ai/SentimentAnalyticsPanel';
import { ExecutiveSummaryCard } from '../../../components/ai/ExecutiveSummaryCard';
import { useTenant } from '../../../context/TenantContext';
import {
  useCampaignAiInsightsQuery,
  useExecutiveSummaryQuery,
  useOverrideSentimentMutation,
  useGenerateSummaryMutation,
} from '../api/useAiAnalyticsQueries';

export const AiAnalyticsPage: React.FC = () => {
  const { activeProject } = useTenant();
  const [activeTab, setActiveTab] = useState<string>('sentiment');

  const campaignId = 'CMP-77102';
  const projectId = activeProject?.projectId || 'PRJ-99201';

  const {
    data: insightsData,
    isLoading: isInsightsLoading,
    isError: isInsightsError,
    refetch: refetchInsights,
  } = useCampaignAiInsightsQuery(projectId, campaignId);

  const {
    data: summaryData,
    isLoading: isSummaryLoading,
    isError: isSummaryError,
    refetch: refetchSummary,
  } = useExecutiveSummaryQuery(campaignId, 'GLOBAL');

  const overrideMutation = useOverrideSentimentMutation();
  const generateSummaryMutation = useGenerateSummaryMutation();

  const handleOverrideTag = (insightId: string, newLabel: 'POSITIVE' | 'NEUTRAL' | 'NEGATIVE') => {
    overrideMutation.mutate({
      insightId,
      payload: {
        overriddenBy: 'ADMIN_USER_88',
        newLabel,
        reason: 'Human analyst calibration',
      },
      projectId,
      campaignId,
    });
  };

  const handleRegenerateSummary = () => {
    generateSummaryMutation.mutate({
      nodeScope: 'GLOBAL',
      providerName: 'OPENAI',
    });
  };

  // Demo fallback insights
  const fallbackInsights: any[] = [
    {
      id: 'INSIGHT-001',
      sanitizedText: 'Workload is manageable but deadlines are tight.',
      sentimentScore: 0.25,
      sentimentLabel: 'NEUTRAL' as const,
      confidence: 0.92,
      themes: ['Workload Audit', 'Deadlines'],
      riskSeverity: 'LOW' as const,
    },
    {
      id: 'INSIGHT-002',
      sanitizedText: 'Working with [MASKED_NAME] was a great and excellent experience!',
      sentimentScore: 0.85,
      sentimentLabel: 'POSITIVE' as const,
      confidence: 0.95,
      themes: ['Team Collaboration', 'Development Workshops'],
      riskSeverity: 'LOW' as const,
    },
    {
      id: 'INSIGHT-003',
      sanitizedText: 'Communication from management is terrible and very poor overall.',
      sentimentScore: -0.75,
      sentimentLabel: 'NEGATIVE' as const,
      confidence: 0.91,
      themes: ['Management Communication'],
      riskSeverity: 'MEDIUM' as const,
    },
  ];

  const fallbackSummary = {
    nodeScope: 'GLOBAL',
    summaryTitle: 'Q3 Enterprise Workforce Culture Audit',
    topStrengths: [
      'Strong managerial leadership trust across corporate nodes.',
      'High collaboration index in engineering teams.',
    ],
    topConcerns: [
      'Workload bottlenecks in maritime logistics unit.',
      'Perceived delays in compensation transparency.',
    ],
    recommendations: [
      'Schedule quarterly workload balancing workshops.',
      'Enhance internal communications regarding career mobility paths.',
    ],
    generatedAt: new Date().toISOString(),
  };

  const tabs = [
    { id: 'sentiment', label: 'AI Sentiment & Topic Studio' },
    { id: 'summary', label: 'LLM Executive Summaries' },
  ];

  return (
    <div>
      <PageHeader
        title="AI Analytics & Sentiment Intelligence Studio"
        subtitle={`NLP sentiment extraction, topic clustering, and LLM executive summaries for ${projectId}`}
      />

      <Tabs tabs={tabs} activeTab={activeTab} onChange={setActiveTab} />

      <div style={{ marginTop: '20px' }}>
        {activeTab === 'sentiment' && (
          <div>
            {isInsightsLoading ? (
              <Card variant="bordered" padding="24px">
                <Skeleton height="320px" borderRadius="12px" />
              </Card>
            ) : isInsightsError ? (
              <Card variant="bordered" padding="24px">
                <ErrorState
                  title="AI Insights Unavailable"
                  message="Could not load NLP sentiment insights from ai-analytics-service."
                  onRetry={refetchInsights}
                />
              </Card>
            ) : (
              <SentimentAnalyticsPanel
                insights={(insightsData as any) || fallbackInsights}
                onOverrideTag={handleOverrideTag}
              />
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
                  title="Executive Summary Unavailable"
                  message="Could not load LLM executive summary report from ai-analytics-service."
                  onRetry={refetchSummary}
                />
              </Card>
            ) : (
              <ExecutiveSummaryCard
                summary={summaryData || fallbackSummary}
                onRegenerate={handleRegenerateSummary}
                isLoading={generateSummaryMutation.isPending}
              />
            )}
          </div>
        )}
      </div>
    </div>
  );
};
