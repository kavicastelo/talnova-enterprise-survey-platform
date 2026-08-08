# TESP Frontend-to-Backend API Contract Map

**Platform:** Talnova Enterprise Survey Platform (TESP)  
**Ingress Gateway:** API Gateway (`http://localhost:8080/api/v1/**`)  
**Mandatory Headers:**  
- `Authorization: Bearer <jwt_token>` (for authenticated endpoints)
- `X-Project-ID: <PRJ-XXXXX>` (for project-scoped endpoints)
- `X-Correlation-ID: <UUID>` (generated per request)

---

## 1. Unified Backend API Response Format

All backend services wrap responses in the standard `ApiResponse<T>` envelope:

```typescript
export interface ApiResponse<T> {
  success: boolean;
  message?: string;
  data: T;
  timestamp: string;
  correlationId?: string;
}
```

Standard Error Envelope (`GlobalExceptionHandler`):
```typescript
export interface ApiErrorResponse {
  success: false;
  message: string;
  errorCode: string;
  status: number;
  timestamp: string;
  errors?: Record<string, string>; // Validation field errors
}
```

---

## 2. Microservice Endpoint Contracts

### 2.1 Project Config Service (Gateway Route: `/api/v1/projects/**`)
| Method | Gateway Path | Request DTO | Response DTO | Headers / Params | Notes |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/projects` | `ProjectCreateRequest` | `ApiResponse<ProjectResponse>` | `Content-Type` | Provision new project tenant |
| `GET` | `/api/v1/projects/{projectId}` | None | `ApiResponse<ProjectResponse>` | `X-Project-ID` | Fetch project configuration |
| `PUT` | `/api/v1/projects/{projectId}` | `ProjectCreateRequest` | `ApiResponse<ProjectResponse>` | `X-Project-ID` | Update project config & branding |
| `PATCH` | `/api/v1/projects/{projectId}/features` | `FeatureFlags` | `ApiResponse<ProjectResponse>` | `X-Project-ID` | Update dynamic feature flags |
| `GET` | `/api/v1/projects/{projectId}/public-theme` | None | `ApiResponse<PublicTheme>` | None (Public) | Respondent theme loading |
| `POST` | `/api/v1/projects/validate-theme` | `ContrastValidationRequest` | `ApiResponse<ContrastValidationResponse>` | None | Real-time WCAG contrast test |
| `DELETE`| `/api/v1/projects/{projectId}` | None | `ApiResponse<Void>` | `X-Project-ID` | Soft delete project |

### 2.2 Organization Service (Gateway Route: `/api/v1/nodes/**`)
| Method | Gateway Path | Request DTO | Response DTO | Headers / Params | Notes |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/nodes` | `CreateNodeRequest` | `ApiResponse<OrgNode>` | `X-Project-ID` | Create organizational node |
| `GET` | `/api/v1/nodes/{nodeId}` | None | `ApiResponse<OrgNode>` | `X-Project-ID` | Get node details |
| `POST` | `/api/v1/nodes/{nodeId}/move` | `MoveNodeRequest` | `ApiResponse<OrgNode>` | `X-Project-ID` | Re-parent node in tree |
| `GET` | `/api/v1/nodes/{nodeId}/subtree` | None | `ApiResponse<OrgNode[]>` | `X-Project-ID` | Fetch complete subtree |
| `GET` | `/api/v1/nodes/{nodeId}/lineage` | None | `ApiResponse<OrgNode[]>` | `X-Project-ID` | Ancestor lineage path |
| `GET` | `/api/v1/nodes/anomalies` | None | `ApiResponse<HierarchyAnomalyReport>` | `X-Project-ID` | Detect orphans / cycles |

### 2.3 Employee Service (Gateway Route: `/api/v1/employees/**`)
| Method | Gateway Path | Request DTO | Response DTO | Headers / Params | Notes |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/employees` | `CreateEmployeePayload` | `ApiResponse<EmployeeProfile>` | `X-Project-ID` | Add single employee |
| `GET` | `/api/v1/employees/{employeeId}` | None | `ApiResponse<EmployeeProfile>` | `X-Project-ID` | Get employee details |
| `PUT` | `/api/v1/employees/{employeeId}` | `UpdateEmployeePayload` | `ApiResponse<EmployeeProfile>` | `X-Project-ID` | Update employee profile |
| `POST` | `/api/v1/employees/bulk-import` | `FormData` (file, autoTerminate) | `ApiResponse<BulkImportResult>` | `X-Project-ID` | Async bulk CSV import |
| `POST` | `/api/v1/employees/ai/map-headers` | `{ headers: string[] }` | `ApiResponse<{ mappings: HeaderMapping[] }>` | `X-Project-ID` | AI CSV header auto-mapper |
| `POST` | `/api/v1/employees/hris/sync` | None | `ApiResponse<HrisSyncResult>` | `X-Project-ID` | Trigger HRIS automated sync |
| `POST` | `/api/v1/employees/{employeeId}/gdpr-anonymize` | None | `ApiResponse<Void>` | `X-Project-ID` | GDPR right-to-be-forgotten |
| `POST` | `/api/v1/employees/snapshots/compile` | None | `ApiResponse<SnapshotResult>` | `X-Project-ID` | Lock census snapshot |
| `GET` | `/api/v1/employees/snapshots/{snapshotId}` | None | `ApiResponse<DemographicSnapshot>` | `X-Project-ID` | Read frozen snapshot |

### 2.4 Survey Builder Service (Gateway Route: `/api/v1/surveys/**`, `/api/v1/question-library/**`)
| Method | Gateway Path | Request DTO | Response DTO | Headers / Params | Notes |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `PUT` | `/api/v1/surveys/{surveyId}` | `SurveySaveDraft` | `ApiResponse<SurveyResponse>` | `X-Project-ID` | Save survey AST draft |
| `GET` | `/api/v1/surveys/{surveyId}` | None | `ApiResponse<SurveyResponse>` | `X-Project-ID` | Fetch survey definition |
| `POST` | `/api/v1/surveys/{surveyId}/publish` | None | `ApiResponse<SurveyResponse>` | `X-Project-ID` | Lock & publish survey |
| `POST` | `/api/v1/surveys/{surveyId}/new-version` | None | `ApiResponse<SurveyResponse>` | `X-Project-ID` | Branch draft version |
| `POST` | `/api/v1/surveys/ai/translate` | `TranslationRequest` | `ApiResponse<TranslationResponse>` | `X-Project-ID` | AI survey translation |
| `POST` | `/api/v1/surveys/ai/analyze-bias` | `BiasRequest` | `ApiResponse<BiasResponse>` | `X-Project-ID` | AI bias inspection |
| `POST` | `/api/v1/question-library` | `QuestionPayload` | `ApiResponse<QuestionItem>` | `X-Project-ID` | Store reusable question |
| `GET` | `/api/v1/question-library` | None | `ApiResponse<QuestionItem[]>` | `X-Project-ID` | Search question bank |

### 2.5 Survey Distribution Service (Gateway Route: `/api/v1/campaigns/**`, `/api/v1/tokens/**`, `/api/v1/distribution/**`)
| Method | Gateway Path | Request DTO | Response DTO | Headers / Params | Notes |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/campaigns` | `CampaignCreate` | `ApiResponse<CampaignResponse>` | `X-Project-ID` | Launch distribution campaign |
| `GET` | `/api/v1/campaigns/{campaignId}` | None | `ApiResponse<CampaignResponse>` | `X-Project-ID` | Campaign metrics |
| `PATCH` | `/api/v1/campaigns/{campaignId}/status` | `?status=ACTIVE` | `ApiResponse<CampaignResponse>` | `X-Project-ID` | Pause / resume / close |
| `POST` | `/api/v1/tokens/generate` | `TokenGenRequest` | `ApiResponse<TokenResult>` | `X-Project-ID` | Generate single-use tokens |
| `POST` | `/api/v1/tokens/burn` | `{ token: string }` | `ApiResponse<TokenBurnResult>` | None | Atomically burn token in Redis |
| `POST` | `/api/v1/distribution/ai-optimal-time` | `OptimalTimeRequest` | `ApiResponse<OptimalDispatchPrediction>` | `X-Project-ID` | AI optimal dispatch prediction |

### 2.6 Response Ingestion Service (Gateway Route: `/api/v1/responses/**`)
| Method | Gateway Path | Request DTO | Response DTO | Headers / Params | Notes |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/responses` | `ResponseSubmission` | `ApiResponse<IngestionResponse>` | `X-Project-ID` | High-throughput intake |

### 2.7 Analytics Engine Service (Gateway Route: `/api/v1/analytics/**`)
| Method | Gateway Path | Request DTO | Response DTO | Headers / Params | Notes |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/analytics/dashboard` | None | `ApiResponse<AnalyticsDashboard>` | `X-Project-ID`, `?campaignId=` | Executive KPI summary |
| `GET` | `/api/v1/analytics/heatmap` | None | `ApiResponse<HeatmapReport>` | `X-Project-ID`, `?nodeId=`, `?slicers=` | Heatmap with N<5 suppression |

### 2.8 AI Analytics Service (Gateway Route: `/api/v1/ai/**`)
| Method | Gateway Path | Request DTO | Response DTO | Headers / Params | Notes |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/ai/insights/projects/{projectId}/campaigns/{campaignId}` | None | `ApiResponse<AiInsightReport>` | `X-Project-ID` | NLP sentiment & topics |
| `PUT` | `/api/v1/ai/insights/{insightId}/override` | `InsightOverrideRequest` | `ApiResponse<AiInsightReport>` | `X-Project-ID` | Human manager override |
| `GET` | `/api/v1/ai/summaries` | None | `ApiResponse<ExecutiveSummary[]>` | `X-Project-ID` | List LLM summaries |
| `POST` | `/api/v1/ai/summary` | `SummaryRequest` | `ApiResponse<ExecutiveSummary>` | `X-Project-ID` | Generate executive summary |

### 2.9 Reporting Service (Gateway Route: `/api/v1/reports/**`)
| Method | Gateway Path | Request DTO | Response DTO | Headers / Params | Notes |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/reports/generate` | `ReportGenerateRequest` | `ApiResponse<ReportJobResponse>` | `X-Project-ID` | Async report creation job |
| `GET` | `/api/v1/reports/jobs/{jobId}` | None | `ApiResponse<ReportJobStatus>` | `X-Project-ID` | Poll job progress |
| `GET` | `/api/v1/reports/jobs/{projectId}/{jobId}` | None | `ApiResponse<ReportJobStatus>` | `X-Project-ID` | Download link retrieval |

### 2.10 Action Planning Service (Gateway Route: `/api/v1/actions/**`)
| Method | Gateway Path | Request DTO | Response DTO | Headers / Params | Notes |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/actions` | `CreateActionPlanRequest` | `ApiResponse<ActionPlanResponse>` | `X-Project-ID` | Create action plan |
| `GET` | `/api/v1/actions/kanban` | None | `ApiResponse<KanbanBoardResponse>` | `X-Project-ID` | Kanban board columns |
| `GET` | `/api/v1/actions/{actionPlanId}` | None | `ApiResponse<ActionPlanResponse>` | `X-Project-ID` | Plan details |
| `POST` | `/api/v1/actions/{actionPlanId}/approve` | None | `ApiResponse<ActionPlanResponse>` | `X-Project-ID` | Manager approval |
| `POST` | `/api/v1/actions/{actionPlanId}/reject` | None | `ApiResponse<ActionPlanResponse>` | `X-Project-ID` | Rejection flow |
| `POST` | `/api/v1/actions/{actionPlanId}/sync-jira` | None | `ApiResponse<JiraSyncResult>` | `X-Project-ID` | Dispatch Jira issue |
| `POST` | `/api/v1/actions/{actionPlanId}/sync-ms-planner` | None | `ApiResponse<MsPlannerSyncResult>` | `X-Project-ID` | Dispatch MS Planner task |
| `GET` | `/api/v1/actions/templates/recommendations` | None | `ApiResponse<ActionTemplate[]>` | `X-Project-ID` | AI action recommendations |

### 2.11 Notification Service (Gateway Route: `/api/v1/notifications/**`)
| Method | Gateway Path | Request DTO | Response DTO | Headers / Params | Notes |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/notifications/send` | `NotificationSendRequest` | `ApiResponse<NotificationResponse>` | `X-Project-ID` | Manual / trigger dispatch |
| `GET` | `/api/v1/notifications/status/{notificationId}` | None | `ApiResponse<NotificationStatusResponse>` | `X-Project-ID` | Delivery status check |

### 2.12 Audit Service (Gateway Route: `/api/v1/audit/**`)
| Method | Gateway Path | Request DTO | Response DTO | Headers / Params | Notes |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/audit/logs` | `AuditLogEntry` | `ApiResponse<AuditLogEntry>` | `X-Project-ID` | Write immutable log |
| `GET` | `/api/v1/audit/logs` | None | `ApiResponse<Page<AuditLogEntry>>` | `X-Project-ID`, `?page=`, `?size=` | Query audit trail |
| `GET` | `/api/v1/audit/logs/{auditId}` | None | `ApiResponse<AuditLogEntry>` | `X-Project-ID` | Single audit record |
