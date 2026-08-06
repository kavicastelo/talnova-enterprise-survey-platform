import React, { useState } from 'react';
import { FeatureFlags } from '../../types/projectConfig';
import { projectConfigApi } from '../../api/projectConfigApi';

interface FeatureFlagMatrixProps {
  projectId: string;
  initialFeatures: FeatureFlags;
  onUpdate?: (updatedFeatures: FeatureFlags) => void;
}

export const FeatureFlagMatrix: React.FC<FeatureFlagMatrixProps> = ({ projectId, initialFeatures, onUpdate }) => {
  const [features, setFeatures] = useState<FeatureFlags>(initialFeatures);
  const [savingKey, setSavingKey] = useState<string | null>(null);
  const [statusMessage, setStatusMessage] = useState<string | null>(null);

  const featureMetadata: { key: keyof FeatureFlags; label: string; description: string; icon: string }[] = [
    {
      key: 'aiAnalyticsEnabled',
      label: 'AI Analytics Engine',
      description: 'Enables LLM sentiment extraction, theme clustering, and predictive key driver analysis.',
      icon: '🤖'
    },
    {
      key: 'actionPlanningEnabled',
      label: 'Action Planning Module',
      description: 'Enables manager action plan tracking, progress metrics, and automated reminders.',
      icon: '🎯'
    },
    {
      key: 'kioskModeEnabled',
      label: 'Kiosk Mode Distribution',
      description: 'Enables shared tablet kiosk mode for offline & front-line worker survey collection.',
      icon: '📱'
    },
    {
      key: 'smsDistributionEnabled',
      label: 'SMS Survey Distribution',
      description: 'Enables SMS link distribution via Twilio / local SMS gateways.',
      icon: '💬'
    }
  ];

  const handleToggle = async (key: keyof FeatureFlags) => {
    const updated = { ...features, [key]: !features[key] };
    setFeatures(updated);
    setSavingKey(key);
    setStatusMessage(null);

    try {
      const res = await projectConfigApi.updateFeatureFlags(projectId, updated);
      if (res.success) {
        setStatusMessage(`Successfully updated ${key} for project ${projectId}`);
        if (onUpdate) onUpdate(updated);
      } else {
        setStatusMessage(`Failed to update feature flag: ${res.message}`);
        setFeatures(features); // Rollback
      }
    } catch (err: any) {
      setStatusMessage(`Network error updating feature flags`);
      setFeatures(features); // Rollback
    } finally {
      setSavingKey(null);
    }
  };

  return (
    <div style={{ background: '#ffffff', padding: '28px', borderRadius: '12px', border: '1px solid #e2e8f0', boxShadow: '0 4px 6px -1px rgba(0, 0, 0, 0.05)' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px' }}>
        <div>
          <h3 style={{ fontSize: '1.25rem', fontWeight: 700, color: '#0f172a', margin: 0 }}>Feature Flag Matrix</h3>
          <p style={{ color: '#64748b', fontSize: '0.9rem', margin: '4px 0 0 0' }}>
            Manage enterprise module activations for project <strong>{projectId}</strong>.
          </p>
        </div>
        <span style={{ fontSize: '0.8rem', background: '#eff6ff', color: '#1d4ed8', padding: '6px 12px', borderRadius: '16px', fontWeight: 600 }}>
          PATCH /api/v1/projects/{projectId}/features
        </span>
      </div>

      {statusMessage && (
        <div style={{ padding: '12px 16px', borderRadius: '8px', background: statusMessage.includes('Failed') ? '#fef2f2' : '#f0fdf4', color: statusMessage.includes('Failed') ? '#991b1b' : '#166534', border: `1px solid ${statusMessage.includes('Failed') ? '#fecaca' : '#bbf7d0'}`, marginBottom: '20px', fontSize: '0.85rem' }}>
          {statusMessage}
        </div>
      )}

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '20px' }}>
        {featureMetadata.map(({ key, label, description, icon }) => {
          const isEnabled = features[key];
          const isSaving = savingKey === key;

          return (
            <div
              key={key}
              style={{
                padding: '20px',
                borderRadius: '10px',
                border: `1px solid ${isEnabled ? '#bfdbfe' : '#e2e8f0'}`,
                background: isEnabled ? '#f8fafc' : '#ffffff',
                transition: 'all 0.2s ease',
                display: 'flex',
                flexDirection: 'column',
                justifyContent: 'space-between'
              }}
            >
              <div>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '8px' }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                    <span style={{ fontSize: '1.4rem' }}>{icon}</span>
                    <span style={{ fontWeight: 600, fontSize: '1rem', color: '#0f172a' }}>{label}</span>
                  </div>
                  <span style={{
                    fontSize: '0.75rem',
                    fontWeight: 700,
                    padding: '2px 10px',
                    borderRadius: '12px',
                    background: isEnabled ? '#22c55e' : '#94a3b8',
                    color: '#ffffff'
                  }}>
                    {isEnabled ? 'ENABLED' : 'DISABLED'}
                  </span>
                </div>
                <p style={{ fontSize: '0.85rem', color: '#64748b', margin: '0 0 16px 0', lineHeight: 1.4 }}>
                  {description}
                </p>
              </div>

              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', paddingTop: '12px', borderTop: '1px solid #f1f5f9' }}>
                <span style={{ fontSize: '0.8rem', color: '#94a3b8' }}>
                  {isSaving ? 'Saving changes...' : isEnabled ? 'Active in workspace' : 'Inactive'}
                </span>
                <label style={{ position: 'relative', display: 'inline-block', width: '44px', height: '24px', cursor: 'pointer' }}>
                  <input
                    type="checkbox"
                    checked={isEnabled}
                    onChange={() => handleToggle(key)}
                    disabled={isSaving}
                    style={{ opacity: 0, width: 0, height: 0 }}
                  />
                  <span style={{
                    position: 'absolute',
                    top: 0, left: 0, right: 0, bottom: 0,
                    background: isEnabled ? '#2563eb' : '#cbd5e1',
                    borderRadius: '24px',
                    transition: '0.2s'
                  }}>
                    <span style={{
                      position: 'absolute',
                      content: '""',
                      height: '18px',
                      width: '18px',
                      left: isEnabled ? '22px' : '3px',
                      bottom: '3px',
                      background: '#ffffff',
                      borderRadius: '50%',
                      transition: '0.2s'
                    }} />
                  </span>
                </label>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};
