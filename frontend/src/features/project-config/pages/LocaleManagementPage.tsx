import React from 'react';
import { PageHeader } from '../../../components/ui/PageHeader';
import { LocaleManagementPanel } from '../../../components/project-config/LocaleManagementPanel';
import { useTenant } from '../../../context/TenantContext';
import { useProjectConfigQuery, useUpdateProjectMutation } from '../api/useProjectConfigQueries';
import { Skeleton } from '../../../components/ui/Skeleton';
import { ErrorState } from '../../../components/ui/ErrorState';
import { EmptyState } from '../../../components/ui/EmptyState';
import { Button } from '../../../components/ui/Button';

export const LocaleManagementPage: React.FC = () => {
  const { activeProject, projectsList, switchProject } = useTenant();
  const { data: projectConfig, isLoading, isError, refetch } = useProjectConfigQuery(activeProject?.projectId);
  const updateProjectMutation = useUpdateProjectMutation();

  if (!activeProject?.projectId) {
    return (
      <div>
        <PageHeader title="Locales & Multilingual Management" subtitle="Configure supported survey languages and default system fallback" />
        <EmptyState
          title="No Active Project Workspace Selected"
          description="Select an active enterprise project workspace tenant to manage supported language packs and default system fallback locale."
          action={
            projectsList.length > 0 ? (
              <div style={{ display: 'flex', gap: '8px', flexWrap: 'wrap', justifyContent: 'center' }}>
                {projectsList.slice(0, 3).map((p) => (
                  <Button key={p.projectId} variant="outline" size="sm" onClick={() => switchProject(p.projectId)}>
                    Select {p.branding?.companyName || p.projectId}
                  </Button>
                ))}
              </div>
            ) : undefined
          }
        />
      </div>
    );
  }

  if (isLoading) {
    return (
      <div>
        <PageHeader title="Locales & Multilingual Management" subtitle="Loading locale configuration..." />
        <Skeleton height="300px" borderRadius="12px" />
      </div>
    );
  }

  if (isError) {
    return (
      <div>
        <PageHeader title="Locales & Multilingual Management" />
        <ErrorState
          title="Failed to Load Locales"
          message="Could not retrieve locale configurations from project-config-service."
          onRetry={refetch}
        />
      </div>
    );
  }

  const supportedLocales = projectConfig?.supportedLocales || activeProject?.supportedLocales || ['en-US', 'si-LK', 'ta-LK'];
  const defaultLocale = projectConfig?.defaultLocale || activeProject?.defaultLocale || 'en-US';

  const handleSaveLocales = (newSupportedLocales: string[], newDefaultLocale: string) => {
    if (!activeProject?.projectId) return;

    const payload = {
      projectId: activeProject.projectId,
      name: projectConfig?.name || activeProject.name || activeProject.projectName,
      branding: projectConfig?.branding || activeProject.branding,
      supportedLocales: newSupportedLocales,
      defaultLocale: newDefaultLocale,
      features: projectConfig?.features || activeProject.features,
      customAttributeDefinitions: projectConfig?.customAttributeDefinitions,
    };

    updateProjectMutation.mutate({
      projectId: activeProject.projectId,
      payload,
    });
  };

  return (
    <div>
      <PageHeader
        title="Locales & Multilingual Management"
        subtitle={`Configure supported languages and primary survey translation pack for project ${activeProject?.projectId}`}
      />
      <LocaleManagementPanel
        key={`${supportedLocales.join(',')}-${defaultLocale}`}
        supportedLocales={supportedLocales}
        defaultLocale={defaultLocale}
        onSave={handleSaveLocales}
        isSaving={updateProjectMutation.isPending}
      />
    </div>
  );
};
