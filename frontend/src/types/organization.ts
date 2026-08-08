export type NodeStatus = 'ACTIVE' | 'INACTIVE' | 'ARCHIVED';

export type NodeType = 'COMPANY' | 'DIVISION' | 'DEPARTMENT' | 'TEAM' | 'UNIT' | 'LOCATION';

export interface CreateNodeRequest {
  projectId: string;
  nodeId: string;
  name: string;
  type: NodeType | string;
  parentId?: string;
  displayOrder?: number;
  attributes?: Record<string, any>;
}

export interface MoveNodeRequest {
  newParentId?: string;
}

export interface OrgNodeResponse {
  id?: string;
  projectId: string;
  nodeId: string;
  name: string;
  type: string;
  parentId?: string;
  path: string;
  depth: number;
  displayOrder?: number;
  status: NodeStatus;
  attributes?: Record<string, any>;
  version?: number;
  createdAt?: string;
  updatedAt?: string;
  children?: OrgNodeResponse[];
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
