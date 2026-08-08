# AGENTS.md

# Talnova Enterprise Survey Platform — Frontend Engineering Agent Guide

## 1. Purpose

This document defines the mandatory engineering rules, architecture boundaries, development workflow, and implementation standards for AI agents and human developers working on the **Talnova Enterprise Survey Platform (TESP) frontend**.

The frontend is a production-grade enterprise application that consumes the TESP backend through the **API Gateway**.

The frontend MUST NOT treat the backend as a collection of independent public services.

All browser communication MUST go through:

```text
Frontend
   ↓
API Gateway :8080
   ↓
TESP Microservices :8081–8092
```

The frontend must respect:

* Multi-tenancy
* RBAC
* Organization-tree scope
* Survey lifecycle/versioning
* Employee PII protection
* Anonymous survey response flows
* Async report generation
* Async analytics processing
* Kafka-driven state changes
* Auditability
* Feature flags
* API contracts
* Backend ownership boundaries

---

# 2. Product Context

TESP is an enterprise survey platform designed for organizations with potentially thousands of employees distributed across:

```text
Project
 ├── Sector
 │    ├── Business Segment
 │    │    ├── Company
 │    │    │    ├── Region
 │    │    │    │    └── Department / Team / Unit
```

The frontend must support highly dynamic project configurations.

A project may define:

* Branding
* Theme
* Feature flags
* Organization structure
* Employee attributes
* Survey structures
* Distribution campaigns
* Analytics dimensions
* Reporting requirements
* Action planning workflows
* Role permissions

Do not hardcode organization structures, employee attributes, survey questions, dashboard metrics, or project configuration assumptions.

---

# 3. Backend Architecture

The certified backend contains:

## Shared / Infrastructure Services

| Service              | Port | Responsibility                                                            |
| -------------------- | ---: | ------------------------------------------------------------------------- |
| API Gateway          | 8080 | Public ingress, routing, JWT context, tenant context, CORS, rate limiting |
| Notification Service | 8091 | Multi-channel notification dispatch                                       |
| Audit Service        | 8092 | Append-only security and administrative audit logs                        |

## Feature Services

| Service             | Port | Frontend Responsibility                                   |
| ------------------- | ---: | --------------------------------------------------------- |
| Project Config      | 8081 | Project settings, branding, feature flags                 |
| Organization        | 8082 | Organization hierarchy and scoped access                  |
| Employee            | 8083 | Employee management, attributes, imports                  |
| Survey Builder      | 8084 | Survey creation, editing, branching, publishing           |
| Survey Distribution | 8085 | Campaigns, invitations, reminders, tokenized distribution |
| Response Ingestion  | 8086 | Public survey response submission                         |
| Analytics Engine    | 8087 | Survey analytics and aggregated metrics                   |
| AI Analytics        | 8088 | Sentiment, risk alerts, executive insights                |
| Reporting           | 8089 | Report generation and export jobs                         |
| Action Planning     | 8090 | Remedial actions and workflow management                  |

---

# 4. API Gateway Rule

The browser MUST NOT directly call:

```text
http://localhost:8081
http://localhost:8082
...
http://localhost:8092
```

or production equivalents.

The frontend MUST communicate through the gateway.

Expected development architecture:

```text
Browser
   ↓
Vite Frontend
   ↓
/api/v1/*
   ↓
API Gateway :8080
   ↓
Internal Services
```

Use relative API paths whenever possible:

```text
/api/v1/projects
/api/v1/employees
/api/v1/surveys
/api/v1/analytics
/api/v1/reports
```

Do not expose internal service URLs to browser-side code.

---

# 5. Frontend Architecture

The frontend should follow a feature-oriented architecture.

Recommended structure:

```text
src/
├── app/
│   ├── router/
│   ├── providers/
│   ├── layouts/
│   ├── guards/
│   └── config/
│
├── core/
│   ├── api/
│   ├── auth/
│   ├── tenant/
│   ├── permissions/
│   ├── errors/
│   ├── telemetry/
│   └── storage/
│
├── features/
│   ├── project-config/
│   ├── organization/
│   ├── employees/
│   ├── survey-builder/
│   ├── distribution/
│   ├── responses/
│   ├── analytics/
│   ├── ai-analytics/
│   ├── reporting/
│   └── action-planning/
│
├── shared/
│   ├── components/
│   ├── ui/
│   ├── forms/
│   ├── tables/
│   ├── charts/
│   ├── feedback/
│   └── utilities/
│
├── hooks/
├── types/
├── constants/
├── styles/
└── main.tsx
```

