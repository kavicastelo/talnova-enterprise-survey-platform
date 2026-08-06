export interface ProjectConfig {
  id: string;
  projectId: string;
  name: string;
  tenantName: string;
  locales: string[];
  theme: {
    primaryColor: string;
    logoUrl?: string;
  };
  features: Record<string, boolean>;
}

export interface OrgNode {
  id: string;
  nodeId: string;
  projectId: string;
  name: string;
  materializedPath: string; // e.g. ",ROOT,N-101,N-201,"
  level: number;
  parentId?: string;
  headCount?: number;
}

export interface Employee {
  id: string;
  employeeId: string;
  projectId: string;
  orgNodeId: string;
  encryptedPii: {
    fullName: string;
    email: string;
  };
  demographics: Record<string, string>;
}

export interface Survey {
  id: string;
  surveyId: string;
  projectId: string;
  title: string;
  description: string;
  version: number;
  status: 'DRAFT' | 'PUBLISHED' | 'CLOSED';
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
  correlationId: string;
}
