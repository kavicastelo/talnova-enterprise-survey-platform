# AGENTS.md

# TESP Frontend — Agent Operating Instructions

## 1. Mission

You are an implementation agent working on the frontend of the **Talnova Enterprise Survey Platform (TESP)**.

Your objective is **not** to create a visual demo, mock dashboard, or collection of disconnected screens.

Your objective is to transform the existing frontend into a **complete, production-oriented application** whose behavior is derived from the original TESP feature documents and verified against the actual backend through real process-flow execution.

The frontend must eventually provide:

* A fully working authentication experience.
* Role-aware application access.
* Project/tenant-aware behavior.
* Complete implementation of FEAT-001 through FEAT-010.
* Talnova Super Admin dashboard.
* Daash Global Consultant dashboard.
* Project administration interfaces.
* Survey management interfaces.
* Employee and organizational management.
* Survey distribution and campaign management.
* Response and analytics experiences.
* AI analytics experiences.
* Reporting workflows.
* Action planning workflows.
* Notification-related interfaces where applicable.
* Audit visibility where applicable.
* Proper loading, empty, error, success, and permission states.
* Real backend integration through the API Gateway.
* Modern, organized, maintainable application architecture.

---

# 2. SOURCE OF TRUTH

The **original feature documents are the primary source of truth** for frontend behavior.

Do NOT derive frontend requirements primarily from:

* Existing frontend screens.
* Existing frontend components.
* Existing mock data.
* Existing routes.
* Existing server implementation.
* Existing API client code.
* Screenshots.
* Placeholder dashboards.
* Agent assumptions.
* Generic SaaS patterns.

The existing frontend is considered an **implementation artifact**, not a requirements document.

The backend is considered the **implementation target** and integration source.

The original feature documents define what the product is supposed to do.

Therefore:

```text
ORIGINAL FEATURE DOCUMENT
        ↓
FEATURE REQUIREMENTS
        ↓
PROCESS FLOWS
        ↓
USER ACTIONS
        ↓
UI REQUIREMENTS
        ↓
API REQUIREMENTS
        ↓
REAL FLOW EXECUTION
        ↓
AUDIT
        ↓
IMPLEMENT / FIX
        ↓
RE-TEST
        ↓
CERTIFY
```

Never reverse this order merely because existing code happens to suggest another approach.

---

# 3. ABSOLUTE RULE: ONE FEATURE AT A TIME

Work through features sequentially.

The expected order is:

1. FEAT-001 — Project Configuration
2. FEAT-002 — Organization
3. FEAT-003 — Employee
4. FEAT-004 — Survey Builder
5. FEAT-005 — Survey Distribution
6. FEAT-006 — Response Ingestion
7. FEAT-007 — Analytics Engine
8. FEAT-008 — AI Analytics
9. FEAT-009 — Reporting
10. FEAT-010 — Action Planning

Do not jump randomly between features.

Do not partially implement ten features simultaneously.

Do not mark a feature complete because its dashboard page exists.

A feature is only considered complete when its documented process flows have been individually verified.

---

# 4. PROCESS-FLOW-FIRST DEVELOPMENT

For every feature:

### Step 1 — Read the original feature document

Read the entire relevant feature document before modifying code.

Identify:

* Actors.
* Roles.
* Permissions.
* Preconditions.
* Main workflows.
* Alternate workflows.
* Failure workflows.
* Business rules.
* Validation rules.
* Data requirements.
* UI requirements.
* API requirements.
* State transitions.
* Success conditions.
* Error conditions.
* Security requirements.
* Tenant/project requirements.
* Dependencies on other features.

Do not begin implementation after reading only the feature summary.

---

### Step 2 — Build a Process Flow Inventory

Create a temporary working inventory for the feature.

Example:

```text
FEAT-003

PF-003-01 Employee List
PF-003-02 Create Employee
PF-003-03 Edit Employee
PF-003-04 View Employee
PF-003-05 Configure Demographics
PF-003-06 Bulk CSV Import
PF-003-07 Import Validation
PF-003-08 Import Completion
PF-003-09 Employee Search
PF-003-10 Permission Enforcement
```

The actual process flows must come from the feature document.

Never invent process flows without labeling them as inferred requirements.

---

# 5. PROCESS FLOW GATE

Every process flow must pass through this lifecycle:

```text
DISCOVER
  ↓
DESIGN
  ↓
IMPLEMENT
  ↓
CONNECT
  ↓
EXECUTE
  ↓
OBSERVE
  ↓
AUDIT
  ↓
FIX
  ↓
RE-EXECUTE
  ↓
PASS
```