The exact directory names may change if the existing repository already has a stronger established convention.

Do not restructure an existing working application unnecessarily.

---

# 6. Technology Rules

The frontend currently follows the established project direction:

* Vite
* React
* TypeScript
* Tailwind CSS
* shadcn/ui
* Modern React component architecture

Prefer:

* TypeScript
* React functional components
* Custom hooks
* Composition
* Typed API clients
* Schema validation
* Accessible components
* Reusable UI primitives

Avoid:

* JavaScript-only files
* Massive components
* Global mutable state without justification
* Direct DOM manipulation
* duplicated API logic
* duplicated UI logic
* arbitrary inline styles
* hardcoded backend data

---

# 7. Feature Boundaries

Each frontend feature should correspond conceptually to its backend ownership boundary.

For example:

```text
features/employees/
```

owns employee-related:

* Pages
* Components
* Forms
* Hooks
* API clients
* Types
* Validation
* State

It must not directly access:

```text
tesp_org_db
tesp_survey_db
tesp_analytics_db
```

The frontend does not know about databases.

It only knows backend API contracts.

---

# 8. API Layer

Never call APIs directly from UI components.

Bad:

```tsx
const response = await fetch("/api/v1/employees");
```

inside a page component.

Preferred:

```text
Component
   ↓
Hook
   ↓
Feature API
   ↓
Core API Client
   ↓
Gateway
```

Example:

```text
EmployeePage
    ↓
useEmployees()
    ↓
employeeApi.list()
    ↓
apiClient.get()
    ↓
/api/v1/employees
```

Centralize:

* Base URL
* Headers
* Authentication
* Correlation ID
* Error normalization
* Retry policy
* Request cancellation
* Serialization
* Response parsing

---

# 9. Tenant Context

TESP is strictly multi-tenant.

The backend requires project context.

The frontend must maintain the currently active:

```text
projectId
```

Tenant context MUST be handled centrally.

Do not scatter:

```tsx
headers: {
  "X-Project-ID": projectId
}
```

throughout the application.

Use a centralized API/context mechanism.

Every authenticated project-scoped API request must preserve the active project context.

Never trust a project ID supplied by an arbitrary UI component.

---

# 10. Authentication

Authentication state must be centralized.

The frontend must provide:

```text
AuthProvider
AuthContext
useAuth()
ProtectedRoute
Role/Permission Guards
```

Do not store authentication logic inside individual feature pages.

The frontend must correctly handle:

* Login
* Logout
* Session expiration
* Token expiration
* Unauthorized responses
* Forbidden responses
* Permission changes
* Project changes

A `401` must not silently become a generic application error.

A `403` must be treated as an authorization failure.

---

# 11. Authorization

The backend remains the ultimate authorization authority.

Frontend permission checks exist for UX and navigation purposes.

They MUST NOT be considered security controls.

Known roles include:

```text
SUPER_ADMIN
PROJECT_ADMIN
HR_MANAGER
DEPARTMENT_MANAGER
CONSULTANT_DAASH
SURVEY_RESPONDENT
```

Frontend components should use permission-aware abstractions:

```tsx
<Can permission="employee.read">
   ...
</Can>
```

or:

```tsx
usePermission("employee.read")
```

Do not scatter role checks such as:

```tsx
if (user.role === "ADMIN")
```

unless the exact business rule genuinely depends on the role itself.

Prefer capability/permission checks.

---

# 12. Organization Scope

The organization service uses a materialized-path hierarchy and ABAC subtree isolation.

The frontend must represent organization scope correctly.

Examples:

```text
Global
 └── Sector
      └── Business Segment
           └── Company
                └── Region
                     └── Department
```

Users may only see or manipulate data permitted by their organizational scope.

Do not assume:

```text
all managers can see all employees
```

The UI should make scope visible where appropriate.

---

# 13. Dynamic Configuration

Project configuration is dynamic.

The frontend must retrieve configuration from:

```text
project-config-service
```

through the gateway.

Do not hardcode:

* Company branding
* Logo
* Primary colors
* Survey settings
* Enabled modules
* Feature flags
* Employee attribute definitions
* Organization labels

