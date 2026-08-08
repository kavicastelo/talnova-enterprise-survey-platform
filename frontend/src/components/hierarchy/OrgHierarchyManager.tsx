import React, { useState } from 'react';
import { CreateNodeRequest, OrgNodeResponse } from '../../types/organization';
import {
  useSubtreeQuery,
  useAnomaliesQuery,
  useCreateNodeMutation,
  useMoveNodeMutation,
} from '../../features/organization/api/useOrgQueries';
import { OrgTreeCanvas } from './OrgTreeCanvas';
import { Card } from '../ui/Card';
import { Button } from '../ui/Button';
import { Badge } from '../ui/Badge';
import { Alert } from '../ui/Alert';
import { Modal } from '../ui/Modal';
import { Input } from '../ui/Input';
import { Select } from '../ui/Select';
import { Skeleton } from '../ui/Skeleton';
import { ErrorState } from '../ui/ErrorState';

interface OrgHierarchyManagerProps {
  projectId: string;
}

export const OrgHierarchyManager: React.FC<OrgHierarchyManagerProps> = ({ projectId }) => {
  const [selectedNode, setSelectedNode] = useState<OrgNodeResponse | null>(null);
  const [statusMessage, setStatusMessage] = useState<string | null>(null);

  // Queries
  const { data: nodes = [], isLoading, isError, refetch } = useSubtreeQuery('N-001', projectId);
  const { data: anomalyReport } = useAnomaliesQuery(projectId);

  // Mutations
  const createMutation = useCreateNodeMutation();
  const moveMutation = useMoveNodeMutation();

  // Move Modal State
  const [moveModalState, setMoveModalState] = useState<{
    draggedNode: OrgNodeResponse;
    targetParent: OrgNodeResponse;
  } | null>(null);

  // Create Node Modal State
  const [showCreateModal, setShowCreateModal] = useState<boolean>(false);
  const [newNodeForm, setNewNodeForm] = useState<{
    nodeId: string;
    name: string;
    type: string;
    parentId: string;
  }>({
    nodeId: '',
    name: '',
    type: 'DEPARTMENT',
    parentId: '',
  });

  const handleMoveAttempt = (draggedNodeId: string, targetParentId: string) => {
    const draggedNode = nodes.find((n) => n.nodeId === draggedNodeId);
    const targetParent = nodes.find((n) => n.nodeId === targetParentId);

    if (!draggedNode || !targetParent) return;

    // Cycle prevention check
    if (targetParent.path.includes(`,${draggedNodeId},`)) {
      setStatusMessage(`⚠️ Circular Move Blocked: Target parent '${targetParent.name}' is a descendant of '${draggedNode.name}'.`);
      return;
    }

    setMoveModalState({ draggedNode, targetParent });
  };

  const confirmMove = () => {
    if (!moveModalState) return;
    const { draggedNode, targetParent } = moveModalState;

    moveMutation.mutate(
      { nodeId: draggedNode.nodeId, newParentId: targetParent.nodeId, projectId },
      {
        onSuccess: () => {
          setStatusMessage(`Successfully moved '${draggedNode.name}' under '${targetParent.name}'.`);
          setMoveModalState(null);
        },
      }
    );
  };

  const handleCreateNodeSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    const payload: CreateNodeRequest = {
      projectId,
      nodeId: newNodeForm.nodeId.trim(),
      name: newNodeForm.name.trim(),
      type: newNodeForm.type,
      parentId: newNodeForm.parentId.trim() || undefined,
    };

    createMutation.mutate(payload, {
      onSuccess: () => {
        setShowCreateModal(false);
        setNewNodeForm({ nodeId: '', name: '', type: 'DEPARTMENT', parentId: '' });
      },
    });
  };

  if (isLoading) {
    return (
      <Card variant="bordered" padding="24px">
        <Skeleton height="320px" borderRadius="12px" />
      </Card>
    );
  }

  if (isError) {
    return (
      <Card variant="bordered" padding="24px">
        <ErrorState
          title="Failed to Load Organization Hierarchy"
          message="Could not retrieve organizational tree sub-tree from organization-service via Gateway."
          onRetry={refetch}
        />
      </Card>
    );
  }

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
      {/* Header Controls */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <h2 style={{ fontSize: '1.4rem', fontWeight: 800, color: '#0f172a', margin: 0 }}>
            Organizational Hierarchy & Tree Canvas
          </h2>
          <p style={{ color: '#64748b', fontSize: '0.85rem', margin: '4px 0 0 0' }}>
            Manage reporting trees, materialized path lineage, and atomic drag-and-drop re-parenting.
          </p>
        </div>
        <Button variant="primary" onClick={() => setShowCreateModal(true)}>
          + Add New Node
        </Button>
      </div>

      {/* Status Alert Notification */}
      {statusMessage && (
        <Alert
          type={statusMessage.includes('Blocked') ? 'error' : 'info'}
          title="Action Feedback"
        >
          {statusMessage}
        </Alert>
      )}

      {/* Selected Node Details Card */}
      {selectedNode && (
        <div style={{ background: '#eff6ff', border: '1px solid #bfdbfe', borderRadius: '10px', padding: '16px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <div>
            <Badge variant="info">SELECTED NODE</Badge>
            <h4 style={{ margin: '6px 0 2px 0', fontSize: '1.1rem', color: '#1e3a8a' }}>{selectedNode.name} ({selectedNode.nodeId})</h4>
            <div style={{ fontSize: '0.85rem', color: '#3b82f6' }}>
              Type: {selectedNode.type} | Depth: {selectedNode.depth} | Path: <code>{selectedNode.path}</code>
            </div>
          </div>
          <Button variant="secondary" size="sm" onClick={() => setSelectedNode(null)}>
            Clear Selection
          </Button>
        </div>
      )}

      {/* AI Hierarchy Anomaly Banner */}
      {anomalyReport && anomalyReport.totalAnomaliesDetected > 0 && (
        <Alert type="warning" title={`AI Structural Anomaly Inspector (${anomalyReport.totalAnomaliesDetected} Issue Detected)`}>
          {anomalyReport.anomalies.map((anomaly, idx) => (
            <div key={idx} style={{ marginTop: '4px', fontSize: '0.85rem' }}>
              • <strong>[{anomaly.anomalyType}]</strong> Node <code>{anomaly.nodeId}</code>: {anomaly.message} — <em>{anomaly.recommendation}</em>
            </div>
          ))}
        </Alert>
      )}

      {/* Main Tree Canvas */}
      <OrgTreeCanvas
        nodes={nodes}
        onSelectNode={(n) => setSelectedNode(n)}
        onMoveNodeAttempt={handleMoveAttempt}
      />

      {/* Re-parenting Move Confirmation Modal */}
      <Modal isOpen={!!moveModalState} onClose={() => setMoveModalState(null)} size="md">
        {moveModalState && (
          <div>
            <h3 style={{ fontSize: '1.2rem', fontWeight: 700, color: '#0f172a', marginTop: 0 }}>Confirm Re-parenting Move</h3>
            <p style={{ color: '#64748b', fontSize: '0.9rem' }}>
              Are you sure you want to move <strong>{moveModalState.draggedNode.name}</strong> under <strong>{moveModalState.targetParent.name}</strong>?
            </p>

            <div style={{ background: '#f8fafc', padding: '14px', borderRadius: '8px', border: '1px solid #e2e8f0', marginBottom: '20px', fontSize: '0.85rem' }}>
              <div><strong>Current Path:</strong> <code style={{ color: '#dc2626' }}>{moveModalState.draggedNode.path}</code></div>
              <div style={{ marginTop: '6px' }}><strong>Target Parent Path:</strong> <code style={{ color: '#16a34a' }}>{moveModalState.targetParent.path}</code></div>
            </div>

            <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '12px' }}>
              <Button variant="secondary" onClick={() => setMoveModalState(null)}>
                Cancel
              </Button>
              <Button variant="primary" onClick={confirmMove} isLoading={moveMutation.isPending}>
                Confirm Re-parenting Move
              </Button>
            </div>
          </div>
        )}
      </Modal>

      {/* Create Node Modal */}
      <Modal isOpen={showCreateModal} onClose={() => setShowCreateModal(false)} size="md">
        <form onSubmit={handleCreateNodeSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
          <h3 style={{ fontSize: '1.2rem', fontWeight: 700, color: '#0f172a', margin: 0 }}>Add New Organization Node</h3>

          <Input
            label="Node ID (N-XXX)"
            value={newNodeForm.nodeId}
            onChange={(e) => setNewNodeForm({ ...newNodeForm, nodeId: e.target.value })}
            placeholder="N-302"
            helperText="Must match pattern ^N-[A-Za-z0-9_-]{3,20}$"
            required
          />

          <Input
            label="Node Name"
            value={newNodeForm.name}
            onChange={(e) => setNewNodeForm({ ...newNodeForm, name: e.target.value })}
            placeholder="Quality Assurance Team"
            required
          />

          <Select
            label="Node Type"
            value={newNodeForm.type}
            onChange={(e) => setNewNodeForm({ ...newNodeForm, type: e.target.value })}
            options={[
              { value: 'COMPANY', label: 'COMPANY' },
              { value: 'DIVISION', label: 'DIVISION' },
              { value: 'DEPARTMENT', label: 'DEPARTMENT' },
              { value: 'TEAM', label: 'TEAM' },
              { value: 'UNIT', label: 'UNIT' },
              { value: 'LOCATION', label: 'LOCATION' },
            ]}
          />

          <Input
            label="Parent Node ID (Optional for Root)"
            value={newNodeForm.parentId}
            onChange={(e) => setNewNodeForm({ ...newNodeForm, parentId: e.target.value })}
            placeholder="N-201"
          />

          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '12px', marginTop: '12px' }}>
            <Button type="button" variant="secondary" onClick={() => setShowCreateModal(false)}>
              Cancel
            </Button>
            <Button type="submit" variant="primary" isLoading={createMutation.isPending}>
              Create Node
            </Button>
          </div>
        </form>
      </Modal>
    </div>
  );
};
