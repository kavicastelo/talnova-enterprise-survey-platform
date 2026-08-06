export type NodeStatus = 'ACTIVE' | 'INACTIVE' | 'ARCHIVED';

export interface OrgNode {
  id: string;
  projectId: string;
  nodeId: string;
  name: string;
  type: string;
  parentId?: string | null;
  path: string;
  depth: number;
  displayOrder?: number;
  status: NodeStatus;
  attributes?: Record<string, any>;
  version?: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface CreateNodeRequest {
  projectId: string;
  nodeId: string;
  name: string;
  type: string;
  parentId?: string | null;
  displayOrder?: number;
  attributes?: Record<string, any>;
}

export interface MoveNodeRequest {
  newParentId?: string | null;
}

export interface AnomalyDetail {
  anomalyType: string;
  nodeId: string;
  severity: 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
  message: string;
  recommendation: string;
}

export interface HierarchyAnomalyReport {
  projectId: string;
  totalNodesInspected: number;
  totalAnomaliesDetected: number;
  anomalies: AnomalyDetail[];
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
  correlationId?: string;
}
