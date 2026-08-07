import React, { useState } from 'react';
import { ActionPlanCard } from '../../pages/ActionKanbanBoardPage';

interface ActionCardDetailDrawerProps {
  isOpen: boolean;
  card: ActionPlanCard | null;
  onClose: () => void;
  onRefresh: () => void;
}

interface ActionTemplate {
  templateId: string;
  groupId: string;
  title: string;
  description: string;
  suggestedMilestones: string[];
}

export const ActionCardDetailDrawer: React.FC<ActionCardDetailDrawerProps> = ({
  isOpen,
  card,
  onClose,
  onRefresh,
}) => {
  const [syncingJira, setSyncingJira] = useState<boolean>(false);
  const [syncingPlanner, setSyncingPlanner] = useState<boolean>(false);
  const [recommendations, setRecommendations] = useState<ActionTemplate[]>([]);
  const [loadingAi, setLoadingAi] = useState<boolean>(false);
  const [message, setMessage] = useState<string | null>(null);

  if (!isOpen || !card) return null;

  const handleSyncJira = async () => {
    setSyncingJira(true);
    setMessage(null);
    try {
      const res = await fetch(`/api/v1/actions/${card.actionPlanId}/sync-jira?projectKey=ENG`, { method: 'POST' });
      if (res.ok) {
        const data = await res.json();
        setMessage(`Synced successfully to Jira Issue '${data.externalKey}'`);
        onRefresh();
      } else {
        throw new Error('Jira sync failed');
      }
    } catch (err: any) {
      setMessage(err.message || 'Error syncing to Jira');
    } finally {
      setSyncingJira(false);
    }
  };

  const handleSyncPlanner = async () => {
    setSyncingPlanner(true);
    setMessage(null);
    try {
      const res = await fetch(`/api/v1/actions/${card.actionPlanId}/sync-ms-planner?planId=PLN-MAIN`, { method: 'POST' });
      if (res.ok) {
        const data = await res.json();
        setMessage(`Synced successfully to Microsoft Planner Task '${data.externalKey}'`);
        onRefresh();
      } else {
        throw new Error('MS Planner sync failed');
      }
    } catch (err: any) {
      setMessage(err.message || 'Error syncing to MS Planner');
    } finally {
      setSyncingPlanner(false);
    }
  };

  const fetchAiRecommendations = async () => {
    setLoadingAi(true);
    try {
      const res = await fetch(`/api/v1/actions/templates/recommendations?groupId=${card.groupId}`);
      if (res.ok) {
        const data: ActionTemplate[] = await res.json();
        setRecommendations(data);
      }
    } catch (err) {
      console.error('Failed to fetch AI template recommendations', err);
    } finally {
      setLoadingAi(false);
    }
  };

  return (
    <div className="fixed inset-y-0 right-0 z-50 flex w-full max-w-lg bg-white shadow-2xl dark:bg-slate-900 border-l border-slate-200 dark:border-slate-800">
      <div className="flex flex-col w-full p-6 overflow-y-auto">
        <div className="flex items-center justify-between border-b border-slate-200 pb-4 dark:border-slate-800">
          <div>
            <span className="text-xs font-mono font-bold text-blue-600">{card.actionPlanId}</span>
            <h2 className="text-lg font-bold text-slate-900 dark:text-white mt-1">{card.title}</h2>
          </div>
          <button onClick={onClose} className="text-slate-400 hover:text-slate-600 dark:hover:text-slate-200 font-bold text-xl">
            &times;
          </button>
        </div>

        {message && (
          <div className="mt-4 rounded-lg bg-emerald-50 p-3 text-xs font-semibold text-emerald-800 dark:bg-emerald-950/50 dark:text-emerald-300 border border-emerald-200">
            {message}
          </div>
        )}

        <div className="mt-4 space-y-6">
          {/* Metadata Badges */}
          <div className="grid grid-cols-2 gap-3 rounded-xl bg-slate-50 p-4 dark:bg-slate-800/50 border border-slate-200 dark:border-slate-700">
            <div>
              <span className="block text-[10px] font-bold uppercase text-slate-500">Baseline Score</span>
              <span className="text-sm font-bold text-red-500">{card.baselineScore}%</span>
            </div>
            <div>
              <span className="block text-[10px] font-bold uppercase text-slate-500">Target Score (+15%)</span>
              <span className="text-sm font-bold text-emerald-500">{card.targetScore}%</span>
            </div>
            <div>
              <span className="block text-[10px] font-bold uppercase text-slate-500">Assignee</span>
              <span className="text-xs font-semibold text-slate-800 dark:text-slate-200">{card.assigneeId}</span>
            </div>
            <div>
              <span className="block text-[10px] font-bold uppercase text-slate-500">Status</span>
              <span className="text-xs font-bold text-blue-600">{card.status}</span>
            </div>
          </div>

          {/* External Task Sync Bridge */}
          <div className="rounded-xl border border-slate-200 p-4 dark:border-slate-800 space-y-3">
            <h3 className="text-xs font-bold uppercase tracking-wider text-slate-700 dark:text-slate-300">
              Bi-Directional External Task Bridge (FR-ACT-004)
            </h3>
            {card.externalSyncSystem ? (
              <div className="flex items-center space-x-2 text-xs font-bold text-indigo-600 dark:text-indigo-400">
                <span className="px-2 py-1 bg-indigo-100 dark:bg-indigo-950 rounded">System: {card.externalSyncSystem}</span>
              </div>
            ) : (
              <div className="flex space-x-2">
                <button
                  onClick={handleSyncJira}
                  disabled={syncingJira}
                  className="w-full rounded-lg bg-blue-600 py-2 text-xs font-bold text-white shadow hover:bg-blue-700 disabled:opacity-50"
                >
                  {syncingJira ? 'Syncing...' : 'Sync to Jira Cloud'}
                </button>
                <button
                  onClick={handleSyncPlanner}
                  disabled={syncingPlanner}
                  className="w-full rounded-lg bg-teal-600 py-2 text-xs font-bold text-white shadow hover:bg-teal-700 disabled:opacity-50"
                >
                  {syncingPlanner ? 'Syncing...' : 'Sync to MS Planner'}
                </button>
              </div>
            )}
          </div>

          {/* AI Recommended Action Templates */}
          <div className="rounded-xl border border-slate-200 p-4 dark:border-slate-800 space-y-3">
            <div className="flex items-center justify-between">
              <h3 className="text-xs font-bold uppercase tracking-wider text-slate-700 dark:text-slate-300">
                Daash AI Template Recommender
              </h3>
              <button
                onClick={fetchAiRecommendations}
                className="text-[11px] font-bold text-blue-600 hover:underline"
              >
                {loadingAi ? 'Matching...' : 'Fetch Recommendations'}
              </button>
            </div>

            {recommendations.length > 0 && (
              <div className="space-y-2 pt-2">
                {recommendations.map((tpl) => (
                  <div key={tpl.templateId} className="rounded-lg bg-slate-50 p-3 text-xs dark:bg-slate-800/60 border border-slate-200 dark:border-slate-700">
                    <span className="font-bold text-slate-900 dark:text-white">{tpl.title}</span>
                    <p className="mt-1 text-slate-500 text-[11px]">{tpl.description}</p>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>

        <div className="pt-6 border-t border-slate-200 dark:border-slate-800 mt-auto">
          <button
            onClick={onClose}
            className="w-full rounded-lg border border-slate-300 py-2 text-xs font-bold text-slate-700 hover:bg-slate-50 dark:border-slate-700 dark:text-slate-300 dark:hover:bg-slate-800"
          >
            Close Drawer
          </button>
        </div>
      </div>
    </div>
  );
};
