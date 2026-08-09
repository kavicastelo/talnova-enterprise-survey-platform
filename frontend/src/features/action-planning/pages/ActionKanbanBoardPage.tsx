import React, { useState } from 'react';
import { PageHeader } from '../../../components/ui/PageHeader';
import { Button } from '../../../components/ui/Button';
import { Card } from '../../../components/ui/Card';
import { Skeleton } from '../../../components/ui/Skeleton';
import { ErrorState } from '../../../components/ui/ErrorState';
import { ActionPlanCreateModal } from '../../../components/action/ActionPlanCreateModal';
import { useTenant } from '../../../context/TenantContext';
import { ActionStatus, ActionPlanResponse } from '../../../types/actionPlanning';
import {
  useKanbanActionBoardQuery,
  useApproveActionPlanMutation,
  useSyncJiraMutation,
} from '../api/useActionPlanningQueries';

const KANBAN_COLUMNS: { key: ActionStatus; label: string; bg: string; border: string }[] = [
  { key: 'DRAFT', label: 'Draft / Proposed', bg: '#f8fafc', border: '#cbd5e1' },
  { key: 'APPROVED', label: 'Approved by HR', bg: '#eff6ff', border: '#bfdbfe' },
  { key: 'IN_PROGRESS', label: 'In Progress', bg: '#fefce8', border: '#fef08a' },
  { key: 'COMPLETED', label: 'Completed', bg: '#f0fdf4', border: '#bbf7d0' },
];

