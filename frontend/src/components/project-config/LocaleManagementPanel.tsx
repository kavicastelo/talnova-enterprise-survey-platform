import React, { useState } from 'react';
import { Button } from '../ui/Button';

interface LocaleManagementPanelProps {
  supportedLocales: string[];
  defaultLocale: string;
  onSave: (supportedLocales: string[], defaultLocale: string) => void;
  isSaving?: boolean;
}

export const LocaleManagementPanel: React.FC<LocaleManagementPanelProps> = ({
  supportedLocales: initialSupportedLocales,
  defaultLocale: initialDefaultLocale,
  onSave,
  isSaving,
}) => {
  const [supportedLocales, setSupportedLocales] = useState<string[]>(initialSupportedLocales);
  const [defaultLocale, setDefaultLocale] = useState<string>(initialDefaultLocale);
  const [newLocaleTag, setNewLocaleTag] = useState<string>('');
  const [validationError, setValidationError] = useState<string | null>(null);

  const availableSuggestions = [
    { tag: 'en-US', name: 'English (United States)' },
    { tag: 'si-LK', name: 'Sinhala (Sri Lanka)' },
    { tag: 'ta-LK', name: 'Tamil (Sri Lanka)' },
    { tag: 'fr-FR', name: 'French (France)' },
    { tag: 'de-DE', name: 'German (Germany)' },
    { tag: 'ja-JP', name: 'Japanese (Japan)' },
    { tag: 'es-ES', name: 'Spanish (Spain)' },
  ];

  const handleAddLocale = (tagToAdd?: string) => {
    const tag = (tagToAdd || newLocaleTag).trim();
    if (!tag) return;
    if (supportedLocales.includes(tag)) {
      setValidationError(`Locale '${tag}' is already present in supported locales.`);
      return;
    }
    const updated = [...supportedLocales, tag];
    setSupportedLocales(updated);
    setNewLocaleTag('');
    setValidationError(null);
  };

  const handleRemoveLocale = (tagToRemove: string) => {
    if (supportedLocales.length <= 1) {
      setValidationError('At least one supported locale is required (VR-CFG-004).');
      return;
    }
    const updated = supportedLocales.filter((loc) => loc !== tagToRemove);

    let updatedDefault = defaultLocale;
    if (defaultLocale === tagToRemove) {
      updatedDefault = updated[0];
      setDefaultLocale(updatedDefault);
    }

    setSupportedLocales(updated);
    setValidationError(null);
  };

  const handleSetDefault = (tag: string) => {
    if (!supportedLocales.includes(tag)) {
      setValidationError(`Default locale '${tag}' must be present in supported locales (BR-CFG-002).`);
      return;
    }
    setDefaultLocale(tag);
    setValidationError(null);
  };

  const handleSave = (e: React.FormEvent) => {
    e.preventDefault();
    setValidationError(null);

    // VR-CFG-004: Supported Locales Guard
    if (!supportedLocales || supportedLocales.length === 0) {
      setValidationError('At least one supported locale is required (VR-CFG-004).');
      return;
    }

    // BR-CFG-002: Default Locale Guard
    if (!supportedLocales.includes(defaultLocale)) {
      setValidationError('Default locale MUST be present in supported locales array (BR-CFG-002).');
      return;
    }

    onSave(supportedLocales, defaultLocale);
  };

  return (
    <form onSubmit={handleSave} style={{ background: '#ffffff', padding: '28px', borderRadius: '12px', border: '1px solid #e2e8f0', boxShadow: '0 4px 6px -1px rgba(0, 0, 0, 0.05)' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '8px' }}>
        <h3 style={{ fontSize: '1.25rem', fontWeight: 700, color: '#0f172a', margin: 0 }}>Locale & Multilingual Management</h3>
        <Button type="submit" variant="primary" isLoading={isSaving}>
          Save Locale Settings
        </Button>
      </div>

      <p style={{ color: '#64748b', fontSize: '0.9rem', marginBottom: '24px' }}>
        Configure IETF BCP 47 language tags for survey translation and tenant localization.
      </p>

      {validationError && (
        <div style={{ padding: '12px 16px', borderRadius: '8px', background: '#fef2f2', border: '1px solid #fecaca', color: '#991b1b', marginBottom: '20px', fontSize: '0.85rem' }}>
          ⚠️ {validationError}
        </div>
      )}

      {/* Active Supported Locales Chips */}
      <div style={{ marginBottom: '24px' }}>
        <label style={{ display: 'block', fontSize: '0.875rem', fontWeight: 600, color: '#334155', marginBottom: '12px' }}>
          Active Supported Locales ({supportedLocales.length}) — VR-CFG-004
        </label>
        <div style={{ display: 'flex', flexWrap: 'wrap', gap: '10px' }}>
          {supportedLocales.map((locale) => {
            const isDefault = locale === defaultLocale;
            return (
              <div
                key={locale}
                style={{
                  display: 'flex',
                  alignItems: 'center',
                  gap: '8px',
                  padding: '8px 14px',
                  borderRadius: '20px',
                  background: isDefault ? '#eff6ff' : '#f8fafc',
                  border: `1.5px solid ${isDefault ? '#3b82f6' : '#cbd5e1'}`,
                  fontWeight: 600,
                  fontSize: '0.9rem',
                  color: isDefault ? '#1d4ed8' : '#334155',
                }}
              >
                <span>{locale}</span>
                {isDefault && (
                  <span style={{ fontSize: '0.7rem', background: '#2563eb', color: '#ffffff', padding: '1px 6px', borderRadius: '8px' }}>
                    DEFAULT
                  </span>
                )}
                <button
                  type="button"
                  onClick={() => handleRemoveLocale(locale)}
                  title="Remove locale"
                  style={{ background: 'none', border: 'none', color: '#94a3b8', cursor: 'pointer', fontWeight: 700, fontSize: '1rem', padding: '0 2px' }}
                >
                  ×
                </button>
              </div>
            );
          })}
        </div>
      </div>

      {/* Add New Locale Input */}
      <div style={{ marginBottom: '28px', padding: '16px', background: '#f8fafc', borderRadius: '8px', border: '1px solid #e2e8f0' }}>
        <label style={{ display: 'block', fontSize: '0.85rem', fontWeight: 600, color: '#334155', marginBottom: '8px' }}>
          Add New Language Tag (BCP 47)
        </label>
        <div style={{ display: 'flex', gap: '12px', marginBottom: '12px' }}>
          <input
            type="text"
            value={newLocaleTag}
            onChange={(e) => setNewLocaleTag(e.target.value)}
            placeholder="e.g. fr-FR, de-DE, ja-JP"
            style={{ flex: 1, padding: '10px 14px', borderRadius: '8px', border: '1px solid #cbd5e1', fontSize: '0.95rem', fontFamily: 'monospace' }}
          />
          <button
            type="button"
            onClick={() => handleAddLocale()}
            style={{ background: '#2563eb', color: '#ffffff', border: 'none', padding: '10px 20px', borderRadius: '8px', fontWeight: 600, cursor: 'pointer' }}
          >
            Add Locale
          </button>
        </div>

        {/* Quick Suggestion Chips */}
        <div style={{ display: 'flex', alignItems: 'center', gap: '8px', flexWrap: 'wrap' }}>
          <span style={{ fontSize: '0.75rem', color: '#64748b', fontWeight: 600 }}>Quick Add:</span>
          {availableSuggestions
            .filter((item) => !supportedLocales.includes(item.tag))
            .map((item) => (
              <button
                key={item.tag}
                type="button"
                onClick={() => handleAddLocale(item.tag)}
                style={{ background: '#ffffff', border: '1px solid #cbd5e1', padding: '3px 10px', borderRadius: '12px', fontSize: '0.75rem', color: '#334155', cursor: 'pointer' }}
              >
                + {item.tag} ({item.name})
              </button>
            ))}
        </div>
      </div>

      {/* Default Locale Selector */}
      <div>
        <label style={{ display: 'block', fontSize: '0.875rem', fontWeight: 600, color: '#334155', marginBottom: '12px' }}>
          Select Mandatory Default System Fallback Locale (BR-CFG-002)
        </label>
        <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
          {supportedLocales.map((locale) => (
            <label
              key={locale}
              style={{
                display: 'flex',
                alignItems: 'center',
                gap: '12px',
                padding: '12px 16px',
                borderRadius: '8px',
                border: `1px solid ${locale === defaultLocale ? '#bfdbfe' : '#e2e8f0'}`,
                background: locale === defaultLocale ? '#eff6ff' : '#ffffff',
                cursor: 'pointer',
              }}
            >
              <input
                type="radio"
                name="defaultLocaleRadio"
                checked={locale === defaultLocale}
                onChange={() => handleSetDefault(locale)}
                style={{ width: '18px', height: '18px' }}
              />
              <span style={{ fontWeight: 600, fontSize: '0.95rem', color: '#1e293b' }}>{locale}</span>
              {locale === defaultLocale && (
                <span style={{ fontSize: '0.8rem', color: '#2563eb', fontWeight: 500 }}>
                  (Primary language for unauthenticated surveys and system notifications)
                </span>
              )}
            </label>
          ))}
        </div>
      </div>
    </form>
  );
};
