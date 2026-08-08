# TESP Frontend Forensic Audit Report

**Platform:** Talnova Enterprise Survey Platform (TESP)  
**Auditor:** Lead Frontend Architect & Senior React Engineer  
**Date:** August 2026  
**Status:** Audit Complete — Action Required Before UI Feature Integration

---

## 1. Executive Summary

A comprehensive forensic audit of the current frontend repository (`/frontend`) was conducted to evaluate its readiness for production integration with the certified 13-service backend reactor. 

While the existing codebase contains rich domain component prototypes (such as `SurveyBuilderCanvas`, `OrgHierarchyManager`, `CampaignLaunchWizard`, and `ActionKanbanBoardPage`), **it is currently architected as a prototype/demo application**. It exhibits structural debt, state fragmentation, scattered `fetch` calls, hardcoded tenant context, mock data fallbacks, and complete absence of real JWT authentication / RBAC enforcement.

---

## 2. Forensic Findings by Subsystem

### 2.1 Package & Dependencies (`package.json`)
* **Core Libraries:** React 18.3.1, Vite 5.3.3, TypeScript 5.5.3.
* **State & Data Fetching:** `@tanstack/react-query` v5.51.1 is listed in `package.json`, but **is completely unused** across all components and pages.
* **Routing:** `react-router-dom` v6.24.1 is installed, but the application root (`App.tsx`) uses a single top-level `useState<'builder' | 'wizard' | ...>` tab switcher, bypassing browser URL history, deep linking, and layout routing.
* **UI & Visuals:** `lucide-react` (icons), `@tanstack/react-table` (tables), `@hello-pangea/dnd` (drag & drop), and `recharts` (charts) are installed.
* **Styling Engine:** TailwindCSS v3.4.4 is installed with PostCSS, but multiple components use extensive inline `style={{ ... }}` objects instead of standard Tailwind utility classes or design tokens.

### 2.2 Routing & Layout Architecture
* **Current Route Implementation:** All major features are loaded conditionally inside `App.tsx` via custom `activeTab` local state:
  ```tsx
  const [activeTab, setActiveTab] = useState<'builder' | 'wizard' | 'customizer' | 'features' | 'locales' | 'hierarchy' | 'roster'>('builder');
  ```
* **Consequences:** 
  1. No support for bookmarking or direct URLs (e.g., `/surveys/SRV-5001/build` or `/analytics`).
  2. Public kiosk/respondent player routes (`KioskPlayerPage`, `SurveyPlayerPage`) are disconnected from the application entry point.
  3. No route-level layout wrapper, navigation guards, or `ProtectedRoute` / `RoleGate` wrappers.

### 2.3 API Integration & HTTP Clients
* **Client Fragmentation:**
  * An `apiClient.ts` exists in `src/services/apiClient.ts` (using Axios with `baseURL: '/api/v1'`), but **is not imported or used by any domain API file**.
  * Domain API files in `src/api/` (`employeeApi.ts`, `orgApi.ts`, `projectConfigApi.ts`) and `src/services/` (`distributionApi.ts`, `responseIngestionApi.ts`, `surveyBuilderApi.ts`) make raw browser `fetch()` calls.
* **Header Context Gaps:**
  * API calls in `projectConfigApi.ts` and `orgApi.ts` omit required `X-Project-ID` and `X-Correlation-ID` headers, passing `projectId` as a query parameter instead (e.g., `?projectId=PRJ-99201`).
  * No JWT `Authorization: Bearer <token>` injection mechanism exists.
* **Error Normalization:** Each API file implements inconsistent error handling (`if (!res.ok) throw new Error(...)` vs `res.json()`), leaking raw exceptions to components without retry or toast notifications.