Do not move to the next process flow while the current flow has unresolved blocking defects.

---

# 6. EXECUTE THE FLOW, DO NOT JUST INSPECT CODE

A process flow is not considered tested because:

* A component exists.
* A route exists.
* An API function exists.
* TypeScript compiles.
* A mock response renders.
* A backend endpoint exists.
* A unit test passes.

The agent must execute the actual user journey wherever the environment allows it.

For example:

```text
Login
  ↓
Select project
  ↓
Open Employees
  ↓
Click Add Employee
  ↓
Fill required fields
  ↓
Submit
  ↓
Observe network request
  ↓
Observe server response
  ↓
Verify UI state
  ↓
Reload
  ↓
Verify persisted data
```

If a flow cannot be executed because infrastructure or backend dependencies are unavailable, record that as a blocked flow rather than pretending it passed.

---

# 7. FRONTEND AUDIT DURING EVERY FLOW

While executing every process flow, inspect all of the following.

## UI

Record:

* Missing pages.
* Missing components.
* Missing actions.
* Missing forms.
* Missing dialogs.
* Missing tables.
* Missing filters.
* Missing search.
* Missing pagination.
* Missing navigation.
* Missing states.
* Missing confirmation dialogs.
* Missing validation.
* Missing feedback.
* Poor information hierarchy.
* Broken responsive behavior.
* Inconsistent design.
* Demo/mock UI.
* Hardcoded values.

---

## API

Record:

* Missing API endpoint.
* Wrong endpoint.
* Wrong HTTP method.
* Wrong request payload.
* Wrong response mapping.
* Missing query parameters.
* Missing path parameters.
* Missing headers.
* Incorrect project/tenant context.
* Authentication failures.
* Authorization failures.
* CORS issues.
* Gateway routing issues.
* Timeout.
* Unexpected status codes.
* Incorrect error handling.

---

## Server

Record:

* HTTP errors.
* Validation errors.
* Authentication errors.
* Authorization errors.
* Business-rule errors.
* Serialization errors.
* Network failures.
* Kafka-related asynchronous failures where observable.
* Missing data.
* Incorrect state transitions.
* Unexpected response contracts.

Never hide server errors behind generic frontend messages during development.

Capture the actual status code and meaningful response body.

---

## State

Check:

* Initial state.
* Loading state.
* Success state.
* Empty state.
* Error state.
* Retry state.
* Partial state.
* Disabled state.
* Permission-denied state.
* Session-expired state.

---

# 8. AUDIT LOG REQUIREMENT

For **every process flow**, create or update a frontend implementation audit record.

Use a consistent format.

Recommended location:

```text
/docs/frontend-audit/
```

Recommended structure:

```text
FEAT-001/
  PF-001-01.md
  PF-001-02.md

FEAT-002/
  PF-002-01.md
  PF-002-02.md

...
```

Every process-flow audit must contain:

```markdown
# PF-XXX-XX — Process Flow Name

## Status

PASS | FAIL | BLOCKED | PARTIAL

## Source Requirement

Feature:
FEAT-XXX

Source document:
<document path>

Requirement references:
<exact references>

---

## Process Flow

1. ...
2. ...
3. ...

---

## Actors

- ...

---

## Preconditions

- ...

---

## UI Audit

### Required UI

- ...

### Existing UI

- ...

### Missing UI

- ...

### Incorrect UI

- ...

---

## API Audit

### Required APIs

- ...

### API Used

- ...

### Missing APIs

- ...

### Incorrect APIs

- ...

---

## Execution

### Request

METHOD:
URL:

Headers:
...

Payload:
...

### Response

HTTP Status:
...

Response:
...

---

## Server Errors

| Time | API | Status | Error | Impact |
|---|---|---:|---|---|
| ... | ... | ... | ... | ... |

---

## Frontend Errors

| Component | Error | Impact |
|---|---|---|
| ... | ... | ... |

---

## Missing Requirements

- ...

---

## Fixes Implemented

- ...

---

## Re-test

### Attempt 1

Result:

### Attempt 2

Result:

---

## Final Verification

- [ ] UI complete
- [ ] API connected
- [ ] Authentication verified
- [ ] Authorization verified
- [ ] Tenant/project context verified
- [ ] Validation verified
- [ ] Loading state verified
- [ ] Empty state verified
- [ ] Error state verified
- [ ] Success state verified
- [ ] Persistence verified
- [ ] Refresh/reload verified
- [ ] Responsive behavior verified
- [ ] No mock data
- [ ] No hardcoded business data

## Final Status

PASS
```

