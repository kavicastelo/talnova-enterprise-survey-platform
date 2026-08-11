import React, { useEffect, useState } from 'react';

export type ActionStatus =
  | 'DRAFT'
  | 'PROPOSED'
  | 'APPROVED'
  | 'REJECTED'
  | 'IN_PROGRESS'
  | 'COMPLETED'
  | 'VERIFIED'
  | 'CANCELLED';

export interface ActionPlanCard {
  actionPlanId: string;
  projectId: string;
  campaignId: string;
  nodeId: string;
  groupId: string;
  title: string;
  description: string;
  baselineScore: number;
  targetScore: number;
  postActionScore?: number;
  status: ActionStatus;
  assigneeId: string;
  targetCompletionDate?: string;
  milestoneCount: number;
  externalSyncSystem?: string;
}

const KANBAN_COLUMNS: { key: ActionStatus; label: string; color: string }[] = [
  { key: 'DRAFT', label: 'Draft (Auto-Triggered)', color: 'bg-slate-100 text-slate-700' },
  { key: 'PROPOSED', label: 'Proposed for Review', color: 'bg-amber-50 text-amber-800 border border-amber-200' },
  { key: 'APPROVED', label: 'Approved by HR', color: 'bg-blue-50 text-blue-800 border border-blue-200' },
  { key: 'IN_PROGRESS', label: 'In Progress (Executing)', color: 'bg-indigo-50 text-indigo-800 border border-indigo-200' },
  { key: 'COMPLETED', label: 'Completed', color: 'bg-purple-50 text-purple-800 border border-purple-200' },
  { key: 'VERIFIED', label: 'Verified (+Δ Score)', color: 'bg-emerald-50 text-emerald-800 border border-emerald-200' },
];

