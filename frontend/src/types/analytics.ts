export type ColorIntensity = 'RED' | 'YELLOW' | 'GREEN' | 'GREY';

export interface GroupScore {
  groupId: string;
  groupName?: string;
  sampleSize: number;
  score: number | null;
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
