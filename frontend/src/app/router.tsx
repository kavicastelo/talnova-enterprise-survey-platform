import React from 'react';
import { createBrowserRouter, Navigate } from 'react-router-dom';
import { MainPlatformLayout } from '../layouts/MainPlatformLayout';
import { AuthLayout } from '../layouts/AuthLayout';
import { ProtectedRoute } from '../components/auth/ProtectedRoute';
import { RoleGate } from '../components/auth/RoleGate';
import { FeatureGate } from '../components/auth/FeatureGate';

// Pages
import { LoginPage } from '../pages/auth/LoginPage';
import { AccessDeniedPage } from '../pages/auth/AccessDeniedPage';
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

// Domain Component Wrappers
import { KioskPlayerPage } from '../components/player/KioskPlayerPage';
import { SurveyPlayerPage } from '../components/player/SurveyPlayerPage';
import { PageHeader } from '../components/ui/PageHeader';
import { Card } from '../components/ui/Card';
import { SurveyResponse } from '../types/survey';

const DummyView: React.FC<{ title: string; subtitle: string; icon: string }> = ({ title, subtitle, icon }) => (
  <div>
    <PageHeader title={title} subtitle={subtitle} />
    <Card variant="bordered">
      <div style={{ padding: '32px', textAlign: 'center' }}>
        <div style={{ fontSize: '3rem', marginBottom: '12px' }}>{icon}</div>
        <h3 style={{ fontSize: '1.2rem', fontWeight: 700, color: '#0f172a' }}>{title} Module Connected</h3>
        <p style={{ color: '#64748b', fontSize: '0.9rem' }}>
          This business domain interface is ready for production feature integration against the API Gateway (:8080).
        </p>
      </div>
    </Card>
  </div>
);

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
  // Public Taker Routes
  {
    path: '/s/:token',
    element: (
      <SurveyPlayerPage
        projectId="PRJ-99201"
        campaignId="CMP-101"
        surveyId="SRV-5001"
        responseToken="DEMO-TOKEN-123"
        respondentType="AUTHENTICATED"
        survey={DEFAULT_DEMO_SURVEY}
      />
    ),
  },
  {
    path: '/kiosk/:surveyId',
    element: (
      <KioskPlayerPage
        projectId="PRJ-99201"
        campaignId="CMP-101"
        surveyId="SRV-5001"
        survey={DEFAULT_DEMO_SURVEY}
      />
    ),
  },

  // Auth Routes
  {
    element: <AuthLayout />,
    children: [
      { path: '/login', element: <LoginPage /> },
    ],
  },

  // Authenticated Platform Routes
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

      { path: '/notifications', element: <RoleGate allowedRoles={['SUPER_ADMIN', 'PROJECT_ADMIN']}><DummyView title="Notification Delivery Logs" subtitle="Inspect Kafka email and SMS dispatch delivery statuses" icon="🔔" /></RoleGate> },
      { path: '/audit', element: <RoleGate allowedRoles={['SUPER_ADMIN', 'PROJECT_ADMIN']}><DummyView title="Immutable System Audit Trail" subtitle="Write-once audit log inspector with correlation ID tracking" icon="🛡️" /></RoleGate> },
    ],
  },
]);
