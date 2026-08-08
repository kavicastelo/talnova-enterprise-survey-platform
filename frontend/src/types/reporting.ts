export type ReportType =
  | 'EXEC_SUMMARY_PDF'
  | 'DEPT_BREAKDOWN_PDF'
  | 'RAW_RESPONSES_XLSX'
  | 'AGGREGATED_SCORES_XLSX';

export type ReportStatus = 'QUEUED' | 'PROCESSING' | 'COMPLETED' | 'FAILED';

export interface ReportRequest {
  projectId: string;
  campaignId: string;
  reportType: ReportType;
  nodeId?: string;
  requestedBy: string;
  passwordProtection?: string;
}

export interface ReportJobResponse {
  jobId: string;
  projectId: string;
  campaignId: string;
  reportType: ReportType;
  nodeId?: string;
  status: ReportStatus;
  downloadUrl?: string;
  createdAt?: string;
  expiresAt?: string;
  message?: string;
}