### 2.4 Hardcoded Mock Data & Fallback Anti-Patterns
A total of **28 instances** of hardcoded IDs and mock data fallbacks were identified across the codebase:
1. `App.tsx`: Hardcoded tenant `PRJ-99201`, employee list (`EMP-10020`, `EMP-10021`, `EMP-10022`), and static AWS S3 logo URL.
2. `apiClient.ts`: Fallback default tenant `PRJ-DEFAULT-001`.
3. `OrgHierarchyManager.tsx`: Embedded mock tree array (`ORG-ROOT`, `ORG-ENG`, `ORG-HR`) fallback if API calls fail.
4. `CsvImportWizardModal.tsx`: Fallback mock job execution (`JOB-DEMO-991`) when backend endpoint fails.
5. `CampaignLaunchWizard.tsx`: Hardcoded optimal time prediction payload using `EMP-AUTO` and `Corporate Head Office`.
6. `ActionKanbanBoardPage.tsx`: Hardcoded `projectId = 'PRJ-99201'` in component state.
7. `SurveyBuilderCanvas.tsx`: Hardcoded survey ID `SRV-5001`.

### 2.5 Multi-Tenant & Authorization Architecture
* **Tenant Isolation:** Tenant context is stored loosely in `localStorage.getItem('tesp_project_id')` or passed as explicit props through components. There is no centralized `TenantContext` provider that clears query caches upon switching projects.
* **Role-Based Access Control (RBAC):** No user authentication, session state, or RBAC guards (`SUPER_ADMIN`, `PROJECT_ADMIN`, `HR_MANAGER`, `DEPARTMENT_MANAGER`, `CONSULTANT_DAASH`, `SURVEY_RESPONDENT`) exist in the frontend UI.

### 2.6 State Management & Data Flow
* **Server State vs Local State:** Server data (employee profiles, survey ASTs, org trees, action plans) is fetched into raw local `useState` hooks inside components without caching, automatic revalidation, deduplication, or optimistic updates.
* **Stale State Risk:** Switching tabs or re-mounting components refetches data from scratch or retains stale component state.

### 2.7 Design System & Visual Consistency
* **Color Palette:** Components use a mix of hardcoded hex values (`#0f172a`, `#334155`, `#f8fafc`, `#3b82f6`) and raw CSS inline styles.
* **Primitive Components:** Missing centralized primitive UI components (e.g., `<Button>`, `<Input>`, `<Select>`, `<Modal>`, `<Badge>`, `<Card>`, `<Tabs>`). Each component manually styles raw HTML `<button>`, `<input>`, `<div style={{ ... }}>` elements.

---

## 3. Summary of Audit Findings Matrix

| Audit Dimension | Current Status | Production Target | Severity |
| :--- | :--- | :--- | :--- |
| **API Client** | Raw `fetch` in 6 files; unused Axios client | Centralized Axios gateway client with JWT & context interceptors | **HIGH** |
| **Routing** | State-based tab switching in `App.tsx` | `react-router-dom` v6 with layout routes & code-splitting | **HIGH** |
| **Authentication** | Absent | JWT Auth Service + `AuthContext` + Login flow | **CRITICAL** |
| **Multi-Tenancy** | Hardcoded props (`PRJ-99201`) / localStorage | Centralized `TenantContext` propagating `X-Project-ID` | **HIGH** |
| **RBAC / Authorization**| None | UX `PermissionGate` & `RoleGate` components | **HIGH** |
| **State Management** | Raw `useState` in components | `@tanstack/react-query` v5 for all server state | **HIGH** |
| **Design Primitives** | Inline styles & duplicate DOM elements | Standardized UI Component Library (TailwindCSS + Lucide) | **MEDIUM** |
| **Async Workflows** | Mock timeouts / faked synchronous updates | Job polling client for async reports, distribution & AI | **HIGH** |

---

## 4. Remediation Mandate

Before feature development resumes, the frontend must undergo architectural stabilization across:
1. Unified API Client Architecture (Gateway :8080 targeting).
2. Domain Router & Navigation Infrastructure.
3. Centralized Auth & Tenant Context.
4. TanStack Query Server-State Cache Setup.
5. Standardized Component Primitive Library.
