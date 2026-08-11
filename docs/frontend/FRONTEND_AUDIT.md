# TESP Frontend Forensic Audit Report

**Platform:** Talnova Enterprise Survey Platform (TESP)  
**Role:** Lead Frontend Architect, Senior React Engineer, UX Engineer, & Integration Engineer  
**Date:** August 2026  
**Status:** Audit Complete — Reconstruction Mission Active

---

## 1. Executive Summary

A comprehensive forensic audit of the `/frontend` application was conducted against the certified 13-service backend reactor. 

While the initial repository contains domain component prototypes (such as `SurveyBuilderCanvas`, `OrgHierarchyManager`, `CampaignLaunchWizard`, `ActionKanbanBoardPage`), the current application suffers from severe prototype debt:
1. **Prototype Routing:** Prior implementation relied on conditional `useState` switching in `App.tsx` bypassing browser history and layout architecture.
2. **Scattered HTTP Calls:** Uncoordinated `fetch()` calls across domain services without token injection, correlation headers, or standardized error handling.
3. **Hardcoded Mock Data:** 28+ occurrences of hardcoded project IDs (`PRJ-99201`), mock survey IDs, and hardcoded employee arrays overriding production flows.
4. **Missing Production Auth & Tenant Context:** Absent JWT authentication, refresh loop, session restoration, tenant context switching, or role-based navigation gates (`SUPER_ADMIN`, `CONSULTANT_DAASH`, etc.).
5. **UI & State Fragmentation:** TanStack Query was installed in `package.json` but completely unused. Server state was held in raw local component `useState`.

---

## 2. Forensic Subsystem Audit

### 2.1 Dependency & Build Configuration (`package.json`, `vite.config.ts`)
* **Tech Stack:** React 18.3.1, Vite 5.3.3, TypeScript 5.5.3, TailwindCSS 3.4.4.
* **Libraries Installed:** `@tanstack/react-query` v5.51.1, `@tanstack/react-table` v8.19.3, `recharts` v2.12.7, `@hello-pangea/dnd` v16.6.0, `lucide-react` v0.408.0, `react-router-dom` v6.24.1, `axios` v1.7.2.
* **Gap:** Query client and Axios instance existed in isolated files but were detached from UI components.

### 2.2 Routing & Navigation Shell
* **Legacy State:** Main application relied on single top-level tab switcher `useState<'builder'|'wizard'|...>` inside `App.tsx`.
* **Required Architecture:** Dedicated `React Router v6` layout wrappers (`MainPlatformLayout`, `SuperAdminLayout`, `ConsultantLayout`, `AuthLayout`, `SurveyPlayerLayout`, `KioskPlayerLayout`) with `<ProtectedRoute>`, `<RoleGate>`, and `<FeatureGate>`.

### 2.3 API Gateway & Security Interceptors
* **Gateway Entry Point:** API Gateway operating on `http://localhost:8080/api/v1`.
* **Headers Required:** `Authorization: Bearer <jwt>`, `X-Project-ID: <projectId>`, `X-Correlation-ID: <uuid>`.
* **Gap:** Existing services omitted `X-Correlation-ID` and passed `projectId` inconsistently via query parameters. Centralized Axios client (`client.ts`) lacked interceptors for dynamic token injection and correlation ID generation.

### 2.4 Hardcoded Business Mock Audit
| File Location | Hardcoded Value / Anti-Pattern | Production Replacement Strategy |
| :--- | :--- | :--- |
| `router.tsx` | `DEFAULT_DEMO_SURVEY` with ID `SRV-5001` | Dynamic fetching from `/api/v1/surveys/{surveyId}` |
| `App.tsx` | `projectId = 'PRJ-99201'` & static employee list | Dynamic `TenantContext` & `employeeApi` fetch |
| `OrgHierarchyManager.tsx` | Embedded mock tree array (`ORG-ROOT`, `ORG-ENG`) | `/api/v1/nodes/{nodeId}/subtree` API query |
| `CsvImportWizardModal.tsx` | Faked job ID `JOB-DEMO-991` | Async POST `/api/v1/employees/bulk-import` |
| `CampaignLaunchWizard.tsx` | Hardcoded optimal time prediction payload | `/api/v1/distribution/ai-optimal-time` endpoint |
| `ActionKanbanBoardPage.tsx` | Fixed `projectId = 'PRJ-99201'` | Dynamic query hook with active project ID |

### 2.5 Multi-Tenancy & Authorization (RBAC / ABAC)
* **Tenant Isolation:** Absence of a centralized `TenantContext` provider. Switching projects must clear query caches to prevent cross-tenant data leaks.
* **RBAC Enforcement:** Absence of user identity state (`AuthContext`) and permission checks for 9 platform roles (`SUPER_ADMIN`, `PROJECT_ADMIN`, `HR_MANAGER`, `EXECUTIVE`, `BUSINESS_UNIT_HEAD`, `DEPARTMENT_MANAGER`, `CONSULTANT_DAASH`, `VIEWER`, `SURVEY_RESPONDENT`).

---

## 3. Remediation Matrix

| Dimension | Legacy Status | Production Target Architecture | Severity |
| :--- | :--- | :--- | :--- |
| **API Client** | Raw `fetch` calls scattered across files | Centralized Axios Gateway client with interceptors | **CRITICAL** |
| **Auth & Session** | Faked `isLoggedIn` flags | JWT Auth service, token decoding, refresh loop, session restore | **CRITICAL** |
| **Tenant Context** | Static `PRJ-99201` strings | Dynamic `TenantContext` with persistent project state | **CRITICAL** |
| **Routing** | State-driven tab switcher | `React Router v6` nested routes, guards, role-based shells | **HIGH** |
| **State Management** | Uncached `useState` | `@tanstack/react-query` v5 caching and automatic revalidation | **HIGH** |
| **Design System** | Mixed inline styles & raw inputs | Standardized primitive UI library (`/components/ui`) | **MEDIUM** |
| **Async Jobs** | Mock timeouts | Job status polling hooks for reports, imports & distribution | **HIGH** |

---

## 4. Certification Criteria
The frontend rebuild will achieve production readiness only when zero mock data remains in core business flows, all requests pass through the API Gateway, and multi-tenant authorization is fully enforced.
