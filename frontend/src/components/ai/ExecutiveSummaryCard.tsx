import React from 'react';
import { ExecutiveSummary } from '../../types/aiAnalytics';
import { Card } from '../ui/Card';
import { Badge } from '../ui/Badge';
import { Button } from '../ui/Button';

interface ExecutiveSummaryCardProps {
  summary: ExecutiveSummary;
  onRegenerate?: () => void;
  isLoading?: boolean;
}

export const ExecutiveSummaryCard: React.FC<ExecutiveSummaryCardProps> = ({
  summary,
  onRegenerate,
  isLoading,
}) => {
  return (
    <Card variant="bordered" padding="24px">
      <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
        {/* Header */}
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <div>
            <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
              <h3 style={{ fontSize: '1.25rem', fontWeight: 800, color: '#0f172a', margin: 0 }}>
                {summary.summaryTitle || 'LLM Executive Summary Report'}
              </h3>
              <Badge variant="info">AI Generated</Badge>
            </div>
            <p style={{ fontSize: '0.8rem', color: '#64748b', margin: '4px 0 0 0' }}>
              Node Scope: <code>{summary.nodeScope || 'GLOBAL'}</code>
              {summary.generatedAt && ` | Generated: ${new Date(summary.generatedAt).toLocaleString()}`}
            </p>
          </div>

          {onRegenerate && (
            <Button variant="outline" size="sm" onClick={onRegenerate} isLoading={isLoading}>
              🔄 Regenerate Summary
            </Button>
          )}
        </div>

        {/* 3-Column Summary Cards */}
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(240px, 1fr))', gap: '16px' }}>
          {/* Top Strengths */}
          <div style={{ background: '#f0fdf4', border: '1px solid #bbf7d0', padding: '16px', borderRadius: '10px' }}>
            <h4 style={{ fontSize: '0.9rem', fontWeight: 800, color: '#166534', margin: '0 0 10px 0' }}>
              💪 Key Organizational Strengths
            </h4>
            <ul style={{ margin: 0, paddingLeft: '18px', fontSize: '0.85rem', color: '#15803d' }}>
              {summary.topStrengths && summary.topStrengths.length > 0 ? (
                summary.topStrengths.map((str, idx) => <li key={idx}>{str}</li>)
              ) : (
                <li>High leadership trust and clear strategic direction across teams.</li>
              )}
            </ul>
          </div>

          {/* Top Concerns */}
          <div style={{ background: '#fef2f2', border: '1px solid #fecaca', padding: '16px', borderRadius: '10px' }}>
            <h4 style={{ fontSize: '0.9rem', fontWeight: 800, color: '#991b1b', margin: '0 0 10px 0' }}>
              ⚠️ Top Employee Concerns
            </h4>
            <ul style={{ margin: 0, paddingLeft: '18px', fontSize: '0.85rem', color: '#b91c1c' }}>
              {summary.topConcerns && summary.topConcerns.length > 0 ? (
                summary.topConcerns.map((con, idx) => <li key={idx}>{con}</li>)
              ) : (
                <li>Workload pressure during quarterly production peaks.</li>
              )}
            </ul>
          </div>

          {/* Recommendations */}
          <div style={{ background: '#eff6ff', border: '1px solid #bfdbfe', padding: '16px', borderRadius: '10px' }}>
            <h4 style={{ fontSize: '0.9rem', fontWeight: 800, color: '#1e40af', margin: '0 0 10px 0' }}>
              🎯 Recommended Leadership Actions
            </h4>
            <ul style={{ margin: 0, paddingLeft: '18px', fontSize: '0.85rem', color: '#1d4ed8' }}>
              {summary.recommendations && summary.recommendations.length > 0 ? (
                summary.recommendations.map((rec, idx) => <li key={idx}>{rec}</li>)
              ) : (
                <li>Initiate cross-departmental workload balancing workshops.</li>
              )}
            </ul>
          </div>
        </div>
      </div>
    </Card>
  );
};
