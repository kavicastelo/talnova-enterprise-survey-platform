import React from 'react';

export interface KpiData {
  totalResponses: number;
  participationRate: number;
  eNPS: number;
  engagementIndex: number;
  longitudinalDelta: {
    enpsDelta: number;
    engagementIndexDelta: number;
    trendDirection: 'UP' | 'DOWN' | 'STABLE';
    baselineCampaignName: string;
  };
  trendHistory: Array<{
    campaignName: string;
    eNPS: number;
    engagementIndex: number;
  }>;
}

interface ExecutiveKpiScorecardProps {
  data: KpiData;
  isLoading?: boolean;
}

export const ExecutiveKpiScorecard: React.FC<ExecutiveKpiScorecardProps> = ({ data, isLoading }) => {
  if (isLoading) {
    return (
      <div className="p-6 bg-slate-900 border border-slate-800 rounded-xl animate-pulse text-white">
        Loading Executive KPI Scorecard...
      </div>
    );
  }

  const getEnpsColorClass = (score: number) => {
    if (score > 30) return 'text-emerald-400 bg-emerald-950/60 border-emerald-800';
    if (score >= 0) return 'text-amber-400 bg-amber-950/60 border-amber-800';
    return 'text-rose-400 bg-rose-950/60 border-rose-800';
  };

  const getTrendBadge = (direction: 'UP' | 'DOWN' | 'STABLE', delta: number) => {
    if (direction === 'UP') {
      return (
        <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-emerald-900/80 text-emerald-300 border border-emerald-700">
          ▲ +{delta.toFixed(1)}% vs Baseline
        </span>
      );
    }
    if (direction === 'DOWN') {
      return (
        <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-rose-900/80 text-rose-300 border border-rose-700">
          ▼ {delta.toFixed(1)}% vs Baseline
        </span>
      );
    }
    return (
      <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-slate-800 text-slate-300 border border-slate-700">
        ➔ {delta >= 0 ? '+' : ''}{delta.toFixed(1)}% Stable
      </span>
    );
  };

  return (
    <div className="space-y-6">
      {/* 4 KPI Scorecard Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        {/* KPI 1: Total Responses */}
        <div className="p-5 bg-slate-900 border border-slate-800 rounded-xl shadow-lg hover:border-slate-700 transition">
          <div className="text-xs font-semibold text-slate-400 uppercase tracking-wider">Total Responses</div>
          <div className="mt-2 text-3xl font-extrabold text-white">
            {data.totalResponses.toLocaleString()}
          </div>
          <div className="mt-2 text-xs text-slate-400 flex items-center gap-2">
            <span className="px-2 py-0.5 bg-blue-950 text-blue-400 rounded border border-blue-800 font-medium">
              {data.participationRate.toFixed(1)}% Participation
            </span>
          </div>
        </div>

        {/* KPI 2: eNPS Score (-100 to +100) */}
        <div className="p-5 bg-slate-900 border border-slate-800 rounded-xl shadow-lg hover:border-slate-700 transition">
          <div className="text-xs font-semibold text-slate-400 uppercase tracking-wider">eNPS Score</div>
          <div className="mt-2 flex items-baseline gap-3">
            <span className="text-3xl font-extrabold text-white">
              {data.eNPS > 0 ? `+${data.eNPS.toFixed(1)}` : data.eNPS.toFixed(1)}
            </span>
            <span className={`text-xs px-2 py-0.5 rounded border font-semibold ${getEnpsColorClass(data.eNPS)}`}>
              {data.eNPS > 30 ? 'Promoter Zone' : data.eNPS >= 0 ? 'Passive Zone' : 'Detractor Zone'}
            </span>
          </div>
          <div className="mt-2 text-xs text-slate-500">Scale: -100 to +100</div>
        </div>

        {/* KPI 3: Engagement Index (100-Point Normalized) */}
        <div className="p-5 bg-slate-900 border border-slate-800 rounded-xl shadow-lg hover:border-slate-700 transition">
          <div className="text-xs font-semibold text-slate-400 uppercase tracking-wider">Engagement Index</div>
          <div className="mt-2 text-3xl font-extrabold text-indigo-400">
            {data.engagementIndex.toFixed(1)}%
          </div>
          <div className="mt-2 w-full bg-slate-800 rounded-full h-2 overflow-hidden">
            <div
              className="bg-indigo-500 h-2 rounded-full transition-all duration-500"
              style={{ width: `${Math.min(100, Math.max(0, data.engagementIndex))}%` }}
            />
          </div>
          <div className="mt-1 text-xs text-slate-500 flex justify-between">
            <span>Target: 75.0%</span>
            <span>{data.engagementIndex >= 75.0 ? 'Target Met' : 'Below Target'}</span>
          </div>
        </div>

        {/* KPI 4: Longitudinal Delta */}
        <div className="p-5 bg-slate-900 border border-slate-800 rounded-xl shadow-lg hover:border-slate-700 transition">
          <div className="text-xs font-semibold text-slate-400 uppercase tracking-wider">Longitudinal Delta</div>
          <div className="mt-2">
            {getTrendBadge(data.longitudinalDelta.trendDirection, data.longitudinalDelta.enpsDelta)}
          </div>
          <div className="mt-3 text-xs text-slate-400">
            Compared to <span className="font-semibold text-slate-300">{data.longitudinalDelta.baselineCampaignName}</span>
          </div>
        </div>
      </div>

      {/* Trend Chart Card */}
      <div className="p-6 bg-slate-900 border border-slate-800 rounded-xl shadow-lg">
        <h3 className="text-sm font-bold text-slate-200 uppercase tracking-wider mb-4">
          Historical Engagement Trend Progression
        </h3>
        <div className="h-48 flex items-end justify-between gap-4 pt-6 px-4 border-b border-slate-800">
          {data.trendHistory.map((item, idx) => (
            <div key={idx} className="flex-1 flex flex-col items-center gap-2 group">
              <div className="text-xs font-semibold text-indigo-400 opacity-0 group-hover:opacity-100 transition">
                {item.engagementIndex.toFixed(1)}%
              </div>
              <div
                className="w-full bg-indigo-600/80 group-hover:bg-indigo-500 rounded-t transition-all duration-300"
                style={{ height: `${(item.engagementIndex / 100) * 120}px` }}
              />
              <div className="text-xs text-slate-400 truncate w-full text-center mt-1">
                {item.campaignName}
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};
