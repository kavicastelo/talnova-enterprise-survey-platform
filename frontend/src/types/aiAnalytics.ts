export type SentimentLabel = 'POSITIVE' | 'NEUTRAL' | 'NEGATIVE';

export type RiskSeverity = 'NONE' | 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';

export interface HumanOverride {
  overriddenBy: string;
  originalLabel: string;
  newLabel: SentimentLabel;
  reason?: string;
  overriddenAt?: string;
}

export interface AiInsightDocument {
  id: string;
  projectId: string;
  campaignId: string;
  responseId?: string;
  questionId?: string;
  sanitizedText?: string;
  sentimentScore?: number;
  sentimentLabel?: SentimentLabel;
  confidence?: number;
  themes?: string[];
  riskCategory?: string;
  riskSeverity?: RiskSeverity;
  humanOverride?: HumanOverride;
  createdAt?: string;
}

export interface SentimentOverrideRequest {
  overriddenBy: string;
  newLabel: SentimentLabel;
  reason?: string;
}

export interface ExecutiveSummary {
  nodeScope?: string;
  summaryTitle?: string;
  topStrengths?: string[];
  topConcerns?: string[];
  recommendations?: string[];
  generatedAt?: string;
}
