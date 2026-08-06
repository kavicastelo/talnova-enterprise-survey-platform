import React, { useState } from 'react';
import { OrgNode } from '../../types/organization';

interface OrgTreeCanvasProps {
  nodes: OrgNode[];
  onSelectNode?: (node: OrgNode) => void;
  onMoveNodeAttempt?: (draggedNodeId: string, targetParentId: string) => void;
}

interface TreeNodeProps {
  node: OrgNode;
  allNodes: OrgNode[];
  level: number;
  searchFilter: string;
  onSelectNode?: (node: OrgNode) => void;
  onMoveNodeAttempt?: (draggedNodeId: string, targetParentId: string) => void;
}

const getNodeTypeBadgeColor = (type: string): { bg: string; color: string } => {
  switch (type.toUpperCase()) {
    case 'COMPANY':
      return { bg: '#1e3a8a', color: '#ffffff' };
    case 'DIVISION':
      return { bg: '#2563eb', color: '#ffffff' };
    case 'DEPARTMENT':
      return { bg: '#0284c7', color: '#ffffff' };
    case 'TEAM':
      return { bg: '#16a34a', color: '#ffffff' };
    default:
      return { bg: '#64748b', color: '#ffffff' };
  }
};

const TreeNode: React.FC<TreeNodeProps> = ({
  node,
  allNodes,
  level,
  searchFilter,
  onSelectNode,
  onMoveNodeAttempt
}) => {
  const [isExpanded, setIsExpanded] = useState<boolean>(true);
  const children = allNodes.filter((n) => n.parentId === node.nodeId);

  const matchesSearch =
    !searchFilter ||
    node.name.toLowerCase().includes(searchFilter.toLowerCase()) ||
    node.type.toLowerCase().includes(searchFilter.toLowerCase()) ||
    node.nodeId.toLowerCase().includes(searchFilter.toLowerCase());

  const badgeStyle = getNodeTypeBadgeColor(node.type);

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
    <div style={{ marginLeft: `${level * 24}px`, marginTop: '8px', marginBottom: '8px' }}>
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
          transition: 'all 0.2s ease'
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
              placeItems: 'center'
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

        <span
          style={{
            fontSize: '0.75rem',
            fontWeight: 700,
            padding: '4px 10px',
            borderRadius: '12px',
            background: badgeStyle.bg,
            color: badgeStyle.color
          }}
        >
          {node.type}
        </span>

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
        <div style={{ borderLeft: '2px dashed #cbd5e1', marginLeft: `${level * 24 + 12}px`, paddingLeft: '8px' }}>
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

export const OrgTreeCanvas: React.FC<OrgTreeCanvasProps> = ({ nodes, onSelectNode, onMoveNodeAttempt }) => {
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
        <input
          type="text"
          value={searchFilter}
          onChange={(e) => setSearchFilter(e.target.value)}
          placeholder="🔍 Search node name or type..."
          style={{ padding: '8px 14px', borderRadius: '8px', border: '1px solid #cbd5e1', fontSize: '0.875rem', width: '260px' }}
        />
      </div>

      {rootNodes.length === 0 ? (
        <div style={{ padding: '32px', textAlign: 'center', color: '#64748b', background: '#ffffff', borderRadius: '8px', border: '1px dashed #cbd5e1' }}>
          No root organization nodes found for this workspace.
        </div>
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