---

# 9. DO NOT MASK BACKEND DEFECTS

If the frontend expects an API that does not exist:

```text
DO NOT:
- create fake response data
- silently mock the endpoint
- bypass the API
- hardcode successful state
```

Instead:

```text
1. Record missing API.
2. Identify expected contract from feature document.
3. Identify closest backend endpoint if available.
4. Record discrepancy.
5. If permitted, implement/fix backend contract.
6. Re-run the flow.
7. Update audit.
```

The goal is a working system, not a visually convincing simulation.

---

# 10. NO MOCK DATA IN PRODUCTION FLOWS

Mock data may only be used for:

* Storybook.
* Isolated component development.
* Visual testing.
* Explicit development fixtures.

Mock data must never drive a completed feature workflow.

The final application must obtain real data through the backend.

Remove or disable:

* Fake dashboards.
* Fake statistics.
* Fake employee lists.
* Fake survey records.
* Fake reports.
* Fake notifications.
* Fake analytics.
* Fake authentication.
* Fake permissions.

---

# 11. AUTHENTICATION

Authentication must be treated as a real application capability.

Implement:

* Login.
* Logout.
* Session persistence.
* Session expiration.
* Unauthorized handling.
* Token handling.
* Protected routes.
* Role-aware navigation.
* Permission-aware UI.
* Project/tenant context.

Never implement:

```text
if (demoUser) ...
```

as the final authentication mechanism.

Never store privileged authentication secrets in frontend source code.

---

# 12. ROLE MODEL

The frontend must support the documented role model.

At minimum account for:

```text
SUPER_ADMIN
PROJECT_ADMIN
HR_MANAGER
DEPARTMENT_MANAGER
CONSULTANT_DAASH
SURVEY_RESPONDENT
```

Do not assume every role can access every screen.

The UI must reflect server authorization, but frontend authorization is only a UX/security boundary. The server remains authoritative.

---

# 13. DASHBOARDS

The frontend must support separate experiences for:

## Talnova Super Admin

The Super Admin experience must be designed around platform-level administration.

It should not simply be the same project dashboard with more menu items.

Expected concerns include:

* Projects/workspaces.
* Platform administration.
* Project configuration.
* Platform users.
* System-level monitoring where supported.
* Cross-project visibility where explicitly authorized.
* Audit visibility.
* Platform configuration.
* Operational status.

Only implement capabilities supported by the feature documents.

---

## Daash Global Consultant

The Consultant experience must be designed around consulting/analytics workflows.

It should emphasize the capabilities permitted to:

```text
CONSULTANT_DAASH
```

The consultant must not automatically receive project administration privileges.

The dashboard should prioritize:

* Assigned projects.
* Survey progress.
* Analytics.
* Reports.
* Insights.
* AI analytics where authorized.
* Action plans where authorized.

Do not expose administrative functionality merely because the API exists.

---

# 14. PROJECT/TENANT CONTEXT

TESP is multi-tenant.

The frontend must consistently maintain project context.

The backend architecture expects project context to flow through the gateway and services. The documented chain is:

```text
Client
  ↓
API Gateway
  ↓
Project Context
  ↓
Microservice
  ↓
Database / Kafka
```

The frontend must therefore:

* Know the active project.
* Send the required project context.
* Never silently switch projects.
* Clear project state on logout.
* Revalidate project access.
* Prevent accidental cross-project navigation.
* Display the active project clearly where appropriate.

Never trust project IDs supplied by arbitrary UI state without server authorization.

---

# 15. API INTEGRATION

All application API calls must go through the intended API Gateway.

Do not directly call internal service ports from the browser.

The documented gateway is the public ingress at port `8080`, with feature services behind it.

Use:

```text
Frontend
   ↓
API Gateway
   ↓
Backend Service
```

not:

```text
Frontend
   ↓
localhost:8083
```

or:

```text
Frontend
   ↓
internal-service-name:8083
```

---

# 16. DESIGN SYSTEM

The frontend must use a coherent design system.

Avoid:

* Random component styles.
* Page-specific button styles.
* Arbitrary spacing.
* Inconsistent typography.
* Multiple competing card styles.
* Excessive gradients.
* Decorative UI without purpose.
* Generic AI-generated dashboard aesthetics.
* Excessive rounded containers.
* Inconsistent icons.

Prefer:

