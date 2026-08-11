# Routing & Navigation Architecture

**Platform:** Talnova Enterprise Survey Platform (TESP)  
**Document ID:** ROUTE-001  
**Status:** Production Standard

---

## 1. Route Hierarchy Overview

Routing is implemented via `React Router v6` (`createBrowserRouter`). The application defines distinct layout boundaries for Super Admin, Daash Consultant, Project Admin/HR, and Public Survey Takers.

```
/
├── auth
│   ├── login
│   └── access-denied
├── super-admin
│   ├── dashboard
│   ├── projects
│   ├── consultants
│   ├── feature-flags
│   ├── system-health
│   └── audit
├── consultant
│   ├── dashboard
│   ├── my-projects
│   ├── analytics
│   ├── ai-insights
│   ├── reports
│   └── action-plans
├── app
│   ├── dashboard
│   ├── organization
│   ├── employees
│   ├── surveys
│   ├── distribution
│   ├── responses
│   ├── analytics
│   ├── ai-insights
│   ├── reports
│   ├── action-plans
│   ├── notifications
│   ├── audit
│   └── settings (projects, branding, features, locales)
└── s / kiosk (Public Survey Respondent Routes)
    ├── /s/:token
    └── /kiosk/:surveyId
```

---

## 2. Shell Layout Responsibilities

- **`MainPlatformLayout`:** Sidebar navigation, project switcher dropdown, user identity menu, notification counter badge, breadcrumbs.
- **`SuperAdminLayout`:** Platform administration console navigation (Projects, System Health, Audit, Feature Flags).
- **`ConsultantLayout`:** Daash Global advisory layout focused on multi-project analytics, AI insights, executive reports, and remedial actions.
- **`SurveyPlayerLayout`:** Distraction-free public taker interface with progress indicator, accessibility controls, and response intake submission.
