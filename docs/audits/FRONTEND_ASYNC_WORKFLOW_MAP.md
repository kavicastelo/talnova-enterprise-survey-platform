# TESP Async Workflow Architecture & Job Polling Specification

**Platform:** Talnova Enterprise Survey Platform (TESP)  
**Asynchronous Mechanics:** Microservice Kafka Event Streams + Client Polling Hooks

---

## 1. Overview of Async Workflows

Because heavy processing operations (report rendering, bulk CSV validation, Kafka email dispatches, LLM analytics generation) are executed asynchronously by backend workers, **the frontend must never block the UI thread or fake synchronous completion**.

Every async workflow in TESP follows a standard 6-state lifecycle:

```
[ 1. INITIAL ACTION ]  --->  [ 2. PENDING / IN-PROGRESS ] (Polls via useAsyncJob)
                                      |
             +------------------------+------------------------+
             |                                                 |
             v                                                 v
   [ 3. SUCCESS STATE ]                              [ 4. FAILURE STATE ]
   (Triggers Cache Invalidation)                     (Exposes Error Details & Retry)
             |                                                 |
             v                                                 v
   [ 5. REFRESH / INVALIDATE ]                       [ 6. MANUAL RETRY ACTION ]
```

---

## 2. Asynchronous Workflow Specifications

### 2.1 Async Report Generation Workflow (`reporting-service`)
1. **Initial Action:** User selects report type (PDF/XLSX) in `ReportExportModal` and clicks "Generate Report". Invokes `POST /api/v1/reports/generate`.
2. **Pending State:** Receives `jobId` (`JOB-XXXXX`) with `status: PENDING`. Modal opens `ReportJobProgressDrawer` showing progress bar (0% - 100%).
3. **Progress / Status Polling:** `useAsyncJob` hook polls `GET /api/v1/reports/jobs/{jobId}` every 2000ms.
4. **Success State:** Job status becomes `COMPLETED` with `downloadUrl`. UI shows green checkmark and "Download File" button.
5. **Failure State:** Job status becomes `FAILED` with error message (e.g. "Sample size N < 5 suppressed"). UI shows error banner.
6. **Retry / Invalidation:** User can click "Re-run Report Job". Invalidate query key `['report-job', projectId, jobId]`.

### 2.2 Bulk CSV Employee Roster Import (`employee-service`)
1. **Initial Action:** Admin uploads CSV file in `CsvImportWizardModal` and submits header mapping. Invokes `POST /api/v1/employees/bulk-import`.
2. **Pending State:** Receives `importJobId` with `status: PROCESSING`. Modal switches to step 4 ("Ingestion in Progress").
3. **Progress / Status Polling:** Polls snapshot / import job status every 3000ms.
4. **Success State:** Displays summary stats: `processedCount`, `createdCount`, `terminatedCount`, `errorCount`.
5. **Failure State:** Ingestion fails due to invalid CSV structure or duplicate IDs. UI renders exact row-level error log.
6. **Retry / Invalidation:** User fixes CSV errors and re-uploads. Triggers TanStack Query invalidation for `['employees', projectId]`.

### 2.3 Survey Campaign Distribution Dispatch (`survey-distribution-service`)
1. **Initial Action:** Admin configures targets and clicks "Dispatch Campaign". Invokes `POST /api/v1/campaigns`.
2. **Pending State:** Campaign record created with status `SCHEDULED` or `DISPATCHING`. Kafka publishes distribution events to `tesp.notifications.queue.v1`.
3. **Progress / Status Polling:** Live monitor polls `GET /api/v1/campaigns/{campaignId}` every 5000ms to update sent/failed counters.
4. **Success State:** Status changes to `ACTIVE`. Live campaign monitor displays progress donut chart (% completed).
5. **Failure State:** Notification dispatch failure. Shows alert with failed recipient count and option to retry dispatch.
6. **Retry / Invalidation:** User triggers manual resend to failed tokens. Invalidates `['campaign', projectId, campaignId]`.

### 2.4 AI Executive Summary Generation (`ai-analytics-service`)
1. **Initial Action:** User opens `ExecutiveSummaryDrawer` and clicks "Generate AI Summary". Invokes `POST /api/v1/ai/summary`.
2. **Pending State:** Shows skeleton loader with message "Analyzing sentiment and extracting themes via LLM...".
3. **Progress / Status Polling:** Backend processes open-text responses via NLP pipeline. Polling endpoint `GET /api/v1/ai/summaries` until ready.
4. **Success State:** Renders generated summary cards, key themes, sentiment distribution scores, and action recommendations.
5. **Failure State:** AI service timeout or error. Shows fallback error message with manual refresh button.
6. **Retry / Invalidation:** User updates parameters and retries. Invalidates `['ai-insights', projectId, campaignId]`.

### 2.5 Action Plan External Integration Sync (`action-planning-service`)
1. **Initial Action:** Manager clicks "Sync to Jira" or "Sync to MS Planner" on an action card. Invokes `POST /api/v1/actions/{actionId}/sync-jira`.
2. **Pending State:** Action card displays syncing spinner badge ("Syncing to Jira...").
3. **Progress / Status Polling:** Awaits backend response or polls action plan status.
4. **Success State:** Badge updates to green "Jira Linked" with clickable external ticket link (`PROJ-102`).
5. **Failure State:** Integration error (invalid API token or network failure). Shows red badge "Sync Failed".
6. **Retry / Invalidation:** User clicks "Retry Sync". Invalidates query key `['action-kanban', projectId]`.

---

## 3. Generic Polling Hook Implementation (`hooks/useAsyncJob.ts`)

```typescript
import { useState, useEffect } from 'react';

interface UseAsyncJobOptions<T> {
  jobId: string | null;
  fetchStatusFn: (jobId: string) => Promise<{ status: 'PENDING' | 'PROCESSING' | 'COMPLETED' | 'FAILED'; progress?: number; data?: T; error?: string }>;
  pollIntervalMs?: number;
  onSuccess?: (data: T) => void;
  onError?: (error: string) => void;
}

export function useAsyncJob<T>({
  jobId,
  fetchStatusFn,
  pollIntervalMs = 2500,
  onSuccess,
  onError
}: UseAsyncJobOptions<T>) {
  const [status, setStatus] = useState<'IDLE' | 'PENDING' | 'PROCESSING' | 'COMPLETED' | 'FAILED'>('IDLE');
  const [progress, setProgress] = useState<number>(0);
  const [result, setResult] = useState<T | null>(null);
  const [errorDetails, setErrorDetails] = useState<string | null>(null);

  useEffect(() => {
    if (!jobId) return;
    setStatus('PENDING');

    const interval = setInterval(async () => {
      try {
        const res = await fetchStatusFn(jobId);
        setStatus(res.status);
        if (res.progress !== undefined) setProgress(res.progress);

        if (res.status === 'COMPLETED') {
          clearInterval(interval);
          setResult(res.data || null);
          if (onSuccess && res.data) onSuccess(res.data);
        } else if (res.status === 'FAILED') {
          clearInterval(interval);
          setErrorDetails(res.error || 'Job execution failed');
          if (onError) onError(res.error || 'Job execution failed');
        }
      } catch (err: any) {
        clearInterval(interval);
        setStatus('FAILED');
        setErrorDetails(err.message || 'Error checking job status');
      }
    }, pollIntervalMs);

    return () => clearInterval(interval);
  }, [jobId]);

  return { status, progress, result, errorDetails };
}
```
