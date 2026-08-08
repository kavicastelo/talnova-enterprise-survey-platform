# TESP Frontend Authorization & RBAC Permission Matrix

**Platform:** Talnova Enterprise Survey Platform (TESP)  
**Security Model:** Frontend UX Guarding & Scoping (Backend Spring Security remains authoritative)

---

## 1. User Roles

1. **`SUPER_ADMIN`**: Global platform administrator. Full access across all tenant projects, system configuration, audit logs, and project provisioning.
2. **`PROJECT_ADMIN`**: Tenant project administrator. Full control over project branding, feature toggles, roster, surveys, distribution, and project audit logs.
3. **`HR_MANAGER`**: Human Resources leader. Controls org hierarchy, employee roster, survey authoring, campaign launching, full analytics, and action planning.
4. **`DEPARTMENT_MANAGER`**: Line manager. Scoped access to subtree employees, survey results for their department ($N \ge 5$), and action planning assigned items.
5. **`CONSULTANT_DAASH`**: Third-party organizational consultant. Read-only access to anonymized analytics, AI sentiment summaries, and report generation. Cannot view PII or launch campaigns.
6. **`SURVEY_RESPONDENT`**: Survey participant (employee or public taker). Access restricted strictly to assigned public or tokenized survey player interfaces.

---

## 2. Feature & Domain Capability Matrix

| Feature Domain / Action | SUPER_ADMIN | PROJECT_ADMIN | HR_MANAGER | DEPARTMENT_MANAGER | CONSULTANT_DAASH | SURVEY_RESPONDENT |
| :--- | :---: | :---: | :---: | :---: | :---: | :---: |
| **Provision New Project** | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ |
| **Configure Branding & Locales** | ✅ | ✅ | ❌ | ❌ | ❌ | ❌ |
| **Toggle Feature Flags** | ✅ | ✅ | ❌ | ❌ | ❌ | ❌ |
| **Manage Org Hierarchy** | ✅ | ✅ | ✅ | 👁️ (Subtree) | 👁️ (Read Only) | ❌ |
| **Import / Edit Employee Roster** | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ |
| **GDPR Anonymization** | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ |
| **Author & Publish Surveys** | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ |
| **Launch Campaigns & Tokens** | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ |
| **Take Survey (Player)** | ❌ | ❌ | ❌ | ❌ | ❌ | ✅ |
| **View High-Level Analytics** | ✅ | ✅ | ✅ | 👁️ ($N \ge 5$) | ✅ | ❌ |
| **View AI Sentiment & NLP** | ✅ | ✅ | ✅ | ❌ | ✅ | ❌ |
| **Override AI Insight Classification**| ✅ | ✅ | ✅ | ❌ | ❌ | ❌ |
| **Generate & Export Reports** | ✅ | ✅ | ✅ | 👁️ (Department) | ✅ | ❌ |
| **Approve / Reject Action Plans** | ✅ | ✅ | ✅ | 👁️ (Assigned) | ❌ | ❌ |
| **Sync Actions to Jira/MS Planner** | ✅ | ✅ | ✅ | 👁️ (Assigned) | ❌ | ❌ |
| **Inspect System Audit Logs** | ✅ | ✅ | ❌ | ❌ | ❌ | ❌ |

---

## 3. Reusable Security Primitives

### 3.1 `<ProtectedRoute>`
Guards routes requiring an active authenticated session. Redirects to `/login` if unauthenticated.

```tsx
export const ProtectedRoute: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const { isAuthenticated, isLoading } = useAuth();
  if (isLoading) return <LoadingSpinner />;
  if (!isAuthenticated) return <Navigate to="/login" replace />;
  return <>{children}</>;
};
```

### 3.2 `<RoleGate>`
Restricts child components or routes based on the current user's role.

```tsx
interface RoleGateProps {
  allowedRoles: Role[];
  fallback?: React.ReactNode;
  children: React.ReactNode;
}

export const RoleGate: React.FC<RoleGateProps> = ({ allowedRoles, fallback = null, children }) => {
  const { user } = useAuth();
  if (!user || !allowedRoles.includes(user.role)) {
    return fallback ? <>{fallback}</> : <Navigate to="/access-denied" replace />;
  }
  return <>{children}</>;
};
```

### 3.3 `<FeatureGate>`
Controls feature access based on active project feature flags (`project-config-service`).

```tsx
interface FeatureGateProps {
  flag: keyof FeatureFlags;
  fallback?: React.ReactNode;
  children: React.ReactNode;
}

export const FeatureGate: React.FC<FeatureGateProps> = ({ flag, fallback = null, children }) => {
  const { featureFlags } = useTenant();
  if (!featureFlags[flag]) {
    return <>{fallback}</>;
  }
  return <>{children}</>;
};
```
