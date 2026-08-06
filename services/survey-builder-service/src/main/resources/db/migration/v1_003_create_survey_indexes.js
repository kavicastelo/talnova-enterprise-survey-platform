// MongoDB Index Creation Script for 'surveys' Collection

// 1. Compound Unique Index: Enforces unique version numbers per survey per project tenant
db.surveys.createIndex(
  { projectId: 1, surveyId: 1, version: 1 },
  { unique: true, name: "uk_surveys_project_survey_version" }
);

// 2. Tenant Status Lookup Index: Optimizes queries filtering active/published surveys by project tenant
db.surveys.createIndex(
  { projectId: 1, status: 1, isDeleted: 1 },
  { name: "idx_surveys_project_status_deleted" }
);

// 3. Survey Lookup Index: Fast retrieval of survey versions by tenant, surveyId and soft delete flag
db.surveys.createIndex(
  { projectId: 1, surveyId: 1, isDeleted: 1 },
  { name: "idx_surveys_project_survey_deleted" }
);
