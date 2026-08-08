# TESP Frontend State Management Architecture

**Platform:** Talnova Enterprise Survey Platform (TESP)  
**Primary Engine:** TanStack Query v5 (`@tanstack/react-query`) + React Context  
**Rule:** Server state must **NEVER** be stored in global UI stores or raw duplicated component state.

---

## 1. State Categorization & Ownership

```
+-------------------------------------------------------------------------+
|                              SERVER STATE                               |
|   Managed EXCLUSIVELY by TanStack Query v5                              |
|   - Project Config & Feature Flags                                      |
|   - Org Hierarchy Trees & Anomalies                                     |
|   - Employee Profiles & Roster Data                                     |
|   - Survey AST Definitions & Question Library                           |
|   - Campaign Metrics & Token Pools                                      |
|   - Analytics Dashboards & Heatmap Grid Data                            |
|   - AI Sentiment Insights & LLM Summaries                               |
|   - Action Plans & Kanban Cards                                         |
|   - Audit Log Entries                                                   |
+-------------------------------------------------------------------------+

+-------------------------------------------------------------------------+
|                             TENANT CONTEXT                              |
|   Managed by React Context (`TenantContext`)                            |
|   - Active Project ID (`PRJ-XXXXX`)                                     |
|   - Active Project Metadata & Branding Colors                           |
|   - Active Tenant Feature Flags                                         |
+-------------------------------------------------------------------------+

+-------------------------------------------------------------------------+
|                              SESSION STATE                              |
|   Managed by React Context (`AuthContext`)                              |
|   - Current User Profile                                                |
|   - JWT Token & Decode Claims                                           |
|   - User Role (`SUPER_ADMIN`, `HR_MANAGER`, etc.)                       |
+-------------------------------------------------------------------------+

+-------------------------------------------------------------------------+
|                            LOCAL UI STATE                               |
|   Managed by standard `useState` / `useReducer`                         |
|   - Modal Open/Close Toggles                                            |
|   - Active Tab Selections                                               |
|   - Form Input Drafts & Unsaved Canvas Edits                            |
|   - Table Sorting & Local Search Filter Input                           |
+-------------------------------------------------------------------------+
```

---

## 2. TanStack Query Query-Key Taxonomy

Query keys are strictly structured arrays containing the domain namespace, tenant scope (`projectId`), and specific parameters to prevent cross-tenant cache contamination:

| Domain | Query Key Hierarchy | Refetch / Cache Policy |
| :--- | :--- | :--- |
| **Project Config** | `['project', projectId]` | `staleTime: 5 * 60 * 1000` (5 mins) |
| **Public Theme** | `['theme', projectId]` | `staleTime: 10 * 60 * 1000` (10 mins) |
| **Org Hierarchy** | `['org-tree', projectId, rootNodeId]` | `staleTime: 2 * 60 * 1000` (2 mins) |
| **Org Anomalies** | `['org-anomalies', projectId]` | `staleTime: 1 * 60 * 1000` (1 min) |
| **Employee Roster**| `['employees', projectId, { page, limit, search }]` | `staleTime: 30 * 1000` (30 secs) |
| **Survey AST** | `['survey', projectId, surveyId]` | `staleTime: 10 * 1000` (10 secs) |
| **Question Bank** | `['questions', projectId]` | `staleTime: 5 * 60 * 1000` (5 mins) |
| **Campaign Status**| `['campaign', projectId, campaignId]` | `staleTime: 5 * 1000` (5 secs refetch) |
| **Analytics KPI** | `['analytics-kpi', projectId, campaignId]` | `staleTime: 1 * 60 * 1000` (1 min) |
| **Heatmap Grid** | `['analytics-heatmap', projectId, { slicers }]` | `staleTime: 1 * 60 * 1000` (1 min) |
| **AI Insights** | `['ai-insights', projectId, campaignId]` | `staleTime: 2 * 60 * 1000` (2 mins) |
| **Action Kanban** | `['action-kanban', projectId]` | `staleTime: 15 * 1000` (15 secs) |
| **Report Status** | `['report-job', projectId, jobId]` | `refetchInterval: 2000` (polling while pending) |
| **Audit Logs** | `['audit-logs', projectId, { page }]` | `staleTime: 30 * 1000` (30 secs) |

---

## 3. Query Client Global Configuration

```typescript
import { QueryClient } from '@tanstack/react-query';

export const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 30 * 1000, // 30 seconds default
      gcTime: 5 * 60 * 1000,  // 5 minutes garbage collection
      retry: (failureCount, error: any) => {
        // Do not retry 401/403/404 HTTP errors
        if (error?.status === 401 || error?.status === 403 || error?.status === 404) {
          return false;
        }
        return failureCount < 2;
      },
      refetchOnWindowFocus: false,
    },
    mutations: {
      retry: false,
    },
  },
});
```

---

## 4. Tenant Cache Invalidation Strategy

When the user selects a new project from the header tenant switcher (`TenantContext.setProjectId(newId)`):
1. `TenantContext` updates `activeProjectId`.
2. `queryClient.cancelQueries()` is executed to abort in-flight requests for the previous tenant.
3. `queryClient.invalidateQueries()` clears all server state queries, guaranteeing **zero stale data leakage** between projects.
