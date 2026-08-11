import React, { useState, useMemo } from 'react';
import { PageHeader } from '../../../components/ui/PageHeader';
import { Button } from '../../../components/ui/Button';
import { Card } from '../../../components/ui/Card';
import { Skeleton } from '../../../components/ui/Skeleton';
import { ErrorState } from '../../../components/ui/ErrorState';
import { ActionPlanCreateModal } from '../../../components/action/ActionPlanCreateModal';
import { ActionCardDetailDrawer } from '../../../components/actions/ActionCardDetailDrawer';
import { useTenant } from '../../../context/TenantContext';
import { useAuth } from '../../../context/AuthContext';
import { ActionStatus, ActionPlanResponse } from '../../../types/actionPlanning';
import { useKanbanActionBoardQuery } from '../api/useActionPlanningQueries';
import { ActionBoardFilterBar } from '../components/ActionBoardFilterBar';
import { ActionBoardHeaderMetrics } from '../components/ActionBoardHeaderMetrics';
import { ActionKanbanBoard } from '../components/ActionKanbanBoard';
import { ActionStateTransitionModal } from '../components/ActionStateTransitionModal';
import { Plus } from 'lucide-react';

export const ActionKanbanBoardPage: React.FC = () => {
  const { activeProject } = useTenant();
  const { user } = useAuth();


  // Multi-dimensional filters
  const [nodeId, setNodeId] = useState<string>('');
  const [groupId, setGroupId] = useState<string>('');
  const [statusFilter, setStatusFilter] = useState<string>('');
  const [assigneeId, setAssigneeId] = useState<string>('');
  const [searchQuery, setSearchQuery] = useState<string>('');

  // Modals & Drawer State
  const [isCreateModalOpen, setIsCreateModalOpen] = useState<boolean>(false);
  const [selectedCard, setSelectedCard] = useState<ActionPlanResponse | null>(null);
  const [isDetailDrawerOpen, setIsDetailDrawerOpen] = useState<boolean>(false);

  // Transition Modal State
  const [transitionCard, setTransitionCard] = useState<ActionPlanResponse | null>(null);
  const [targetTransitionStatus, setTargetTransitionStatus] = useState<ActionStatus | null>(null);
  const [isTransitionModalOpen, setIsTransitionModalOpen] = useState<boolean>(false);

  const projectId = activeProject?.projectId || 'PRJ-99201';

  // API Query - Real backend data
  const {
    data: cardsData,
    isLoading,
    isError,
    error,
    refetch,
    isFetching,
  } = useKanbanActionBoardQuery(projectId, nodeId || undefined, groupId || undefined, statusFilter || undefined, assigneeId || undefined);

  const handleCardClick = (card: ActionPlanResponse) => {
    setSelectedCard(card);
    setIsDetailDrawerOpen(true);
  };

  const handleRequestTransition = (card: ActionPlanResponse, targetStatus: ActionStatus) => {
    setTransitionCard(card);
    setTargetTransitionStatus(targetStatus);
    setIsTransitionModalOpen(true);
  };

  // Client-side search filtering (by title, description, or actionPlanId)
  const filteredCards = useMemo(() => {
    if (!cardsData) return [];
    if (!searchQuery.trim()) return cardsData;

    const q = searchQuery.toLowerCase();
    return cardsData.filter(
      (c) =>
        c.actionPlanId.toLowerCase().includes(q) ||
        c.title.toLowerCase().includes(q) ||
        (c.description && c.description.toLowerCase().includes(q)) ||
        (c.assigneeId && c.assigneeId.toLowerCase().includes(q))
    );
  }, [cardsData, searchQuery]);

  return (
    <div className="min-h-screen bg-slate-50/50 pb-12">
      {/* Header */}
      <PageHeader
        title="Closed-Loop Action Planning & Remediation Board"
        subtitle={`Turn employee survey feedback into accountable, trackable workplace improvements (FEAT-010) — Project: ${projectId}`}
        actions={
          <div className="flex items-center space-x-2">
            <Button
              variant="primary"
              onClick={() => setIsCreateModalOpen(true)}
              className="flex items-center gap-1.5 shadow-sm font-bold"
            >
              <Plus className="h-4 w-4" />
              Create Action Plan
            </Button>
          </div>
        }
      />

      <div className="mt-6">
        {/* Header Metrics Summary */}
        <ActionBoardHeaderMetrics cards={cardsData || []} />

        {/* Multi-Dimensional Filter Bar */}
        <ActionBoardFilterBar
          nodeId={nodeId}
          setNodeId={setNodeId}
          groupId={groupId}
          setGroupId={setGroupId}
          statusFilter={statusFilter}
          setStatusFilter={setStatusFilter}
          assigneeId={assigneeId}
          setAssigneeId={setAssigneeId}
          searchQuery={searchQuery}
          setSearchQuery={setSearchQuery}
          onRefresh={refetch}
          isRefreshing={isFetching}
        />

        {/* Content States */}
        {isLoading ? (
          <div className="grid grid-cols-1 md:grid-cols-3 lg:grid-cols-6 gap-4">
            {[1, 2, 3, 4, 5, 6].map((i) => (
              <div key={i} className="bg-slate-100/80 rounded-xl p-3 border border-slate-200 min-h-[500px]">
                <Skeleton height="24px" className="mb-4 rounded-md" />
                <Skeleton height="140px" className="mb-3 rounded-xl" />
                <Skeleton height="140px" className="rounded-xl" />
              </div>
            ))}
          </div>
        ) : isError ? (
          <Card variant="bordered" padding="24px" className="bg-white border-red-200">
            <ErrorState
              title="Action Board Data Unavailable"
              message={
                error && (error as any).message
                  ? (error as any).message
                  : 'Could not connect to action-planning-service backend endpoint (/api/v1/actions/kanban).'
              }
              onRetry={refetch}
            />
          </Card>
        ) : (
          <ActionKanbanBoard
            cards={filteredCards}
            onSelectCard={handleCardClick}
            onRequestTransition={handleRequestTransition}
          />
        )}
      </div>

      {/* Action Plan Create Modal */}
      <ActionPlanCreateModal
        isOpen={isCreateModalOpen}
        projectId={projectId}
        onClose={() => setIsCreateModalOpen(false)}
      />

      {/* Action Plan Detail Drawer */}
      <ActionCardDetailDrawer
        isOpen={isDetailDrawerOpen}
        card={selectedCard}
        onClose={() => setIsDetailDrawerOpen(false)}
        onRefresh={refetch}
      />

      {/* Action State Transition Modal */}
      <ActionStateTransitionModal
        isOpen={isTransitionModalOpen}
        onClose={() => setIsTransitionModalOpen(false)}
        actionPlan={transitionCard}
        targetStatus={targetTransitionStatus}
        currentUserRole={user?.roles?.[0] || 'HR_MANAGER'}
        currentUserId={user?.id || 'USR-HR-DIRECTOR'}
      />
    </div>
  );
};


