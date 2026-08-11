# Backend API Contract Gap Analysis

**Platform:** Talnova Enterprise Survey Platform (TESP)  
**Document ID:** GAP-001  
**Status:** Audit & Tracking Record

---

## 1. Backend Contract Gap Log

| Service | Operation | Required Endpoint | Current Status / Fallback Mechanism |
| :--- | :--- | :--- | :--- |
| `project-config-service` | Multi-Tenant List | `GET /api/v1/projects` | Supported. Gateway forwards to port 8081. |
| `organization-service` | Org Node Tree | `GET /api/v1/nodes/{id}/subtree` | Supported. Returns node tree structure. |
| `employee-service` | HRIS Bulk Import | `POST /api/v1/employees/bulk-import` | Supported via Multipart form data. |
| `survey-distribution-service` | AI Optimal Time | `POST /api/v1/distribution/ai-optimal-time` | Supported. Returns optimal hour predictions. |
| `reporting-service` | Async Job Status | `GET /api/v1/reports/jobs/{jobId}` | Supported via 202 Accepted & Polling. |

All 13 backend services expose functional REST APIs via the Gateway at port 8080.
