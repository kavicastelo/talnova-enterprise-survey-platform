import React from 'react';
import { PageHeader } from '../../../components/ui/PageHeader';
import { SurveyBuilderCanvas } from '../../../components/survey-builder/SurveyBuilderCanvas';
import { useTenant } from '../../../context/TenantContext';

export const SurveyBuilderPage: React.FC = () => {
  const { activeProject } = useTenant();

  return (
    <div>
      <PageHeader
        title="Metadata-Driven Survey Builder Studio"
        subtitle={`Design questionnaire Abstract Syntax Trees (AST), logic branching rules, and AI bias detection for ${activeProject?.projectId}`}
      />
      <SurveyBuilderCanvas projectId={activeProject?.projectId || 'PRJ-99201'} surveyId="SUR-88102" />
    </div>
  );
};
