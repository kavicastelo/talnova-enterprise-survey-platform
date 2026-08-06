import React, { useState, useEffect } from 'react';
import { Branding, ContrastValidationResponse } from '../../types/projectConfig';

interface ThemeCustomizerProps {
  branding: Branding;
  onChange: (updatedBranding: Branding) => void;
}

export const ThemeCustomizer: React.FC<ThemeCustomizerProps> = ({ branding, onChange }) => {
  const [contrastResult, setContrastResult] = useState<ContrastValidationResponse>({
    passed: true,
    contrastRatio: 21.0,
    wcagLevel: 'AAA',
    recommendation: 'Color contrast meets WCAG 2.1 AA and AAA standards.'
  });

  const calculateLuminance = (hex: string): number => {
    const cleanHex = hex.startsWith('#') ? hex.slice(1) : hex;
    if (cleanHex.length !== 6) return 0;
    const r = parseInt(cleanHex.slice(0, 2), 16) / 255;
    const g = parseInt(cleanHex.slice(2, 4), 16) / 255;
    const b = parseInt(cleanHex.slice(4, 6), 16) / 255;

    const rL = r <= 0.04045 ? r / 12.92 : Math.pow((r + 0.055) / 1.055, 2.4);
    const gL = g <= 0.04045 ? g / 12.92 : Math.pow((g + 0.055) / 1.055, 2.4);
    const bL = b <= 0.04045 ? b / 12.92 : Math.pow((b + 0.055) / 1.055, 2.4);

    return 0.2126 * rL + 0.7152 * gL + 0.0722 * bL;
  };

  useEffect(() => {
    if (/^#([A-Fa-f0-9]{6})$/.test(branding.primaryColor)) {
      const l1 = calculateLuminance(branding.primaryColor);
      const l2 = 1.0; // White background luminance
      const max = Math.max(l1, l2);
      const min = Math.min(l1, l2);
      const ratio = Math.round(((max + 0.05) / (min + 0.05)) * 100) / 100;

      let level: 'AAA' | 'AA' | 'FAIL' = 'FAIL';
      let passed = false;
      let rec = '';

      if (ratio >= 7.0) {
        level = 'AAA';
        passed = true;
        rec = 'Complies with WCAG 2.1 AAA standards (7:1 ratio).';
      } else if (ratio >= 4.5) {
        level = 'AA';
        passed = true;
        rec = 'Complies with WCAG 2.1 AA standards for normal text (4.5:1 ratio).';
      } else {
        level = 'FAIL';
        passed = false;
        rec = `Ratio ${ratio}:1 fails WCAG 2.1 AA requirement (4.5:1). Darken text or lighten background.`;
      }

      setContrastResult({
        passed,
        contrastRatio: ratio,
        wcagLevel: level,
        recommendation: rec
      });
    }
  }, [branding.primaryColor]);

  const handleFieldChange = (field: keyof Branding, value: string) => {
    onChange({
      ...branding,
      [field]: value
    });
  };

  return (
    <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '24px', alignItems: 'start' }}>
      {/* Controls Form */}
      <div style={{ background: '#ffffff', padding: '24px', borderRadius: '12px', border: '1px solid #e2e8f0', boxShadow: '0 4px 6px -1px rgba(0, 0, 0, 0.1)' }}>
        <h3 style={{ fontSize: '1.25rem', fontWeight: 600, color: '#0f172a', marginBottom: '16px' }}>Theme & Branding Settings</h3>

        <div style={{ marginBottom: '16px' }}>
          <label style={{ display: 'block', fontSize: '0.875rem', fontWeight: 500, color: '#334155', marginBottom: '6px' }}>Company / Workspace Name</label>
          <input
            type="text"
            value={branding.companyName}
            onChange={(e) => handleFieldChange('companyName', e.target.value)}
            style={{ width: '100%', padding: '10px 14px', borderRadius: '8px', border: '1px solid #cbd5e1', fontSize: '0.95rem' }}
            placeholder="e.g. Aitken Spence Enterprise"
          />
        </div>

        <div style={{ marginBottom: '16px' }}>
          <label style={{ display: 'block', fontSize: '0.875rem', fontWeight: 500, color: '#334155', marginBottom: '6px' }}>Logo URL</label>
          <input
            type="text"
            value={branding.logoUrl || ''}
            onChange={(e) => handleFieldChange('logoUrl', e.target.value)}
            style={{ width: '100%', padding: '10px 14px', borderRadius: '8px', border: '1px solid #cbd5e1', fontSize: '0.95rem' }}
            placeholder="https://s3.amazonaws.com/assets/logo.png"
          />
        </div>

        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px', marginBottom: '16px' }}>
          <div>
            <label style={{ display: 'block', fontSize: '0.875rem', fontWeight: 500, color: '#334155', marginBottom: '6px' }}>Primary Brand Color</label>
            <div style={{ display: 'flex', gap: '8px', alignItems: 'center' }}>
              <input
                type="color"
                value={branding.primaryColor}
                onChange={(e) => handleFieldChange('primaryColor', e.target.value)}
                style={{ width: '42px', height: '42px', border: 'none', borderRadius: '6px', cursor: 'pointer' }}
              />
              <input
                type="text"
                value={branding.primaryColor}
                onChange={(e) => handleFieldChange('primaryColor', e.target.value)}
                style={{ width: '100%', padding: '8px 12px', borderRadius: '8px', border: '1px solid #cbd5e1', fontSize: '0.9rem', fontFamily: 'monospace' }}
              />
            </div>
          </div>

          <div>
            <label style={{ display: 'block', fontSize: '0.875rem', fontWeight: 500, color: '#334155', marginBottom: '6px' }}>Secondary Accent Color</label>
            <div style={{ display: 'flex', gap: '8px', alignItems: 'center' }}>
              <input
                type="color"
                value={branding.secondaryColor}
                onChange={(e) => handleFieldChange('secondaryColor', e.target.value)}
                style={{ width: '42px', height: '42px', border: 'none', borderRadius: '6px', cursor: 'pointer' }}
              />
              <input
                type="text"
                value={branding.secondaryColor}
                onChange={(e) => handleFieldChange('secondaryColor', e.target.value)}
                style={{ width: '100%', padding: '8px 12px', borderRadius: '8px', border: '1px solid #cbd5e1', fontSize: '0.9rem', fontFamily: 'monospace' }}
              />
            </div>
          </div>
        </div>

        {/* Accessibility Indicator Badge */}
        <div style={{
          padding: '12px 16px',
          borderRadius: '8px',
          background: contrastResult.passed ? '#f0fdf4' : '#fef2f2',
          border: `1px solid ${contrastResult.passed ? '#bbf7d0' : '#fecaca'}`,
          marginTop: '20px'
        }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '4px' }}>
            <span style={{ fontSize: '0.875rem', fontWeight: 600, color: contrastResult.passed ? '#166534' : '#991b1b' }}>
              WCAG 2.1 AA Contrast: {contrastResult.contrastRatio}:1
            </span>
            <span style={{
              padding: '2px 8px',
              borderRadius: '12px',
              fontSize: '0.75rem',
              fontWeight: 700,
              background: contrastResult.passed ? '#22c55e' : '#ef4444',
              color: '#ffffff'
            }}>
              {contrastResult.wcagLevel}
            </span>
          </div>
          <p style={{ fontSize: '0.8rem', color: contrastResult.passed ? '#15803d' : '#b91c1c', margin: 0 }}>
            {contrastResult.recommendation}
          </p>
        </div>
      </div>

      {/* Real-time Workspace Preview Card */}
      <div style={{
        background: '#ffffff',
        borderRadius: '12px',
        border: '1px solid #e2e8f0',
        overflow: 'hidden',
        boxShadow: '0 10px 15px -3px rgba(0, 0, 0, 0.1)'
      }}>
        {/* Workspace Top Header Bar */}
        <div style={{
          background: branding.primaryColor,
          padding: '16px 20px',
          color: '#ffffff',
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          transition: 'background 0.2s ease'
        }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
            {branding.logoUrl ? (
              <img src={branding.logoUrl} alt="Logo" style={{ height: '28px', borderRadius: '4px' }} />
            ) : (
              <div style={{ width: '28px', height: '28px', borderRadius: '6px', background: 'rgba(255, 255, 255, 0.3)', display: 'grid', placeItems: 'center', fontWeight: 700 }}>
                {branding.companyName.substring(0, 1) || 'T'}
              </div>
            )}
            <span style={{ fontWeight: 600, fontSize: '1.05rem' }}>{branding.companyName || 'Workspace Preview'}</span>
          </div>
          <span style={{ fontSize: '0.75rem', background: 'rgba(255, 255, 255, 0.2)', padding: '4px 10px', borderRadius: '12px' }}>LIVE PREVIEW</span>
        </div>

        {/* Workspace Preview Content */}
        <div style={{ padding: '24px' }}>
          <h4 style={{ fontSize: '1.1rem', fontWeight: 600, color: '#1e293b', marginBottom: '8px' }}>Enterprise Survey Portal</h4>
          <p style={{ fontSize: '0.9rem', color: '#64748b', marginBottom: '20px' }}>
            Welcome to the white-label employee experience survey workspace.
          </p>

          <div style={{ display: 'flex', gap: '12px', marginBottom: '24px' }}>
            <button style={{
              background: branding.primaryColor,
              color: '#ffffff',
              border: 'none',
              padding: '10px 18px',
              borderRadius: '8px',
              fontWeight: 600,
              fontSize: '0.9rem',
              cursor: 'pointer',
              transition: 'background 0.2s ease'
            }}>
              Primary Action
            </button>
            <button style={{
              background: branding.secondaryColor,
              color: '#ffffff',
              border: 'none',
              padding: '10px 18px',
              borderRadius: '8px',
              fontWeight: 600,
              fontSize: '0.9rem',
              cursor: 'pointer',
              transition: 'background 0.2s ease'
            }}>
              Secondary Accent
            </button>
          </div>

          <div style={{ padding: '16px', background: '#f8fafc', borderRadius: '8px', borderLeft: `4px solid ${branding.primaryColor}` }}>
            <span style={{ fontSize: '0.85rem', fontWeight: 600, color: '#334155' }}>Branding Status</span>
            <p style={{ fontSize: '0.8rem', color: '#64748b', margin: '4px 0 0 0' }}>
              Custom theme active. All survey distribution channels and web forms will render using these color variables.
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};
