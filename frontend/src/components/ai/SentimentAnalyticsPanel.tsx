import React, { useState } from 'react';

export interface ThemeCluster {
  themeName: string;
  frequency: number;
  dominantSentiment: 'POSITIVE' | 'NEUTRAL' | 'NEGATIVE';
}

export interface SentimentInsight {
  id: string;
  sanitizedText: string;
  sentimentScore: number;
  sentimentLabel: 'POSITIVE' | 'NEUTRAL' | 'NEGATIVE';
  confidence: number;
  themes: string[];
  riskSeverity?: 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
}

interface SentimentAnalyticsPanelProps {
  insights?: SentimentInsight[];
  themeClusters?: ThemeCluster[];
  onSelectTheme?: (themeName: string | null) => void;
  onOverrideTag?: (insightId: string, newLabel: 'POSITIVE' | 'NEUTRAL' | 'NEGATIVE') => void;
}

export const SentimentAnalyticsPanel: React.FC<SentimentAnalyticsPanelProps> = ({
  insights = [],
  themeClusters,
  onSelectTheme,
  onOverrideTag
}) => {
  const [selectedTheme, setSelectedTheme] = useState<string | null>(null);

  const positiveCount = insights.filter(i => i.sentimentLabel === 'POSITIVE').length;
  const neutralCount = insights.filter(i => i.sentimentLabel === 'NEUTRAL').length;
  const negativeCount = insights.filter(i => i.sentimentLabel === 'NEGATIVE').length;
  const total = insights.length || 1;

  const posPct = Math.round((positiveCount / total) * 100);
  const neuPct = Math.round((neutralCount / total) * 100);
  const negPct = Math.round((negativeCount / total) * 100);

  const avgPolarity = (
    insights.reduce((acc, curr) => acc + (curr.sentimentScore || 0), 0) / total
  ).toFixed(2);

  // Compute theme clusters dynamically if not passed directly
  const derivedThemeClusters: ThemeCluster[] = themeClusters || (() => {
    const counts: Record<string, { count: number; pos: number; neg: number }> = {};
    insights.forEach((i) => {
      (i.themes || []).forEach((t) => {
        if (!counts[t]) counts[t] = { count: 0, pos: 0, neg: 0 };
        counts[t].count += 1;
        if (i.sentimentLabel === 'POSITIVE') counts[t].pos += 1;
        if (i.sentimentLabel === 'NEGATIVE') counts[t].neg += 1;
      });
    });

    return Object.entries(counts).map(([themeName, val]) => ({
      themeName,
      frequency: val.count,
      dominantSentiment: (val.pos >= val.neg
        ? val.pos > 0
          ? 'POSITIVE'
          : 'NEUTRAL'
        : 'NEGATIVE') as 'POSITIVE' | 'NEUTRAL' | 'NEGATIVE',
    })).sort((a, b) => b.frequency - a.frequency);
  })();

  const handleThemeClick = (themeName: string) => {
    const newTheme = selectedTheme === themeName ? null : themeName;
    setSelectedTheme(newTheme);
    if (onSelectTheme) onSelectTheme(newTheme);
  };

  const filteredInsights = selectedTheme
    ? insights.filter(i => (i.themes || []).includes(selectedTheme))
    : insights;

  const hasPiiMasking = (text: string) => {
    return text.includes('[MASKED_NAME]') || text.includes('[MASKED_EMAIL]') || text.includes('[MASKED_PHONE]');
  };

  return (
    <div className="bg-white rounded-xl shadow-sm border border-slate-200 p-6 space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 border-b border-slate-100 pb-4">
        <div>
          <h2 className="text-xl font-bold text-slate-900">AI Sentiment &amp; Topic Analytics (FEAT-008)</h2>
          <p className="text-sm text-slate-500">NLP sentiment extraction, PII pre-sanitization, and topic clustering</p>
        </div>
        <div className="flex items-center space-x-2 bg-slate-100 px-3 py-1.5 rounded-lg border border-slate-200 text-xs font-semibold text-slate-700">
          <span>Average Polarity:</span>
          <span className={Number(avgPolarity) >= 0 ? 'text-emerald-600 font-bold' : 'text-rose-600 font-bold'}>
            {Number(avgPolarity) > 0 ? `+${avgPolarity}` : avgPolarity}
          </span>
        </div>
      </div>

      {/* Donut Gauges */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        <div className="p-4 rounded-xl bg-emerald-50/60 border border-emerald-200 text-center space-y-1">
          <div className="text-3xl font-extrabold text-emerald-800">{posPct}%</div>
          <div className="text-xs font-bold text-emerald-900 uppercase tracking-wider">Positive Sentiment</div>
          <div className="text-xs text-emerald-700 font-medium">{positiveCount} comments</div>
        </div>

        <div className="p-4 rounded-xl bg-slate-50 border border-slate-200 text-center space-y-1">
          <div className="text-3xl font-extrabold text-slate-800">{neuPct}%</div>
          <div className="text-xs font-bold text-slate-700 uppercase tracking-wider">Neutral Sentiment</div>
          <div className="text-xs text-slate-500 font-medium">{neutralCount} comments</div>
        </div>

        <div className="p-4 rounded-xl bg-rose-50/60 border border-rose-200 text-center space-y-1">
          <div className="text-3xl font-extrabold text-rose-800">{negPct}%</div>
          <div className="text-xs font-bold text-rose-900 uppercase tracking-wider">Negative Sentiment</div>
          <div className="text-xs text-rose-700 font-medium">{negativeCount} comments</div>
        </div>
      </div>

      {/* Interactive Theme Topic Cloud */}
      {derivedThemeClusters.length > 0 && (
        <div className="space-y-2">
          <h3 className="text-xs font-bold text-slate-700 uppercase tracking-wider">
            Interactive Topic Cloud (US-AI-003)
          </h3>
          <div className="flex flex-wrap gap-2">
            {derivedThemeClusters.map((cluster) => {
              const isSelected = selectedTheme === cluster.themeName;
              return (
                <button
                  key={cluster.themeName}
                  onClick={() => handleThemeClick(cluster.themeName)}
                  className={`px-3 py-1.5 rounded-full text-xs font-semibold border transition-all ${
                    isSelected
                      ? 'bg-indigo-600 text-white border-indigo-600 shadow-sm'
                      : 'bg-slate-50 text-slate-700 border-slate-200 hover:bg-slate-100'
                  }`}
                >
                  <span>{cluster.themeName}</span>
                  <span className="ml-1.5 text-[10px] px-1.5 py-0.5 rounded-full bg-slate-200 text-slate-700">
                    {cluster.frequency}
                  </span>
                </button>
              );
            })}
          </div>
        </div>
      )}

      {/* Filtered Comments List */}
      <div className="space-y-3">
        <div className="flex justify-between items-center">
          <h3 className="text-xs font-bold text-slate-700 uppercase tracking-wider">
            Comments {selectedTheme ? `Filtered by '${selectedTheme}'` : 'All Comments'} ({filteredInsights.length})
          </h3>
          {selectedTheme && (
            <button
              onClick={() => setSelectedTheme(null)}
              className="text-xs text-indigo-600 font-semibold hover:underline"
            >
              Clear Theme Filter
            </button>
          )}
        </div>

        {filteredInsights.length === 0 ? (
          <div className="p-8 text-center bg-slate-50 border border-dashed border-slate-300 rounded-xl text-slate-500 text-xs">
            No qualitative comments found for active selection.
          </div>
        ) : (
          filteredInsights.map((insight) => (
            <div
              key={insight.id}
              className="p-4 rounded-xl border border-slate-200 bg-slate-50/50 flex flex-col md:flex-row items-start md:items-center justify-between gap-4 hover:border-slate-300 transition"
            >
              <div className="space-y-1.5 flex-1">
                <div className="flex flex-wrap items-center gap-2">
                  <p className="text-sm font-medium text-slate-900 italic">"{insight.sanitizedText}"</p>
                  {hasPiiMasking(insight.sanitizedText) && (
                    <span className="px-2 py-0.5 rounded bg-blue-100 text-blue-800 text-[10px] font-bold border border-blue-200">
                      🛡️ PII Masked (US-AI-001)
                    </span>
                  )}
                </div>

                <div className="flex flex-wrap items-center gap-2 text-xs">
                  <span
                    className={`px-2.5 py-0.5 rounded font-bold text-[11px] border ${
                      insight.sentimentLabel === 'POSITIVE'
                        ? 'bg-emerald-100 text-emerald-900 border-emerald-300'
                        : insight.sentimentLabel === 'NEGATIVE'
                        ? 'bg-rose-100 text-rose-900 border-rose-300'
                        : 'bg-slate-200 text-slate-800 border-slate-300'
                    }`}
                  >
                    {insight.sentimentLabel} ({insight.sentimentScore > 0 ? `+${insight.sentimentScore}` : insight.sentimentScore})
                  </span>

                  <span className="text-slate-500 text-[11px]">
                    Confidence: {(insight.confidence * 100).toFixed(0)}%
                  </span>

                  {(insight.themes || []).map((theme) => (
                    <span key={theme} className="px-2 py-0.5 bg-slate-200 text-slate-700 rounded text-[10px] font-medium">
                      {theme}
                    </span>
                  ))}
                </div>
              </div>

              {onOverrideTag && (
                <div className="flex items-center space-x-1.5 shrink-0">
                  <button
                    onClick={() => onOverrideTag(insight.id, 'POSITIVE')}
                    className="px-2.5 py-1 text-[11px] font-bold bg-emerald-50 text-emerald-700 rounded-lg border border-emerald-200 hover:bg-emerald-100 transition"
                  >
                    Override +
                  </button>
                  <button
                    onClick={() => onOverrideTag(insight.id, 'NEUTRAL')}
                    className="px-2.5 py-1 text-[11px] font-bold bg-slate-100 text-slate-700 rounded-lg border border-slate-300 hover:bg-slate-200 transition"
                  >
                    Override =
                  </button>
                  <button
                    onClick={() => onOverrideTag(insight.id, 'NEGATIVE')}
                    className="px-2.5 py-1 text-[11px] font-bold bg-rose-50 text-rose-700 rounded-lg border border-rose-200 hover:bg-rose-100 transition"
                  >
                    Override -
                  </button>
                </div>
              )}
            </div>
          ))
        )}
      </div>
    </div>
  );
};

export default SentimentAnalyticsPanel;
