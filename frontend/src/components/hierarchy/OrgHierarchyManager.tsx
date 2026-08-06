import React, { useEffect, useState } from 'react';
import { orgApi } from '../../api/orgApi';
import { CreateNodeRequest, HierarchyAnomalyReport, OrgNode } from '../../types/organization';
import { OrgTreeCanvas } from './OrgTreeCanvas';

interface OrgHierarchyManagerProps {
  projectId: string;
}

export const OrgHierarchyManager: React.FC<OrgHierarchyManagerProps> = ({ projectId }) => {
  const [nodes, setNodes] = useState<OrgNode[]>([]);
  const [anomalyReport, setAnomalyReport] = useState<HierarchyAnomalyReport | null>(null);
  const [selectedNode, setSelectedNode] = useState<OrgNode | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [statusMessage, setStatusMessage] = useState<string | null>(null);

  // Move Modal State
  const [moveModalState, setMoveModalState] = useState<{
    draggedNode: OrgNode;
    targetParent: OrgNode;
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
    parentId: ''
  });

  const loadData = async () => {
    setLoading(true);
    try {
      // Fetch sub-tree from root level
      const rootRes = await orgApi.fetchSubTree(projectId, 'N-001').catch(() => null);
      if (rootRes && rootRes.success) {
        setNodes(rootRes.data);
      } else {
        // Fallback demo dataset if backend not running
        setNodes([
          {
            id: '1',
            projectId: projectId,
            nodeId: 'N-001',
            name: 'Aitken Spence PLC',
            type: 'COMPANY',
            parentId: null,
            path: ',N-001,',
            depth: 1,
            status: 'ACTIVE'
          },
          {
            id: '2',
            projectId: projectId,
            nodeId: 'N-101',
            name: 'Maritime & Logistics Division',
            type: 'DIVISION',
            parentId: 'N-001',
            path: ',N-001,N-101,',
            depth: 2,
            status: 'ACTIVE'
          },
          {
            id: '3',
            projectId: projectId,
            nodeId: 'N-201',
            name: 'Port Terminal Operations Dept',
            type: 'DEPARTMENT',
            parentId: 'N-101',
            path: ',N-001,N-101,N-201,',
            depth: 3,
            status: 'ACTIVE'
          },
          {
            id: '4',
            projectId: projectId,
            nodeId: 'N-301',
            name: 'Container Logistics Team',
            type: 'TEAM',
            parentId: 'N-201',
            path: ',N-001,N-101,N-201,N-301,',
            depth: 4,
            status: 'ACTIVE'
          }
        ]);
      }

      const anomalyRes = await orgApi.fetchAnomalies(projectId).catch(() => null);
      if (anomalyRes && anomalyRes.success) {
        setAnomalyReport(anomalyRes.data);
      }
    } catch (err) {
      console.error('Error loading hierarchy data:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, [projectId]);

  const handleMoveAttempt = (draggedNodeId: string, targetParentId: string) => {
    const draggedNode = nodes.find((n) => n.nodeId === draggedNodeId);
    const targetParent = nodes.find((n) => n.nodeId === targetParentId);

    if (!draggedNode || !targetParent) return;

    // Client-side cycle prevention validation
    if (targetParent.path.includes(`,${draggedNodeId},`)) {
      setStatusMessage(`⚠️ Circular Move Blocked: Target parent '${targetParent.name}' is a child of '${draggedNode.name}'.`);
      return;
    }

    setMoveModalState({ draggedNode, targetParent });
  };

  const confirmMove = async () => {
    if (!moveModalState) return;
    const { draggedNode, targetParent } = moveModalState;

    try {
      const res = await orgApi.moveNode(projectId, draggedNode.nodeId, targetParent.nodeId);
      if (res.success) {
        setStatusMessage(`Successfully moved '${draggedNode.name}' under '${targetParent.name}'.`);
        loadData();
      } else {
        setStatusMessage(`Failed to move node: ${res.message}`);
      }
    } catch (err: any) {
      setStatusMessage(`Network error performing re-parenting move.`);
    } finally {
      setMoveModalState(null);
    }
  };

  const handleCreateNodeSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    const payload: CreateNodeRequest = {
      projectId,
      nodeId: newNodeForm.nodeId.trim(),
      name: newNodeForm.name.trim(),
      type: newNodeForm.type,
      parentId: newNodeForm.parentId ? newNodeForm.parentId.trim() : null
    };

    try {
      const res = await orgApi.createNode(payload);
      if (res.success) {
        setStatusMessage(`Successfully created node '${payload.name}' (${payload.nodeId}).`);
        setShowCreateModal(false);
        setNewNodeForm({ nodeId: '', name: '', type: 'DEPARTMENT', parentId: '' });
        loadData();
      } else {
        setStatusMessage(`Failed to create node: ${res.message}`);
      }
    } catch (err) {
      setStatusMessage(`Network error creating organization node.`);
    }
  };

  return (
    <div style={{ maxWidth: '1100px', margin: '0 auto' }}>
      {/* Header Bar */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px' }}>
        <div>
          <h2 style={{ fontSize: '1.5rem', fontWeight: 800, color: '#0f172a', margin: 0 }}>
            Organizational Hierarchy & Structure Studio
          </h2>
          <p style={{ color: '#64748b', fontSize: '0.9rem', margin: '4px 0 0 0' }}>
            Manage enterprise reporting lines, dynamic node types, and atomic drag-and-drop re-parenting.
          </p>
        </div>
        <button
          onClick={() => setShowCreateModal(true)}
          style={{ background: '#2563eb', color: '#ffffff', border: 'none', padding: '10px 20px', borderRadius: '8px', fontWeight: 600, cursor: 'pointer' }}
        >
          + Add New Node
        </button>
      </div>

      {/* Status Message Notification */}
      {statusMessage && (
        <div style={{ padding: '12px 16px', borderRadius: '8px', background: statusMessage.includes('Failed') || statusMessage.includes('Blocked') ? '#fef2f2' : '#f0fdf4', color: statusMessage.includes('Failed') || statusMessage.includes('Blocked') ? '#991b1b' : '#166534', border: `1px solid ${statusMessage.includes('Failed') || statusMessage.includes('Blocked') ? '#fecaca' : '#bbf7d0'}`, marginBottom: '20px', fontSize: '0.875rem' }}>
          {statusMessage}
        </div>
      )}

      {/* Selected Node Details Card */}
      {selectedNode && (
        <div style={{ background: '#eff6ff', border: '1px solid #bfdbfe', borderRadius: '10px', padding: '16px', marginBottom: '20px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <div>
            <span style={{ fontSize: '0.75rem', fontWeight: 700, background: '#2563eb', color: '#ffffff', padding: '2px 8px', borderRadius: '10px' }}>
              SELECTED NODE
            </span>
            <h4 style={{ margin: '6px 0 2px 0', fontSize: '1.1rem', color: '#1e3a8a' }}>{selectedNode.name} ({selectedNode.nodeId})</h4>
            <div style={{ fontSize: '0.85rem', color: '#3b82f6' }}>
              Type: {selectedNode.type} | Depth: {selectedNode.depth} | Path: <code>{selectedNode.path}</code>
            </div>
          </div>
          <button
            onClick={() => setSelectedNode(null)}
            style={{ background: '#dbeafe', border: 'none', color: '#1e40af', padding: '6px 12px', borderRadius: '6px', cursor: 'pointer', fontWeight: 600 }}
          >
            Clear Selection
          </button>
        </div>
      )}

      {/* AI Hierarchy Anomaly Banner */}
      {anomalyReport && anomalyReport.totalAnomaliesDetected > 0 && (
        <div style={{ background: '#fffbeb', border: '1px solid #fde68a', borderRadius: '10px', padding: '16px', marginBottom: '24px' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '10px', marginBottom: '8px' }}>
            <span style={{ fontSize: '1.2rem' }}>🤖</span>
            <span style={{ fontWeight: 700, color: '#92400e', fontSize: '0.95rem' }}>
              AI Structural Anomaly Inspector ({anomalyReport.totalAnomaliesDetected} Issue Detected)
            </span>
          </div>
          {anomalyReport.anomalies.map((anomaly, idx) => (
            <div key={idx} style={{ fontSize: '0.85rem', color: '#78350f', marginTop: '4px' }}>
              • <strong>[{anomaly.anomalyType}]</strong> Node {anomaly.nodeId}: {anomaly.message} — <em>{anomaly.recommendation}</em>
            </div>
          ))}
        </div>
      )}

      {/* Main Tree Canvas */}
      {loading ? (
        <div style={{ padding: '40px', textAlign: 'center', color: '#64748b' }}>Loading hierarchy tree...</div>
      ) : (
        <OrgTreeCanvas
          nodes={nodes}
          onSelectNode={(n) => setSelectedNode(n)}
          onMoveNodeAttempt={handleMoveAttempt}
        />
      )}

      {/* Re-parenting Move Confirmation Modal */}
      {moveModalState && (
        <div style={{ position: 'fixed', top: 0, left: 0, right: 0, bottom: 0, background: 'rgba(15, 23, 42, 0.6)', display: 'grid', placeItems: 'center', zIndex: 1000 }}>
          <div style={{ background: '#ffffff', padding: '28px', borderRadius: '12px', width: '480px', boxShadow: '0 20px 25px -5px rgba(0, 0, 0, 0.1)' }}>
            <h3 style={{ fontSize: '1.2rem', fontWeight: 700, color: '#0f172a', marginTop: 0 }}>Confirm Re-parenting Move</h3>
            <p style={{ color: '#64748b', fontSize: '0.9rem' }}>
              Are you sure you want to move <strong>{moveModalState.draggedNode.name}</strong> under <strong>{moveModalState.targetParent.name}</strong>?
            </p>

            <div style={{ background: '#f8fafc', padding: '14px', borderRadius: '8px', border: '1px solid #e2e8f0', marginBottom: '20px', fontSize: '0.85rem' }}>
              <div><strong>Current Path:</strong> <code style={{ color: '#dc2626' }}>{moveModalState.draggedNode.path}</code></div>
              <div style={{ marginTop: '6px' }}><strong>Target Parent Path:</strong> <code style={{ color: '#16a34a' }}>{moveModalState.targetParent.path}</code></div>
            </div>

            <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '12px' }}>
              <button
                onClick={() => setMoveModalState(null)}
                style={{ background: '#f1f5f9', color: '#334155', border: 'none', padding: '10px 18px', borderRadius: '6px', fontWeight: 600, cursor: 'pointer' }}
              >
                Cancel
              </button>
              <button
                onClick={confirmMove}
                style={{ background: '#2563eb', color: '#ffffff', border: 'none', padding: '10px 18px', borderRadius: '6px', fontWeight: 600, cursor: 'pointer' }}
              >
                Confirm Move
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Create Node Modal */}
      {showCreateModal && (
        <div style={{ position: 'fixed', top: 0, left: 0, right: 0, bottom: 0, background: 'rgba(15, 23, 42, 0.6)', display: 'grid', placeItems: 'center', zIndex: 1000 }}>
          <form onSubmit={handleCreateNodeSubmit} style={{ background: '#ffffff', padding: '28px', borderRadius: '12px', width: '480px', boxShadow: '0 20px 25px -5px rgba(0, 0, 0, 0.1)' }}>
            <h3 style={{ fontSize: '1.2rem', fontWeight: 700, color: '#0f172a', marginTop: 0 }}>Add New Organization Node</h3>

            <div style={{ marginBottom: '14px' }}>
              <label style={{ display: 'block', fontSize: '0.85rem', fontWeight: 600, color: '#334155', marginBottom: '4px' }}>Node ID</label>
              <input
                type="text"
                required
                value={newNodeForm.nodeId}
                onChange={(e) => setNewNodeForm({ ...newNodeForm, nodeId: e.target.value })}
                placeholder="e.g. N-302"
                style={{ width: '100%', padding: '8px 12px', borderRadius: '6px', border: '1px solid #cbd5e1' }}
              />
            </div>

            <div style={{ marginBottom: '14px' }}>
              <label style={{ display: 'block', fontSize: '0.85rem', fontWeight: 600, color: '#334155', marginBottom: '4px' }}>Node Name</label>
              <input
                type="text"
                required
                value={newNodeForm.name}
                onChange={(e) => setNewNodeForm({ ...newNodeForm, name: e.target.value })}
                placeholder="e.g. Quality Assurance Team"
                style={{ width: '100%', padding: '8px 12px', borderRadius: '6px', border: '1px solid #cbd5e1' }}
              />
            </div>

            <div style={{ marginBottom: '14px' }}>
              <label style={{ display: 'block', fontSize: '0.85rem', fontWeight: 600, color: '#334155', marginBottom: '4px' }}>Node Type</label>
              <select
                value={newNodeForm.type}
                onChange={(e) => setNewNodeForm({ ...newNodeForm, type: e.target.value })}
                style={{ width: '100%', padding: '8px 12px', borderRadius: '6px', border: '1px solid #cbd5e1' }}
              >
                <option value="COMPANY">COMPANY</option>
                <option value="DIVISION">DIVISION</option>
                <option value="DEPARTMENT">DEPARTMENT</option>
                <option value="TEAM">TEAM</option>
                <option value="BRANCH">BRANCH</option>
                <option value="FUNCTION">FUNCTION</option>
              </select>
            </div>

            <div style={{ marginBottom: '20px' }}>
              <label style={{ display: 'block', fontSize: '0.85rem', fontWeight: 600, color: '#334155', marginBottom: '4px' }}>Parent Node ID (Optional for Root)</label>
              <input
                type="text"
                value={newNodeForm.parentId}
                onChange={(e) => setNewNodeForm({ ...newNodeForm, parentId: e.target.value })}
                placeholder="e.g. N-201"
                style={{ width: '100%', padding: '8px 12px', borderRadius: '6px', border: '1px solid #cbd5e1' }}
              />
            </div>

            <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '12px' }}>
              <button
                type="button"
                onClick={() => setShowCreateModal(false)}
                style={{ background: '#f1f5f9', color: '#334155', border: 'none', padding: '10px 18px', borderRadius: '6px', fontWeight: 600, cursor: 'pointer' }}
              >
                Cancel
              </button>
              <button
                type="submit"
                style={{ background: '#2563eb', color: '#ffffff', border: 'none', padding: '10px 18px', borderRadius: '6px', fontWeight: 600, cursor: 'pointer' }}
              >
                Create Node
              </button>
            </div>
          </form>
        </div>
      )}
    </div>
  );
};
