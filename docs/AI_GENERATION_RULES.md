# AI_GENERATION_RULES.md

# TESP Frontend AI Generation & Verification Rules

## 1. Purpose

This document governs AI-assisted implementation of the TESP frontend.

The AI must generate frontend code from **verified product requirements**, not from visual assumptions.

The fundamental rule is:

> **Never generate a UI first and invent its behavior afterward.**

Instead:

```text
FEATURE DOCUMENT
      ↓
PROCESS FLOW
      ↓
REQUIREMENTS
      ↓
UI CONTRACT
      ↓
API CONTRACT
      ↓
IMPLEMENTATION
      ↓
REAL EXECUTION
      ↓
AUDIT
      ↓
CORRECTION
      ↓
VERIFICATION
```

---

# 2. SOURCE PRIORITY

When sources disagree, use this priority order:

```text
1. Original Feature Documents
2. Formal Product / Architecture Documents
3. Backend API Contracts
4. Backend Runtime Behavior
5. Existing Frontend Implementation
6. Existing Mock Data
7. AI Assumptions
```

Never let an existing frontend implementation override a documented requirement.

Never treat mock data as a product requirement.

---

# 3. FEATURE DOCUMENT ANALYSIS RULE

Before generating code for a feature, AI must answer internally:

```text
What is this feature supposed to accomplish?

Who uses it?

What are the process flows?

What starts each flow?

What is the expected result?

What data does the user provide?

What data does the system return?

What validation rules exist?

What permissions apply?

What states exist?

What errors can occur?

Which backend API supports each step?

Which UI components are required?
```

If these cannot be determined from the source documents, do not invent critical behavior.

---

# 4. PROCESS FLOW EXTRACTION

Break every feature into atomic process flows.

A process flow should represent a meaningful user journey.

Good:

```text
Create Employee
Import Employees
Configure Demographics
Publish Survey
Create Campaign
Generate Report
Create Action Plan
```

Bad:

```text
Click Button
Open Modal
Render Table
```

UI interactions belong inside process flows, not as independent product flows.

---

# 5. PROCESS FLOW IDENTIFIER

Every process flow must have a stable identifier.

Format:

```text
PF-{FEATURE}-{SEQUENCE}
```

Examples:

```text
PF-001-01
PF-001-02

PF-004-01
PF-004-02

PF-010-01
```

Never randomly rename identifiers after audit records have been created.

---

# 6. REQUIREMENT TRACEABILITY

Every implementation must be traceable to a source requirement.

Use comments or documentation where useful:

```text
FEAT-003
PF-003-06
BR-EMP-003
```

Do not add meaningless comments such as:

```ts
// Create employee
```

Prefer traceability where the logic implements an important business rule:

```ts
// FEAT-003 / PF-003-06 / BR-EMP-003
```

---

# 7. UI GENERATION RULE

For each process flow, AI must identify the complete UI required.

Consider:

```text
Route
Page
Navigation
Page header
Filters
Search
Tables
Cards
Forms
Dialogs
Drawers
Tabs
Pagination
Validation
Confirmation
Loading
Empty
Error
Success
Permission
Responsive
Accessibility
```

Do not stop after generating the primary screen.

---

# 8. API GENERATION RULE

Every server interaction must have an explicit API definition.

Record:

```text
Service
Endpoint
HTTP method
Path parameters
Query parameters
Headers
Request body
Response body
Error responses
Authentication requirement
Project context requirement
```

Example:

```text
GET /api/v1/employees
X-Project-ID: <projectId>
Authorization: Bearer <token>
```

Do not guess response structures when the API contract is available.

---

# 9. API GAP RULE

If a feature requires an API that does not exist:

```text
DO NOT:
- mock it permanently
- fake success
- store the result only in local state
- bypass the server
```

Create an audit entry:

```text
API GAP

Feature:
FEAT-XXX

Process:
PF-XXX-XX

Expected API:
...

Observed:
API unavailable

Impact:
...

Required backend change:
...
```

Then determine whether the backend needs modification.

---

# 10. SERVER ERROR RULE

Every real server error discovered during flow execution must be recorded.

Minimum information:

```text
Timestamp
Process flow
Endpoint
HTTP status
Response body
Frontend behavior
Expected behavior
Root cause
Resolution
```

