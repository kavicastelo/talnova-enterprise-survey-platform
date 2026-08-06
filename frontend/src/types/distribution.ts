export type AnonymityLevel = 'AUTHENTICATED' | 'SEMI_ANONYMOUS' | 'FULLY_ANONYMOUS' | 'KIOSK';

export type DistributionChannel = 'EMAIL' | 'SMS' | 'QR_CODE' | 'KIOSK_PIN' | 'TEAMS' | 'SLACK';

export type CampaignStatus = 'DRAFT' | 'SCHEDULED' | 'ACTIVE' | 'PAUSED' | 'COMPLETED' | 'EXPIRED' | 'CANCELLED';

export interface TargetAudience {
  nodeIds: string[];
  demographicFilters?: Record<string, string>;
}

export interface CampaignSchedule {
  reminderDates?: string[];
  reminderFrequencyDays?: number;
}

export interface CampaignMetrics {
  totalTargeted: number;
  sent: number;
  delivered: number;
  opened: number;
  started: number;
  completed: number;
  bounced: number;
}

export interface CampaignCreate {
  projectId: string;
  campaignId: string;
  surveyId: string;
  surveyVersion?: number;
  title: string;
  anonymityLevel: AnonymityLevel;
  channels: DistributionChannel[];
  targetAudience: TargetAudience;
  schedule?: CampaignSchedule;
  startDate?: string;
  expirationDate: string;
}

export interface CampaignResponse {
  id: string;
  projectId: string;
  campaignId: string;
  surveyId: string;
  surveyVersion: number;
  title: string;
  anonymityLevel: AnonymityLevel;
  channels: DistributionChannel[];
  targetAudience: TargetAudience;
  schedule?: CampaignSchedule;
  metrics: CampaignMetrics;
  status: CampaignStatus;
  startDate?: string;
  expirationDate: string;
  createdAt: string;
  updatedAt: string;
}

export interface OptimalDispatchPrediction {
  employeeId: string;
  recommendedHour: number;
  recommendedTimeString: string;
  confidenceScore: number;
  reasoning: string;
}
