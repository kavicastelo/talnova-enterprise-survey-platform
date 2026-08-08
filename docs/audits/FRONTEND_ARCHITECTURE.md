# TESP Target Frontend Architecture Specification

**Platform:** Talnova Enterprise Survey Platform (TESP)  
**Author:** Lead Frontend Architect & Senior React Engineer  
**Status:** Approved Architectural Blueprint

---

## 1. Domain-Driven Clean Architecture

The TESP frontend architecture adopts a domain-driven, feature-encapsulated modular React structure. It decouples core platform infrastructure (HTTP client, authentication, multi-tenancy, design system) from business feature domains.

### Target Directory Layout (`/frontend/src`)

```
src/
├── app/                        # Application entry, providers, global app wrapper
│   ├── App.tsx                 # Root application component with global providers
│   └── router.tsx              # React Router v6 route configuration tree
├── routes/                     # Page-level route view components
│   ├── auth/                   # Login, Access Denied, Token Auth routes
│   ├── dashboard/              # Executive & Tenant Dashboard
│   ├── project-config/         # Provisioning, Themes, Feature Flags, Locales
│   ├── organization/           # Org Hierarchy Tree, Lineage & Anomalies
│   ├── employees/              # Roster Grid, CSV Import Wizard, GDPR
│   ├── surveys/                # Builder Canvas, Logic Simulator, Library
│   ├── distribution/           # Campaign Launch Wizard, Live Monitor
│   ├── player/                 # Public Survey & Kiosk Player routes
│   ├── responses/              # Response Intake Monitor
│   ├── analytics/              # Executive KPI Scorecard, Heatmap Grid
│   ├── ai-insights/            # AI Sentiment, Executive Summaries, Bias
│   ├── reports/                # Report Job Generator, Export Modal
│   ├── action-plans/           # Kanban Board, Action Detail Drawer
│   ├── notifications/          # Notification Logs & Dispatch Monitor
│   └── audit/                  # Audit Trail Search & Immutable Viewer
├── layouts/                    # Application structural layouts
│   ├── MainPlatformLayout.tsx  # Sidebar, Header, Tenant Selector, User Menu
│   ├── SurveyPlayerLayout.tsx  # Distraction-free respondent interface
│   ├── KioskPlayerLayout.tsx   # Offline/Kiosk touch-optimized wrapper
│   └── AuthLayout.tsx          # Centered card layout for authentication
├── components/                 # Shared & Primitive UI Components
│   ├── ui/                     # Design system primitives (Button, Modal, Card, Input, etc.)
│   ├── data-display/           # Data grids, heatmaps, scorecards, badges
│   ├── forms/                  # Form controls, select slicers, file dropzones
│   ├── feedback/               # Toast notifications, progress bars, empty states
│   └── navigation/             # Sidebar, Breadcrumbs, Tenant Switcher, Tab Bar
├── features/                   # Domain Business Feature Modules
│   ├── project-config/         # Components & hooks for project configuration
│   ├── organization/           # Hierarchy tree canvas, node creation, move drawer
│   ├── employees/              # Roster table, import modal, mapping steps
│   ├── surveys/                # Builder canvas, question drag-drop, logic editor
│   ├── distribution/           # Launch wizard, schedule predictor, token manager
│   ├── responses/              # Offline response queue, ingestion logger
│   ├── analytics/              # KPI scorecard, heatmap filter bar, chart widgets
│   ├── ai-analytics/           # Sentiment analyzer, bias indicator, summary generator
│   ├── reports/                # Export modal, job status drawer, progress bar
│   ├── action-planning/        # Kanban board, Jira/Planner sync triggers
│   ├── notifications/          # Dispatch status table, manual trigger modal
│   └── audit/                  # Log search bar, JSON payload inspector
├── services/                   # Core Infrastructure Services
│   ├── api/                    # Centralized Axios client & normalized error handling
│   │   ├── client.ts           # Configured Axios instance with interceptors
│   │   ├── endpoints.ts        # Typed API endpoint path definitions
│   │   └── error.ts            # Error normalization & status code handling
│   ├── auth/                   # Auth service, JWT token storage, decode logic
│   └── tenant/                 # Tenant context service, project switching logic
├── context/                    # React Context Providers
│   ├── AuthContext.tsx         # User identity, JWT, roles, login/logout
│   ├── TenantContext.tsx       # Active Project ID, project metadata, switcher
│   └── ToastContext.tsx        # Global feedback notification toasts
├── hooks/                      # Custom Utility & Service Hooks
│   ├── useAuth.ts              # Quick access to user session & RBAC roles
│   ├── useTenant.ts            # Quick access to active tenant project
│   ├── useApiQuery.ts          # Wrapped TanStack Query custom hook
│   └── useAsyncJob.ts          # Async polling hook for background job statuses
├── types/                      # TypeScript Interface Definitions
│   ├── auth.ts                 # User, JWT Claims, Roles, Permissions
│   ├── domain.ts               # Generic API Response wrapper, Pagination
│   ├── projectConfig.ts        # Project, Branding, FeatureFlags, Locales
│   ├── organization.ts         # OrgNode, SubTree, Lineage, Anomalies
│   ├── employee.ts             # EmployeeProfile, Roster, HeaderMapping
│   ├── survey.ts               # Survey AST, Question, LogicRule, Branch
│   ├── distribution.ts         # Campaign, Target, Prediction, Webhook
│   ├── response.ts             # Submission, OfflineItem, IngestionStatus
│   ├── analytics.ts            # KPI, HeatmapNode, SlicerFilters
│   ├── ai.ts                   # SentimentInsight, ExecutiveSummary, Bias
│   ├── reporting.ts            # ReportJob, ExportConfig, DownloadUrl
│   ├── action.ts               # ActionPlan, KanbanColumn, Integration
│   └── audit.ts                # AuditLogEntry, EventFilter
└── utils/                      # Helper Utilities
    ├── formatters.ts           # Date, currency, percentage formatters
    ├── validators.ts           # WCAG contrast checker, regex patterns
    ├── storage.ts              # LocalStorage & IndexedDB helpers
    └── logicEvaluator.ts       # Survey branching rule evaluation logic
```

