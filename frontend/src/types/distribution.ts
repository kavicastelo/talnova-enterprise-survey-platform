export type DistributionChannel = 'EMAIL' | 'SMS' | 'QR_CODE' | 'KIOSK_PIN' | 'TEAMS' | 'SLACK';

export type AnonymityLevel = 'AUTHENTICATED' | 'SEMI_ANONYMOUS' | 'FULLY_ANONYMOUS' | 'KIOSK';

export type CampaignStatus =
  | 'DRAFT'
  | 'SCHEDULED'
  | 'ACTIVE'
  | 'PAUSED'
  | 'COMPLETED'
  | 'EXPIRED'
  | 'CANCELLED';

export interface TargetAudience {
  includedNodeIds: string[];
  excludedNodeIds?: string[];
  filterAttributes?: Record<string, string>;
  totalRecipientCount: number;
}

export interface CampaignSchedule {
  scheduledLaunchTime?: string;
  reminderIntervalDays?: number;
  maxReminderCount?: number;
  timezone?: string;
}

export interface CampaignCreateRequest {
  projectId: string;
  campaignId: string;
  surveyId: string;
  surveyVersion: number;
  title: string;
  anonymityLevel: AnonymityLevel;
  channels: DistributionChannel[];
  targetAudience?: TargetAudience;
  schedule?: CampaignSchedule;
  startDate: string;
  expirationDate: string;
}

export interface CampaignMetrics {
  totalTargeted: number;
  totalDispatched: number;
  totalOpened: number;
  totalCompleted: number;
  responseRatePercent: number;
  bounceRatePercent: number;
}

export interface CampaignResponse {
  id?: string;
  projectId: string;
  campaignId: string;
  surveyId: string;
  surveyVersion: number;
  title: string;
  anonymityLevel: AnonymityLevel;
  status: CampaignStatus;
  channels: DistributionChannel[];
  targetAudience?: TargetAudience;
  schedule?: CampaignSchedule;
  startDate: string;
  expirationDate: string;
  metrics?: CampaignMetrics;
  createdAt?: string;
  updatedAt?: string;
}

export interface TokenBatchGenerationRequest {
  projectId: string;
  campaignId: string;
  surveyId: string;
  anonymityLevel: AnonymityLevel;
  count: number;
  expirationTimeSeconds?: number;
  generateKioskPin?: boolean;
}

export interface TokenBatchGenerationResponse {
  campaignId: string;
  generatedCount: number;
  tokens: string[];
  kioskPin?: string;
}

export interface OptimalDispatchPredictionRequest {
  projectId: string;
  channel: DistributionChannel;
  targetAudienceCount: number;
}

export interface OptimalDispatchPredictionResponse {
  recommendedHourUtc: number;
  predictedOpenRateImprovementPercent: number;
  rationale: string;
}
