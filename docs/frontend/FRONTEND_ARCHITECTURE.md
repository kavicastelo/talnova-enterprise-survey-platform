# TESP Frontend Target Architecture Specification

**Platform:** Talnova Enterprise Survey Platform (TESP)  
**Role:** Lead Frontend Architect & Senior React Engineer  
**Status:** Production Architectural Specification

---

## 1. Domain-Driven Clean Architecture

The TESP frontend architecture adopts a clean, feature-encapsulated modular structure separating application foundations (HTTP client, Auth, Multi-tenancy, Design System primitives) from business domain features.

```
frontend/src/
├── app/                        # Application entry, providers, global router
│   ├── App.tsx                 # Root application wrapper with query & auth providers
│   └── router.tsx              # React Router v6 route configuration tree
├── core/                       # Platform core infrastructure
│   ├── api/                    # Centralized Axios client & API error handling
│   │   ├── client.ts           # Configured Axios instance with request/response interceptors
│   │   ├── endpoints.ts        # Typed API endpoint definitions
│   │   └── error.ts            # Error normalization & status code translation
│   ├── auth/                   # Authentication service & token manager
│   │   ├── auth.service.ts     # Login, logout, JWT decode, refresh loop
│   │   ├── auth.store.ts       # Session state management
│   │   └── auth.types.ts       # JWT payload, user identity & role interfaces
│   └── tenant/                 # Tenant context service & project context
│       ├── tenant.service.ts   # Active project switcher, metadata persistence
│       └── tenant.types.ts     # Project context interfaces
├── context/                    # React Context Providers
│   ├── AuthContext.tsx         # User session, JWT tokens, RBAC roles, permission evaluation
│   ├── TenantContext.tsx       # Active Project ID (`X-Project-ID`), project list, switcher
│   └── ToastContext.tsx        # Toast feedback notifications
├── components/                 # Standardized Primitive UI & Navigation
│   ├── ui/                     # Design tokens & UI primitives (Button, Modal, Input, Badge, etc.)
│   ├── auth/                   # ProtectedRoute, RoleGate, PermissionGate, FeatureGate
│   ├── layout/                 # Sidebar, Header, Breadcrumbs, TenantSwitcher, UserMenu
│   └── feedback/               # LoadingOverlay, ErrorAlert, EmptyState, JobProgress
├── layouts/                    # Role & Application Layout Shells
│   ├── MainPlatformLayout.tsx  # Standard project management layout shell
│   ├── SuperAdminLayout.tsx    # Dedicated platform administration console
│   ├── ConsultantLayout.tsx   # Advisory consultant layout shell
│   ├── AuthLayout.tsx          # Card wrapper for login and recovery screens
│   ├── SurveyPlayerLayout.tsx  # Distraction-free public respondent layout
│   └── KioskPlayerLayout.tsx   # Touch-optimized kiosk player layout
├── features/                   # Business Feature Modules (Domain Clients & Views)
│   ├── project-config/         # Provisioning, Themes, Feature Flags, Locales
│   ├── organization/           # Org Hierarchy Tree, Subtree Lineage, Anomalies
│   ├── employee/               # Employee Roster, CSV Import, Demographic Snapshots
│   ├── survey-builder/         # Survey Canvas, Question Library, AI Translator, Bias Inspector
│   ├── distribution/           # Campaign Studio, Token Generator, AI Optimal Time Predictor
│   ├── response-intake/        # Ingestion Monitor, Offline Queue Sync
│   ├── analytics/              # eNPS Scorecard, Engagement Heatmaps, Segment Slicers
│   ├── ai-analytics/           # AI Sentiment Analyzer, Risk Alerts, Executive Summaries
│   ├── reporting/              # Async Report Generator, Job Status Poller, PDF Exporter
│   ├── action-planning/        # Remedial Action Kanban Board, Integration Triggers
│   ├── notifications/          # Notification Log Viewer & Dispatch Monitor
│   └── audit/                  # Security Audit Trail Viewer & JSON Payload Inspector
├── hooks/                      # Custom React Hooks
│   ├── useAuth.ts              # Identity & role helper
│   ├── useTenant.ts            # Active project helper
│   ├── useApiQuery.ts          # TanStack Query wrapper
│   └── useAsyncJob.ts          # Polling hook for asynchronous job execution
└── types/                      # Domain Type Definitions matching Backend DTOs
```

---

## 2. Network & API Gateway Integration

All HTTP traffic from the React application flows exclusively through the API Gateway at `http://localhost:8080/api/v1`. The browser must **never** connect directly to individual microservice ports (`8081`-`8092`).

```
+-----------------------------------------------------------------------+
|                         REACT SINGLE PAGE APP                         |
|   (AuthContext, TenantContext, TanStack Query, Page Views, Shells)    |
+-----------------------------------------------------------------------+
                                   |
                         HTTP Bearer JWT Header
                         X-Project-ID Header
                         X-Correlation-ID Header
                                   v
+-----------------------------------------------------------------------+
|                        API GATEWAY (Port 8080)                        |
|                        /api/v1/[service]/**                           |
+-----------------------------------------------------------------------+
        |                  |                  |                  |
        v                  v                  v                  v
+---------------+  +---------------+  +---------------+  +---------------+
| ProjectConfig |  | Organization  |  |   Employee    |  | SurveyBuilder |
|  (Port 8081)  |  |  (Port 8082)  |  |  (Port 8083)  |  |  (Port 8084)  |
+---------------+  +---------------+  +---------------+  +---------------+
```

---

## 3. Data Flow & State Scoping

1. **Session State (`AuthContext`):** Current user profile, decoded JWT claims, global roles (`SUPER_ADMIN`, `CONSULTANT_DAASH`, `PROJECT_ADMIN`, etc.), token storage, login/logout functions.
2. **Tenant State (`TenantContext`):** Currently active `projectId`, project list, tenant switcher callback. Modifying tenant automatically invalidates TanStack Query cache.
3. **Server State (`TanStack Query v5`):** Managed backend query responses cached by keys `[domain, projectId, ...params]`. Handles automatic revalidation, optimistic updates, and background refetching.
4. **Client UI State:** Form inputs, modal open states, drawer visibility, active filters, local tab selections.
