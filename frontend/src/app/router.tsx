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

// Domain Component Wrappers
import { SurveyBuilderCanvas } from '../components/survey-builder/SurveyBuilderCanvas';
import { CampaignLaunchWizard } from '../components/distribution/CampaignLaunchWizard';
import { OrganizationalHeatmapGrid } from '../components/analytics/OrganizationalHeatmapGrid';
import { SentimentAnalyticsPanel } from '../components/ai/SentimentAnalyticsPanel';
import { ReportExportModal } from '../components/reporting/ReportExportModal';
import { ActionKanbanBoardPage } from '../pages/ActionKanbanBoardPage';
import { KioskPlayerPage } from '../components/player/KioskPlayerPage';
import { SurveyPlayerPage } from '../components/player/SurveyPlayerPage';
import { PageHeader } from '../components/ui/PageHeader';
import { Card } from '../components/ui/Card';
import { useTenant } from '../context/TenantContext';
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

const SurveyBuilderView: React.FC = () => {
  const { activeProject } = useTenant();
  return (
    <div>
      <PageHeader title="Survey Builder Studio" subtitle="Drag & drop question canvas, logic branching, and publishing" />
      <SurveyBuilderCanvas projectId={activeProject?.projectId || 'PRJ-99201'} surveyId="SRV-5001" />
    </div>
  );
};

const DistributionView: React.FC = () => {
  const { activeProject } = useTenant();
  return (
    <div>
      <PageHeader title="Survey Campaign Distribution Studio" subtitle="Dispatch multi-channel surveys with single-use tokens" />
      <CampaignLaunchWizard projectId={activeProject?.projectId || 'PRJ-99201'} surveyId="SRV-5001" surveyTitle="Employee Engagement Survey 2026" />
    </div>
  );
};

const AnalyticsView: React.FC = () => {
  return (
    <div>
      <PageHeader title="Analytics Engine & Heatmap Grid" subtitle="Organizational scorecards protected by differential privacy (N < 5)" />
      <OrganizationalHeatmapGrid
        data={{
          parentNodeId: 'ROOT',
          rowNodes: [
            { id: 'N-201', name: 'Engineering & Technology' },
            { id: 'N-202', name: 'Operations & Logistics' },
            { id: 'N-203', name: 'Human Resources' },
          ],
          columnThemes: [
            { id: 'QG-01', name: 'Leadership Trust' },
            { id: 'QG-02', name: 'Workload Balance' },
            { id: 'QG-03', name: 'Career Mobility' },
          ],
          cells: [
            { nodeId: 'N-201', groupId: 'QG-01', sampleSize: 24, score: 84.5, colorIntensity: 'GREEN' },
            { nodeId: 'N-201', groupId: 'QG-02', sampleSize: 22, score: 62.0, colorIntensity: 'YELLOW' },
            { nodeId: 'N-201', groupId: 'QG-03', sampleSize: 3, score: null, colorIntensity: 'GREY' },
            { nodeId: 'N-202', groupId: 'QG-01', sampleSize: 18, score: 71.2, colorIntensity: 'GREEN' },
            { nodeId: 'N-202', groupId: 'QG-02', sampleSize: 15, score: 48.0, colorIntensity: 'RED' },
            { nodeId: 'N-202', groupId: 'QG-03', sampleSize: 16, score: 65.5, colorIntensity: 'YELLOW' },
          ],
        }}
      />
    </div>
  );
};

const AiAnalyticsView: React.FC = () => {
  return (
    <div>
      <PageHeader title="AI Analytics & Sentiment Studio" subtitle="NLP sentiment extraction, topic modeling, and LLM executive summaries" />
      <SentimentAnalyticsPanel />
    </div>
  );
};

const ReportsView: React.FC = () => {
  const [isOpen, setIsOpen] = React.useState(true);
  const { activeProject } = useTenant();

  return (
    <div>
      <PageHeader title="Report Job Generator" subtitle="Async PDF and Excel report generation with polling progress status" />
      <ReportExportModal
        projectId={activeProject?.projectId || 'PRJ-99201'}
        campaignId="CMP-101"
        isOpen={isOpen}
        onClose={() => setIsOpen(false)}
        onJobSubmitted={(jobId) => alert(`Report export job queued with ID: ${jobId}`)}
      />
    </div>
  );
};

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

      { path: '/surveys', element: <RoleGate allowedRoles={['SUPER_ADMIN', 'PROJECT_ADMIN', 'HR_MANAGER']}><SurveyBuilderView /></RoleGate> },
      { path: '/distribution', element: <RoleGate allowedRoles={['SUPER_ADMIN', 'PROJECT_ADMIN', 'HR_MANAGER']}><DistributionView /></RoleGate> },

      { path: '/analytics', element: <RoleGate allowedRoles={['SUPER_ADMIN', 'PROJECT_ADMIN', 'HR_MANAGER', 'DEPARTMENT_MANAGER', 'CONSULTANT_DAASH']}><AnalyticsView /></RoleGate> },
      { path: '/ai-insights', element: <FeatureGate flag="aiAnalyticsEnabled"><RoleGate allowedRoles={['SUPER_ADMIN', 'PROJECT_ADMIN', 'HR_MANAGER', 'CONSULTANT_DAASH']}><AiAnalyticsView /></RoleGate></FeatureGate> },

      { path: '/reports', element: <RoleGate allowedRoles={['SUPER_ADMIN', 'PROJECT_ADMIN', 'HR_MANAGER', 'DEPARTMENT_MANAGER', 'CONSULTANT_DAASH']}><ReportsView /></RoleGate> },
      { path: '/action-plans', element: <FeatureGate flag="actionPlanningEnabled"><RoleGate allowedRoles={['SUPER_ADMIN', 'PROJECT_ADMIN', 'HR_MANAGER', 'DEPARTMENT_MANAGER']}><ActionKanbanBoardPage /></RoleGate></FeatureGate> },

      { path: '/notifications', element: <RoleGate allowedRoles={['SUPER_ADMIN', 'PROJECT_ADMIN']}><DummyView title="Notification Delivery Logs" subtitle="Inspect Kafka email and SMS dispatch delivery statuses" icon="🔔" /></RoleGate> },
      { path: '/audit', element: <RoleGate allowedRoles={['SUPER_ADMIN', 'PROJECT_ADMIN']}><DummyView title="Immutable System Audit Trail" subtitle="Write-once audit log inspector with correlation ID tracking" icon="🛡️" /></RoleGate> },
    ],
  },
]);
