import React, { useState } from 'react';
import { CreateNodeRequest, OrgNodeResponse } from '../../types/organization';
import {
  useSubtreeQuery,
  useAnomaliesQuery,
  useCreateNodeMutation,
  useMoveNodeMutation,
  useLineageQuery,
  useDeleteNodeMutation,
} from '../../features/organization/api/useOrgQueries';
import { OrgTreeCanvas } from './OrgTreeCanvas';
import { Card } from '../ui/Card';
import { Button } from '../ui/Button';
import { Badge } from '../ui/Badge';
import { Alert } from '../ui/Alert';
import { Modal } from '../ui/Modal';
import { ConfirmDialog } from '../ui/ConfirmDialog';
import { Input } from '../ui/Input';
import { Select } from '../ui/Select';
import { Skeleton } from '../ui/Skeleton';
import { ErrorState } from '../ui/ErrorState';

interface OrgHierarchyManagerProps {
  projectId: string;
  initialSelectedNodeId?: string;
}

export const OrgHierarchyManager: React.FC<OrgHierarchyManagerProps> = ({ projectId, initialSelectedNodeId }) => {
  const [selectedNode, setSelectedNode] = useState<OrgNodeResponse | null>(null);
  const [statusMessage, setStatusMessage] = useState<string | null>(null);
  const [nodeToDelete, setNodeToDelete] = useState<OrgNodeResponse | null>(null);

  // Queries
  const { data: nodes = [], isLoading, isError, refetch } = useSubtreeQuery('N-001', projectId);

  React.useEffect(() => {
    if (initialSelectedNodeId && nodes.length > 0) {
      const match = nodes.find((n) => n.nodeId === initialSelectedNodeId);
      if (match) {
        setSelectedNode(match);
      }
    }
  }, [initialSelectedNodeId, nodes]);
  const { data: anomalyReport } = useAnomaliesQuery(projectId);
  const { data: lineageNodes = [], isLoading: isLineageLoading } = useLineageQuery(selectedNode?.nodeId, projectId);

  // Mutations
  const createMutation = useCreateNodeMutation();
  const moveMutation = useMoveNodeMutation();
  const deleteMutation = useDeleteNodeMutation();

  // Move Modal State
  const [moveModalState, setMoveModalState] = useState<{
    draggedNode: OrgNodeResponse;
    targetParent: OrgNodeResponse;
  } | null>(null);

  // Create Node Modal State
  const [showCreateModal, setShowCreateModal] = useState<boolean>(false);
  const [validationError, setValidationError] = useState<string | null>(null);
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

    // VR-ORG-004: Cycle prevention check
    if (targetParent.path.includes(`,${draggedNodeId},`) || targetParent.nodeId === draggedNodeId) {
      setStatusMessage(`⚠️ Circular Move Blocked (VR-ORG-004): Target parent '${targetParent.name}' is a descendant of '${draggedNode.name}'.`);
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

  const handleDeleteAttempt = (node: OrgNodeResponse) => {
    // FR-ORG-006: Deletion Integrity Guard Check
    const hasChildren = nodes.some((n) => n.parentId === node.nodeId);
    if (hasChildren) {
      setStatusMessage(`⚠️ Deletion Blocked (FR-ORG-006): Node '${node.name}' (${node.nodeId}) contains active child nodes. Re-parent or delete child nodes first.`);
      return;
    }
    setNodeToDelete(node);
  };

  const confirmDelete = () => {
    if (!nodeToDelete) return;
    deleteMutation.mutate(
      { nodeId: nodeToDelete.nodeId, projectId },
      {
        onSuccess: () => {
          setSelectedNode(null);
          setNodeToDelete(null);
        },
      }
    );
  };

  const handleCreateNodeSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setValidationError(null);

    // VR-ORG-001: Node ID regex validation
    const nodeRegex = /^N-[A-Za-z0-9_-]{3,20}$/;
    if (!nodeRegex.test(newNodeForm.nodeId.trim())) {
      setValidationError('Node ID must match pattern ^N-[A-Za-z0-9_-]{3,20}$ (VR-ORG-001, e.g. N-301).');
      return;
    }

    if (!newNodeForm.name.trim()) {
      setValidationError('Node Name is required.');
      return;
    }

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
      onError: (err: any) => {
        setValidationError(err.message || 'Failed to create organization node.');
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
        <Button variant="primary" onClick={() => { setValidationError(null); setShowCreateModal(true); }}>
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

      {/* Selected Node Details Card & Ancestor Lineage Breadcrumb (FR-ORG-005) */}
      {selectedNode && (
        <div style={{ background: '#eff6ff', border: '1px solid #bfdbfe', borderRadius: '10px', padding: '20px', display: 'flex', flexDirection: 'column', gap: '12px' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '12px' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
              <Badge variant="indigo">SELECTED NODE</Badge>
              <Badge variant={selectedNode.status === 'INACTIVE' ? 'warning' : selectedNode.status === 'ARCHIVED' ? 'neutral' : 'success'}>
                {selectedNode.status || 'ACTIVE'}
              </Badge>
              <h4 style={{ margin: 0, fontSize: '1.15rem', color: '#1e3a8a', fontWeight: 800 }}>{selectedNode.name}</h4>
              <code style={{ fontSize: '0.8rem', color: '#1d4ed8', background: '#dbeafe', padding: '2px 6px', borderRadius: '4px' }}>{selectedNode.nodeId}</code>
            </div>
            <div style={{ display: 'flex', gap: '8px', flexWrap: 'wrap' }}>
              <Button
                variant="primary"
                size="sm"
                onClick={() => {
                  setValidationError(null);
                  setNewNodeForm({
                    nodeId: '',
                    name: '',
                    type: 'TEAM',
                    parentId: selectedNode.nodeId,
                  });
                  setShowCreateModal(true);
                }}
              >
                + Add Child Under Node
              </Button>
              <Button
                variant="secondary"
                size="sm"
                onClick={() => {
                  setStatusMessage(null);
                  setValidationError(null);
                  setMoveModalState(null);
                  // Trigger move picker for selectedNode
                  const firstValidParent = nodes.find(
                    (n) => n.nodeId !== selectedNode.nodeId && !n.path.includes(`,${selectedNode.nodeId},`)
                  );
                  if (firstValidParent) {
                    setMoveModalState({ draggedNode: selectedNode, targetParent: firstValidParent });
                  } else {
                    setStatusMessage(`⚠️ No valid target parent available for node '${selectedNode.name}'.`);
                  }
                }}
              >
                📦 Move / Re-parent Node
              </Button>

              {/* Deletion Guard (FR-ORG-006) Inline Feedback */}
              {nodes.some((n) => n.parentId === selectedNode.nodeId) ? (
                <Button
                  variant="secondary"
                  size="sm"
                  onClick={() => handleDeleteAttempt(selectedNode)}
                  title="Node contains active child nodes. Re-parent children first before soft deleting."
                  style={{ border: '1px solid #fca5a5', color: '#dc2626', background: '#fff5f5' }}
                >
                  🚫 Delete Blocked ({nodes.filter((n) => n.parentId === selectedNode.nodeId).length} Children)
                </Button>
              ) : (
                <Button variant="danger" size="sm" onClick={() => handleDeleteAttempt(selectedNode)}>
                  Delete Node (FR-ORG-006)
                </Button>
              )}

              <Button variant="secondary" size="sm" onClick={() => setSelectedNode(null)}>
                Clear Selection
              </Button>
            </div>
          </div>

          {/* Ancestor Lineage Path (FR-ORG-005) */}
          <div style={{ background: '#ffffff', padding: '12px 16px', borderRadius: '8px', border: '1px solid #dbeafe' }}>
            <div style={{ fontSize: '0.75rem', fontWeight: 700, color: '#64748b', textTransform: 'uppercase', marginBottom: '6px' }}>
              ANCESTOR LINEAGE PATH (FR-ORG-005 — Click node to inspect)
            </div>
            {isLineageLoading ? (
              <Skeleton height="24px" borderRadius="4px" />
            ) : lineageNodes.length > 0 ? (
              <div style={{ display: 'flex', alignItems: 'center', flexWrap: 'wrap', gap: '6px', fontSize: '0.875rem' }}>
                {lineageNodes.map((ancestor, index) => (
                  <React.Fragment key={ancestor.nodeId}>
                    <button
                      type="button"
                      onClick={() => {
                        const found = nodes.find((n) => n.nodeId === ancestor.nodeId);
                        if (found) setSelectedNode(found);
                      }}
                      style={{
                        background: ancestor.nodeId === selectedNode.nodeId ? '#dbeafe' : '#f1f5f9',
                        border: ancestor.nodeId === selectedNode.nodeId ? '1px solid #93c5fd' : '1px solid #cbd5e1',
                        borderRadius: '6px',
                        padding: '3px 8px',
                        fontWeight: ancestor.nodeId === selectedNode.nodeId ? 700 : 600,
                        color: ancestor.nodeId === selectedNode.nodeId ? '#1d4ed8' : '#334155',
                        cursor: 'pointer',
                        fontSize: '0.825rem',
                      }}
                    >
                      {ancestor.name} ({ancestor.type})
                    </button>
                    {index < lineageNodes.length - 1 && <span style={{ color: '#94a3b8', fontWeight: 700 }}>→</span>}
                  </React.Fragment>
                ))}
              </div>
            ) : (
              <code style={{ fontSize: '0.85rem', color: '#3b82f6' }}>{selectedNode.path}</code>
            )}
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: '12px', marginTop: '4px' }}>
            <div style={{ background: '#ffffff', padding: '10px 14px', borderRadius: '8px', border: '1px solid #dbeafe' }}>
              <div style={{ fontSize: '0.75rem', color: '#64748b', fontWeight: 600 }}>Node Depth</div>
              <div style={{ fontSize: '1.1rem', fontWeight: 800, color: '#1e3a8a' }}>Level {selectedNode.depth}</div>
            </div>
            <div style={{ background: '#ffffff', padding: '10px 14px', borderRadius: '8px', border: '1px solid #dbeafe' }}>
              <div style={{ fontSize: '0.75rem', color: '#64748b', fontWeight: 600 }}>Direct Child Nodes</div>
              <div style={{ fontSize: '1.1rem', fontWeight: 800, color: '#0284c7' }}>
                {nodes.filter((n) => n.parentId === selectedNode.nodeId).length} node(s)
              </div>
            </div>
            <div style={{ background: '#ffffff', padding: '10px 14px', borderRadius: '8px', border: '1px solid #dbeafe' }}>
              <div style={{ fontSize: '0.75rem', color: '#64748b', fontWeight: 600 }}>Total Sub-Tree Scope</div>
              <div style={{ fontSize: '1.1rem', fontWeight: 800, color: '#16a34a' }}>
                {nodes.filter((n) => n.path.includes(`,${selectedNode.nodeId},`)).length} total node(s)
              </div>
            </div>
          </div>
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
        onCreateRootNode={() => {
          setValidationError(null);
          setNewNodeForm({ nodeId: '', name: '', type: 'COMPANY', parentId: '' });
          setShowCreateModal(true);
        }}
      />

      {/* Confirm Soft Delete Modal */}
      <ConfirmDialog
        isOpen={!!nodeToDelete}
        onClose={() => setNodeToDelete(null)}
        onConfirm={confirmDelete}
        title="Confirm Soft Delete Organization Node"
        message={`Are you sure you want to delete node ${nodeToDelete?.name} (${nodeToDelete?.nodeId})? (FR-ORG-006 Deletion Integrity Guard).`}
        confirmText="Delete Node"
        isLoading={deleteMutation.isPending}
      />

      {/* Re-parenting Move Confirmation Modal */}
      <Modal isOpen={!!moveModalState} onClose={() => setMoveModalState(null)} size="md">
        {moveModalState && (
          <div>
            <h3 style={{ fontSize: '1.2rem', fontWeight: 700, color: '#0f172a', marginTop: 0 }}>Confirm Re-parenting Move</h3>
            <p style={{ color: '#64748b', fontSize: '0.9rem' }}>
              Select target parent node to move <strong>{moveModalState.draggedNode.name}</strong> (and its descendant sub-tree):
            </p>

            {/* Target Parent Selector with Cycle Prevention Filter (VR-ORG-004) */}
            <div style={{ marginBottom: '16px' }}>
              <Select
                label="Target New Parent Node (VR-ORG-004 Guarded)"
                value={moveModalState.targetParent.nodeId}
                onChange={(e) => {
                  const newParent = nodes.find((n) => n.nodeId === e.target.value);
                  if (newParent) {
                    if (newParent.path.includes(`,${moveModalState.draggedNode.nodeId},`) || newParent.nodeId === moveModalState.draggedNode.nodeId) {
                      setStatusMessage(`⚠️ Circular Move Blocked (VR-ORG-004): '${newParent.name}' is a descendant of '${moveModalState.draggedNode.name}'.`);
                      return;
                    }
                    setMoveModalState({ ...moveModalState, targetParent: newParent });
                  }
                }}
                options={nodes
                  .filter((n) => n.nodeId !== moveModalState.draggedNode.nodeId)
                  .map((node) => {
                    const isDescendant = node.path.includes(`,${moveModalState.draggedNode.nodeId},`);
                    return {
                      value: node.nodeId,
                      label: isDescendant
                        ? `🚫 ${node.name} (${node.type}) [CIRCULAR - INVALID TARGET]`
                        : `${node.name} (${node.type}) [${node.nodeId}]`,
                      disabled: isDescendant,
                    };
                  })}
              />
            </div>

            <div style={{ background: '#f8fafc', padding: '14px', borderRadius: '8px', border: '1px solid #e2e8f0', marginBottom: '20px', fontSize: '0.85rem' }}>
              <div><strong>Node to Move:</strong> {moveModalState.draggedNode.name} (<code>{moveModalState.draggedNode.nodeId}</code>)</div>
              <div style={{ marginTop: '6px' }}><strong>Target Parent:</strong> {moveModalState.targetParent.name} (<code>{moveModalState.targetParent.nodeId}</code>)</div>
              <div style={{ marginTop: '6px' }}><strong>Current Path:</strong> <code style={{ color: '#dc2626' }}>{moveModalState.draggedNode.path}</code></div>
              <div style={{ marginTop: '6px' }}><strong>Target Parent Path:</strong> <code style={{ color: '#16a34a' }}>{moveModalState.targetParent.path}</code></div>
              <div style={{ marginTop: '10px', padding: '8px 12px', background: '#eff6ff', borderRadius: '6px', color: '#1e4ed8', fontWeight: 600 }}>
                ⚡ Sub-Tree Impact (FR-ORG-003): {nodes.filter((n) => n.path.includes(`,${moveModalState.draggedNode.nodeId},`)).length} node(s) will be updated atomically in MongoDB session transaction.
              </div>
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

          {validationError && (
            <Alert type="error" title="Validation Error">
              {validationError}
            </Alert>
          )}

          <Input
            label="Node ID (VR-ORG-001)"
            value={newNodeForm.nodeId}
            onChange={(e) => setNewNodeForm({ ...newNodeForm, nodeId: e.target.value.toUpperCase() })}
            placeholder="e.g. N-301"
            helperText="Must match pattern ^N-[A-Za-z0-9_-]{3,20}$ (e.g. N-301)"
            required
          />

          <Input
            label="Node Name"
            value={newNodeForm.name}
            onChange={(e) => setNewNodeForm({ ...newNodeForm, name: e.target.value })}
            placeholder="e.g. Engineering & Technology Department"
            required
          />

          <Select
            label="Node Type (VR-ORG-003)"
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

          <Select
            label="Parent Node (Optional for Top Level Root)"
            value={newNodeForm.parentId}
            onChange={(e) => setNewNodeForm({ ...newNodeForm, parentId: e.target.value })}
            options={[
              { value: '', label: '(None — Top Level Root Node)' },
              ...nodes.map((node) => ({
                value: node.nodeId,
                label: `${node.name} (${node.type}) [${node.nodeId}]`,
              })),
            ]}
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