---

## 2. Core Architectural Layers & Responsibilities

```
+-------------------------------------------------------------------+
|                        REACT UI COMPONENTS                        |
|   (Routes, Page Views, Feature Components, Primitive UI)          |
+-------------------------------------------------------------------+
                                  |
                                  v
+-------------------------------------------------------------------+
|                   TANSTACK QUERY (SERVER STATE)                   |
|   (Caching, Automatic Invalidation, Refetching, Optimistic State) |
+-------------------------------------------------------------------+
                                  |
                                  v
+-------------------------------------------------------------------+
|                     FEATURE API CLIENT MODULES                    |
|   (projectConfigApi, orgApi, employeeApi, surveyApi, etc.)        |
+-------------------------------------------------------------------+
                                  |
                                  v
+-------------------------------------------------------------------+
|                    CENTRALIZED GATEWAY HTTP CLIENT                |
|   (Axios Ingress, JWT Interceptor, X-Project-ID, Correlation-ID)   |
+-------------------------------------------------------------------+
                                  |
                                  v
+-------------------------------------------------------------------+
|                      API GATEWAY (Port 8080)                      |
|                      /api/v1/[service]/**                         |
+-------------------------------------------------------------------+
```

### Layer 1: Centralized HTTP Client (`services/api/client.ts`)
- All requests target `http://localhost:8080/api/v1` (or relative `/api/v1`).
- Interceptors inject:
  * `Authorization: Bearer <jwt_token>` (if user logged in)
  * `X-Project-ID: <active_project_id>` (from `TenantContext`)
  * `X-Correlation-ID: <uuid_v4>` (generated per request)
- Normalizes backend error responses into standard `ApiError` objects.

### Layer 2: Domain API Clients
- Every backend service has a dedicated API client module (`projectConfigApi.ts`, `orgApi.ts`, `employeeApi.ts`, etc.).
- Direct component call to `fetch()` or `axios()` is **strictly prohibited**.

### Layer 3: Server State Management (TanStack Query)
- Serves as the single source of truth for backend data.
- Automatically handles background revalidation, query keys (`['employees', activeProjectId]`), cache invalidation upon mutation, and loading/error states.

### Layer 4: React UI & Layout Layer
- Component library uses design tokens and Tailwind utility classes.
- Pages are mounted via React Router v6 inside layout shells (`MainPlatformLayout`, `SurveyPlayerLayout`).
- Authorization gates (`<RoleGate>`, `<PermissionGate>`) protect components based on user role from `AuthContext`.
