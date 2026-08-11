# Role-Based & Attribute Access Control (RBAC/ABAC) Specification

**Platform:** Talnova Enterprise Survey Platform (TESP)  
**Document ID:** AUTH-002  
**Status:** Production Standard

---

## 1. System Role Inventory

1. `SUPER_ADMIN`: Global platform owner with access to multi-project management, feature flags, system logs, and global configuration.
2. `PROJECT_ADMIN`: Tenant platform owner managing project settings, employee imports, survey distribution, and organization nodes.
3. `HR_MANAGER`: HR leader managing employee rosters, survey creation, campaign execution, and reporting.
4. `EXECUTIVE`: Enterprise executive with read-only visibility into high-level eNPS and organizational analytics.
5. `BUSINESS_UNIT_HEAD`: Regional or division leader scoped to specific organizational sub-trees.
6. `DEPARTMENT_MANAGER`: Manager scoped strictly to their department sub-tree.
7. `CONSULTANT_DAASH`: External advisor with read/write access to assigned project analytics, AI insights, reports, and action planning.
8. `VIEWER`: Read-only user permitted to view shared scorecards.
9. `SURVEY_RESPONDENT`: Participant submitting survey responses via public/token links.

---

## 2. Frontend Security Guards & Components

- **`<ProtectedRoute>`:** Ensures user is authenticated before rendering child routes. Redirects to `/login` if unauthenticated.
- **`<RoleGate allowedRoles={[...]} >`:** Checks if active user possesses at least one authorized role. Renders fallback or redirects to `/access-denied`.
- **`<PermissionGate permission="..." >`:** Evaluates granular feature permissions.
- **`<FeatureGate flag="..." >`:** Checks if active tenant project has enabled specific feature flags (`aiAnalyticsEnabled`, `actionPlanningEnabled`).
