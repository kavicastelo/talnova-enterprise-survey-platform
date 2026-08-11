export const API_ENDPOINTS = {
  // Project Config Service (:8081)
  PROJECTS: '/projects',
  PROJECT_BY_ID: (id: string) => `/projects/${id}`,
  PROJECT_BRANDING: (id: string) => `/projects/${id}/public-theme`,
  PROJECT_FEATURES: (id: string) => `/projects/${id}/features`,
  PROJECT_LOCALES: (id: string) => `/projects/${id}/locales`,

  // Organization Service (:8082)
  NODES: '/nodes',
  NODE_BY_ID: (id: string) => `/nodes/${id}`,
  NODE_MOVE: (id: string) => `/nodes/${id}/move`,
  NODE_SUBTREE: (id: string) => `/nodes/${id}/subtree`,
  NODE_LINEAGE: (id: string) => `/nodes/${id}/lineage`,
  NODE_ANOMALIES: '/nodes/anomalies',

  // Employee Service (:8083)
  EMPLOYEES: '/employees',
  EMPLOYEE_BY_ID: (id: string) => `/employees/${id}`,
  EMPLOYEE_BULK_IMPORT: '/employees/bulk-import',
  EMPLOYEE_HRIS_SYNC: '/employees/hris/sync',
  EMPLOYEE_GDPR_ANONYMIZE: (id: string) => `/employees/${id}/gdpr-anonymize`,
  EMPLOYEE_SNAPSHOTS_COMPILE: '/employees/snapshots/compile',
  EMPLOYEE_SNAPSHOT_BY_ID: (id: string) => `/employees/snapshots/${id}`,
  EMPLOYEE_AI_MAP_HEADERS: '/employees/ai/map-headers',

  // Survey Builder Service (:8084)
  SURVEYS: '/surveys',
  SURVEY_BY_ID: (id: string) => `/surveys/${id}`,
  SURVEY_PUBLISH: (id: string) => `/surveys/${id}/publish`,
  SURVEY_NEW_VERSION: (id: string) => `/surveys/${id}/new-version`,
  QUESTION_LIBRARY: '/question-library',
  SURVEY_AI_TRANSLATE: '/surveys/ai/translate',
  SURVEY_AI_BIAS: '/surveys/ai/analyze-bias',

  // Survey Distribution Service (:8085)
  CAMPAIGNS: '/campaigns',
  CAMPAIGN_BY_ID: (id: string) => `/campaigns/${id}`,
  TOKENS_GENERATE: '/tokens/generate',
  TOKENS_BURN: '/tokens/burn',
  DISTRIBUTION_AI_OPTIMAL_TIME: '/distribution/ai-optimal-time',
  WEBHOOKS_DELIVERY_STATUS: '/webhooks/delivery-status',

  // Response Ingestion Service (:8086)
  RESPONSES: '/responses',

  // Analytics Engine Service (:8087)
  ANALYTICS_METRICS: '/analytics/metrics',
  ANALYTICS_HEATMAP: '/analytics/heatmap',
  ANALYTICS_ENPS: '/analytics/enps',
  ANALYTICS_ENGAGEMENT: '/analytics/engagement',

  // AI Analytics Service (:8088)
  AI_SENTIMENT: '/ai/sentiment',
  AI_RISK_ALERTS: '/ai/risk-alerts',
  AI_EXECUTIVE_SUMMARY: '/ai/executive-summary',
  AI_THEMATIC: '/ai/thematic',

  // Reporting Service (:8089)
  REPORTS_GENERATE: '/reports/generate',
  REPORT_JOB_STATUS: (jobId: string) => `/reports/jobs/${jobId}`,
  REPORT_JOB_BY_PROJECT: (projectId: string, jobId: string) => `/reports/jobs/${projectId}/${jobId}`,

  // Action Planning Service (:8090)
  ACTIONS: '/actions',
  ACTION_BY_ID: (id: string) => `/actions/${id}`,
  ACTION_STATUS: (id: string) => `/actions/${id}/status`,
  ACTION_JIRA_SYNC: (id: string) => `/actions/${id}/sync/jira`,

  // Notification Service (:8091)
  NOTIFICATIONS_SEND: '/notifications/send',
  NOTIFICATION_STATUS: (id: string) => `/notifications/status/${id}`,

  // Audit Service (:8092)
  AUDIT_LOGS: '/audit',
};
