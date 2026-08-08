import React, { useState } from 'react';
import { useCreateActionPlanMutation } from '../../features/action-planning/api/useActionPlanningQueries';
import { Button } from '../ui/Button';

interface ActionPlanCreateModalProps {
  isOpen: boolean;
  projectId: string;
  onClose: () => void;
}

export const ActionPlanCreateModal: React.FC<ActionPlanCreateModalProps> = ({
  isOpen,
  projectId,
  onClose,
}) => {
  const [title, setTitle] = useState<string>('');
  const [description, setDescription] = useState<string>('');
  const [groupId, setGroupId] = useState<string>('QG-01');
  const [baselineScore, setBaselineScore] = useState<number>(45.0);
  const [targetScore, setTargetScore] = useState<number>(75.0);
  const [assigneeId, setAssigneeId] = useState<string>('EMP-1002');
  const [targetCompletionDate, setTargetCompletionDate] = useState<string>('');

  const createMutation = useCreateActionPlanMutation();

  if (!isOpen) return null;

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    createMutation.mutate(
      {
        projectId,
        campaignId: 'CMP-101',
        nodeId: 'N-301',
        groupId,
        title,
        description,
        baselineScore,
        targetScore,
        assigneeId,
        targetCompletionDate: targetCompletionDate ? new Date(targetCompletionDate).toISOString() : undefined,
        status: 'DRAFT',
      },
      {
        onSuccess: () => {
          onClose();
        },
      }
    );
  };

  return (
    <div style={{ position: 'fixed', inset: 0, zIndex: 100, background: 'rgba(15, 23, 42, 0.6)', backdropFilter: 'blur(4px)', display: 'flex', alignItems: 'center', justifyContent: 'center', padding: '16px' }}>
      <div style={{ background: '#ffffff', borderRadius: '16px', border: '1px solid #e2e8f0', padding: '28px', maxWidth: '520px', width: '100%', fontFamily: 'system-ui, sans-serif' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px', borderBottom: '1px solid #f1f5f9', paddingBottom: '12px' }}>
          <div>
            <h3 style={{ fontSize: '1.2rem', fontWeight: 800, color: '#0f172a', margin: 0 }}>
              Create Remediation Action Plan
            </h3>
            <p style={{ fontSize: '0.8rem', color: '#64748b', margin: '4px 0 0 0' }}>
              Define trackable workplace improvement targets per FR-ACT-007
            </p>
          </div>
          <button onClick={onClose} style={{ background: 'none', border: 'none', fontSize: '1.4rem', color: '#94a3b8', cursor: 'pointer' }}>
            &times;
          </button>
        </div>

        <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
          <div>
            <label style={{ display: 'block', fontSize: '0.75rem', fontWeight: 700, textTransform: 'uppercase', color: '#475569', marginBottom: '4px' }}>
              Action Title
            </label>
            <input
              type="text"
              required
              placeholder="e.g. Workload Rebalancing & Senior Leadership Mentorship"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              style={{ width: '100%', padding: '10px 12px', borderRadius: '8px', border: '1px solid #cbd5e1', fontSize: '0.85rem' }}
            />
          </div>

          <div>
            <label style={{ display: 'block', fontSize: '0.75rem', fontWeight: 700, textTransform: 'uppercase', color: '#475569', marginBottom: '4px' }}>
              Target Category / Group
            </label>
            <select
              value={groupId}
              onChange={(e) => setGroupId(e.target.value)}
              style={{ width: '100%', padding: '8px 12px', borderRadius: '8px', border: '1px solid #cbd5e1', fontSize: '0.85rem' }}
            >
              <option value="QG-01">Leadership Trust (QG-01)</option>
              <option value="QG-02">Workload Balance (QG-02)</option>
              <option value="QG-03">Career Mobility (QG-03)</option>
              <option value="QG-04">Compensation Fairness (QG-04)</option>
            </select>
          </div>

          <div>
            <label style={{ display: 'block', fontSize: '0.75rem', fontWeight: 700, textTransform: 'uppercase', color: '#475569', marginBottom: '4px' }}>
              Description & Action Scope
            </label>
            <textarea
              rows={3}
              required
              placeholder="Detailed description of planned interventions..."
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              style={{ width: '100%', padding: '10px 12px', borderRadius: '8px', border: '1px solid #cbd5e1', fontSize: '0.85rem' }}
            />
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px' }}>
            <div>
              <label style={{ display: 'block', fontSize: '0.75rem', fontWeight: 700, textTransform: 'uppercase', color: '#475569', marginBottom: '4px' }}>
                Baseline Score (%)
              </label>
              <input
                type="number"
                step="0.1"
                required
                value={baselineScore}
                onChange={(e) => setBaselineScore(parseFloat(e.target.value))}
                style={{ width: '100%', padding: '8px 12px', borderRadius: '8px', border: '1px solid #cbd5e1', fontSize: '0.85rem' }}
              />
            </div>

            <div>
              <label style={{ display: 'block', fontSize: '0.75rem', fontWeight: 700, textTransform: 'uppercase', color: '#475569', marginBottom: '4px' }}>
                Target Score (%)
              </label>
              <input
                type="number"
                step="0.1"
                required
                value={targetScore}
                onChange={(e) => setTargetScore(parseFloat(e.target.value))}
                style={{ width: '100%', padding: '8px 12px', borderRadius: '8px', border: '1px solid #cbd5e1', fontSize: '0.85rem' }}
              />
            </div>
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px' }}>
            <div>
              <label style={{ display: 'block', fontSize: '0.75rem', fontWeight: 700, textTransform: 'uppercase', color: '#475569', marginBottom: '4px' }}>
                Assignee Employee ID
              </label>
              <input
                type="text"
                required
                value={assigneeId}
                onChange={(e) => setAssigneeId(e.target.value)}
                style={{ width: '100%', padding: '8px 12px', borderRadius: '8px', border: '1px solid #cbd5e1', fontSize: '0.85rem' }}
              />
            </div>

            <div>
              <label style={{ display: 'block', fontSize: '0.75rem', fontWeight: 700, textTransform: 'uppercase', color: '#475569', marginBottom: '4px' }}>
                Target Completion Date
              </label>
              <input
                type="date"
                value={targetCompletionDate}
                onChange={(e) => setTargetCompletionDate(e.target.value)}
                style={{ width: '100%', padding: '8px 12px', borderRadius: '8px', border: '1px solid #cbd5e1', fontSize: '0.85rem' }}
              />
            </div>
          </div>

          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '10px', marginTop: '12px' }}>
            <Button variant="outline" type="button" onClick={onClose}>
              Cancel
            </Button>
            <Button variant="primary" type="submit" isLoading={createMutation.isPending}>
              Create Action Plan
            </Button>
          </div>
        </form>
      </div>
    </div>
  );
};
