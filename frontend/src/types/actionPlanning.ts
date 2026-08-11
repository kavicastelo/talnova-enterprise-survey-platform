export type ActionStatus =
  | 'DRAFT'
  | 'PROPOSED'
  | 'APPROVED'
  | 'REJECTED'
  | 'IN_PROGRESS'
  | 'COMPLETED'
  | 'VERIFIED'
  | 'CANCELLED';

export interface ActionMilestone {
  milestoneId: string;
  title: string;
  status: 'PENDING' | 'IN_PROGRESS' | 'COMPLETED';
  dueDate: string;
}

export interface ActionExternalSync {
  system: 'NONE' | 'JIRA' | 'MS_PLANNER';
  externalKey?: string;
  lastSyncedAt?: string;
}

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
  approverId?: string;
  targetCompletionDate?: string;
  milestoneCount?: number;
  milestones?: ActionMilestone[];
  externalSyncSystem?: string;
  externalSync?: ActionExternalSync;
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
  milestones?: ActionMilestone[];
  status?: ActionStatus;
}

export interface StateTransitionRequest {
  targetStatus: ActionStatus;
  actorId?: string;
  userRole?: string;
  comments?: string;
}

export interface ApprovalRequest {
  actorId: string;
  userRole: string;
  rationale?: string;
}

export interface ExternalSyncInfo {
  system: string;
  externalId?: string;
  externalKey?: string;
  externalUrl?: string;
  syncedAt?: string;
}

export interface ActionTemplate {
  templateId: string;
  groupId: string;
  title: string;
  description: string;
  recommendedDurationDays: number;
  milestones?: ActionMilestone[];
}

