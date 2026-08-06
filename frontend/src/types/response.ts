export type RespondentType = 'AUTHENTICATED' | 'SEMI_ANONYMOUS' | 'FULLY_ANONYMOUS' | 'KIOSK';

export interface AnswerSubmission {
  questionId: string;
  questionType: string;
  numericValue?: number;
  textValue?: string;
  selectedOptions?: string[];
}

export interface ResponseSubmission {
  projectId: string;
  campaignId: string;
  surveyId: string;
  surveyVersion?: number;
  responseToken?: string;
  respondentType: RespondentType;
  nodeId?: string;
  answers: AnswerSubmission[];
}

export interface IngestionResponse {
  responseId: string;
  status: string;
  message: string;
  timestamp: string;
}
