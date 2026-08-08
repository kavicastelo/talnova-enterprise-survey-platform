import React, { useState } from 'react';
import { FeatureFlags } from '../../types/projectConfig';
import { useUpdateFeatureFlagsMutation } from '../../features/project-config/api/useProjectConfigQueries';
import { Card } from '../ui/Card';
import { Badge } from '../ui/Badge';
import { Button } from '../ui/Button';

interface Props {
  projectId: string;
  initialFeatures: FeatureFlags;
  onUpdate?: (updated: FeatureFlags) => void;
}

export const FeatureFlagMatrix: React.FC<Props> = ({ projectId, initialFeatures, onUpdate }) => {
  const [features, setFeatures] = useState<FeatureFlags>(initialFeatures);
  const patchMutation = useUpdateFeatureFlagsMutation();

  const handleToggle = (flagKey: keyof FeatureFlags) => {
    const updated = {
      ...features,
      [flagKey]: !features[flagKey],
    };
    setFeatures(updated);

    patchMutation.mutate(
      { projectId, features: updated },
      {
        onSuccess: (data) => {
          if (onUpdate) onUpdate(data.features);
        },
      }
    );
  };

  const featureDefinitions: Array<{ key: keyof FeatureFlags; label: string; description: string; icon: string }> = [
    {
      key: 'aiAnalyticsEnabled',
      label: 'AI Analytics & Sentiment NLP',
      description: 'Enables qualitative open-text comment analysis, sentiment scoring, topic clustering, and LLM executive summary generation.',
      icon: '✨',
    },
    {
      key: 'actionPlanningEnabled',
      label: 'Action Planning Kanban Module',
      description: 'Enables manager action planning, approval workflows, and two-way integration sync with Jira and MS Planner.',
      icon: '🎯',
    },
    {
      key: 'kioskModeEnabled',
      label: 'Factory Kiosk Mode Interface',
      description: 'Enables touch-optimized 6-digit PIN screen player and offline IndexedDB response queueing for factory floor tablets.',
      icon: '🏬',
    },
    {
      key: 'smsDistributionEnabled',
      label: 'SMS Distribution Channel',
      description: 'Enables SMS dispatch targeting for mobile workforce survey invitations via Twilio/SMS gateway.',
      icon: '📲',
    },
  ];

  return (
    <Card variant="bordered" padding="24px">
      <div style={{ marginBottom: '20px' }}>
        <h3 style={{ fontSize: '1.2rem', fontWeight: 800, color: '#0f172a', margin: 0 }}>
          Tenant Feature Flag Matrix
        </h3>
        <p style={{ fontSize: '0.85rem', color: '#64748b', margin: '4px 0 0 0' }}>
          Enable or disable domain feature modules for project <code style={{ fontWeight: 700 }}>{projectId}</code>. Changes apply immediately via Gateway PATCH.
        </p>
      </div>

      <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
        {featureDefinitions.map((item) => {
          const isEnabled = features[item.key];

          return (
            <div
              key={item.key}
              style={{
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center',
                padding: '16px 20px',
                borderRadius: '10px',
                background: isEnabled ? '#f0fdf4' : '#f8fafc',
                border: `1px solid ${isEnabled ? '#bbf7d0' : '#e2e8f0'}`,
                transition: 'all 0.15s ease-in-out',
              }}
            >
              <div style={{ display: 'flex', gap: '14px', alignItems: 'flex-start' }}>
                <span style={{ fontSize: '1.5rem', lineHeight: 1 }}>{item.icon}</span>
                <div>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                    <span style={{ fontWeight: 700, color: '#0f172a', fontSize: '0.95rem' }}>{item.label}</span>
                    <Badge variant={isEnabled ? 'success' : 'neutral'} dot>
                      {isEnabled ? 'ACTIVE' : 'DISABLED'}
                    </Badge>
                  </div>
                  <p style={{ fontSize: '0.825rem', color: '#64748b', margin: '4px 0 0 0', maxWidth: '650px', lineHeight: 1.4 }}>
                    {item.description}
                  </p>
                </div>
              </div>

              <Button
                variant={isEnabled ? 'secondary' : 'primary'}
                size="sm"
                onClick={() => handleToggle(item.key)}
                isLoading={patchMutation.isPending}
              >
                {isEnabled ? 'Disable Feature' : 'Enable Feature'}
              </Button>
            </div>
          );
        })}
      </div>
    </Card>
  );
};
