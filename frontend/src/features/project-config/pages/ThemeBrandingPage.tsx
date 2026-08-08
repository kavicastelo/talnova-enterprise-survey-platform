import React from 'react';
import { PageHeader } from '../../../components/ui/PageHeader';
import { ThemeCustomizer } from '../../../components/project-config/ThemeCustomizer';
import { useTenant } from '../../../context/TenantContext';
import { useProjectConfigQuery } from '../api/useProjectConfigQueries';
import { Skeleton } from '../../../components/ui/Skeleton';
import { ErrorState } from '../../../components/ui/ErrorState';

export const ThemeBrandingPage: React.FC = () => {
  const { activeProject, updateBranding } = useTenant();
  const { data: projectConfig, isLoading, isError, refetch } = useProjectConfigQuery(activeProject?.projectId);

  if (isLoading) {
    return (
      <div>
        <PageHeader title="White-Label Brand & Accessibility Studio" subtitle="Loading theme settings..." />
        <Skeleton height="300px" borderRadius="12px" />
      </div>
    );
  }

  if (isError) {
    return (
      <div>
        <PageHeader title="White-Label Brand & Accessibility Studio" />
        <ErrorState
          title="Failed to Load Project Theme Configuration"
          message="Could not retrieve branding settings from project-config-service via API Gateway."
          onRetry={refetch}
        />
      </div>
    );
  }

  const currentBranding = projectConfig?.branding || activeProject?.branding || {
    companyName: 'Aitken Spence PLC',
    primaryColor: '#1E3A8A',
    secondaryColor: '#3B82F6',
  };

  return (
    <div>
      <PageHeader
        title="White-Label Brand & Accessibility Studio"
        subtitle={`Configure branding colors and WCAG 2.1 AA accessibility for project tenant ${activeProject?.projectId}`}
      />
      <ThemeCustomizer branding={currentBranding} onChange={(updated) => updateBranding(updated)} />
    </div>
  );
};
