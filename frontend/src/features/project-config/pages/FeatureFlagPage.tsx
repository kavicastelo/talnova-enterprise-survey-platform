import React from 'react';
import { PageHeader } from '../../../components/ui/PageHeader';
import { FeatureFlagMatrix } from '../../../components/project-config/FeatureFlagMatrix';
import { useTenant } from '../../../context/TenantContext';
import { useProjectConfigQuery } from '../api/useProjectConfigQueries';
import { Skeleton } from '../../../components/ui/Skeleton';
import { ErrorState } from '../../../components/ui/ErrorState';

export const FeatureFlagPage: React.FC = () => {
  const { activeProject, updateFeatureFlags } = useTenant();
  const { data: projectConfig, isLoading, isError, refetch } = useProjectConfigQuery(activeProject?.projectId);

  if (isLoading) {
    return (
      <div>
        <PageHeader title="Feature Flag Matrix" subtitle="Loading tenant feature settings..." />
        <Skeleton height="280px" borderRadius="12px" />
      </div>
    );
  }

  if (isError) {
    return (
      <div>
        <PageHeader title="Feature Flag Matrix" />
        <ErrorState
          title="Failed to Load Feature Flags"
          message="Could not retrieve feature activation flags from project-config-service."
          onRetry={refetch}
        />
      </div>
    );
  }

  const currentFeatures = projectConfig?.features || activeProject?.features || {
    aiAnalyticsEnabled: true,
    actionPlanningEnabled: true,
    kioskModeEnabled: false,
    smsDistributionEnabled: true,
  };

  return (
    <div>
      <PageHeader
        title="Feature Flag Matrix"
        subtitle={`Toggle feature availability dynamically for project tenant ${activeProject?.projectId}`}
      />
      <FeatureFlagMatrix
        projectId={activeProject?.projectId || 'PRJ-99201'}
        initialFeatures={currentFeatures}
        onUpdate={(updated) => updateFeatureFlags(updated)}
      />
    </div>
  );
};
