import React from 'react';
import { DashboardMetrics } from '../../types/analytics';
import { Card } from '../ui/Card';
import { Badge } from '../ui/Badge';

interface ExecutiveScorecardPanelProps {
  metrics: DashboardMetrics;
}

export const ExecutiveScorecardPanel: React.FC<ExecutiveScorecardPanelProps> = ({ metrics }) => {
  return (
    <Card variant="bordered" padding="24px">
      <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
        <div>
          <h3 style={{ fontSize: '1.25rem', fontWeight: 800, color: '#0f172a', margin: 0 }}>
            Executive Engagement & Sentiment Scorecard
          </h3>
          <p style={{ fontSize: '0.85rem', color: '#64748b', margin: '4px 0 0 0' }}>
            Real-time aggregate engagement index, eNPS metric, and participation rates.
          </p>
        </div>

        {/* Top Key Metrics Cards */}
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(160px, 1fr))', gap: '16px' }}>
          <div style={{ background: '#f8fafc', border: '1px solid #e2e8f0', padding: '16px', borderRadius: '12px', textAlign: 'center' }}>
            <div style={{ fontSize: '1.75rem', fontWeight: 800, color: '#1e40af' }}>
              {metrics.engagementIndex !== null && metrics.engagementIndex !== undefined
                ? `${metrics.engagementIndex.toFixed(1)}%`
                : '—'}
            </div>
            <div style={{ fontSize: '0.8rem', fontWeight: 600, color: '#64748b', marginTop: '4px' }}>
              Overall Engagement Index
            </div>
          </div>

          <div style={{ background: '#f0fdf4', border: '1px solid #bbf7d0', padding: '16px', borderRadius: '12px', textAlign: 'center' }}>
            <div style={{ fontSize: '1.75rem', fontWeight: 800, color: '#166534' }}>
              {metrics.eNPS !== null && metrics.eNPS !== undefined
                ? `${metrics.eNPS > 0 ? '+' : ''}${metrics.eNPS.toFixed(0)}`
                : '—'}
            </div>
            <div style={{ fontSize: '0.8rem', fontWeight: 600, color: '#15803d', marginTop: '4px' }}>
              Employee Net Promoter (eNPS)
            </div>
          </div>

          <div style={{ background: '#eff6ff', border: '1px solid #bfdbfe', padding: '16px', borderRadius: '12px', textAlign: 'center' }}>
            <div style={{ fontSize: '1.75rem', fontWeight: 800, color: '#1e293b' }}>
              {metrics.participationRate.toFixed(1)}%
            </div>
            <div style={{ fontSize: '0.8rem', fontWeight: 600, color: '#475569', marginTop: '4px' }}>
              Participation Rate ({metrics.totalResponses} Total)
            </div>
          </div>
        </div>

        {/* Dimension Group Scores Breakdown */}
        {metrics.groupScores && metrics.groupScores.length > 0 && (
          <div>
            <h4 style={{ fontSize: '0.95rem', fontWeight: 700, color: '#0f172a', marginBottom: '12px' }}>
              Category & Theme Dimension Scores
            </h4>
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))', gap: '12px' }}>
              {metrics.groupScores.map((group) => (
                <div
                  key={group.groupId}
                  style={{
                    background: '#ffffff',
                    border: '1px solid #cbd5e1',
                    borderRadius: '8px',
                    padding: '12px 16px',
                    display: 'flex',
                    justifyContent: 'space-between',
                    alignItems: 'center',
                  }}
                >
                  <div>
                    <div style={{ fontWeight: 700, fontSize: '0.9rem', color: '#0f172a' }}>
                      {group.groupName || group.groupId}
                    </div>
                    <div style={{ fontSize: '0.75rem', color: '#64748b' }}>
                      Sample Size: {group.sampleSize} Responses
                    </div>
                  </div>

                  <div>
                    {group.score !== null ? (
                      <Badge
                        variant={
                          group.colorIntensity === 'GREEN'
                            ? 'success'
                            : group.colorIntensity === 'YELLOW'
                            ? 'warning'
                            : group.colorIntensity === 'RED'
                            ? 'danger'
                            : 'neutral'
                        }
                      >
                        {group.score.toFixed(1)}%
                      </Badge>
                    ) : (
                      <Badge variant="neutral">Suppressed (N &lt; 5)</Badge>
                    )}
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}
      </div>
    </Card>
  );
};