Example:

```text
POST /api/v1/employees
HTTP 400

Server:
DEMOGRAPHIC_ATTRIBUTE_INVALID

Frontend:
Displayed generic error.

Required:
Map validation error to affected form field.

Fix:
Added field-level API validation mapping.
```

---

# 11. NEVER HIDE ERRORS

Do not write:

```ts
catch {
  return [];
}
```

when an API fails.

Do not write:

```ts
catch {
  setData(mockData);
}
```

Do not silently ignore:

```ts
catch {}
```

Do not convert server failures into successful UI state.

Errors must propagate to an appropriate user-facing state and remain observable during development.

---

# 12. REAL-DATA RULE

Production application flows must use real backend data.

Forbidden:

```ts
const employees = [...]
```

as the source for a completed feature.

Forbidden:

```ts
setTimeout(() => {
  setLoading(false);
}, 1000);
```

as simulation of backend behavior.

Forbidden:

```ts
const fakeAnalytics = ...
```

for production analytics.

Forbidden:

```ts
if (demoMode) ...
```

as the permanent application architecture.

---

# 13. AUTHENTICATION GENERATION

Authentication must be implemented as an application subsystem.

AI must consider:

```text
Login
Logout
Session restoration
Token expiration
Unauthorized response
Protected routes
Role resolution
Project access
Permission checks
```

Do not generate a fake login page that simply navigates to `/dashboard`.

---

# 14. AUTHORIZATION GENERATION

UI authorization must correspond to documented permissions.

Use:

```text
Role
+
Permission
+
Project Scope
+
Organizational Scope
```

where applicable.

Never assume:

```text
authenticated === authorized
```

The backend remains the final authorization authority.

---

# 15. PROJECT CONTEXT RULE

Every project-scoped API operation must preserve project context.

The application must know:

```text
currentUser
currentProject
currentRole
```

where required.

Do not allow arbitrary project IDs from user-controlled URL parameters to automatically become trusted tenant context.

---

# 16. STATE GENERATION

Every data-driven UI must define:

```text
INITIAL
LOADING
SUCCESS
EMPTY
ERROR
RETRY
```

Where relevant also:

```text
SAVING
SAVED
VALIDATION_ERROR
FORBIDDEN
UNAUTHORIZED
PROCESSING
COMPLETED
FAILED
```

Do not use a single boolean:

```ts
loading
```

to represent a complex asynchronous workflow.

---

# 17. FORM GENERATION

Every form must define:

```text
Initial values
Required fields
Validation
Field errors
Server errors
Submit state
Success state
Cancel behavior
Dirty state
Reset behavior
```

Never rely exclusively on HTML validation when business rules exist on the server.

---

# 18. TABLE GENERATION

For enterprise data tables consider:

```text
Loading
Empty
Error
Pagination
Search
Filters
Sorting
Selection
Row actions
Bulk actions
Permission-based actions
Responsive behavior
```

Do not generate a static table merely because the feature document mentions a list.

---

# 19. ASYNC JOB GENERATION

For operations returning asynchronous processing states, generate a state-aware UI.

Example:

```text
REQUESTED
   ↓
QUEUED
   ↓
PROCESSING
   ↓
COMPLETED
   ↓
DOWNLOAD
```

Failure:

```text
PROCESSING
   ↓
FAILED
   ↓
RETRY
```

Do not show "Completed" immediately after receiving `202 Accepted`.

The backend architecture explicitly defines asynchronous flows such as report generation and Kafka-driven processing, so the frontend must respect those semantics.

---

# 20. COMPONENT GENERATION

Before creating a component, determine whether an equivalent component already exists.

Prefer:

```text
shared/
features/
layouts/
pages/
```

over:

```text
components/
  Button1
  Button2
  ButtonNew
  BetterButton
```

Do not duplicate functionality.

---

# 21. HOOK GENERATION

Data-fetching and mutation logic should be separated from presentation.

Prefer:

```text
useEmployees()
useCreateEmployee()
useUpdateEmployee()
useDeleteEmployee()
```

rather than putting API calls directly inside complex page components.

---

# 22. TYPE GENERATION

Do not use:

```ts
any
```

to bypass uncertain API contracts.