* Clear hierarchy.
* Dense but readable enterprise interfaces.
* Consistent spacing.
* Reusable components.
* Accessible contrast.
* Predictable interaction patterns.
* Responsive layouts.
* Professional data visualization.
* Useful empty states.
* Meaningful micro-interactions.

---

# 17. COMPONENT ARCHITECTURE

Do not create one giant component per page.

Prefer:

```text
Page
 ├── PageHeader
 ├── Filters
 ├── DataSection
 │    ├── Table
 │    └── EmptyState
 ├── Dialog
 │    └── Form
 └── Feedback
```

Shared components belong in shared directories.

Feature-specific components belong inside their feature.

Avoid duplicating:

* Tables.
* Forms.
* Dialogs.
* Filters.
* Pagination.
* Toast handling.
* Error handling.
* API client logic.
* Loading indicators.

---

# 18. API CLIENT ARCHITECTURE

Separate:

```text
UI
↓
Hooks / Query Layer
↓
API Service
↓
HTTP Client
↓
API Gateway
```

Do not place raw fetch/axios calls throughout JSX.

Each feature should have a clear API layer.

Example:

```text
features/
  employees/
    api/
      employee.api.ts
    hooks/
      useEmployees.ts
      useCreateEmployee.ts
    components/
    pages/
    types/
```

---

# 19. ERROR HANDLING

Errors must be actionable.

Bad:

```text
Something went wrong.
```

Better:

```text
Unable to create the employee.
The server rejected the demographic attributes.
```

When possible expose:

* What failed.
* Why it failed.
* What the user can do.
* Retry action.

Do not expose sensitive internal stack traces to users.

---

# 20. ASYNC OPERATIONS

Several TESP operations are asynchronous.

Do not pretend an asynchronous operation completed immediately.

For example:

```text
Generate Report
      ↓
202 Accepted
      ↓
Job Created
      ↓
Processing
      ↓
Completed / Failed
      ↓
Download
```

The frontend must represent the actual state machine.

---

# 21. CROSS-FEATURE FLOWS

After individual feature flows pass, verify important cross-feature journeys.

Examples:

```text
Organization
   ↓
Employees
   ↓
Survey
   ↓
Distribution
   ↓
Responses
   ↓
Analytics
   ↓
AI Analytics
   ↓
Reports
   ↓
Action Plans
```

Cross-feature flows must be tested only after their individual feature dependencies are stable.

---

# 22. DEFINITION OF DONE

A feature is DONE only when:

* [ ] Original feature document reviewed.
* [ ] All documented process flows identified.
* [ ] Every process flow implemented.
* [ ] Every process flow executed.
* [ ] Real backend APIs used.
* [ ] Authentication verified.
* [ ] Authorization verified.
* [ ] Project context verified.
* [ ] Validation verified.
* [ ] Error states implemented.
* [ ] Empty states implemented.
* [ ] Loading states implemented.
* [ ] Success states implemented.
* [ ] Persistence verified.
* [ ] No production mocks remain.
* [ ] No hardcoded business data remains.
* [ ] Responsive behavior checked.
* [ ] Audit created for every process flow.
* [ ] All blocking defects resolved.
* [ ] Re-tests pass.
* [ ] Feature marked CERTIFIED.

---

# 23. AGENT BEHAVIOR

You are an implementation agent, not a speculative designer.

Before making changes:

1. Inspect the relevant feature document.
2. Inspect the current implementation.
3. Identify the process flow.
4. Determine required behavior.
5. Determine required API contract.
6. Implement the minimum complete solution.
7. Execute the flow.
8. Record defects.
9. Fix defects.
10. Re-execute.
11. Record the final result.

Do not perform broad rewrites without evidence.

Do not delete working functionality merely because it is stylistically different.

Do not create duplicate implementations.

Prefer incremental, verifiable changes.

---

# 24. STOP CONDITIONS

Stop and report instead of guessing when:

* The feature document is missing.
* A requirement is ambiguous and materially affects behavior.
* The backend contract contradicts the feature document.
* A required API does not exist.
* Authentication infrastructure is unavailable.
* Required external infrastructure is unavailable.
* A destructive migration is required.
* A security-sensitive behavior is unclear.

Record the issue in the audit rather than silently inventing behavior.

---

# 25. FINAL PRINCIPLE

The target is not:

> "A frontend that looks complete."

The target is:

> **"A frontend in which every documented business process can actually be performed by an authorized user, through the real API, with correct UI behavior, correct server interaction, correct state handling, and a recorded verification trail."**
