import { createBrowserRouter, Navigate } from 'react-router-dom';
import { MainPlatformLayout } from '../layouts/MainPlatformLayout';
import { SuperAdminLayout } from '../layouts/SuperAdminLayout';
import { ConsultantLayout } from '../layouts/ConsultantLayout';
import { AuthLayout } from '../layouts/AuthLayout';
import { PublicSurveyLayout } from '../layouts/PublicSurveyLayout';
import { ProtectedRoute } from '../components/auth/ProtectedRoute';
import { RoleGate } from '../components/auth/RoleGate';
import { FeatureGate } from '../components/auth/FeatureGate';

// Auth Pages
import { LoginPage } from '../pages/auth/LoginPage';
import { AccessDeniedPage } from '../pages/auth/AccessDeniedPage';

// Super Admin Pages
import { SuperAdminDashboardPage } from '../features/super-admin/pages/SuperAdminDashboardPage';

// Consultant Pages
import { ConsultantDashboardPage } from '../features/consultant/pages/ConsultantDashboardPage';

// General Executive Dashboard Page
import { ExecutiveDashboardPage } from '../pages/dashboard/ExecutiveDashboardPage';

// FEAT-001 Project Config Domain Pages
import { ProjectProvisioningPage } from '../features/project-config/pages/ProjectProvisioningPage';
import { ThemeBrandingPage } from '../features/project-config/pages/ThemeBrandingPage';
import { FeatureFlagPage } from '../features/project-config/pages/FeatureFlagPage';
import { LocaleManagementPage } from '../features/project-config/pages/LocaleManagementPage';

// FEAT-002 Organization Hierarchy Domain Pages
import { OrgHierarchyPage } from '../features/organization/pages/OrgHierarchyPage';

// FEAT-003 Employee Management Domain Pages
import { EmployeeRosterPage } from '../features/employee/pages/EmployeeRosterPage';

// FEAT-004 Survey Builder Domain Pages
import { SurveyBuilderPage } from '../features/survey-builder/pages/SurveyBuilderPage';

// FEAT-005 Survey Distribution Domain Pages
import { DistributionStudioPage } from '../features/distribution/pages/DistributionStudioPage';

// FEAT-007 Analytics Engine Domain Pages
import { AnalyticsDashboardPage } from '../features/analytics/pages/AnalyticsDashboardPage';

// FEAT-008 AI Analytics Domain Pages
import { AiAnalyticsPage } from '../features/ai-analytics/pages/AiAnalyticsPage';

// FEAT-009 Reporting Engine Domain Pages
import { ReportingCenterPage } from '../features/reporting/pages/ReportingCenterPage';

// FEAT-010 Action Planning Domain Pages
import { ActionKanbanBoardPage } from '../features/action-planning/pages/ActionKanbanBoardPage';

// Notification & Audit Domain Pages
import { NotificationLogsPage } from '../features/notifications/pages/NotificationLogsPage';
import { AuditTrailPage } from '../features/audit/pages/AuditTrailPage';

// Public Taker Pages
import { KioskPlayerPage } from '../components/player/KioskPlayerPage';
import { SurveyPlayerPage } from '../components/player/SurveyPlayerPage';
import { SurveyResponse } from '../types/survey';

const DEFAULT_DEMO_SURVEY: SurveyResponse = {
  id: 'SRV-5001',
  surveyId: 'SRV-5001',
  projectId: 'PRJ-99201',
  title: { 'en-US': 'Employee Engagement Survey 2026' },
  description: { 'en-US': 'Group-wide annual employee engagement and culture audit.' },
  pages: [],
  version: 1,
  status: 'PUBLISHED',
  createdAt: new Date().toISOString(),
  updatedAt: new Date().toISOString(),
};

