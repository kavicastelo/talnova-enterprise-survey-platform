export type QuestionType =
  | 'LIKERT'
  | 'NPS'
  | 'MATRIX'
  | 'SINGLE_CHOICE'
  | 'MULTIPLE_CHOICE'
  | 'RANKING'
  | 'SHORT_TEXT'
  | 'LONG_TEXT'
  | 'NUMERIC'
  | 'DATE';

export type SurveyStatus = 'DRAFT' | 'PUBLISHED' | 'ACTIVE' | 'CLOSED' | 'ARCHIVED';

export type LogicOperator = 'EQUALS' | 'NOT_EQUALS' | 'LESS_THAN' | 'GREATER_THAN';

export interface LogicRule {
  ruleId: string;
  operator: LogicOperator;
  comparisonValue: string;
  targetPageId: string;
}

export interface SurveyQuestion {
  questionId: string;
  type: QuestionType;
  groupId?: string;
  prompt: Record<string, string>;
  isMandatory: boolean;
  questionOrder: number;
  logicRules?: LogicRule[];
}

export interface SurveySection {
  sectionId: string;
  title?: Record<string, string>;
  sectionOrder: number;
  questions: SurveyQuestion[];
}

export interface SurveyPage {
  pageId: string;
  pageOrder: number;
  title?: Record<string, string>;
  sections: SurveySection[];
}

export interface SurveySaveDraftRequest {
  projectId: string;
  surveyId: string;
  title: Record<string, string>;
  description?: Record<string, string>;
  pages: SurveyPage[];
}

export interface SurveyResponse {
  id?: string;
  projectId: string;
  surveyId: string;
  version: number;
  title: Record<string, string>;
  description?: Record<string, string>;
  status: SurveyStatus;
  pages: SurveyPage[];
  publishedAt?: string;
  versionHistory?: number[];
  isDeleted?: boolean;
  createdAt?: string;
  updatedAt?: string;
}

export interface QuestionLibraryTemplate {
  libraryId: string;
  category: string;
  themeGroup: string;
  defaultPrompt: Record<string, string>;
  questionType: QuestionType;
  validationRules?: Record<string, any>;
}

export interface AIBiasAnalysisRequest {
  questionPrompt: string;
  locale?: string;
}

export interface AIBiasAnalysisResponse {
  riskScore: number;
  biasDetected: boolean;
  biasCategory?: string;
  message: string;
  suggestedPrompts: string[];
}
