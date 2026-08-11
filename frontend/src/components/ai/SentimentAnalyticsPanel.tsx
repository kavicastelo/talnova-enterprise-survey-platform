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
  insights = [
    {
      id: 'INSIGHT-001',
      sanitizedText: 'Workload is manageable but deadlines are tight.',
      sentimentScore: 0.25,
      sentimentLabel: 'NEUTRAL',
      confidence: 0.92,
      themes: ['Workload Audit', 'Deadlines'],
      riskSeverity: 'LOW'
    },
    {
      id: 'INSIGHT-002',
      sanitizedText: 'Working with [MASKED_NAME] was a great and excellent experience!',
      sentimentScore: 0.85,
      sentimentLabel: 'POSITIVE',
      confidence: 0.95,
      themes: ['Team Collaboration', 'Development Workshops'],
      riskSeverity: 'LOW'
    },
    {
      id: 'INSIGHT-003',
      sanitizedText: 'Communication from management is terrible and very poor overall.',
      sentimentScore: -0.75,
      sentimentLabel: 'NEGATIVE',
      confidence: 0.91,
      themes: ['Management Communication'],
      riskSeverity: 'MEDIUM'
    }
  ],
  themeClusters = [
    { themeName: 'Workload Audit', frequency: 12, dominantSentiment: 'NEUTRAL' },
    { themeName: 'Development Workshops', frequency: 18, dominantSentiment: 'POSITIVE' },
    { themeName: 'Management Communication', frequency: 8, dominantSentiment: 'NEGATIVE' },
    { themeName: 'Workplace Safety', frequency: 4, dominantSentiment: 'NEGATIVE' },
    { themeName: 'Team Collaboration', frequency: 15, dominantSentiment: 'POSITIVE' }
  ],
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
    insights.reduce((acc, curr) => acc + curr.sentimentScore, 0) / total
  ).toFixed(2);

  const handleThemeClick = (themeName: string) => {
    const newTheme = selectedTheme === themeName ? null : themeName;
    setSelectedTheme(newTheme);
    if (onSelectTheme) onSelectTheme(newTheme);
  };

  const filteredInsights = selectedTheme
    ? insights.filter(i => i.themes.includes(selectedTheme))
    : insights;

  return (
    <div className="bg-white rounded-xl shadow-sm border border-slate-200 p-6 space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-xl font-bold text-slate-900">AI Sentiment & Topic Analytics</h2>
          <p className="text-sm text-slate-500">Qualitative comment analysis, multi-lingual sentiment, and topic clustering</p>
        </div>
        <div className="flex items-center space-x-2 bg-slate-100 px-3 py-1.5 rounded-lg border border-slate-200 text-xs font-semibold text-slate-700">
          <span>Avg Polarity:</span>
          <span className={Number(avgPolarity) >= 0 ? 'text-emerald-600 font-bold' : 'text-rose-600 font-bold'}>
            {avgPolarity}
          </span>
        </div>
      </div>

      {/* Donut Gauges */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div className="p-4 rounded-lg bg-emerald-50 border border-emerald-200 text-center">
          <div className="text-2xl font-black text-emerald-700">{posPct}%</div>
          <div className="text-xs font-semibold text-emerald-800 uppercase tracking-wide">Positive Sentiment</div>
          <div className="text-xs text-emerald-600 mt-1">{positiveCount} comments</div>
        </div>

        <div className="p-4 rounded-lg bg-slate-50 border border-slate-200 text-center">
          <div className="text-2xl font-black text-slate-700">{neuPct}%</div>
          <div className="text-xs font-semibold text-slate-800 uppercase tracking-wide">Neutral Sentiment</div>
          <div className="text-xs text-slate-600 mt-1">{neutralCount} comments</div>
        </div>

        <div className="p-4 rounded-lg bg-rose-50 border border-rose-200 text-center">
          <div className="text-2xl font-black text-rose-700">{negPct}%</div>
          <div className="text-xs font-semibold text-rose-800 uppercase tracking-wide">Negative Sentiment</div>
          <div className="text-xs text-rose-600 mt-1">{negativeCount} comments</div>
        </div>
      </div>

      {/* Interactive Theme Topic Cloud */}
      <div>
        <h3 className="text-sm font-bold text-slate-800 uppercase tracking-wider mb-3">Interactive Topic Cloud (US-AI-003)</h3>
        <div className="flex flex-wrap gap-2">
          {themeClusters.map((cluster) => {
            const isSelected = selectedTheme === cluster.themeName;
            return (
              <button
                key={cluster.themeName}
                onClick={() => handleThemeClick(cluster.themeName)}
                className={`px-3 py-1.5 rounded-full text-xs font-medium border transition-all ${
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

      {/* Filtered Comments List */}
      <div className="space-y-3">
        <h3 className="text-sm font-bold text-slate-800 uppercase tracking-wider">
          Comments {selectedTheme ? `Filtered by '${selectedTheme}'` : 'All Comments'} ({filteredInsights.length})
        </h3>
        {filteredInsights.map((insight) => (
          <div key={insight.id} className="p-3.5 rounded-lg border border-slate-100 bg-slate-50/50 flex flex-col md:flex-row items-start md:items-center justify-between gap-3">
            <div className="space-y-1">
              <p className="text-sm text-slate-800 italic">"{insight.sanitizedText}"</p>
              <div className="flex items-center space-x-2 text-xs">
                <span className={`px-2 py-0.5 rounded font-semibold text-[11px] ${
                  insight.sentimentLabel === 'POSITIVE' ? 'bg-emerald-100 text-emerald-800' :
                  insight.sentimentLabel === 'NEGATIVE' ? 'bg-rose-100 text-rose-800' : 'bg-slate-200 text-slate-700'
                }`}>
                  {insight.sentimentLabel} ({insight.sentimentScore})
                </span>
                <span className="text-slate-400">Confidence: {(insight.confidence * 100).toFixed(0)}%</span>
              </div>
            </div>

            {onOverrideTag && (
              <div className="flex items-center space-x-1">
                <button
                  onClick={() => onOverrideTag(insight.id, 'POSITIVE')}
                  className="px-2 py-1 text-[11px] font-semibold bg-emerald-50 text-emerald-700 rounded border border-emerald-200 hover:bg-emerald-100"
                >
                  Override +
                </button>
                <button
                  onClick={() => onOverrideTag(insight.id, 'NEGATIVE')}
                  className="px-2 py-1 text-[11px] font-semibold bg-rose-50 text-rose-700 rounded border border-rose-200 hover:bg-rose-100"
                >
                  Override -
                </button>
              </div>
            )}
          </div>
        ))}
      </div>
    </div>
  );
};

export default SentimentAnalyticsPanel;
