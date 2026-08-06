export type EmployeeStatus = 'ACTIVE' | 'INACTIVE' | 'TERMINATED';

export interface EmployeeProfile {
  id: string;
  projectId: string;
  employeeId: string;
  email?: string;
  fullName?: string;
  phoneNumber?: string;
  nodeId: string;
  matrixNodeIds?: string[];
  status: EmployeeStatus;
  attributes?: Record<string, any>;
  version?: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface CreateEmployeePayload {
  projectId: string;
  employeeId: string;
  email?: string;
  fullName: string;
  phoneNumber?: string;
  nodeId: string;
  matrixNodeIds?: string[];
  status?: EmployeeStatus;
  attributes?: Record<string, any>;
}

export interface UpdateEmployeePayload {
  email?: string;
  fullName?: string;
  phoneNumber?: string;
  nodeId?: string;
  matrixNodeIds?: string[];
  status?: EmployeeStatus;
  attributes?: Record<string, any>;
}

export interface HeaderMapping {
  sourceHeader: string;
  targetAttributeKey: string;
  confidence: number;
  isCoreField: boolean;
}

export interface BulkImportResult {
  jobId: string;
  projectId: string;
  totalProcessed: number;
  insertedCount: number;
  updatedCount: number;
  terminatedCount: number;
  failedCount: number;
  errors: string[];
}
