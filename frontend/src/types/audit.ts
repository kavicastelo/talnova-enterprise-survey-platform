export interface AuditLogRequest {
  projectId?: string;
  actorId: string;
  userRole: string;
  action: string;
  resourceId?: string;
  details?: Record<string, string>;
}

export interface AuditLogResponse {
  auditId: string;
  projectId: string;
  actorId: string;
  userRole: string;
  action: string;
  resourceId?: string;
  timestamp: string;
  details?: Record<string, string>;
}
