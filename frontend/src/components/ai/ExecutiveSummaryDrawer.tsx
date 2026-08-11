import React from 'react';

export interface WorkplaceRiskAlert {
  alertId: string;
  category: string;
  severity: 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
  detectedKeyword: string;
  sanitizedSnippet: string;
  detectedAt: string;
}

export interface ExecutiveSummaryData {
  nodeScope: string;
  summaryTitle: string;
  topStrengths: string[];
  topConcerns: string[];
  recommendations: string[];
  generatedAt: string;
}

interface ExecutiveSummaryDrawerProps {
  isOpen: boolean;
  onClose: () => void;
  summary?: ExecutiveSummaryData;
  riskAlerts?: WorkplaceRiskAlert[];
  onExportSlide?: () => void;
  onViewRiskDetails?: (alertId: string) => void;
}

export const ExecutiveSummaryDrawer: React.FC<ExecutiveSummaryDrawerProps> = ({
  isOpen,
  onClose,
  summary = {
    nodeScope: 'IT_DIVISION',
    summaryTitle: 'AI Executive Summary — IT Division',
    topStrengths: [
      'Strong cross-team collaboration and supportive culture',
      'High appreciation for technical skill development workshops',
      'Transparent local line-management communication'
    ],
    topConcerns: [
      'Elevated burnout risk driven by tight project deadlines',
      'Need for systematic departmental workload rebalancing audit',
      'Equipment maintenance requests in branch offices'
    ],
    recommendations: [
      'Initiate immediate workload rebalancing audit for IT and factory teams',
      'Establish bi-weekly leadership Q&A sessions to address deadline pressure'
    ],
    generatedAt: new Date().toISOString()
  },
  riskAlerts = [
    {
      alertId: 'ALT-9901',
      category: 'SAFETY',
      severity: 'CRITICAL',
      detectedKeyword: 'safety guard broken',
      sanitizedSnippet: 'The safety guard broken on machine #3 in Factory Branch B.',
      detectedAt: new Date().toISOString()
    }
  ],
  onExportSlide,
  onViewRiskDetails
}) => {
  if (!isOpen) return null;

  const criticalAlerts = riskAlerts.filter(a => a.severity === 'CRITICAL' || a.severity === 'HIGH');

  return (
    <div className="fixed inset-0 z-50 overflow-hidden bg-slate-900/40 backdrop-blur-sm flex justify-end">
      <div className="w-full max-w-2xl bg-white h-full shadow-2xl flex flex-col border-l border-slate-200 animate-in slide-in-from-right duration-200">
        
        {/* Drawer Header */}
        <div className="p-6 border-b border-slate-200 flex items-center justify-between bg-white text-slate-900">
          <div>
            <span className="text-xs font-bold tracking-wider text-indigo-600 uppercase">Node Scope: {summary.nodeScope}</span>
            <h2 className="text-xl font-bold mt-1 text-slate-900">{summary.summaryTitle}</h2>
          </div>
          <button
            onClick={onClose}
            className="p-2 rounded-lg text-slate-400 hover:text-slate-700 hover:bg-slate-100 transition-colors"
          >
            ✕
          </button>
        </div>

        {/* Risk Alert Banner (US-AI-002) */}
        {criticalAlerts.length > 0 && (
          <div className="bg-rose-600 text-white p-4 border-b border-rose-700 space-y-2">
            {criticalAlerts.map(alert => (
              <div key={alert.alertId} className="flex items-start justify-between">
                <div>
                  <div className="flex items-center space-x-2">
                    <span className="bg-white text-rose-700 font-extrabold text-[10px] px-2 py-0.5 rounded uppercase">
                      {alert.severity} RISK ALERT
                    </span>
                    <span className="font-bold text-sm">[{alert.category}]</span>
                  </div>
                  <p className="text-xs text-rose-100 italic mt-1 font-mono">"{alert.sanitizedSnippet}"</p>
                </div>
                {onViewRiskDetails && (
                  <button
                    onClick={() => onViewRiskDetails(alert.alertId)}
                    className="px-3 py-1 bg-white text-rose-700 text-xs font-bold rounded shadow-sm hover:bg-rose-50"
                  >
                    Investigate →
                  </button>
                )}
              </div>
            ))}
          </div>
        )}

        {/* Content Body */}
        <div className="p-6 overflow-y-auto space-y-6 flex-1">
          
          {/* Strengths (3 Items) */}
          <div className="space-y-3">
            <h3 className="text-sm font-bold text-emerald-700 uppercase tracking-wider flex items-center space-x-2">
              <span>Key Strengths (3)</span>
            </h3>
            <div className="space-y-2">
              {summary.topStrengths.map((str, idx) => (
                <div key={idx} className="p-3 bg-emerald-50/70 border border-emerald-200 rounded-lg text-sm text-emerald-950 font-medium">
                  • {str}
                </div>
              ))}
            </div>
          </div>

          {/* Concerns (3 Items) */}
          <div className="space-y-3">
            <h3 className="text-sm font-bold text-amber-700 uppercase tracking-wider flex items-center space-x-2">
              <span>Top Concerns (3)</span>
            </h3>
            <div className="space-y-2">
              {summary.topConcerns.map((con, idx) => (
                <div key={idx} className="p-3 bg-amber-50/70 border border-amber-200 rounded-lg text-sm text-amber-950 font-medium">
                  • {con}
                </div>
              ))}
            </div>
          </div>

          {/* Recommendations (2 Items) */}
          <div className="space-y-3">
            <h3 className="text-sm font-bold text-indigo-700 uppercase tracking-wider flex items-center space-x-2">
              <span>Executive Recommendations (2)</span>
            </h3>
            <div className="space-y-2">
              {summary.recommendations.map((rec, idx) => (
                <div key={idx} className="p-3.5 bg-indigo-50/70 border border-indigo-200 rounded-lg text-sm text-indigo-950 font-semibold flex items-start space-x-2">
                  <span className="text-indigo-600 font-bold">{idx + 1}.</span>
                  <span>{rec}</span>
                </div>
              ))}
            </div>
          </div>
        </div>

        {/* Drawer Footer */}
        <div className="p-4 border-t border-slate-200 bg-slate-50 flex items-center justify-between">
          <span className="text-xs text-slate-400">Generated: {new Date(summary.generatedAt).toLocaleString()}</span>
          <div className="flex items-center space-x-3">
            <button
              onClick={onClose}
              className="px-4 py-2 text-xs font-semibold text-slate-600 bg-white border border-slate-300 rounded-lg hover:bg-slate-100"
            >
              Close
            </button>
            <button
              onClick={onExportSlide}
              className="px-4 py-2 text-xs font-bold text-white bg-indigo-600 rounded-lg shadow-sm hover:bg-indigo-700 transition-colors"
            >
              Export to Slide / PDF →
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ExecutiveSummaryDrawer;
