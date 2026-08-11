import React, { useState } from 'react';
import { OrgNodeResponse } from '../../types/organization';
import { Badge } from '../ui/Badge';
import { EmptyState } from '../ui/EmptyState';
import { Button } from '../ui/Button';

interface OrgTreeCanvasProps {
  nodes: OrgNodeResponse[];
  onSelectNode?: (node: OrgNodeResponse) => void;
  onMoveNodeAttempt?: (draggedNodeId: string, targetParentId: string) => void;
  onCreateRootNode?: () => void;
}

interface TreeNodeProps {
  node: OrgNodeResponse;
  allNodes: OrgNodeResponse[];
  level: number;
  searchFilter: string;
  onSelectNode?: (node: OrgNodeResponse) => void;
  onMoveNodeAttempt?: (draggedNodeId: string, targetParentId: string) => void;
}

const getNodeBadgeVariant = (type: string): 'success' | 'warning' | 'info' | 'danger' | 'neutral' => {
  switch (type.toUpperCase()) {
    case 'COMPANY':
      return 'info';
    case 'DIVISION':
      return 'neutral';
    case 'DEPARTMENT':
      return 'success';
    case 'TEAM':
      return 'warning';
    default:
      return 'neutral';
  }
};

const TreeNode: React.FC<TreeNodeProps> = ({
  node,
  allNodes,
  level,
  searchFilter,
  onSelectNode,
  onMoveNodeAttempt,
}) => {
  const [isExpanded, setIsExpanded] = useState<boolean>(true);
  const children = allNodes.filter((n) => n.parentId === node.nodeId);

  const matchesSearch =
    !searchFilter ||
    node.name.toLowerCase().includes(searchFilter.toLowerCase()) ||
    node.type.toLowerCase().includes(searchFilter.toLowerCase()) ||
    node.nodeId.toLowerCase().includes(searchFilter.toLowerCase());

  const handleDragStart = (e: React.DragEvent) => {
    e.dataTransfer.setData('text/plain', node.nodeId);
  };

  const handleDragOver = (e: React.DragEvent) => {
    e.preventDefault();
  };

  const handleDrop = (e: React.DragEvent) => {
    e.preventDefault();
    const draggedNodeId = e.dataTransfer.getData('text/plain');
    if (draggedNodeId && draggedNodeId !== node.nodeId && onMoveNodeAttempt) {
      onMoveNodeAttempt(draggedNodeId, node.nodeId);
    }
  };

  if (!matchesSearch && children.length === 0) {
    return null;
  }

  return (
    <div style={{ marginLeft: `${level * 20}px`, marginTop: '8px', marginBottom: '8px' }}>
      <div
        draggable
        onDragStart={handleDragStart}
        onDragOver={handleDragOver}
        onDrop={handleDrop}
        onClick={() => onSelectNode && onSelectNode(node)}
        style={{
          display: 'flex',
          alignItems: 'center',
          gap: '12px',
          padding: '12px 16px',
          background: '#ffffff',
          borderRadius: '8px',
          border: '1px solid #e2e8f0',
          boxShadow: '0 1px 3px rgba(0,0,0,0.05)',
          cursor: 'pointer',
          transition: 'all 0.2s ease',
        }}
      >
        {children.length > 0 ? (
          <button
            onClick={(e) => {
              e.stopPropagation();
              setIsExpanded(!isExpanded);
            }}
            style={{
              background: '#f1f5f9',
              border: 'none',
              borderRadius: '4px',
              width: '24px',
              height: '24px',
              fontWeight: 700,
              cursor: 'pointer',
              display: 'grid',
              placeItems: 'center',
            }}
          >
            {isExpanded ? '−' : '+'}
          </button>
        ) : (
          <div style={{ width: '24px' }} />
        )}

        <div style={{ flex: 1 }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <span style={{ fontWeight: 700, color: '#0f172a', fontSize: '0.95rem' }}>{node.name}</span>
            <span style={{ fontSize: '0.75rem', color: '#64748b', fontFamily: 'monospace' }}>({node.nodeId})</span>
          </div>
          <div style={{ fontSize: '0.8rem', color: '#94a3b8', marginTop: '2px' }}>
            Path: <code style={{ fontSize: '0.75rem', background: '#f8fafc', padding: '2px 4px', borderRadius: '4px' }}>{node.path}</code>
          </div>
        </div>

        <Badge variant={getNodeBadgeVariant(node.type)}>{node.type}</Badge>

        <span style={{ fontSize: '0.75rem', background: '#eff6ff', color: '#1d4ed8', padding: '2px 8px', borderRadius: '10px', fontWeight: 600 }}>
          Depth {node.depth}
        </span>

        {children.length > 0 && (
          <span style={{ fontSize: '0.75rem', background: '#f1f5f9', color: '#475569', padding: '2px 8px', borderRadius: '10px', fontWeight: 600 }}>
            {children.length} {children.length === 1 ? 'child' : 'children'}
          </span>
        )}
      </div>

      {isExpanded && children.length > 0 && (
        <div style={{ borderLeft: '2px dashed #cbd5e1', marginLeft: `${level * 20 + 12}px`, paddingLeft: '8px' }}>
          {children.map((child) => (
            <TreeNode
              key={child.nodeId}
              node={child}
              allNodes={allNodes}
              level={1}
              searchFilter={searchFilter}
              onSelectNode={onSelectNode}
              onMoveNodeAttempt={onMoveNodeAttempt}
            />
          ))}
        </div>
      )}
    </div>
  );
};

export const OrgTreeCanvas: React.FC<OrgTreeCanvasProps> = ({ nodes, onSelectNode, onMoveNodeAttempt, onCreateRootNode }) => {
  const [searchFilter, setSearchFilter] = useState<string>('');

  const rootNodes = nodes.filter((n) => !n.parentId || n.parentId.trim() === '');

  return (
    <div style={{ background: '#f8fafc', padding: '24px', borderRadius: '12px', border: '1px solid #e2e8f0' }}>
      {/* Search Filter Header Bar */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
        <div>
          <h3 style={{ fontSize: '1.2rem', fontWeight: 700, color: '#0f172a', margin: 0 }}>
            Organizational Hierarchy Canvas
          </h3>
          <p style={{ color: '#64748b', fontSize: '0.85rem', margin: '4px 0 0 0' }}>
            Materialized path tree visualizer with arbitrary depth exploration and drag-and-drop re-parenting.
          </p>
        </div>
        {nodes.length > 0 && (
          <div style={{ display: 'flex', gap: '8px', alignItems: 'center' }}>
            <input
              type="text"
              value={searchFilter}
              onChange={(e) => setSearchFilter(e.target.value)}
              placeholder="🔍 Search node name or type..."
              style={{ padding: '8px 14px', borderRadius: '8px', border: '1px solid #cbd5e1', fontSize: '0.875rem', width: '240px' }}
            />
            {searchFilter && (
              <Button variant="secondary" size="sm" onClick={() => setSearchFilter('')}>
                Clear
              </Button>
            )}
          </div>
        )}
      </div>

      {nodes.length === 0 ? (
        <EmptyState
          title="No Organization Nodes Created Yet"
          description="Create your root enterprise organization node (e.g. Company or Head Office) to build your reporting tree."
          action={
            onCreateRootNode ? (
              <Button variant="primary" size="sm" onClick={onCreateRootNode}>
                + Create Root Organization Node
              </Button>
            ) : undefined
          }
        />
      ) : rootNodes.length === 0 ? (
        <EmptyState
          title="No Root Nodes Found"
          description="No top-level root organization nodes exist in this workspace hierarchy."
          action={
            onCreateRootNode ? (
              <Button variant="primary" size="sm" onClick={onCreateRootNode}>
                + Create Root Organization Node
              </Button>
            ) : undefined
          }
        />
      ) : (
        <div>
          {rootNodes.map((rootNode) => (
            <TreeNode
              key={rootNode.nodeId}
              node={rootNode}
              allNodes={nodes}
              level={0}
              searchFilter={searchFilter}
              onSelectNode={onSelectNode}
              onMoveNodeAttempt={onMoveNodeAttempt}
            />
          ))}
        </div>
      )}
    </div>
  );
};
