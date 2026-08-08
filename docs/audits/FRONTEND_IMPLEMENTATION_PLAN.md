# TESP Frontend Implementation & Integration Plan

**Platform:** Talnova Enterprise Survey Platform (TESP)  
**Lead Architect:** Frontend Architecture Team  
**Status:** Ready for Phase-by-Phase Execution

---

## 1. Overview & Phased Roadmap

This implementation plan outlines the step-by-step technical execution strategy to transition the TESP frontend from a prototype application to a production-grade enterprise platform integrated with the 13 backend microservices via API Gateway (:8080).

```
Phase 1: Architecture & Core Foundation  --->  Phase 2: Core Platform & Identity
                 |                                                |
                 v                                                v
Phase 4: Analytics, AI & Reporting        <---  Phase 3: Survey Engine & Distribution
                 |
                 v
Phase 5: Action Planning, Audit & E2E Verification
```

---

## 2. Detailed Execution Phases

### Phase 1: Architectural Foundation & Core Infrastructure
* **Goal:** Establish centralized HTTP client, authentication, multi-tenancy context, routing framework, and design primitives.
* **Tasks:**
  1. Configure `apiClient.ts` with Axios interceptors (`Authorization`, `X-Project-ID`, `X-Correlation-ID`) and error normalization.
  2. Implement `AuthContext.tsx` and `TenantContext.tsx` providers.
  3. Create `src/app/router.tsx` with React Router v6 domain route structure.
  4. Create reusable UI primitives in `src/components/ui/` (`Button`, `Input`, `Select`, `Modal`, `Badge`, `Card`, `Tabs`, `DataTable`, `Spinner`, `Toast`).
  5. Setup TanStack Query `queryClient` provider with project-scoped cache invalidation.

### Phase 2: Core Platform Services & Identity Integration
* **Goal:** Integrate Project Config, Organization Hierarchy, and Employee Roster services against API Gateway endpoints.
* **Tasks:**
  1. Refactor `projectConfigApi.ts` to use `apiClient`. Wire `ProjectSetupWizard`, `ThemeCustomizer`, `FeatureFlagMatrix`, and `LocaleManagementPanel`.
  2. Refactor `orgApi.ts`. Connect `OrgHierarchyManager` and `OrgTreeCanvas` to `/api/v1/nodes/**` (subtree, lineage, anomalies).
  3. Refactor `employeeApi.ts`. Connect `EmployeeDataGrid`, `CsvImportWizardModal`, AI header mapping, and GDPR anonymization endpoints.

### Phase 3: Survey Engine & Distribution Integration
* **Goal:** Connect Survey Builder Studio, Question Library, Distribution Campaign Launch, and Respondent Player interfaces.
* **Tasks:**
  1. Refactor `surveyBuilderApi.ts`. Connect `SurveyBuilderCanvas` AST editor, logic rules, publishing, versioning, AI translation, and AI bias inspector.
  2. Refactor `distributionApi.ts`. Connect `CampaignLaunchWizard`, optimal AI dispatch predictor, single-use token generator, and live campaign monitor.
  3. Wire public `SurveyPlayerPage` and `KioskPlayerPage` to `responseIngestionApi.ts` (`POST /api/v1/responses`) with offline IndexedDB queueing.

### Phase 4: Analytics Engine, AI Insights & Reporting Integration
* **Goal:** Connect Executive KPI Scorecards, Heatmap Grid (N<5 privacy), AI Sentiment, and Async Report Generation.
* **Tasks:**
  1. Connect `ExecutiveKpiScorecard` and `OrganizationalHeatmapGrid` to `/api/v1/analytics/**`. Enforce max 5 demographic slicers.
  2. Connect `SentimentAnalyticsPanel` and `ExecutiveSummaryDrawer` to `/api/v1/ai/**`.
  3. Connect `ReportExportModal` and `ReportJobProgressDrawer` to `/api/v1/reports/**` using `useAsyncJob` polling hook.

### Phase 5: Action Planning, Audit Logs & End-to-End Verification
* **Goal:** Connect Action Planning Kanban, External Sync (Jira/MS Planner), Audit Log Search, and conduct end-to-end verification.
* **Tasks:**
  1. Connect `ActionKanbanBoardPage` and `ActionCardDetailDrawer` to `/api/v1/actions/**`. Wire Jira and MS Planner sync triggers.
  2. Build `AuditTrailPage` connecting to `/api/v1/audit/logs`.
  3. Connect `NotificationLogsPage` to `/api/v1/notifications/**`.
  4. Perform full end-to-end user flow verification across all 10 business domain views.

---

## 3. Blockers & Risk Identification

| Risk / Blocker | Severity | Mitigation Strategy |
| :--- | :---: | :--- |
| **API Gateway Reachability** | Medium | Gateway must be running on `:8080`. Vite dev server proxy configured in `vite.config.ts`. |
| **CORS Policy** | Low | Gateway Spring Security handles CORS for `http://localhost:5173`. |
| **JWT Token Availability** | Medium | Auth mock/provider created for local development if auth server unavailable. |
| **Differential Privacy ($N < 5$)** | High | Analytics endpoints mask cells with $N < 5$; frontend UI handles `SUPPRESSED` cell values gracefully. |