export const ActionKanbanBoardPage: React.FC = () => {
  const [projectId] = useState<string>('PRJ-99201');
  const [nodeId, setNodeId] = useState<string>('N-301');
  const [actionCards, setActionCards] = useState<ActionPlanCard[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  const fetchKanbanCards = async () => {
    setLoading(true);
    setError(null);
    try {
      const res = await fetch(`/api/v1/actions/kanban?projectId=${projectId}&nodeId=${nodeId}`);
      if (res.ok) {
        const data: ActionPlanCard[] = await res.json();
        setActionCards(data);
      } else {
        throw new Error(`Failed to fetch kanban board cards (${res.status})`);
      }
    } catch (err: any) {
      setError(err.message || 'Error loading kanban board');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchKanbanCards();
  }, [projectId, nodeId]);

  const handleTransition = async (actionPlanId: string, targetStatus: ActionStatus) => {
    try {
      let endpoint = '';
      let body: any = { actorId: 'USR-HR-DIR', userRole: 'HR_MANAGER', rationale: 'Kanban board drag transition' };

      if (targetStatus === 'APPROVED') {
        endpoint = `/api/v1/actions/${actionPlanId}/approve`;
      } else if (targetStatus === 'REJECTED') {
        endpoint = `/api/v1/actions/${actionPlanId}/reject`;
      } else {
        return;
      }

      const res = await fetch(endpoint, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body),
      });

      if (res.ok) {
        fetchKanbanCards();
      } else {
        const errData = await res.json();
        alert(errData.detail || 'State transition failed');
      }
    } catch (err: any) {
      alert(err.message || 'State transition error');
    }
  };

  return (
    <div className="min-h-screen bg-slate-50 p-6 text-slate-900">
      {/* Header */}
      <div className="flex flex-col md:flex-row md:items-center justify-between pb-6 border-b border-slate-200 gap-4">
        <div>
          <h1 className="text-2xl font-black tracking-tight">Closed-Loop Action Planning & Remediation Board</h1>
          <p className="text-xs text-slate-500">
            Turn survey feedback insights into accountable, trackable workplace improvements (FR-ACT-007)
          </p>
        </div>

        {/* Filter Controls */}
        <div className="flex items-center space-x-3">
          <div>
            <label className="block text-[10px] font-bold uppercase text-slate-500">Organization Node</label>
            <select
              value={nodeId}
              onChange={(e) => setNodeId(e.target.value)}
              className="rounded-lg border border-slate-300 bg-white px-3 py-1.5 text-xs font-semibold"
            >
              <option value="N-301">Engineering Dept (N-301)</option>
              <option value="GLOBAL_ORG">Global Enterprise Scope</option>
              <option value="IT_DIVISION">IT Division (N-102)</option>
            </select>
          </div>
          <button
            onClick={fetchKanbanCards}
            className="rounded-lg bg-blue-600 px-4 py-2 text-xs font-bold text-white shadow hover:bg-blue-700"
          >
            Refresh Board
          </button>
        </div>
      </div>

      {error && (
        <div className="mt-4 rounded-lg bg-red-50 p-3 text-xs font-semibold text-red-800 border border-red-200">
          {error}
        </div>
      )}

      {/* 6-Column Kanban Grid */}
      <div className="mt-6 grid grid-cols-1 md:grid-cols-3 lg:grid-cols-6 gap-4 overflow-x-auto pb-4">
        {KANBAN_COLUMNS.map((col) => {
          const colCards = actionCards.filter((card) => card.status === col.key);

          return (
            <div
              key={col.key}
              className="flex flex-col rounded-xl bg-slate-100 p-3 border border-slate-200 min-h-[500px]"
            >
              <div className="flex items-center justify-between mb-3 pb-2 border-b border-slate-200">
                <span className={`px-2 py-0.5 text-[11px] font-bold rounded-md ${col.color}`}>
                  {col.label}
                </span>
                <span className="text-xs font-bold text-slate-500">{colCards.length}</span>
              </div>

              <div className="flex-1 space-y-3">
                {loading ? (
                  <div className="text-center py-6 text-xs text-slate-400 animate-pulse">Loading cards...</div>
                ) : colCards.length === 0 ? (
                  <div className="text-center py-8 text-xs text-slate-400 italic">No action plans</div>
                ) : (
                  colCards.map((card) => (
                    <div
                      key={card.actionPlanId}
                      className="rounded-lg bg-white p-3 shadow-sm border border-slate-200 hover:shadow-md transition-shadow"
                    >
                      <div className="flex items-center justify-between mb-1">
                        <span className="text-[10px] font-mono font-bold text-blue-600">{card.actionPlanId}</span>
                        {card.externalSyncSystem && (
                          <span className="px-1.5 py-0.5 text-[9px] font-bold bg-indigo-50 text-indigo-700 border border-indigo-200 rounded">
                            {card.externalSyncSystem}
                          </span>
                        )}
                      </div>

                      <h3 className="text-xs font-bold text-slate-900 line-clamp-2">{card.title}</h3>
                      <p className="mt-1 text-[11px] text-slate-500 line-clamp-2">{card.description}</p>

                      <div className="mt-3 flex items-center justify-between pt-2 border-t border-slate-100 text-[10px]">
                        <span className="font-semibold text-slate-600">
                          Score: <strong className="text-red-500">{card.baselineScore}%</strong> &rarr; <strong className="text-emerald-500">{card.targetScore}%</strong>
                        </span>
                        <span className="text-slate-400">{card.milestoneCount} Milestones</span>
                      </div>

                      {card.status === 'PROPOSED' && (
                        <div className="mt-2 flex space-x-2 pt-2">
                          <button
                            onClick={() => handleTransition(card.actionPlanId, 'APPROVED')}
                            className="w-full rounded bg-emerald-600 py-1 text-[10px] font-bold text-white hover:bg-emerald-700"
                          >
                            Approve HR
                          </button>
                          <button
                            onClick={() => handleTransition(card.actionPlanId, 'REJECTED')}
                            className="w-full rounded bg-red-600 py-1 text-[10px] font-bold text-white hover:bg-red-700"
                          >
                            Reject
                          </button>
                        </div>
                      )}
                    </div>
                  ))
                )}
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};
