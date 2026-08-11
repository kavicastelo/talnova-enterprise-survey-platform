# API Integration Specification

**Platform:** Talnova Enterprise Survey Platform (TESP)  
**Document ID:** API-001  
**Status:** Production Standard

---

## 1. Centralized Axios Gateway Client (`src/core/api/client.ts`)

All HTTP communication targets the API Gateway (`http://localhost:8080/api/v1`). Component-level `fetch()` or `axios()` calls are strictly forbidden.

### Request Interceptors
1. **Bearer Token:** Injects `Authorization: Bearer <jwt_token>` from `AuthContext`.
2. **Tenant Header:** Injects `X-Project-ID: <activeProjectId>` from `TenantContext`.
3. **Correlation ID:** Generates unique `X-Correlation-ID: <uuid_v4>` per request.

### Response Interceptors & Error Normalization
Errors are mapped to standard `ApiError` instances:
- `400 Bad Request` -> Form/validation error payload.
- `401 Unauthorized` -> Triggers token refresh flow or redirects to `/login`.
- `403 Forbidden` -> Permission error notification / `<AccessDeniedPage />`.
- `404 Not Found` -> Resource missing notification.
- `409 Conflict` -> Business state conflict exception.
- `429 Rate Limit` -> Backoff warning notification.
- `500+ Internal Error` -> Server error alert with retry option.

---

## 2. Microservice API Endpoint Mapping

| Service Name | Base Route Path | Target Port | Service Client File |
| :--- | :--- | :--- | :--- |
| `project-config-service` | `/api/v1/projects/**` | 8081 | `src/features/project-config/api/projectConfigApi.ts` |
| `organization-service` | `/api/v1/nodes/**` | 8082 | `src/features/organization/api/orgApi.ts` |
| `employee-service` | `/api/v1/employees/**` | 8083 | `src/features/employee/api/employeeApi.ts` |
| `survey-builder-service` | `/api/v1/surveys/**` | 8084 | `src/features/survey-builder/api/surveyApi.ts` |
| `survey-distribution-service` | `/api/v1/campaigns/**` | 8085 | `src/features/distribution/api/distributionApi.ts` |
| `response-ingestion-service` | `/api/v1/responses/**` | 8086 | `src/features/response-intake/api/responseApi.ts` |
| `analytics-engine-service` | `/api/v1/analytics/**` | 8087 | `src/features/analytics/api/analyticsApi.ts` |
| `ai-analytics-service` | `/api/v1/ai/**` | 8088 | `src/features/ai-analytics/api/aiAnalyticsApi.ts` |
| `reporting-service` | `/api/v1/reports/**` | 8089 | `src/features/reporting/api/reportingApi.ts` |
| `action-planning-service` | `/api/v1/actions/**` | 8090 | `src/features/action-planning/api/actionApi.ts` |
| `notification-service` | `/api/v1/notifications/**` | 8091 | `src/features/notifications/api/notificationApi.ts` |
| `audit-service` | `/api/v1/audit/**` | 8092 | `src/features/audit/api/auditApi.ts` |