Feature flags must be evaluated consistently.

If a backend feature flag disables a feature:

```text
Frontend navigation
Frontend routes
Frontend actions
Frontend pages
```

should respect the disabled state.

---

# 14. Survey Builder Rules

The survey builder is a complex product area.

Do not implement it as a collection of uncontrolled form fields.

It must account for:

* Survey metadata
* Sections
* Questions
* Question types
* Validation
* Branching logic
* Conditions
* Question ordering
* Versioning
* Draft state
* Published state
* Locked versions

Published surveys are immutable according to backend rules.

The frontend must never present editing controls for immutable published versions unless the backend explicitly exposes a supported versioning operation.

---

# 15. Survey Distribution

Distribution workflows may include:

* Campaign creation
* Employee targeting
* Organization targeting
* Channels
* Invitations
* Reminders
* Tokenized access
* Campaign state
* Delivery state

Distribution operations can be asynchronous.

Do not assume:

```text
POST campaign
=
campaign completely dispatched
```

The UI must distinguish:

```text
Created
Processing
Scheduled
Sending
Completed
Partially Failed
Failed
```

when those states are supported by the backend.

---

# 16. Response Intake

Public survey response intake is a special frontend surface.

It may be:

```text
Unauthenticated
Token-based
Public
Anonymous
```

Do not apply the same application shell and authentication assumptions used by administrative pages.

Response intake must prioritize:

* Speed
* Accessibility
* Mobile usability
* Minimal dependencies
* Clear validation
* Secure token handling
* Reliable submission
* Duplicate-submission handling

Do not expose administrative APIs from the public response application flow.

---

# 17. Analytics

Analytics are aggregated and privacy-protected.

The UI must never attempt to reverse engineer or reconstruct suppressed populations.

The backend enforces:

```text
N < 5 → suppression
```

Analytics components must correctly represent suppressed data.

Never display:

```text
0
```

when the actual state is:

```text
suppressed
```

Use an explicit presentation such as:

```text
Not available for privacy protection
```

when appropriate.

---

# 18. AI Analytics

AI analytics may provide:

* Sentiment
* Risk alerts
* Executive summaries
* Multilingual insights

AI output must be treated as backend-generated analytical data.

Do not expose raw employee PII unnecessarily.

Do not perform client-side AI processing on sensitive employee information.

The frontend should consume sanitized backend responses.

AI-generated information must have appropriate visual distinction from verified system facts where applicable.

---

# 19. Reporting

Report generation is asynchronous.

Expected flow:

```text
User
 ↓
Generate Report
 ↓
POST /reports/generate
 ↓
202 Accepted
 ↓
Job ID
 ↓
Polling / status mechanism
 ↓
Completed
 ↓
Download
```

Do not freeze the UI while waiting for report generation.

The UI should show:

```text
Queued
Processing
Completed
Failed
```

where supported.

Large exports must not be loaded entirely into browser memory unnecessarily.

---

# 20. Action Planning

Action planning is a state-machine-driven feature.

Do not implement arbitrary client-side state transitions.

The backend owns workflow validity.

The frontend should:

1. Display current state.
2. Display valid actions.
3. Request the transition.
4. Refresh authoritative state.
5. Display errors if transition is rejected.

Never assume:

```text
button clicked = state changed
```

---

# 21. Audit

The backend provides append-only audit logging.

Frontend actions that trigger auditable operations should provide appropriate request metadata.

Do not attempt to create or modify audit records directly in the frontend.

The frontend must never expose audit-log mutation functionality.

Audit data is read-only from the UI perspective.

---

# 22. Notifications

Notification delivery is asynchronous.

The frontend must not assume immediate delivery.

Example:

```text
Notification Requested
        ↓
Queued
        ↓
Processing
        ↓
Delivered / Failed
```

If notification status is displayed, it must come from backend state.

Never simulate successful delivery merely because the API request succeeded.

---

# 23. Loading States

Every asynchronous operation requires an intentional loading state.

Avoid generic:

```text
Loading...
```

everywhere.

Prefer contextual states:

```text
Loading employees
Loading survey structure
Generating report
Publishing survey
Processing campaign
Analyzing responses
Saving configuration
```

Use skeletons for page-level content where appropriate.

Use inline loading indicators for actions.

---