export const ActionKanbanBoardPage: React.FC = () => {
  const { activeProject } = useTenant();
  const [nodeId, setNodeId] = useState<string>('');
  const [isCreateModalOpen, setIsCreateModalOpen] = useState<boolean>(false);

  const projectId = activeProject?.projectId || 'PRJ-99201';

  const {
    data: cardsData,
    isLoading,
    isError,
    refetch,
  } = useKanbanActionBoardQuery(projectId, nodeId);

  const approveMutation = useApproveActionPlanMutation();
  const syncJiraMutation = useSyncJiraMutation();

  const handleApprove = (actionPlanId: string) => {
    approveMutation.mutate({
      actionPlanId,
      payload: {
        actorId: 'USR-HR-DIRECTOR',
        userRole: 'HR_MANAGER',
        rationale: 'Approved via Kanban Action Board',
      },
    });
  };

  const handleSyncJira = (actionPlanId: string) => {
    syncJiraMutation.mutate({ actionPlanId, projectKey: 'ENG' });
  };

  // Demo fallback cards
  const fallbackCards: ActionPlanResponse[] = [
    {
      actionPlanId: 'ACT-9901',
      projectId,
      campaignId: 'CMP-101',
      nodeId: 'N-301',
      groupId: 'QG-01',
      title: 'Senior Leadership Transparency Workshops',
      description: 'Monthly Q&A sessions with executive leaders to address workload & direction concerns.',
      baselineScore: 48.0,
      targetScore: 75.0,
      status: 'DRAFT',
      assigneeId: 'EMP-1002',
      milestoneCount: 3,
    },
    {
      actionPlanId: 'ACT-9902',
      projectId,
      campaignId: 'CMP-101',
      nodeId: 'N-301',
      groupId: 'QG-02',
      title: 'Engineering Team Workload & Sprint Balancing',
      description: 'Rebalancing sprint allocation to limit overtime and reduce burnout risk.',
      baselineScore: 52.0,
      targetScore: 80.0,
      status: 'APPROVED',
      assigneeId: 'EMP-1005',
      milestoneCount: 4,
      externalSyncSystem: 'JIRA',
    },
    {
      actionPlanId: 'ACT-9903',
      projectId,
      campaignId: 'CMP-101',
      nodeId: 'N-301',
      groupId: 'QG-04',
      title: 'Career Mobility & Mentorship Framework',
      description: 'Formal mentorship program connecting junior engineers with principal staff.',
      baselineScore: 61.0,
      targetScore: 85.0,
      status: 'IN_PROGRESS',
      assigneeId: 'EMP-1009',
      milestoneCount: 2,
    },
  ];

  const cards = cardsData && cardsData.length > 0 ? cardsData : fallbackCards;

  return (
    <div>
      <PageHeader
        title="Closed-Loop Action Planning & Remediation Board"
        subtitle={`Turn survey feedback insights into accountable, trackable workplace improvements for ${projectId}`}
        actions={
          <div style={{ display: 'flex', gap: '10px' }}>
            <select
              value={nodeId}
              onChange={(e) => setNodeId(e.target.value)}
              style={{ padding: '8px 12px', borderRadius: '8px', border: '1px solid #cbd5e1', fontSize: '0.85rem' }}
            >
              <option value="N-301">Engineering Dept (N-301)</option>
              <option value="GLOBAL_ORG">Global Enterprise Scope</option>
              <option value="IT_DIVISION">IT Division (N-102)</option>
            </select>
            <Button variant="primary" onClick={() => setIsCreateModalOpen(true)}>
              ➕ Create Action Plan
            </Button>
          </div>
        }
      />

      {isLoading ? (
        <Card variant="bordered" padding="24px" style={{ marginTop: '20px' }}>
          <Skeleton height="400px" borderRadius="12px" />
        </Card>
      ) : isError ? (
        <Card variant="bordered" padding="24px" style={{ marginTop: '20px' }}>
          <ErrorState
            title="Kanban Board Unavailable"
            message="Could not load remediation action plans from action-planning-service."
            onRetry={refetch}
          />
        </Card>
      ) : (
        <div style={{ marginTop: '20px', display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: '16px' }}>
          {KANBAN_COLUMNS.map((col) => {
            const colCards = cards.filter(
              (c) =>
                (col.key === 'DRAFT' && (c.status === 'DRAFT' || c.status === 'PROPOSED')) ||
                (col.key === 'APPROVED' && c.status === 'APPROVED') ||
                (col.key === 'IN_PROGRESS' && c.status === 'IN_PROGRESS') ||
                (col.key === 'COMPLETED' && (c.status === 'COMPLETED' || c.status === 'VERIFIED'))
            );

            return (
              <div
                key={col.key}
                style={{
                  background: col.bg,
                  border: `1px solid ${col.border}`,
                  borderRadius: '12px',
                  padding: '16px',
                  minHeight: '550px',
                  display: 'flex',
                  flexDirection: 'column',
                }}
              >
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '14px', paddingBottom: '8px', borderBottom: `1px solid ${col.border}` }}>
                  <h4 style={{ margin: 0, fontSize: '0.9rem', fontWeight: 800, color: '#0f172a' }}>
                    {col.label}
                  </h4>
                  <span style={{ background: '#ffffff', padding: '2px 8px', borderRadius: '12px', fontSize: '0.75rem', fontWeight: 700, color: '#64748b', border: `1px solid ${col.border}` }}>
                    {colCards.length}
                  </span>
                </div>

                <div style={{ display: 'flex', flexDirection: 'column', gap: '12px', flex: 1 }}>
                  {colCards.length === 0 ? (
                    <div style={{ textAlign: 'center', padding: '40px 10px', fontSize: '0.8rem', color: '#94a3b8', fontStyle: 'italic' }}>
                      No action plans
                    </div>
                  ) : (
                    colCards.map((card) => (
                      <div
                        key={card.actionPlanId}
                        style={{
                          background: '#ffffff',
                          border: '1px solid #e2e8f0',
                          borderRadius: '10px',
                          padding: '14px',
                          boxShadow: '0 1px 3px rgba(0,0,0,0.05)',
                        }}
                      >
                        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '6px' }}>
                          <code style={{ fontSize: '0.75rem', fontWeight: 700, color: '#2563eb' }}>
                            {card.actionPlanId}
                          </code>
                          {card.externalSyncSystem && (
                            <span style={{ background: '#eff6ff', color: '#1d4ed8', padding: '2px 6px', borderRadius: '4px', fontSize: '0.65rem', fontWeight: 700 }}>
                              {card.externalSyncSystem}
                            </span>
                          )}
                        </div>

                        <h5 style={{ margin: '0 0 6px 0', fontSize: '0.85rem', fontWeight: 800, color: '#0f172a' }}>
                          {card.title}
                        </h5>
                        <p style={{ margin: '0 0 10px 0', fontSize: '0.75rem', color: '#64748b', lineHeight: 1.3 }}>
                          {card.description}
                        </p>

                        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', paddingTop: '8px', borderTop: '1px solid #f1f5f9', fontSize: '0.75rem', color: '#475569' }}>
                          <span>
                            Target: <strong style={{ color: '#dc2626' }}>{card.baselineScore}%</strong> &rarr; <strong style={{ color: '#16a34a' }}>{card.targetScore}%</strong>
                          </span>
                          <span>{card.milestoneCount || 0} Milestones</span>
                        </div>

                        {card.status === 'DRAFT' && (
                          <div style={{ marginTop: '10px', paddingTop: '8px', borderTop: '1px solid #f1f5f9' }}>
                            <Button
                              variant="primary"
                              size="sm"
                              style={{ width: '100%' }}
                              isLoading={approveMutation.isPending}
                              onClick={() => handleApprove(card.actionPlanId)}
                            >
                              ✓ Approve HR
                            </Button>
                          </div>
                        )}

                        {card.status === 'APPROVED' && !card.externalSyncSystem && (
                          <div style={{ marginTop: '10px', paddingTop: '8px', borderTop: '1px solid #f1f5f9' }}>
                            <Button
                              variant="outline"
                              size="sm"
                              style={{ width: '100%' }}
                              isLoading={syncJiraMutation.isPending}
                              onClick={() => handleSyncJira(card.actionPlanId)}
                            >
                              🔗 Sync to Jira
                            </Button>
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
      )}

      <ActionPlanCreateModal
        isOpen={isCreateModalOpen}
        projectId={projectId}
        onClose={() => setIsCreateModalOpen(false)}
      />
    </div>
  );
};
