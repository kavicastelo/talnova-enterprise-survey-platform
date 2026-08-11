import React from 'react';
import { PageHeader } from '../../../components/ui/PageHeader';
import { SurveyBuilderCanvas } from '../../../components/survey-builder/SurveyBuilderCanvas';
import { useTenant } from '../../../context/TenantContext';

export const SurveyBuilderPage: React.FC = () => {
  const { activeProject } = useTenant();
  const searchParams = new URLSearchParams(typeof window !== 'undefined' ? window.location.search : '');
  const surveyId = searchParams.get('surveyId') || 'SRV-5001';

  return (
    <div>
      <PageHeader
        title="Survey Questionnaire Builder Studio"
        subtitle="Construct multi-page surveys, configure branching logic rules, and manage engagement dimensions."
      />
      <SurveyBuilderCanvas projectId={activeProject?.projectId || ''} surveyId={surveyId} />
    </div>
  );
};
