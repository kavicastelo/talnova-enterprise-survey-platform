import React, { useState } from 'react';
import { Branding, ContrastValidationResponse } from '../../types/projectConfig';
import { useValidateThemeMutation } from '../../features/project-config/api/useProjectConfigQueries';
import { Card } from '../ui/Card';
import { Input } from '../ui/Input';
import { Button } from '../ui/Button';
import { Badge } from '../ui/Badge';
import { Alert } from '../ui/Alert';

interface Props {
  branding: Branding;
  onSave?: (updated: Branding) => void;
  isSaving?: boolean;
}

export const ThemeCustomizer: React.FC<Props> = ({ branding, onSave, isSaving }) => {
  const [primaryColor, setPrimaryColor] = useState(branding.primaryColor || '#1E3A8A');
  const [secondaryColor, setSecondaryColor] = useState(branding.secondaryColor || '#3B82F6');
  const [companyName, setCompanyName] = useState(branding.companyName || 'Aitken Spence PLC');
  const [logoUrl, setLogoUrl] = useState(branding.logoUrl || '');
  const [customCssUrl, setCustomCssUrl] = useState(branding.customCssUrl || '');
  const [validationResult, setValidationResult] = useState<ContrastValidationResponse | null>(null);
  const [validationError, setValidationError] = useState<string | null>(null);

  const validateMutation = useValidateThemeMutation();

  const handleValidateAccessibility = () => {
    setValidationError(null);
    const hexRegex = /^#([A-Fa-f0-9]{6})$/;
    if (!hexRegex.test(primaryColor)) {
      setValidationError('Primary color must be a valid 6-character HEX format (e.g. #1E3A8A).');
      return;
    }

    validateMutation.mutate(
      { primaryColor, backgroundColor: '#FFFFFF' },
      {
        onSuccess: (data) => {
          setValidationResult(data);
        },
      }
    );
  };

  const handleSave = (e: React.FormEvent) => {
    e.preventDefault();
    setValidationError(null);

    // VR-CFG-002: HEX Format check
    const hexRegex = /^#([A-Fa-f0-9]{6})$/;
    if (!hexRegex.test(primaryColor)) {
      setValidationError('Primary Color must be a valid HEX color string matching ^#([A-Fa-f0-9]{6})$ (VR-CFG-002).');
      return;
    }
    if (!hexRegex.test(secondaryColor)) {
      setValidationError('Secondary Color must be a valid HEX color string matching ^#([A-Fa-f0-9]{6})$ (VR-CFG-002).');
      return;
    }

    if (!companyName.trim()) {
      setValidationError('Company Brand Name is required.');
      return;
    }

    if (onSave) {
      onSave({
        companyName,
        logoUrl,
        primaryColor,
        secondaryColor,
        customCssUrl,
      });
    }
  };

  return (
    <Card variant="bordered" padding="24px">
      <div style={{ marginBottom: '20px' }}>
        <h3 style={{ fontSize: '1.2rem', fontWeight: 800, color: '#0f172a', margin: 0 }}>
          White-Label Brand & Accessibility Studio
        </h3>
        <p style={{ fontSize: '0.85rem', color: '#64748b', margin: '4px 0 0 0' }}>
          Customize company primary/secondary theme colors and calculate WCAG 2.1 contrast ratio against light enterprise background.
        </p>
      </div>

      {validationError && (
        <Alert type="error" title="Validation Error" style={{ marginBottom: '16px' }}>
          {validationError}
        </Alert>
      )}

      <form onSubmit={handleSave} style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(300px, 1fr))', gap: '24px' }}>
        {/* Controls Column */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
          <Input
            label="Company Brand Name"
            value={companyName}
            onChange={(e) => setCompanyName(e.target.value)}
            required
          />

          <Input
            label="Brand Logo URL (HTTPS VR-CFG-003)"
            value={logoUrl}
            onChange={(e) => setLogoUrl(e.target.value)}
            placeholder="https://s3.amazonaws.com/tesp-assets/prj-99201/logo.png"
            helperText="Must be a valid HTTPS image URL"
          />

          {/* Primary & Secondary Color Dual Inputs */}
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))', gap: '16px' }}>
            <div>
              <label style={{ fontSize: '0.85rem', fontWeight: 600, color: '#334155', display: 'block', marginBottom: '6px' }}>
                Primary Theme Color (HEX VR-CFG-002)
              </label>
              <div style={{ display: 'flex', gap: '8px', alignItems: 'center' }}>
                <input
                  type="color"
                  value={primaryColor}
                  onChange={(e) => setPrimaryColor(e.target.value)}
                  style={{ width: '42px', height: '38px', padding: '2px', border: '1px solid #cbd5e1', borderRadius: '6px', cursor: 'pointer' }}
                />
                <input
                  type="text"
                  value={primaryColor}
                  onChange={(e) => setPrimaryColor(e.target.value.toUpperCase())}
                  placeholder="#1E3A8A"
                  style={{ flex: 1, padding: '8px 12px', borderRadius: '6px', border: '1px solid #cbd5e1', fontSize: '0.85rem', fontWeight: 600, fontFamily: 'monospace' }}
                />
              </div>
            </div>

            <div>
              <label style={{ fontSize: '0.85rem', fontWeight: 600, color: '#334155', display: 'block', marginBottom: '6px' }}>
                Secondary Accent Color (HEX VR-CFG-002)
              </label>
              <div style={{ display: 'flex', gap: '8px', alignItems: 'center' }}>
                <input
                  type="color"
                  value={secondaryColor}
                  onChange={(e) => setSecondaryColor(e.target.value)}
                  style={{ width: '42px', height: '38px', padding: '2px', border: '1px solid #cbd5e1', borderRadius: '6px', cursor: 'pointer' }}
                />
                <input
                  type="text"
                  value={secondaryColor}
                  onChange={(e) => setSecondaryColor(e.target.value.toUpperCase())}
                  placeholder="#3B82F6"
                  style={{ flex: 1, padding: '8px 12px', borderRadius: '6px', border: '1px solid #cbd5e1', fontSize: '0.85rem', fontWeight: 600, fontFamily: 'monospace' }}
                />
              </div>
            </div>
          </div>

          {/* Enterprise Color Presets Quick Picker */}
          <div>
            <label style={{ fontSize: '0.8rem', fontWeight: 700, color: '#64748b', display: 'block', marginBottom: '8px', textTransform: 'uppercase', letterSpacing: '0.05em' }}>
              Curated Enterprise Color Presets
            </label>
            <div style={{ display: 'flex', gap: '8px', flexWrap: 'wrap' }}>
              {[
                { name: 'Navy & Blue', primary: '#1E3A8A', secondary: '#3B82F6' },
                { name: 'Slate & Indigo', primary: '#312E81', secondary: '#6366F1' },
                { name: 'Emerald & Teal', primary: '#064E3B', secondary: '#10B981' },
                { name: 'Royal & Amber', primary: '#78350F', secondary: '#F59E0B' },
              ].map((preset) => (
                <button
                  key={preset.name}
                  type="button"
                  onClick={() => {
                    setPrimaryColor(preset.primary);
                    setSecondaryColor(preset.secondary);
                  }}
                  style={{
                    display: 'flex',
                    alignItems: 'center',
                    gap: '6px',
                    padding: '4px 10px',
                    borderRadius: '6px',
                    border: '1px solid #cbd5e1',
                    background: '#ffffff',
                    fontSize: '0.75rem',
                    fontWeight: 600,
                    cursor: 'pointer',
                  }}
                >
                  <span style={{ width: '12px', height: '12px', borderRadius: '50%', background: preset.primary }} />
                  <span style={{ width: '12px', height: '12px', borderRadius: '50%', background: preset.secondary }} />
                  {preset.name}
                </button>
              ))}
            </div>
          </div>

          <Input
            label="Custom CSS Sandbox URL (Optional)"
            value={customCssUrl}
            onChange={(e) => setCustomCssUrl(e.target.value)}
            placeholder="https://cdn.enterprise.com/custom-theme.css"
          />

          <div style={{ display: 'flex', gap: '12px', marginTop: '8px', flexWrap: 'wrap' }}>
            <Button type="button" variant="outline" onClick={handleValidateAccessibility} isLoading={validateMutation.isPending}>
              🔍 Audit WCAG Contrast Ratio
            </Button>
            <Button type="submit" variant="primary" isLoading={isSaving}>
              Save Branding Settings
            </Button>
          </div>
        </div>

        {/* Live Preview Card */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
          <div
            style={{
              padding: '24px',
              borderRadius: '12px',
              background: '#ffffff',
              border: '1px solid #e2e8f0',
              boxShadow: '0 4px 6px -1px rgba(0,0,0,0.05)',
              display: 'flex',
              flexDirection: 'column',
              gap: '16px',
            }}
          >
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <h4 style={{ fontSize: '0.8rem', fontWeight: 700, color: '#64748b', textTransform: 'uppercase', margin: 0 }}>
                LIVE ENTERPRISE PREVIEW
              </h4>
              <Badge variant="indigo">White-Label</Badge>
            </div>

            {/* Header bar preview */}
            <div
              style={{
                padding: '16px',
                borderRadius: '8px',
                background: primaryColor,
                color: '#ffffff',
                fontWeight: 700,
                fontSize: '1.1rem',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'space-between',
              }}
            >
              <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                {logoUrl ? (
                  <img src={logoUrl} alt="Logo" style={{ height: '24px', objectFit: 'contain' }} onError={(e) => (e.currentTarget.style.display = 'none')} />
                ) : (
                  <div style={{ width: '24px', height: '24px', borderRadius: '4px', background: 'rgba(255,255,255,0.2)', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '0.75rem' }}>
                    🏢
                  </div>
                )}
                <span>{companyName || 'Company Name'}</span>
              </div>
              <div
                style={{
                  padding: '6px 12px',
                  borderRadius: '6px',
                  background: secondaryColor,
                  color: '#ffffff',
                  fontSize: '0.8rem',
                  fontWeight: 600,
                }}
              >
                Action Button
              </div>
            </div>

            {/* Sample Card */}
            <div style={{ background: '#f8fafc', padding: '16px', borderRadius: '8px', border: '1px solid #e2e8f0' }}>
              <div style={{ fontSize: '0.9rem', fontWeight: 700, color: '#0f172a', marginBottom: '4px' }}>
                Sample Employee Pulse Survey Header
              </div>
              <p style={{ fontSize: '0.8rem', color: '#64748b', margin: 0 }}>
                This is a live preview of how employee survey portals and executive PDF exports will render.
              </p>
            </div>
          </div>

          {validationResult && (
            <Alert
              type={validationResult.passed ? 'success' : 'error'}
              title={`WCAG Rating: ${validationResult.wcagLevel} (${validationResult.contrastRatio.toFixed(1)}:1)`}
            >
              {validationResult.recommendation}
              <div style={{ marginTop: '6px' }}>
                <Badge variant={validationResult.passed ? 'success' : 'danger'}>
                  {validationResult.passed ? '✓ Passed WCAG 2.1 AA' : '✖ Failed WCAG 2.1 AA'}
                </Badge>
              </div>
            </Alert>
          )}
        </div>
      </form>
    </Card>
  );
};
