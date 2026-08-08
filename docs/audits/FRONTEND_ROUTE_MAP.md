# TESP Frontend Route & Navigation Architecture

**Platform:** Talnova Enterprise Survey Platform (TESP)  
**Routing Framework:** React Router v6 (`react-router-dom`)  
**Design Principle:** Routes are structured around **Business Domains**, completely decoupled from underlying microservice topology and internal port numbers.

---

## 1. Top-Level Layout Hierarchy

```
<BrowserRouter>
  <Routes>
    {/* Public & Respondent Routes (No Sidebar) */}
    <Route element={<SurveyPlayerLayout />}>
      <Route path="/s/:token" element={<SurveyPlayerPage />} />
    </Route>
    <Route element={<KioskPlayerLayout />}>
      <Route path="/kiosk/:surveyId" element={<KioskPlayerPage />} />
    </Route>
    <Route element={<AuthLayout />}>
      <Route path="/login" element={<LoginPage />} />
      <Route path="/access-denied" element={<AccessDeniedPage />} />
    </Route>

    {/* Authenticated Platform App Routes */}
    <Route element={<ProtectedRoute><MainPlatformLayout /></ProtectedRoute>}>
      <Route path="/" element={<Navigate to="/dashboard" replace />} />
      <Route path="/dashboard" element={<ExecutiveDashboardPage />} />

      {/* Project Config */}
      <Route path="/settings/projects" element={<RoleGate allowedRoles={['SUPER_ADMIN']}><ProjectProvisioningPage /></RoleGate>} />
      <Route path="/settings/branding" element={<RoleGate allowedRoles={['SUPER_ADMIN', 'PROJECT_ADMIN']}><ThemeBrandingPage /></RoleGate>} />
      <Route path="/settings/features" element={<RoleGate allowedRoles={['SUPER_ADMIN', 'PROJECT_ADMIN']}><FeatureFlagPage /></RoleGate>} />
      <Route path="/settings/locales" element={<RoleGate allowedRoles={['SUPER_ADMIN', 'PROJECT_ADMIN']}><LocaleManagementPage /></RoleGate>} />

      {/* Organization & Employees */}
      <Route path="/organization" element={<RoleGate allowedRoles={['SUPER_ADMIN', 'PROJECT_ADMIN', 'HR_MANAGER']}><OrgHierarchyPage /></RoleGate>} />
      <Route path="/employees" element={<RoleGate allowedRoles={['SUPER_ADMIN', 'PROJECT_ADMIN', 'HR_MANAGER']}><EmployeeRosterPage /></RoleGate>} />

      {/* Survey Builder & Distribution */}
      <Route path="/surveys" element={<RoleGate allowedRoles={['SUPER_ADMIN', 'PROJECT_ADMIN', 'HR_MANAGER']}><SurveyListPage /></RoleGate>} />
      <Route path="/surveys/:surveyId/build" element={<RoleGate allowedRoles={['SUPER_ADMIN', 'PROJECT_ADMIN', 'HR_MANAGER']}><SurveyBuilderPage /></RoleGate>} />
      <Route path="/distribution" element={<RoleGate allowedRoles={['SUPER_ADMIN', 'PROJECT_ADMIN', 'HR_MANAGER']}><CampaignDistributionPage /></RoleGate>} />

      {/* Analytics & AI Insights */}
      <Route path="/analytics" element={<RoleGate allowedRoles={['SUPER_ADMIN', 'PROJECT_ADMIN', 'HR_MANAGER', 'DEPARTMENT_MANAGER', 'CONSULTANT_DAASH']}><AnalyticsDashboardPage /></RoleGate>} />
      <Route path="/ai-insights" element={<FeatureGate flag="aiAnalyticsEnabled"><RoleGate allowedRoles={['SUPER_ADMIN', 'PROJECT_ADMIN', 'HR_MANAGER', 'CONSULTANT_DAASH']}><AiAnalyticsPage /></RoleGate></FeatureGate>} />

      {/* Reports & Action Planning */}
      <Route path="/reports" element={<RoleGate allowedRoles={['SUPER_ADMIN', 'PROJECT_ADMIN', 'HR_MANAGER', 'DEPARTMENT_MANAGER', 'CONSULTANT_DAASH']}><ReportGeneratorPage /></RoleGate>} />
      <Route path="/action-plans" element={<FeatureGate flag="actionPlanningEnabled"><RoleGate allowedRoles={['SUPER_ADMIN', 'PROJECT_ADMIN', 'HR_MANAGER', 'DEPARTMENT_MANAGER']}><ActionKanbanBoardPage /></RoleGate></FeatureGate>} />

      {/* Platform Operations */}
      <Route path="/notifications" element={<RoleGate allowedRoles={['SUPER_ADMIN', 'PROJECT_ADMIN']}><NotificationLogsPage /></RoleGate>} />
      <Route path="/audit" element={<RoleGate allowedRoles={['SUPER_ADMIN', 'PROJECT_ADMIN']}><AuditTrailPage /></RoleGate>} />
    </Route>
  </Routes>
</BrowserRouter>
```

