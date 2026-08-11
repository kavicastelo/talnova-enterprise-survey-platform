# Process-Flow Audit Record: FEAT-007 PF-ANL-005

| Metadata Field | Value |
|---|---|
| **Feature ID** | FEAT-007 |
| **Process Flow ID** | PF-ANL-005 |
| **Process Flow Name** | AI Anomaly Alerting & Key Driver Regression Insights |
| **Status** | PASS |
| **Date** | 2026-08-11 |
| **Owner** | Senior Principal Frontend Engineer & Auditor |

---

## 1. Description & Goal

Validate that automated trend anomaly alerts (flagging department nodes with negative score drops > 10% compared to baseline per Section 20) and key driver regression analysis (multiple linear regression identifying top Question Group themes explaining eNPS variance) are processed and rendered via the `AiInsightsPanel` component and `analyticsApi.getAiInsights` API integration.

---

## 2. Actor & Preconditions

- **Actor**: `EXECUTIVE`, `HR_MANAGER`.
- **Preconditions**:
  - Authenticated session with valid user role (`EXECUTIVE`, `HR_MANAGER`).
  - Active Project ID set (`X-Project-ID` header).
  - Campaign ID (`campaignId`) & Node Scope (`nodeId`).

---

## 3. Test Steps & Verification Executed

1. **API Integration**:
   - `analyticsApi.getAiInsights(campaignId, nodeId)` executes `GET /api/v1/analytics/ai-insights`.

2. **Multiple Linear Regression Key Driver Cards**:
   - Displays top Question Group themes with importance weights (beta coefficients %), correlation scores, and impact badges (`HIGH_IMPACT`, `MEDIUM_IMPACT`, `LOW_IMPACT`).

3. **Automated Score Drop Anomaly Alerts**:
   - Displays department node alerts experiencing negative score drops > 10% vs baseline snapshot.
   - Severity badges:
     - `CRITICAL DROP` (`bg-rose-50 border-rose-200 text-rose-700`).
     - `WARNING DROP` (`bg-amber-50 border-amber-200 text-amber-700`).

4. **UI Lifecycle States**:
   - Loading: Skeleton container.
   - Error: `ErrorState` with retry action.
   - Empty: `EmptyState` when AI background workers have not detected score drop anomalies or key drivers.

---

## 4. Verification Results

| Verification Type | Command / Method | Result | Details |
|---|---|---|---|
| **Unit & Integration Tests** | `npx vitest run src/__tests__/analyticsEngineDomain.test.ts` | **PASS (3/3)** | Verified analytics domain endpoints. |
| **Full Suite Tests** | `npx vitest run` | **PASS (69/69)** | All 14 test suites passed. |
| **TypeScript Compilation** | `npx tsc --noEmit` | **PASS (0 Errors)** | Zero type errors. |

---

## 5. File Changes Summary

- `frontend/src/types/analytics.ts`: Exported `AnomalyReport` and `KeyDriver` interfaces.
- `frontend/src/features/analytics/api/analyticsApi.ts`: Added `getAiInsights` method.
- `frontend/src/features/analytics/api/useAnalyticsQueries.ts`: Added `useAiAnalyticsInsightsQuery` hook.
- `frontend/src/components/analytics/AiInsightsPanel.tsx`: Created AI Insights Panel rendering key driver regression cards and anomaly alerts.
- `frontend/src/features/analytics/pages/AnalyticsDashboardPage.tsx`: Integrated "AI Anomaly Alerts & Key Drivers" tab.
- `docs/audits/FEAT-007-PF-ANL-005-AUDIT.md`: Created Process-Flow Audit Record.

---

## 6. Audit Conclusion

**STATUS: PASS**

Process Flow **PF-ANL-005** has reached PASS.