export const router = createBrowserRouter([
  // Public Respondent Taker Routes
  {
    path: '/s/:token',
    element: (
      <PublicSurveyLayout projectId="PRJ-99201">
        <SurveyPlayerPage
          projectId="PRJ-99201"
          campaignId="CMP-101"
          surveyId="SRV-5001"
          responseToken="DEMO-TOKEN-123"
          respondentType="AUTHENTICATED"
          survey={DEFAULT_DEMO_SURVEY}
        />
      </PublicSurveyLayout>
    ),
  },
  {
    path: '/kiosk/:surveyId',
    element: (
      <PublicSurveyLayout projectId="PRJ-99201">
        <KioskPlayerPage
          projectId="PRJ-99201"
          campaignId="CMP-101"
          surveyId="SRV-5001"
          survey={DEFAULT_DEMO_SURVEY}
        />
      </PublicSurveyLayout>
    ),
  },

  // Auth Routes
  {
    element: <AuthLayout />,
    children: [
      { path: '/login', element: <LoginPage /> },
    ],
  },

  // Super Admin Dedicated Console Routes
  {
    path: '/super-admin',
    element: (
      <ProtectedRoute>
        <RoleGate allowedRoles={['SUPER_ADMIN']}>
          <SuperAdminLayout />
        </RoleGate>
      </ProtectedRoute>
    ),
    children: [
      { path: '', element: <Navigate to="/super-admin/dashboard" replace /> },
      { path: 'dashboard', element: <SuperAdminDashboardPage /> },
      { path: 'projects', element: <ProjectProvisioningPage /> },
      { path: 'consultants', element: <ConsultantDashboardPage /> },
      { path: 'feature-flags', element: <FeatureFlagPage /> },
      { path: 'audit', element: <AuditTrailPage /> },
      { path: 'system', element: <SuperAdminDashboardPage /> },
    ],
  },

  // Consultant Dedicated Portal Routes
  {
    path: '/consultant',
    element: (
      <ProtectedRoute>
        <RoleGate allowedRoles={['SUPER_ADMIN', 'CONSULTANT_DAASH']}>
          <ConsultantLayout />
        </RoleGate>
      </ProtectedRoute>
    ),
    children: [
      { path: '', element: <Navigate to="/consultant/dashboard" replace /> },
      { path: 'dashboard', element: <ConsultantDashboardPage /> },
      { path: 'projects', element: <ProjectProvisioningPage /> },
      { path: 'analytics', element: <AnalyticsDashboardPage /> },
      { path: 'ai-insights', element: <AiAnalyticsPage /> },
      { path: 'reports', element: <ReportingCenterPage /> },
      { path: 'actions', element: <ActionKanbanBoardPage /> },
    ],
  },

  // Standard Authenticated Platform Routes
  {
    element: (
      <ProtectedRoute>
        <MainPlatformLayout />
      </ProtectedRoute>
    ),
    children: [
      { path: '/', element: <Navigate to="/dashboard" replace /> },
      { path: '/dashboard', element: <ExecutiveDashboardPage /> },
      { path: '/access-denied', element: <AccessDeniedPage /> },

      // FEAT-001 Project Config Domain Routes
      { path: '/settings/projects', element: <RoleGate allowedRoles={['SUPER_ADMIN']}><ProjectProvisioningPage /></RoleGate> },
      { path: '/settings/branding', element: <RoleGate allowedRoles={['SUPER_ADMIN', 'PROJECT_ADMIN']}><ThemeBrandingPage /></RoleGate> },
      { path: '/settings/features', element: <RoleGate allowedRoles={['SUPER_ADMIN', 'PROJECT_ADMIN']}><FeatureFlagPage /></RoleGate> },
      { path: '/settings/locales', element: <RoleGate allowedRoles={['SUPER_ADMIN', 'PROJECT_ADMIN']}><LocaleManagementPage /></RoleGate> },

      // FEAT-002 Organization Hierarchy Domain Routes
      { path: '/organization', element: <RoleGate allowedRoles={['SUPER_ADMIN', 'PROJECT_ADMIN', 'HR_MANAGER']}><OrgHierarchyPage /></RoleGate> },

      // FEAT-003 Employee Management Domain Routes
      { path: '/employees', element: <RoleGate allowedRoles={['SUPER_ADMIN', 'PROJECT_ADMIN', 'HR_MANAGER']}><EmployeeRosterPage /></RoleGate> },

      // FEAT-004 Survey Builder Domain Routes
      { path: '/surveys', element: <RoleGate allowedRoles={['SUPER_ADMIN', 'PROJECT_ADMIN', 'HR_MANAGER']}><SurveyBuilderPage /></RoleGate> },

      // FEAT-005 Survey Distribution Domain Routes
      { path: '/distribution', element: <RoleGate allowedRoles={['SUPER_ADMIN', 'PROJECT_ADMIN', 'HR_MANAGER']}><DistributionStudioPage /></RoleGate> },

      // FEAT-007 Analytics Engine Domain Routes
      { path: '/analytics', element: <RoleGate allowedRoles={['SUPER_ADMIN', 'PROJECT_ADMIN', 'HR_MANAGER', 'DEPARTMENT_MANAGER', 'CONSULTANT_DAASH']}><AnalyticsDashboardPage /></RoleGate> },

      // FEAT-008 AI Analytics Domain Routes
      { path: '/ai-insights', element: <FeatureGate flag="aiAnalyticsEnabled"><RoleGate allowedRoles={['SUPER_ADMIN', 'PROJECT_ADMIN', 'HR_MANAGER', 'CONSULTANT_DAASH']}><AiAnalyticsPage /></RoleGate></FeatureGate> },

      // FEAT-009 Reporting Engine Domain Routes
      { path: '/reports', element: <RoleGate allowedRoles={['SUPER_ADMIN', 'PROJECT_ADMIN', 'HR_MANAGER', 'DEPARTMENT_MANAGER', 'CONSULTANT_DAASH']}><ReportingCenterPage /></RoleGate> },

      // FEAT-010 Action Planning Domain Routes
      { path: '/action-plans', element: <FeatureGate flag="actionPlanningEnabled"><RoleGate allowedRoles={['SUPER_ADMIN', 'PROJECT_ADMIN', 'HR_MANAGER', 'DEPARTMENT_MANAGER']}><ActionKanbanBoardPage /></RoleGate></FeatureGate> },

      // Notification & Audit Domain Routes
      { path: '/notifications', element: <RoleGate allowedRoles={['SUPER_ADMIN', 'PROJECT_ADMIN']}><NotificationLogsPage /></RoleGate> },
      { path: '/audit', element: <RoleGate allowedRoles={['SUPER_ADMIN', 'PROJECT_ADMIN']}><AuditTrailPage /></RoleGate> },
    ],
  },
]);
