export type ColorIntensity = 'RED' | 'YELLOW' | 'GREEN' | 'GREY';

export interface GroupScore {
  groupId: string;
  groupName?: string;
  sampleSize: number;
  score: number | null;
  status?: 'VALID' | 'SUPPRESSED';
  colorIntensity: ColorIntensity;
}

export interface DashboardMetrics {
  campaignId: string;
  nodeId: string;
  totalResponses: number;
  participationRate: number;
  eNPS?: number | null;
  engagementIndex?: number | null;
  groupScores: GroupScore[];
  status?: 'VALID' | 'SUPPRESSED';
}

export interface HeatmapCell {
  nodeId: string;
  groupId: string;
  sampleSize: number;
  score: number | null;
  colorIntensity: ColorIntensity;
}

export interface HeatmapMatrixRowNode {
  id: string;
  name: string;
}

export interface HeatmapMatrixColumnTheme {
  id: string;
  name: string;
}

export interface HeatmapMatrix {
  campaignId: string;
  parentNodeId: string;
  rowNodes: (string | HeatmapMatrixRowNode)[];
  columnThemes: (string | HeatmapMatrixColumnTheme)[];
  cells: HeatmapCell[];
}

export interface LongitudinalDelta {
  enpsDelta: number;
  engagementIndexDelta: number;
  trendDirection: 'UP' | 'DOWN' | 'STABLE';
  baselineCampaignName: string;
}

export interface TrendHistoryItem {
  campaignName: string;
  eNPS: number;
  engagementIndex: number;
}

export interface AnalyticalSnapshot {
  id: string;
  projectId: string;
  campaignId: string;
  surveyId: string;
  snapshotDate: string;
  totalResponses: number;
  overallEnps: number;
  overallEngagementIndex: number;
  participationRate: number;
  createdAt: string;
}

export interface AnomalyReport {
  nodeId: string;
  nodeName: string;
  metricName: string;
  currentScore: number;
  baselineScore: number;
  scoreDropDelta: number;
  severity: 'CRITICAL' | 'WARNING';
  insightMessage: string;
}

export interface KeyDriver {
  themeGroupId: string;
  themeName: string;
  importanceWeight: number;
  correlationScore: number;
  impactCategory: 'HIGH_IMPACT' | 'MEDIUM_IMPACT' | 'LOW_IMPACT';
}