If the API contract is uncertain:

```text
1. inspect API documentation
2. inspect backend DTO
3. inspect actual response
4. define explicit type
```

Use runtime validation where appropriate for critical external data.

---

# 23. DESIGN GENERATION

AI-generated design must follow enterprise product principles.

Avoid:

* Generic dashboard templates.
* Excessive gradients.
* Decorative blobs.
* Unnecessary glassmorphism.
* Excessive animations.
* Huge empty spaces.
* Repeated oversized cards.
* Fake KPI numbers.
* Random illustrations.
* Excessive rounded corners.
* "AI dashboard" visual clichés.

Prefer:

* Information density.
* Clear hierarchy.
* Strong typography.
* Consistent spacing.
* Data-first layouts.
* Professional charts.
* Contextual actions.
* Accessible interaction patterns.

---

# 24. RESPONSIVE RULE

Every feature must work at:

```text
Desktop
Tablet
Mobile
```

Do not simply shrink desktop layouts.

Determine how:

* tables
* filters
* forms
* sidebars
* navigation
* dialogs
* charts

behave on smaller screens.

---

# 25. ACCESSIBILITY RULE

Generated UI should include:

* Keyboard navigation.
* Visible focus states.
* Semantic HTML.
* Accessible labels.
* Meaningful button names.
* Appropriate ARIA usage.
* Sufficient contrast.
* Error announcements where appropriate.
* Non-color-only status indicators.

---

# 26. FEATURE COMPLETION RULE

Never report:

```text
FEAT-XXX complete
```

because the page renders.

Instead require:

```text
All documented process flows:
PASS
```

and:

```text
No unresolved blocking UI/API defects
```

and:

```text
Audit records created
```

---

# 27. FLOW TESTING RULE

For every process flow:

### A. Start from the real UI

Do not call the API manually and call that a frontend test.

### B. Perform the documented user actions

Follow the actual business workflow.

### C. Observe network traffic

Confirm:

```text
URL
Method
Headers
Payload
Response
Status
```

### D. Observe application state

Confirm:

```text
Loading
Success
Error
Persistence
Navigation
```

### E. Check backend result

Confirm that the expected server-side state was actually created/changed.

### F. Reload

Confirm the UI reconstructs state from the backend.

### G. Record the audit.

---

# 28. NEGATIVE TESTING

Where the feature document defines validation or security behavior, test failure paths too.

Examples:

```text
Missing required field
Invalid value
Duplicate record
Unauthorized user
Wrong project
Expired session
Forbidden operation
Invalid token
Expired token
Server unavailable
Malformed response
```

A successful happy path alone is insufficient.

---

# 29. CROSS-TENANT TESTING

Where applicable, verify:

```text
Project A
  ↓
Project A data visible

Project B
  ↓
Project B data visible

Project A
  ↓
Project B data NOT visible
```

Never treat frontend filtering as sufficient tenant isolation.

The backend architecture uses project-scoped data ownership and tenant indexes, so frontend behavior must preserve rather than undermine that model.

---

# 30. AUDIT FILE GENERATION

After each flow execution create:

```text
/docs/frontend-audit/{feature}/PF-{feature}-{flow}.md
```

The audit must explicitly record:

```text
UI missing
API missing
API mismatch
Server errors
Frontend errors
Validation issues
Authorization issues
Tenant issues
State issues
Responsive issues
Accessibility issues
Fixes
Re-test results
Final status
```

Do not write:

```text
Everything works.
```

without evidence.

---

# 31. AUDIT STATUS VALUES

Only use:

```text
PASS
PARTIAL
FAIL
BLOCKED
```

Meaning:

### PASS

The documented flow was executed successfully.

### PARTIAL

Some functionality works but documented requirements remain incomplete.

### FAIL

The flow was executed and produced an incorrect result.

### BLOCKED

The flow cannot currently be executed because of an external dependency or missing prerequisite.

---

# 32. FIX PRIORITY

When defects are found, prioritize:

```text
P0 — Security / tenant isolation / authentication
P1 — Broken core business workflow
P2 — Missing required functionality
P3 — Incorrect state/error behavior
P4 — UX/accessibility/responsive defects
P5 — Visual refinement
```

