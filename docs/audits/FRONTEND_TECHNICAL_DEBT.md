# TESP Frontend Technical Debt & Remediation Register

**Platform:** Talnova Enterprise Survey Platform (TESP)  
**Status:** Action Register Prioritized for Architectural Refactoring

---

## 1. Technical Debt Inventory Matrix

| Debt ID | Category | Affected File(s) | Description / Root Cause | Severity | Remediation Strategy |
| :--- | :--- | :--- | :--- | :---: | :--- |
| **DEBT-001** | **API Client** | `src/api/*.ts`, `src/services/*.ts` | Raw `fetch()` used in 6 domain files; bypasses `apiClient.ts` | **HIGH** | Refactor all domain API files to use centralized `apiClient.ts` |
| **DEBT-002** | **Headers** | `src/api/projectConfigApi.ts`, `orgApi.ts` | Missing `X-Project-ID`, `X-Correlation-ID`, `Authorization` headers | **HIGH** | Move header context injection into Axios request interceptors |
| **DEBT-003** | **Routing** | `src/App.tsx` | Tab state (`activeTab`) used instead of URL routing | **HIGH** | Replace tab state with React Router v6 domain routes |
| **DEBT-004** | **Auth** | `src/App.tsx`, All views | No login page, session state, JWT decode, or logout | **CRITICAL** | Implement `AuthContext`, JWT storage, and `<ProtectedRoute>` |
| **DEBT-005** | **Multi-Tenancy** | `src/App.tsx`, `ActionKanbanBoardPage.tsx` | Hardcoded project IDs (`PRJ-99201`) passed as props | **HIGH** | Create `TenantContext` provider and project dropdown switcher |
| **DEBT-006** | **State** | All domain components | Raw `useState` used for server state; `@tanstack/react-query` unused | **HIGH** | Wrap all backend API calls in TanStack Query custom hooks |
| **DEBT-007** | **Mock Data** | `App.tsx`, `OrgHierarchyManager.tsx`, `CsvImportWizardModal.tsx` | Fallback arrays and hardcoded mock data in UI components | **MEDIUM** | Remove fallbacks; expose real loading/error/empty UI states |
| **DEBT-008** | **Design System**| All components | Inline `style={{ ... }}` sprawl and duplicate raw HTML elements | **MEDIUM** | Extract standard primitive components into `src/components/ui/` |
| **DEBT-009** | **Async Jobs** | `ReportExportModal.tsx`, `CampaignLaunchWizard.tsx` | Synchronous UI assumptions for async backend processes | **HIGH** | Implement `useAsyncJob` status polling hook |
| **DEBT-010** | **Types** | `src/types/*.ts` | Field name mismatches between frontend types and backend DTOs | **MEDIUM** | Align TypeScript types 1:1 with Java backend DTO schemas |

---

## 2. Detailed Technical Debt Analysis & Remediation Steps

### 2.1 DEBT-001 & DEBT-002: API Client & Header Injection
- **Problem:** Currently, `projectConfigApi.ts` has lines like `const res = await fetch('/api/v1/projects/' + projectId)`. It does not include JWT tokens or correlation IDs.
- **Remediation:** Replace all raw `fetch` statements with `apiClient.get(...)` or `apiClient.post(...)`.

### 2.2 DEBT-003 & DEBT-004: Routing & Authentication
- **Problem:** User opens app directly into `SurveyBuilderCanvas` without logging in. URL remains `http://localhost:5173/`.
- **Remediation:** Introduce `src/app/router.tsx` with full URL path hierarchy and auth guards.

### 2.3 DEBT-005: Multi-Tenant Context Isolation
- **Problem:** Changing projects in one component does not update other components or invalidate queries.
- **Remediation:** Wrap application in `<TenantProvider>` and use `useTenant()` hook across all features.

### 2.4 DEBT-006: Server State Management
- **Problem:** Every time a user opens `OrgHierarchyManager`, it runs a raw `useEffect` fetching data into `useState`.
- **Remediation:** Implement `useOrgTreeQuery(projectId, nodeId)` using TanStack Query `useQuery`.

### 2.5 DEBT-007: Fallback & Mock Data Elimination
- **Problem:** `CsvImportWizardModal` contains: `// Fallback result for demonstration if API endpoint unavailable`.
- **Remediation:** Delete all fallback mock data objects. Return genuine error states when the backend API fails.
