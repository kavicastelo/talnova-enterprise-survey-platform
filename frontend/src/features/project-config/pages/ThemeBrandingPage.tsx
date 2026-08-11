import React from 'react';
import { PageHeader } from '../../../components/ui/PageHeader';
import { ThemeCustomizer } from '../../../components/project-config/ThemeCustomizer';
import { useTenant } from '../../../context/TenantContext';
import { useProjectConfigQuery, useUpdateProjectMutation } from '../api/useProjectConfigQueries';
import { Skeleton } from '../../../components/ui/Skeleton';
import { ErrorState } from '../../../components/ui/ErrorState';
import { Branding } from '../../../types/projectConfig';

export const ThemeBrandingPage: React.FC = () => {
  const { activeProject, updateBranding } = useTenant();
  const { data: projectConfig, isLoading, isError, refetch } = useProjectConfigQuery(activeProject?.projectId);
  const updateProjectMutation = useUpdateProjectMutation();

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

  const handleSaveBranding = (updatedBranding: Branding) => {
    if (!activeProject?.projectId) return;

    const updatedPayload = {
      projectId: activeProject.projectId,
      name: projectConfig?.name || activeProject.name || activeProject.projectName,
      branding: updatedBranding,
      supportedLocales: projectConfig?.supportedLocales || activeProject.supportedLocales || ['en-US'],
      defaultLocale: projectConfig?.defaultLocale || activeProject.defaultLocale || 'en-US',
      features: projectConfig?.features || activeProject.features,
      customAttributeDefinitions: projectConfig?.customAttributeDefinitions,
    };

    updateProjectMutation.mutate(
      { projectId: activeProject.projectId, payload: updatedPayload },
      {
        onSuccess: () => {
          updateBranding(updatedBranding);
        },
      }
    );
  };

  return (
    <div>
      <PageHeader
        title="White-Label Brand & Accessibility Studio"
        subtitle={`Configure branding colors and WCAG 2.1 AA accessibility for project tenant ${activeProject?.projectId}`}
      />
      <ThemeCustomizer
        branding={currentBranding}
        onSave={handleSaveBranding}
        isSaving={updateProjectMutation.isPending}
      />
    </div>
  );
};
