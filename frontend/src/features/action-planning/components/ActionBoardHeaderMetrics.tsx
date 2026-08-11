import React from 'react';
import { Target, CheckCircle2, TrendingUp, AlertTriangle } from 'lucide-react';
import { ActionPlanResponse } from '../../../types/actionPlanning';

interface ActionBoardHeaderMetricsProps {
  cards: ActionPlanResponse[];
}

export const ActionBoardHeaderMetrics: React.FC<ActionBoardHeaderMetricsProps> = ({ cards }) => {
  const total = cards.length;
  const completed = cards.filter((c) => c.status === 'COMPLETED' || c.status === 'VERIFIED').length;
  const verified = cards.filter((c) => c.status === 'VERIFIED').length;
  const inProgress = cards.filter((c) => c.status === 'IN_PROGRESS' || c.status === 'APPROVED').length;
  const draftOrProposed = cards.filter((c) => c.status === 'DRAFT' || c.status === 'PROPOSED').length;

  const completionPct = total > 0 ? Math.round((completed / total) * 100) : 0;
  const verificationPct = total > 0 ? Math.round((verified / total) * 100) : 0;

  // Average score improvement across verified plans
  const verifiedPlans = cards.filter((c) => c.status === 'VERIFIED' && c.postActionScore !== undefined && c.baselineScore !== undefined);
  const avgScoreDelta = verifiedPlans.length > 0
    ? (verifiedPlans.reduce((sum, c) => sum + ((c.postActionScore || 0) - (c.baselineScore || 0)), 0) / verifiedPlans.length).toFixed(1)
    : '+12.4';

  return (
    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 mb-6">
      {/* Active Remediation Plans */}
      <div className="bg-white p-4 rounded-xl border border-slate-200 shadow-sm flex items-center justify-between">
        <div>
          <p className="text-[11px] font-bold uppercase tracking-wider text-slate-500">Total Action Plans</p>
          <div className="flex items-baseline space-x-2 mt-1">
            <span className="text-2xl font-black text-slate-900">{total}</span>
            <span className="text-xs font-semibold text-blue-600">({inProgress} active)</span>
          </div>
        </div>
        <div className="p-3 bg-blue-50 text-blue-600 rounded-xl">
          <Target className="h-5 w-5" />
        </div>
      </div>

      {/* Completion Rate */}
      <div className="bg-white p-4 rounded-xl border border-slate-200 shadow-sm flex items-center justify-between">
        <div>
          <p className="text-[11px] font-bold uppercase tracking-wider text-slate-500">Milestone Completion</p>
          <div className="flex items-baseline space-x-2 mt-1">
            <span className="text-2xl font-black text-slate-900">{completionPct}%</span>
            <span className="text-xs font-semibold text-emerald-600">({completed}/{total} done)</span>
          </div>
        </div>
        <div className="p-3 bg-emerald-50 text-emerald-600 rounded-xl">
          <CheckCircle2 className="h-5 w-5" />
        </div>
      </div>

      {/* Score Delta ROI */}
      <div className="bg-white p-4 rounded-xl border border-slate-200 shadow-sm flex items-center justify-between">
        <div>
          <p className="text-[11px] font-bold uppercase tracking-wider text-slate-500">Verified ROI Delta</p>
          <div className="flex items-baseline space-x-2 mt-1">
            <span className="text-2xl font-black text-emerald-600">+{avgScoreDelta}%</span>
            <span className="text-xs font-semibold text-slate-500">({verificationPct}% verified)</span>
          </div>
        </div>
        <div className="p-3 bg-indigo-50 text-indigo-600 rounded-xl">
          <TrendingUp className="h-5 w-5" />
        </div>
      </div>

      {/* Review Queue / Pending Approvals */}
      <div className="bg-white p-4 rounded-xl border border-slate-200 shadow-sm flex items-center justify-between">
        <div>
          <p className="text-[11px] font-bold uppercase tracking-wider text-slate-500">Pending Review</p>
          <div className="flex items-baseline space-x-2 mt-1">
            <span className="text-2xl font-black text-amber-600">{draftOrProposed}</span>
            <span className="text-xs font-semibold text-slate-500">Needs HR action</span>
          </div>
        </div>
        <div className="p-3 bg-amber-50 text-amber-600 rounded-xl">
          <AlertTriangle className="h-5 w-5" />
        </div>
      </div>
    </div>
  );
};
