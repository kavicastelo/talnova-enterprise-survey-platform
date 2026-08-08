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
  onChange?: (updated: Branding) => void;
}

export const ThemeCustomizer: React.FC<Props> = ({ branding, onChange }) => {
  const [primaryColor, setPrimaryColor] = useState(branding.primaryColor || '#1E3A8A');
  const [secondaryColor, setSecondaryColor] = useState(branding.secondaryColor || '#3B82F6');
  const [companyName, setCompanyName] = useState(branding.companyName || 'Aitken Spence PLC');
  const [logoUrl, setLogoUrl] = useState(branding.logoUrl || '');
  const [validationResult, setValidationResult] = useState<ContrastValidationResponse | null>(null);

  const validateMutation = useValidateThemeMutation();

  const handleColorChange = (newPrimary: string, newSecondary: string) => {
    setPrimaryColor(newPrimary);
    setSecondaryColor(newSecondary);
    if (onChange) {
      onChange({ companyName, logoUrl, primaryColor: newPrimary, secondaryColor: newSecondary });
    }
  };

  const handleValidateAccessibility = () => {
    validateMutation.mutate(
      { primaryColor, backgroundColor: '#FFFFFF' },
      {
        onSuccess: (data) => {
          setValidationResult(data);
        },
      }
    );
  };

  return (
    <Card variant="bordered" padding="24px">
      <div style={{ marginBottom: '20px' }}>
        <h3 style={{ fontSize: '1.2rem', fontWeight: 800, color: '#0f172a', margin: 0 }}>
          White-Label Brand & Accessibility Studio
        </h3>
        <p style={{ fontSize: '0.85rem', color: '#64748b', margin: '4px 0 0 0' }}>
          Customize company primary/secondary theme colors and calculate WCAG 2.1 contrast ratio against white background.
        </p>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))', gap: '24px' }}>
        {/* Controls Column */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
          <Input
            label="Company Brand Name"
            value={companyName}
            onChange={(e) => {
              setCompanyName(e.target.value);
              if (onChange) onChange({ companyName: e.target.value, logoUrl, primaryColor, secondaryColor });
            }}
          />

          <Input
            label="Brand Logo URL (HTTPS)"
            value={logoUrl}
            onChange={(e) => {
              setLogoUrl(e.target.value);
              if (onChange) onChange({ companyName, logoUrl: e.target.value, primaryColor, secondaryColor });
            }}
            placeholder="https://s3.amazonaws.com/tesp-assets/prj-99201/logo.png"
          />

          <div style={{ display: 'flex', gap: '16px' }}>
            <div style={{ flex: 1 }}>
              <Input
                label="Primary Color"
                type="color"
                value={primaryColor}
                onChange={(e) => handleColorChange(e.target.value, secondaryColor)}
              />
            </div>
            <div style={{ flex: 1 }}>
              <Input
                label="Secondary Color"
                type="color"
                value={secondaryColor}
                onChange={(e) => handleColorChange(primaryColor, e.target.value)}
              />
            </div>
          </div>

          <Button variant="outline" onClick={handleValidateAccessibility} isLoading={validateMutation.isPending}>
            🔍 Run WCAG Accessibility Audit
          </Button>
        </div>

        {/* Live Preview & Accessibility Card */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
          <div
            style={{
              padding: '24px',
              borderRadius: '12px',
              background: '#f8fafc',
              border: '1px solid #e2e8f0',
              display: 'flex',
              flexDirection: 'column',
              gap: '16px',
            }}
          >
            <h4 style={{ fontSize: '0.85rem', fontWeight: 700, color: '#475569', textTransform: 'uppercase', margin: 0 }}>
              LIVE BRAND PREVIEW
            </h4>

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
              <span>{companyName || 'Company Name'}</span>
              <div
                style={{
                  padding: '6px 12px',
                  borderRadius: '6px',
                  background: secondaryColor,
                  color: '#ffffff',
                  fontSize: '0.8rem',
                }}
              >
                Secondary Action
              </div>
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
      </div>
    </Card>
  );
};