# 24. Error Handling

Errors must be normalized centrally.

The frontend should distinguish:

```text
400 Bad Request
401 Unauthorized
403 Forbidden
404 Not Found
409 Conflict
422 Validation Error
429 Rate Limited
500 Server Error
502/503 Infrastructure Error
504 Timeout
```

Do not expose raw backend stack traces to users.

Use:

```text
Human-readable message
Correlation ID
Retry action where appropriate
Support/debug information where appropriate
```

Correlation IDs are important for distributed troubleshooting.

---

# 25. Correlation IDs

The backend architecture uses:

```text
X-Correlation-ID
```

The frontend API layer should preserve or generate correlation identifiers according to the backend contract.

When displaying an error support reference, expose the correlation ID when available.

Example:

```text
Something went wrong.

Reference:
CORR-XXXXXXXX
```

---

# 26. State Management

Use the smallest appropriate state scope.

Prefer:

```text
Local UI state
   ↓
Feature state
   ↓
Server/cache state
   ↓
Global application state
```

Do not put everything into one global store.

Server state should be treated differently from UI state.

Avoid duplicating server entities unnecessarily.

---

# 27. Forms

Forms must provide:

* Strong TypeScript types
* Client-side validation
* Server-side error handling
* Accessible labels
* Keyboard navigation
* Clear required-field indicators
* Dirty-state detection where appropriate
* Unsaved-change protection where necessary

Client-side validation improves UX.

Backend validation remains authoritative.

---

# 28. Tables

Enterprise data tables should support appropriate combinations of:

* Search
* Filtering
* Sorting
* Pagination
* Column visibility
* Empty states
* Loading states
* Error states
* Bulk operations
* Selection
* Responsive behavior

Do not build one giant generic table abstraction that makes every use case difficult.

Build composable table primitives.

---

# 29. Responsive Design

The application must work across:

```text
Desktop
Laptop
Tablet
Mobile
```

Administrative dashboards may prioritize desktop but must not become unusable on smaller screens.

Public response experiences must be strongly mobile-first.

---

# 30. Accessibility

All UI must target modern accessibility standards.

At minimum:

* Semantic HTML
* Keyboard navigation
* Visible focus states
* Proper labels
* ARIA only when necessary
* Accessible dialogs
* Accessible dropdowns
* Accessible tables
* Sufficient contrast
* Screen-reader-friendly status messages

Do not sacrifice accessibility for visual effects.

---

# 31. Design System

Use the project's shadcn/ui foundation.

Do not introduce a second UI framework without explicit approval.

Build reusable primitives for:

```text
Buttons
Inputs
Selects
Dialogs
Drawers
Tables
Cards
Tabs
Badges
Alerts
Empty states
Skeletons
Charts
Command menus
Navigation
```

Visual consistency is mandatory.

---

# 32. Visual Quality

The frontend must look like a serious enterprise SaaS product.

Avoid:

* Default browser controls
* Generic Bootstrap-looking layouts
* Excessive rounded cards
* Excessive shadows
* Random gradients
* Huge typography
* Decorative UI with no purpose
* Dashboard-template repetition
* Inconsistent spacing
* Random colors

Prioritize:

```text
Information hierarchy
Whitespace
Typography
Consistency
Density
Clarity
Accessibility
Interaction feedback
```

---

# 33. Component Rules

A component should have one clear responsibility.

Avoid:

```text
Dashboard.tsx
```

containing:

* API calls
* employee transformation
* chart calculations
* permissions
* modal state
* routing
* table rendering
* form logic
* notifications

Break responsibilities into:

```text
DashboardPage
DashboardHeader
DashboardFilters
DashboardMetrics
DashboardCharts
DashboardActivity
```

as appropriate.

---

# 34. Type Safety

Avoid:

```ts
any
```

unless there is a documented technical reason.

API responses must have explicit types.

Prefer:

```ts
type Employee = {
  id: string;
  projectId: string;
  ...
};
```

over untyped objects.

API contracts should be represented explicitly.

---

# 35. API Contract Integrity

Never invent an endpoint because the UI needs one.

Before implementing an API call:

1. Inspect the backend controller.
2. Inspect request DTO.
3. Inspect response DTO.
4. Inspect validation rules.
5. Inspect authentication requirements.
6. Inspect tenant requirements.
7. Inspect state transitions.
8. Inspect error behavior.

