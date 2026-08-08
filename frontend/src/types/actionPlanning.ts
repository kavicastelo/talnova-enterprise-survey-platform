export type ActionStatus =
  | 'DRAFT'
  | 'PROPOSED'
  | 'APPROVED'
  | 'REJECTED'
  | 'IN_PROGRESS'
  | 'COMPLETED'
  | 'VERIFIED'
  | 'CANCELLED';

export interface ActionPlanResponse {
  actionPlanId: string;
  projectId: string;
  campaignId: string;
  nodeId?: string;
  groupId?: string;
  title: string;
  description?: string;
  baselineScore?: number;
  targetScore?: number;
  postActionScore?: number;
  status: ActionStatus;
  assigneeId?: string;
  targetCompletionDate?: string;
  milestoneCount?: number;
  externalSyncSystem?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface ActionPlanCreateRequest {
  actionPlanId?: string;
  projectId: string;
  campaignId: string;
  nodeId?: string;
  groupId?: string;
  title: string;
  description?: string;
  baselineScore?: number;
  targetScore?: number;
  assigneeId?: string;
  targetCompletionDate?: string;
  status?: ActionStatus;
}

export interface ApprovalRequest {
  actorId: string;
  userRole: string;
  rationale?: string;
}

export interface ExternalSyncInfo {
  system: string;
  externalId: string;
  externalUrl?: string;
  syncedAt?: string;
}

export interface ActionTemplate {
  templateId: string;
  groupId: string;
  title: string;
  description: string;
  recommendedDurationDays: number;
}
