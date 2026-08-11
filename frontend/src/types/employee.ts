export type EmployeeStatus = 'ACTIVE' | 'INACTIVE' | 'TERMINATED';

export interface CreateEmployeeRequest {
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

export interface UpdateEmployeeRequest {
  email?: string;
  fullName?: string;
  phoneNumber?: string;
  nodeId?: string;
  matrixNodeIds?: string[];
  status?: EmployeeStatus;
  attributes?: Record<string, any>;
}

export interface EmployeeResponse {
  id?: string;
  projectId: string;
  employeeId: string;
  email?: string;
  fullName: string;
  phoneNumber?: string;
  nodeId: string;
  matrixNodeIds?: string[];
  status: EmployeeStatus;
  attributes?: Record<string, any>;
  version?: number;
  createdAt?: string;
  updatedAt?: string;
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

export interface HrisSyncRequest {
  projectId: string;
  provider: 'WORKDAY_RAAS' | 'SUCCESSFACTORS_ODATA' | 'BAMBOOHR';
  apiEndpoint: string;
  apiKey?: string;
  autoTerminateMissing?: boolean;
}

export interface HeaderMappingRecommendation {
  sourceHeader: string;
  targetAttributeKey: string;
  confidence: number;
  isCoreField: boolean;
}

export type HeaderMapping = HeaderMappingRecommendation;

export interface HeaderMappingResponse {
  mappings: HeaderMappingRecommendation[];
  overallConfidence: number;
}

export interface CompileSnapshotRequest {
  projectId: string;
  surveyId: string;
  employeeIds: string[];
}

export interface DemographicSnapshotResponse {
  snapshotId: string;
  projectId: string;
  surveyId: string;
  employeeId: string;
  attributes: Record<string, any>;
  frozenAt: string;
}

