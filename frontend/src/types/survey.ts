export type QuestionType =
  | 'LIKERT'
  | 'NPS'
  | 'MATRIX'
  | 'MULTIPLE_CHOICE'
  | 'SINGLE_CHOICE'
  | 'TEXT_OPEN'
  | 'SHORT_TEXT'
  | 'LONG_TEXT'
  | 'NUMERIC'
  | 'RATING_STARS'
  | 'SLIDER'
  | 'DATE_PICKER';

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

export type SurveyStatus = 'DRAFT' | 'PUBLISHED' | 'ACTIVE' | 'ARCHIVED';

export interface SurveySaveDraft {
  projectId: string;
  surveyId: string;
  title: Record<string, string>;
  description?: Record<string, string>;
  pages: SurveyPage[];
}

export interface SurveyResponse {
  id: string;
  projectId: string;
  surveyId: string;
  version: number;
  title: Record<string, string>;
  description?: Record<string, string>;
  status: SurveyStatus;
  pages: SurveyPage[];
  versionHistory?: number[];
  publishedAt?: string;
  createdAt: string;
  updatedAt: string;
}