If the backend does not support an operation, do not fake it in production code.

Mark it as:

```text
TODO: Backend contract required
```

or stop and report the missing contract.

---

# 36. Mock Data

Mock data is permitted only during isolated UI development.

It must never silently remain as production behavior.

Clearly isolate mock data:

```text
mocks/
fixtures/
storybook/
tests/
```

Never mix mock data with real API code.

---

# 37. Routing

Routes must be organized by application area.

Example:

```text
/login

/app
/app/dashboard
/app/settings

/app/organization
/app/organization/tree

/app/employees
/app/employees/import

/app/surveys
/app/surveys/new
/app/surveys/:surveyId
/app/surveys/:surveyId/builder

/app/distribution
/app/analytics
/app/ai-insights
/app/reports
/app/actions
/app/audit
```

Exact URLs should follow the existing frontend specification.

Routes must respect:

* Authentication
* Project context
* Feature flags
* Permissions
* Organization scope

---

# 38. Navigation

Navigation must be permission-aware and feature-flag-aware.

Do not show every module to every user.

Navigation should be generated from application capabilities rather than duplicated role-specific menus.

---

# 39. Testing

Frontend implementation must include appropriate:

### Unit Tests

For:

* Utilities
* Hooks
* State logic
* Validation
* Transformations
* Permission logic

### Component Tests

For:

* Forms
* Tables
* Dialogs
* Critical workflows

### Integration Tests

For:

* API interactions
* Authentication
* Tenant propagation
* Permission enforcement
* Survey publishing
* Campaign workflows
* Reporting workflows

### E2E Tests

For critical user journeys.

At minimum, eventually cover:

```text
Login
Project selection
Employee management
Survey creation
Survey publishing
Campaign creation
Survey response
Analytics viewing
Report generation
Action creation
Notification status
Audit viewing
```

---

# 40. Definition of Done

A frontend feature is NOT complete when:

```text
The page renders.
```

It is complete only when:

* UI is implemented
* API integration is implemented
* Types are defined
* Loading states exist
* Error states exist
* Empty states exist
* Permissions are respected
* Tenant context is respected
* Feature flags are respected
* Responsive behavior is handled
* Accessibility is handled
* Backend contract is verified
* Tests exist for important logic
* No production mock data remains
* No console errors remain
* No unnecessary duplicated code remains

---

# 41. AI Agent Workflow

AI agents must follow this workflow:

```text
1. Inspect repository
2. Inspect existing frontend architecture
3. Inspect relevant backend contract
4. Identify dependencies
5. Plan implementation
6. Implement API layer
7. Implement state/data layer
8. Implement UI
9. Implement loading/error/empty states
10. Implement permissions
11. Implement tenant handling
12. Add tests
13. Run lint/typecheck
14. Run tests
15. Inspect affected screens
16. Fix regressions
17. Report completed work
```

Do not skip directly from:

```text
Requirement
   ↓
UI code
```

---

# 42. Change Discipline

Do not make unrelated changes.

If implementing:

```text
Employee Import
```

do not simultaneously rewrite:

```text
Dashboard
Navigation
Authentication
Theme system
```

unless required by the feature.

Keep commits and changes logically scoped.

---

# 43. Existing Code First

Before creating a new:

```text
Component
Hook
Utility
API client
Type
UI primitive
```

search the repository for an existing implementation.

Reuse when appropriate.

Do not create:

```text
EmployeeTable.tsx
EmployeeDataTable.tsx
EmployeesTable.tsx
EmployeeGridTable.tsx
```

for essentially the same responsibility.

---

# 44. Documentation

When architecture changes, update the relevant documentation.

Important documentation includes:

```text
AGENTS.md
AI_GENERATION_RULES.md
API contract documentation
Feature documentation
Architecture documentation
Environment documentation
```

Do not allow implementation to silently diverge from architecture.

---

# 45. Final Principle

The frontend is not a visual prototype.

It is the production client of a distributed enterprise backend.

Every implementation must preserve:

```text
Security
+
Tenant Isolation
+
Authorization
+
API Contract Integrity
+
Data Ownership
+
Async Workflow Correctness
+
Accessibility
+
Visual Quality
+
Maintainability
```

When in doubt:

> Prefer an explicit, typed, contract-driven implementation over a convenient shortcut.