---

## 2. Route Specification Matrix

| Route Path | View Component | Layout | Auth | Allowed Roles | Feature Flag Guard | Business Purpose |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| `/login` | `LoginPage` | `AuthLayout` | Public | All | None | User authentication |
| `/s/:token` | `SurveyPlayerPage` | `SurveyPlayerLayout` | Public | Respondent | None | Single-use survey response intake |
| `/kiosk/:surveyId` | `KioskPlayerPage` | `KioskPlayerLayout` | Public | Respondent | `kioskModeEnabled` | Offline kiosk survey response intake |
| `/dashboard` | `ExecutiveDashboardPage` | `MainPlatformLayout` | Required | All authenticated | None | Executive overview metrics |
| `/settings/projects` | `ProjectProvisioningPage` | `MainPlatformLayout` | Required | `SUPER_ADMIN` | None | Provision new project tenant |
| `/settings/branding` | `ThemeBrandingPage` | `MainPlatformLayout` | Required | `SUPER_ADMIN`, `PROJECT_ADMIN` | None | WCAG white-label brand studio |
| `/settings/features` | `FeatureFlagPage` | `MainPlatformLayout` | Required | `SUPER_ADMIN`, `PROJECT_ADMIN` | None | Dynamic tenant feature toggles |
| `/settings/locales` | `LocaleManagementPage` | `MainPlatformLayout` | Required | `SUPER_ADMIN`, `PROJECT_ADMIN` | None | Supported survey language management |
| `/organization` | `OrgHierarchyPage` | `MainPlatformLayout` | Required | `SUPER_ADMIN`, `PROJECT_ADMIN`, `HR_MANAGER` | None | N-ary org tree visualizer & anomaly inspector |
| `/employees` | `EmployeeRosterPage` | `MainPlatformLayout` | Required | `SUPER_ADMIN`, `PROJECT_ADMIN`, `HR_MANAGER` | None | Roster data grid, AI CSV import, GDPR |
| `/surveys` | `SurveyListPage` | `MainPlatformLayout` | Required | `SUPER_ADMIN`, `PROJECT_ADMIN`, `HR_MANAGER` | None | Survey campaign listing |
| `/surveys/:surveyId/build` | `SurveyBuilderPage` | `MainPlatformLayout` | Required | `SUPER_ADMIN`, `PROJECT_ADMIN`, `HR_MANAGER` | None | Drag & drop survey AST builder & logic rules |
| `/distribution` | `CampaignDistributionPage` | `MainPlatformLayout` | Required | `SUPER_ADMIN`, `PROJECT_ADMIN`, `HR_MANAGER` | `smsDistributionEnabled` (optional) | Campaign dispatch wizard & live token monitor |
| `/analytics` | `AnalyticsDashboardPage` | `MainPlatformLayout` | Required | All except `SURVEY_RESPONDENT` | None | Scorecard, Heatmap Grid (N<5 privacy) |
| `/ai-insights` | `AiAnalyticsPage` | `MainPlatformLayout` | Required | `SUPER_ADMIN`, `PROJECT_ADMIN`, `HR_MANAGER`, `CONSULTANT_DAASH` | `aiAnalyticsEnabled` | Sentiment analysis & LLM summary drawer |
| `/reports` | `ReportGeneratorPage` | `MainPlatformLayout` | Required | All except `SURVEY_RESPONDENT` | None | Async PDF/XLSX export job progress |
| `/action-plans` | `ActionKanbanBoardPage` | `MainPlatformLayout` | Required | All except `SURVEY_RESPONDENT`, `CONSULTANT_DAASH` | `actionPlanningEnabled` | Kanban board with Jira/Planner sync |
| `/notifications` | `NotificationLogsPage` | `MainPlatformLayout` | Required | `SUPER_ADMIN`, `PROJECT_ADMIN` | None | Delivery log inspector |
| `/audit` | `AuditTrailPage` | `MainPlatformLayout` | Required | `SUPER_ADMIN`, `PROJECT_ADMIN` | None | Write-once immutable audit log search |
