import React, { useState } from 'react';
import { ProjectCreateRequest, ProjectResponse } from '../../types/projectConfig';
import { useCreateProjectMutation } from '../../features/project-config/api/useProjectConfigQueries';
import { Card } from '../ui/Card';
import { Input } from '../ui/Input';
import { Select } from '../ui/Select';
import { Button } from '../ui/Button';
import { Alert } from '../ui/Alert';

interface Props {
  onSuccess?: (proj: ProjectResponse) => void;
  onCancel?: () => void;
}

export const ProjectSetupWizard: React.FC<Props> = ({ onSuccess, onCancel }) => {
  const createMutation = useCreateProjectMutation();

  const [formData, setFormData] = useState<ProjectCreateRequest>({
    projectId: 'PRJ-99201',
    name: 'Aitken Spence Enterprise Workspace',
    branding: {
      companyName: 'Aitken Spence PLC',
      logoUrl: 'https://s3.amazonaws.com/tesp-assets/prj-99201/logo.png',
      primaryColor: '#1E3A8A',
      secondaryColor: '#3B82F6',
      customCssUrl: '',
    },
    supportedLocales: ['en-US', 'si-LK', 'ta-LK'],
    defaultLocale: 'en-US',
    features: {
      aiAnalyticsEnabled: true,
      actionPlanningEnabled: true,
      kioskModeEnabled: false,
      smsDistributionEnabled: true,
    },
    customAttributeDefinitions: [
      { key: 'TenureYears', displayName: 'Tenure (Years)', dataType: 'NUMERIC' },
      { key: 'Department', displayName: 'Department', dataType: 'STRING' },
    ],
  });

  const [validationError, setValidationError] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setValidationError(null);

    if (!formData.projectId || !/^PRJ-[A-Z0-9]{4,10}$/.test(formData.projectId)) {
      setValidationError('Project ID must match pattern ^PRJ-[A-Z0-9]{4,10}$ (e.g. PRJ-99201)');
      return;
    }

    if (!formData.name.trim()) {
      setValidationError('Project Workspace Name is required.');
      return;
    }

    if (!formData.branding.companyName?.trim()) {
      setValidationError('Company Name is required.');
      return;
    }

    createMutation.mutate(formData, {
      onSuccess: (data) => {
        if (onSuccess) onSuccess(data);
      },
    });
  };

  return (
    <Card variant="bordered" padding="28px">
      <div style={{ marginBottom: '24px' }}>
        <h3 style={{ fontSize: '1.25rem', fontWeight: 800, color: '#0f172a', margin: 0 }}>
          Provision New Tenant Project Workspace
        </h3>
        <p style={{ fontSize: '0.85rem', color: '#64748b', margin: '4px 0 0 0' }}>
          Configure multi-tenant isolated database bounds, white-label branding, supported locales, and module feature flags.
        </p>
      </div>

      {validationError && (
        <Alert type="error" title="Validation Error" style={{ marginBottom: '20px' }}>
          {validationError}
        </Alert>
      )}

      <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
        {/* Workspace Identity */}
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(240px, 1fr))', gap: '16px' }}>
          <Input
            label="Project Tenant ID (PRJ-XXXXX)"
            value={formData.projectId}
            onChange={(e) => setFormData({ ...formData, projectId: e.target.value })}
            placeholder="PRJ-99201"
            helperText="Must match pattern ^PRJ-[A-Z0-9]{4,10}$"
            required
          />

          <Input
            label="Project Workspace Name"
            value={formData.name}
            onChange={(e) => setFormData({ ...formData, name: e.target.value })}
            placeholder="Aitken Spence Enterprise Workspace"
            required
          />
        </div>

        {/* Branding Configuration */}
        <div style={{ background: '#f8fafc', padding: '16px', borderRadius: '10px', border: '1px solid #e2e8f0' }}>
          <h4 style={{ fontSize: '0.9rem', fontWeight: 700, color: '#0f172a', margin: '0 0 12px 0' }}>
            🎨 White-Label Branding Setup
          </h4>
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))', gap: '12px' }}>
            <Input
              label="Company Name"
              value={formData.branding.companyName}
              onChange={(e) =>
                setFormData({
                  ...formData,
                  branding: { ...formData.branding, companyName: e.target.value },
                })
              }
              required
            />
            <Input
              label="Primary Theme Color (HEX)"
              type="color"
              value={formData.branding.primaryColor}
              onChange={(e) =>
                setFormData({
                  ...formData,
                  branding: { ...formData.branding, primaryColor: e.target.value },
                })
              }
            />
            <Input
              label="Secondary Theme Color (HEX)"
              type="color"
              value={formData.branding.secondaryColor}
              onChange={(e) =>
                setFormData({
                  ...formData,
                  branding: { ...formData.branding, secondaryColor: e.target.value },
                })
              }
            />
          </div>
        </div>

        {/* Locales & Defaults */}
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))', gap: '16px' }}>
          <Select
            label="Default Primary Language"
            value={formData.defaultLocale}
            onChange={(e) => setFormData({ ...formData, defaultLocale: e.target.value })}
            options={[
              { value: 'en-US', label: 'English (United States) — en-US' },
              { value: 'si-LK', label: 'Sinhala (Sri Lanka) — si-LK' },
              { value: 'ta-LK', label: 'Tamil (Sri Lanka) — ta-LK' },
            ]}
          />
        </div>

        {/* Action Controls */}
        <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '12px', marginTop: '12px' }}>
          {onCancel && (
            <Button type="button" variant="secondary" onClick={onCancel}>
              Cancel
            </Button>
          )}
          <Button type="submit" variant="primary" isLoading={createMutation.isPending}>
            Provision Project Workspace
          </Button>
        </div>
      </form>
    </Card>
  );
};
