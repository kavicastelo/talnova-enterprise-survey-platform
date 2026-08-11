import React, { useState } from 'react';
import { Modal } from '../../../components/ui/Modal';
import { Button } from '../../../components/ui/Button';
import { ActionStatus, ActionPlanResponse } from '../../../types/actionPlanning';
import { useTransitionActionPlanMutation } from '../api/useActionPlanningQueries';
import { AlertCircle, ShieldAlert } from 'lucide-react';


interface ActionStateTransitionModalProps {
  isOpen: boolean;
  onClose: () => void;
  actionPlan: ActionPlanResponse | null;
  targetStatus: ActionStatus | null;
  currentUserRole?: string;
  currentUserId?: string;
}

export const ActionStateTransitionModal: React.FC<ActionStateTransitionModalProps> = ({
  isOpen,
  onClose,
  actionPlan,
  targetStatus,
  currentUserRole = 'HR_MANAGER',
  currentUserId = 'USR-HR-DIRECTOR',
}) => {
  const [rationale, setRationale] = useState<string>('');
  const transitionMutation = useTransitionActionPlanMutation();

  if (!actionPlan || !targetStatus) return null;

  const requiresHrRole = targetStatus === 'APPROVED' || targetStatus === 'REJECTED';
  const isUnauthorized = requiresHrRole && currentUserRole !== 'HR_MANAGER' && currentUserRole !== 'SUPER_ADMIN' && currentUserRole !== 'PROJECT_ADMIN';

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (isUnauthorized) return;

    transitionMutation.mutate(
      {
        actionPlanId: actionPlan.actionPlanId,
        payload: {
          targetStatus,
          actorId: currentUserId,
          userRole: currentUserRole,
          comments: rationale || `Transitioned state to ${targetStatus}`,
        },
      },
      {
        onSuccess: () => {
          setRationale('');
          onClose();
        },
      }
    );
  };

  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      title={`Confirm Status Transition: ${targetStatus}`}
    >
      <form onSubmit={handleSubmit} className="space-y-4">
        <div className="bg-slate-50 p-3.5 rounded-lg border border-slate-200 text-xs text-slate-700">
          <div className="flex justify-between items-center mb-1">
            <span className="font-mono font-bold text-blue-600">{actionPlan.actionPlanId}</span>
            <span className="font-semibold text-slate-500">
              Current: <strong className="text-slate-900">{actionPlan.status}</strong> &rarr; Target: <strong className="text-blue-700">{targetStatus}</strong>
            </span>
          </div>
          <p className="font-bold text-slate-900 line-clamp-1">{actionPlan.title}</p>
        </div>

        {/* Authorization Alert */}
        {isUnauthorized ? (
          <div className="bg-red-50 p-3.5 rounded-lg border border-red-200 flex items-start space-x-2.5">
            <ShieldAlert className="h-5 w-5 text-red-600 shrink-0 mt-0.5" />
            <div>
              <h4 className="text-xs font-bold text-red-900">HR Manager Authorization Required (BR-ACT-001)</h4>
              <p className="text-[11px] text-red-700 mt-0.5">
                Only HR Managers or Administrators can approve or reject proposed action plans. Your current role is <strong className="uppercase">{currentUserRole}</strong>.
              </p>
            </div>
          </div>
        ) : (
          <div className="bg-blue-50 p-3 rounded-lg border border-blue-100 flex items-center space-x-2 text-xs text-blue-800">
            <AlertCircle className="h-4 w-4 text-blue-600 shrink-0" />
            <span>
              This transition will record an immutable audit log entry and emit an event to <code className="font-mono font-bold">tesp.action.events.v1</code>.
            </span>
          </div>
        )}

        {/* Rationale / Comments */}
        <div>
          <label className="block text-xs font-bold text-slate-700 mb-1">
            Transition Comments / Approval Rationale <span className="text-slate-400 font-normal">(Optional)</span>
          </label>
          <textarea
            value={rationale}
            onChange={(e) => setRationale(e.target.value)}
            disabled={isUnauthorized || transitionMutation.isPending}
            rows={3}
            placeholder="Provide context or instructions for this status change..."
            className="w-full p-2.5 bg-slate-50 border border-slate-200 rounded-lg text-xs text-slate-900 placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:bg-white disabled:opacity-60 font-medium"
          />
        </div>

        {/* Action Buttons */}
        <div className="flex justify-end space-x-2 pt-2 border-t border-slate-100">
          <Button variant="outline" type="button" onClick={onClose} disabled={transitionMutation.isPending}>
            Cancel
          </Button>
          <Button
            variant={targetStatus === 'REJECTED' || targetStatus === 'CANCELLED' ? 'danger' : 'primary'}
            type="submit"
            isLoading={transitionMutation.isPending}
            disabled={isUnauthorized}
          >
            Confirm Transition
          </Button>
        </div>
      </form>
    </Modal>
  );
};
