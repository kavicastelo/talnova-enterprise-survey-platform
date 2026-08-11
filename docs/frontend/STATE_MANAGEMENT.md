# State Management Architecture Specification

**Platform:** Talnova Enterprise Survey Platform (TESP)  
**Document ID:** STATE-001  
**Status:** Production Standard

---

## 1. State Category Separation

1. **Session & Auth State (`AuthContext`):** Active user token, user identity, roles, permissions.
2. **Tenant & Context State (`TenantContext`):** Active project ID (`projectId`), list of authorized projects. Changing project triggers query cache cleanup.
3. **Server State (`TanStack Query v5`):** All API responses (org node tree, employee list, survey AST, eNPS metrics, AI insights, report jobs).
4. **Transient Client UI State:** Modal open states, dropdown toggles, active form inputs, local filter selections.

---

## 2. TanStack Query Configuration (`src/app/queryClient.ts`)

- **Stale Time:** 5 minutes default for configuration data; 30 seconds for live analytics.
- **Retry Logic:** 2 automatic retries for idempotent GET requests with exponential backoff; 0 retries for POST/PUT mutations.
- **Cache Invalidation:** Calling `tenantService.switchProject(newProjectId)` invalidates active queries to prevent cross-tenant stale data rendering.