Never prioritize visual polish over a broken business process.

---

# 33. NO PREMATURE REFACTORING

Do not perform large architectural refactors while validating a process flow unless the existing architecture prevents correct implementation.

First make the flow work.

Then refactor when evidence demonstrates the need.

---

# 34. NO SILENT REQUIREMENT CHANGES

If the implementation contradicts the feature document:

Do not silently rewrite the requirement.

Record:

```text
DOCUMENTATION / IMPLEMENTATION DISCREPANCY
```

Then identify:

```text
Document requirement:
...

Current implementation:
...

Observed backend behavior:
...

Recommended resolution:
...
```

---

# 35. NO FALSE CERTIFICATION

The AI must never claim:

```text
COMPLETE
CERTIFIED
FULLY IMPLEMENTED
PRODUCTION READY
```

unless the defined verification gates have actually passed.

In particular:

```text
mvn test passed
```

does not mean:

```text
frontend feature passed
```

and:

```text
page renders
```

does not mean:

```text
process flow passed
```

---

# 36. FINAL FEATURE CERTIFICATION

A feature may be marked:

```text
CERTIFIED
```

only when:

```text
Feature document analyzed
        +
All process flows identified
        +
All required UI implemented
        +
All required APIs connected
        +
Authentication verified
        +
Authorization verified
        +
Project context verified
        +
Happy paths verified
        +
Required negative paths verified
        +
Persistence verified
        +
Refresh verified
        +
Error states verified
        +
Audit records completed
        +
Blocking issues resolved
```

---

# 37. FRONTEND IMPLEMENTATION ORDER

Use this implementation order:

```text
PHASE 1
Application Foundation
    ↓
Authentication
    ↓
Authorization
    ↓
Project Context
    ↓
Application Shell
    ↓
Navigation
    ↓
Shared UI System

PHASE 2
FEAT-001
    ↓
Process Flow Verification

PHASE 3
FEAT-002
    ↓
Process Flow Verification

PHASE 4
FEAT-003
    ↓
Process Flow Verification

PHASE 5
FEAT-004
    ↓
Process Flow Verification

PHASE 6
FEAT-005
    ↓
Process Flow Verification

PHASE 7
FEAT-006
    ↓
Process Flow Verification

PHASE 8
FEAT-007
    ↓
Process Flow Verification

PHASE 9
FEAT-008
    ↓
Process Flow Verification

PHASE 10
FEAT-009
    ↓
Process Flow Verification

PHASE 11
FEAT-010
    ↓
Process Flow Verification

PHASE 12
Talnova Super Admin
    ↓
Cross-feature verification

PHASE 13
Daash Global Consultant
    ↓
Cross-feature verification

PHASE 14
End-to-End Certification
```

---

# 38. CROSS-FEATURE VERIFICATION

After individual features pass, verify the major business chain:

```text
Project
   ↓
Organization
   ↓
Employees
   ↓
Survey
   ↓
Distribution
   ↓
Response
   ↓
Analytics
   ↓
AI Analytics
   ↓
Reporting
   ↓
Action Planning
   ↓
Notifications
```

This validates that individually passing features also work together.

The backend architecture explicitly connects response ingestion to analytics and AI analytics, analytics snapshots to action planning, and notification-producing services to the notification service.

---

# 39. AI RESPONSE FORMAT DURING IMPLEMENTATION

When reporting progress, use:

```text
FEATURE: FEAT-XXX

PROCESS FLOW:
PF-XXX-XX

STATUS:
PASS / PARTIAL / FAIL / BLOCKED

IMPLEMENTED:
- ...

UI GAPS:
- ...

API GAPS:
- ...

SERVER ERRORS:
- ...

FRONTEND ERRORS:
- ...

FIXES:
- ...

RETEST:
- ...

NEXT:
PF-XXX-XX
```

Do not provide vague progress statements.

---

# 40. FINAL PRINCIPLE

The AI is not being asked to:

> "Build a frontend based on what you think the product should look like."

The AI is being asked to:

> **"Discover the documented business process, implement the complete user journey, connect it to the real backend, execute the journey, observe every failure, document every gap, fix the gap, execute it again, and only then certify the flow."**

The frontend is complete only when the **business processes work**, not when the **screens look complete**.
