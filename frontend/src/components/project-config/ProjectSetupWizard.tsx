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

const AVAILABLE_LOCALES = [
  { value: 'en-US', label: 'English (United States) — en-US' },
  { value: 'si-LK', label: 'Sinhala (Sri Lanka) — si-LK' },
  { value: 'ta-LK', label: 'Tamil (Sri Lanka) — ta-LK' },
  { value: 'es-ES', label: 'Spanish (Spain) — es-ES' },
  { value: 'fr-FR', label: 'French (France) — fr-FR' },
];

export const ProjectSetupWizard: React.FC<Props> = ({ onSuccess, onCancel }) => {
  const createMutation = useCreateProjectMutation();

  const [formData, setFormData] = useState<ProjectCreateRequest>({
    projectId: '',
    name: '',
    branding: {
      companyName: '',
      logoUrl: '',
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
      emailDistributionEnabled: true,
      teamsDistributionEnabled: false,
      slackDistributionEnabled: false,
      hrisSyncEnabled: true,
      gdprAnonymizationEnabled: true,
    },
    customAttributeDefinitions: [
      { key: 'TenureYears', displayName: 'Tenure (Years)', dataType: 'NUMERIC' },
      { key: 'Department', displayName: 'Department', dataType: 'STRING' },
    ],
  });

  const [validationError, setValidationError] = useState<string | null>(null);

  const toggleLocale = (localeCode: string) => {
    let updatedLocales = [...formData.supportedLocales];
    if (updatedLocales.includes(localeCode)) {
      if (updatedLocales.length === 1) {
        setValidationError('At least one supported locale is required (VR-CFG-004).');
        return;
      }
      updatedLocales = updatedLocales.filter((l) => l !== localeCode);
    } else {
      updatedLocales.push(localeCode);
    }

    // Ensure defaultLocale remains inside supportedLocales (BR-CFG-002)
    let newDefaultLocale = formData.defaultLocale;
    if (!updatedLocales.includes(newDefaultLocale)) {
      newDefaultLocale = updatedLocales[0] || 'en-US';
    }

    setFormData({
      ...formData,
      supportedLocales: updatedLocales,
      defaultLocale: newDefaultLocale,
    });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setValidationError(null);

    // VR-CFG-001: Project ID Regex
    if (!formData.projectId || !/^PRJ-[A-Z0-9]{4,10}$/.test(formData.projectId)) {
      setValidationError('Project ID must match pattern ^PRJ-[A-Z0-9]{4,10}$ (e.g. PRJ-99201).');
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

    // VR-CFG-002: HEX Color Format
    const hexRegex = /^#([A-Fa-f0-9]{6})$/;
    if (!hexRegex.test(formData.branding.primaryColor)) {
      setValidationError('Primary Color must be a valid 6-character HEX color string matching ^#([A-Fa-f0-9]{6})$ (VR-CFG-002).');
      return;
    }
    if (!hexRegex.test(formData.branding.secondaryColor)) {
      setValidationError('Secondary Color must be a valid 6-character HEX color string matching ^#([A-Fa-f0-9]{6})$ (VR-CFG-002).');
      return;
    }

    // VR-CFG-004: Supported Locales Array
    if (!formData.supportedLocales || formData.supportedLocales.length === 0) {
      setValidationError('At least one supported locale is required (VR-CFG-004).');
      return;
    }

    // BR-CFG-002: Mandatory Default Locale inside Supported Locales
    if (!formData.supportedLocales.includes(formData.defaultLocale)) {
      setValidationError('Default locale MUST be present in supported locales array (BR-CFG-002).');
      return;
    }

    createMutation.mutate(formData, {
      onSuccess: (data) => {
        if (onSuccess) onSuccess(data);
      },
      onError: (err: any) => {
        setValidationError(err.message || 'Failed to provision project workspace.');
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
            onChange={(e) => setFormData({ ...formData, projectId: e.target.value.toUpperCase() })}
            placeholder="PRJ-99201"
            helperText="Must match pattern ^PRJ-[A-Z0-9]{4,10}$ (BR-CFG-001)"
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
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))', gap: '12px', marginBottom: '16px' }}>
            <Input
              label="Company Name"
              value={formData.branding.companyName}
              onChange={(e) =>
                setFormData({
                  ...formData,
                  branding: { ...formData.branding, companyName: e.target.value },
                })
              }
              placeholder="e.g. Aitken Spence PLC"
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
            <Input
              label="Logo URL (HTTPS)"
              value={formData.branding.logoUrl}
              onChange={(e) =>
                setFormData({
                  ...formData,
                  branding: { ...formData.branding, logoUrl: e.target.value },
                })
              }
              placeholder="https://s3.amazonaws.com/tesp-assets/logo.png"
            />
          </div>

          {/* Live Branding Preview Swatch */}
          <div style={{ background: '#ffffff', padding: '14px', borderRadius: '8px', border: '1px solid #cbd5e1' }}>
            <div style={{ fontSize: '0.75rem', fontWeight: 700, color: '#475569', marginBottom: '8px', textTransform: 'uppercase', letterSpacing: '0.05em' }}>
              Live Theme Preview
            </div>
            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', padding: '12px 16px', borderRadius: '6px', background: formData.branding.primaryColor || '#1E3A8A', color: '#ffffff' }}>
              <div style={{ fontWeight: 700, fontSize: '0.9rem' }}>
                {formData.branding.companyName || 'Enterprise Workspace Name'}
              </div>
              <button
                type="button"
                style={{
                  background: formData.branding.secondaryColor || '#3B82F6',
                  color: '#ffffff',
                  border: 'none',
                  padding: '6px 14px',
                  borderRadius: '4px',
                  fontSize: '0.75rem',
                  fontWeight: 600,
                }}
              >
                Sample Action
              </button>
            </div>
          </div>
        </div>

        {/* Locales & Defaults */}
        <div style={{ background: '#ffffff', padding: '16px', borderRadius: '10px', border: '1px solid #e2e8f0' }}>
          <h4 style={{ fontSize: '0.9rem', fontWeight: 700, color: '#0f172a', margin: '0 0 12px 0' }}>
            🌐 Locales & Default Language
          </h4>
          <div style={{ marginBottom: '12px' }}>
            <label style={{ fontSize: '0.85rem', fontWeight: 600, color: '#334155', display: 'block', marginBottom: '6px' }}>
              Supported Languages (VR-CFG-004)
            </label>
            <div style={{ display: 'flex', flexWrap: 'wrap', gap: '8px' }}>
              {AVAILABLE_LOCALES.map((loc) => {
                const isSelected = formData.supportedLocales.includes(loc.value);
                return (
                  <button
                    key={loc.value}
                    type="button"
                    onClick={() => toggleLocale(loc.value)}
                    style={{
                      padding: '6px 12px',
                      borderRadius: '6px',
                      fontSize: '0.8rem',
                      fontWeight: 600,
                      cursor: 'pointer',
                      border: isSelected ? '1px solid #2563eb' : '1px solid #cbd5e1',
                      background: isSelected ? '#eff6ff' : '#f8fafc',
                      color: isSelected ? '#1d4ed8' : '#64748b',
                    }}
                  >
                    {isSelected ? '✓ ' : '+ '}
                    {loc.label}
                  </button>
                );
              })}
            </div>
          </div>

          <Select
            label="Default Primary Language (BR-CFG-002)"
            value={formData.defaultLocale}
            onChange={(e) => setFormData({ ...formData, defaultLocale: e.target.value })}
            options={AVAILABLE_LOCALES.filter((loc) => formData.supportedLocales.includes(loc.value))}
          />
        </div>

        {/* Initial Feature Modules */}
        <div style={{ background: '#f8fafc', padding: '16px', borderRadius: '10px', border: '1px solid #e2e8f0' }}>
          <h4 style={{ fontSize: '0.9rem', fontWeight: 700, color: '#0f172a', margin: '0 0 12px 0' }}>
            ⚡ Feature Module Subscription Flags
          </h4>
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '12px' }}>
            {[
              ['aiAnalyticsEnabled', 'AI Analytics Engine'],
              ['actionPlanningEnabled', 'Action Planning Suite'],
              ['kioskModeEnabled', 'Kiosk Response Mode'],
              ['smsDistributionEnabled', 'SMS Survey Distribution'],
              ['emailDistributionEnabled', 'Email Survey Distribution'],
              ['hrisSyncEnabled', 'HRIS Roster Sync'],
              ['gdprAnonymizationEnabled', 'GDPR Anonymization'],
            ].map(([key, label]) => {
              const checked = Boolean((formData.features as any)?.[key]);
              return (
                <label key={key} style={{ display: 'flex', alignItems: 'center', gap: '8px', cursor: 'pointer', fontSize: '0.85rem' }}>
                  <input
                    type="checkbox"
                    checked={checked}
                    onChange={(e) => {
                      const updatedFeatures = {
                        aiAnalyticsEnabled: formData.features?.aiAnalyticsEnabled ?? true,
                        actionPlanningEnabled: formData.features?.actionPlanningEnabled ?? true,
                        kioskModeEnabled: formData.features?.kioskModeEnabled ?? false,
                        ...formData.features,
                        [key]: e.target.checked,
                      };
                      setFormData({
                        ...formData,
                        features: updatedFeatures,
                      });
                    }}
                  />
                  <span>{label}</span>
                </label>
              );
            })}
          </div>
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
