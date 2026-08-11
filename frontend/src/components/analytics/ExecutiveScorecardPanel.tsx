import React from 'react';
import { DashboardMetrics } from '../../types/analytics';
import { Card } from '../ui/Card';
import { Badge } from '../ui/Badge';

interface ExecutiveScorecardPanelProps {
  metrics: DashboardMetrics;
}

export const ExecutiveScorecardPanel: React.FC<ExecutiveScorecardPanelProps> = ({ metrics }) => {
  const isSuppressed = metrics.status === 'SUPPRESSED';

  const getEnpsZoneBadge = (enps: number | null | undefined) => {
    if (enps === null || enps === undefined) {
      return <Badge variant="neutral">Suppressed (N &lt; 5)</Badge>;
    }
    if (enps > 30) {
      return <Badge variant="success">Promoter Zone (&gt; +30)</Badge>;
    }
    if (enps >= 0) {
      return <Badge variant="warning">Passive Zone (0 to +30)</Badge>;
    }
    return <Badge variant="danger">Detractor Zone (&lt; 0)</Badge>;
  };

  return (
    <Card variant="bordered" padding="24px">
      <div className="space-y-6">
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-2 border-b border-slate-100 pb-4">
          <div>
            <h3 className="text-xl font-bold text-slate-900">
              Executive Engagement &amp; Sentiment Scorecard
            </h3>
            <p className="text-xs text-slate-500 mt-1">
              Real-time aggregate engagement index, eNPS score, and participation rates (Campaign: {metrics.campaignId}, Node: {metrics.nodeId})
            </p>
          </div>
          {isSuppressed && (
            <Badge variant="warning">
              Differential Privacy Active (N &lt; 5)
            </Badge>
          )}
        </div>

        {/* Top Key Metrics Cards */}
        <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
          {/* Overall Engagement Index */}
          <div className="bg-slate-50 border border-slate-200 p-5 rounded-xl text-center space-y-1">
            <div className="text-2xl font-extrabold text-blue-700">
              {metrics.engagementIndex !== null && metrics.engagementIndex !== undefined
                ? `${metrics.engagementIndex.toFixed(1)}%`
                : '—'}
            </div>
            <div className="text-xs font-semibold text-slate-600">
              Overall Engagement Index
            </div>
            <div className="text-[11px] text-slate-400">100-Point Normalized Scale</div>
          </div>

          {/* eNPS Metric */}
          <div className="bg-emerald-50/50 border border-emerald-200 p-5 rounded-xl text-center space-y-1">
            <div className="text-2xl font-extrabold text-emerald-800">
              {metrics.eNPS !== null && metrics.eNPS !== undefined
                ? `${metrics.eNPS > 0 ? '+' : ''}${metrics.eNPS.toFixed(1)}`
                : '—'}
            </div>
            <div className="text-xs font-semibold text-emerald-900">
              Employee Net Promoter (eNPS)
            </div>
            <div className="mt-1 flex justify-center">
              {getEnpsZoneBadge(metrics.eNPS)}
            </div>
          </div>

          {/* Participation Rate */}
          <div className="bg-indigo-50/50 border border-indigo-200 p-5 rounded-xl text-center space-y-1">
            <div className="text-2xl font-extrabold text-indigo-800">
              {metrics.participationRate.toFixed(1)}%
            </div>
            <div className="text-xs font-semibold text-indigo-900">
              Participation Rate
            </div>
            <div className="text-[11px] text-slate-500">
              {metrics.totalResponses.toLocaleString()} Total Submissions
            </div>
          </div>
        </div>

        {/* Dimension Group Scores Breakdown */}
        {metrics.groupScores && metrics.groupScores.length > 0 && (
          <div className="space-y-3">
            <h4 className="text-sm font-bold text-slate-800 uppercase tracking-wider">
              Category &amp; Theme Dimension Scores
            </h4>
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-3">
              {metrics.groupScores.map((group) => (
                <div
                  key={group.groupId}
                  className="bg-white border border-slate-200 rounded-lg p-3 flex justify-between items-center shadow-sm hover:border-slate-300 transition"
                >
                  <div className="space-y-0.5">
                    <div className="font-bold text-xs text-slate-900">
                      {group.groupName || group.groupId}
                    </div>
                    <div className="text-[11px] text-slate-500">
                      Sample Size: N = {group.sampleSize}
                    </div>
                  </div>

                  <div>
                    {group.score !== null && group.status !== 'SUPPRESSED' ? (
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
