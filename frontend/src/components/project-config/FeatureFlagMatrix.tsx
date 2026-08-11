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
          if (onUpdate) onUpdate(data.features || updated);
        },
      }
    );
  };

  const featureDefinitions: Array<{ key: keyof FeatureFlags; label: string; description: string; icon: string }> = [
    {
      key: 'aiAnalyticsEnabled',
      label: 'AI Analytics & Sentiment NLP Engine',
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
      description: 'Enables SMS dispatch targeting for mobile workforce survey invitations via SMS gateway.',
      icon: '📲',
    },
    {
      key: 'emailDistributionEnabled',
      label: 'Email Distribution Channel',
      description: 'Enables automated email invitation and reminder dispatch with unique single-use magic tokens.',
      icon: '✉️',
    },
    {
      key: 'teamsDistributionEnabled',
      label: 'Microsoft Teams Integration',
      description: 'Enables survey notification cards and in-chat response intake inside Microsoft Teams.',
      icon: '💬',
    },
    {
      key: 'slackDistributionEnabled',
      label: 'Slack Enterprise App',
      description: 'Enables Slack bot survey delivery and real-time manager alerts.',
      icon: '⚡',
    },
    {
      key: 'hrisSyncEnabled',
      label: 'Automated HRIS Roster Sync',
      description: 'Enables scheduled nightly employee roster synchronization with Workday, SAP SuccessFactors, and BambooHR.',
      icon: '🔄',
    },
    {
      key: 'gdprAnonymizationEnabled',
      label: 'GDPR Anonymization Engine',
      description: 'Enables differential privacy thresholds (k-anonymity = 5) and automatic PII redaction.',
      icon: '🛡️',
    },
  ];

  return (
    <Card variant="bordered" padding="24px">
      <div style={{ marginBottom: '20px' }}>
        <h3 style={{ fontSize: '1.2rem', fontWeight: 800, color: '#0f172a', margin: 0 }}>
          Tenant Feature Flag Subscription Matrix
        </h3>
        <p style={{ fontSize: '0.85rem', color: '#64748b', margin: '4px 0 0 0' }}>
          Enable or disable platform domain modules for project <code style={{ fontWeight: 700, color: '#1d4ed8' }}>{projectId}</code> (BR-CFG-004). Changes apply immediately.
        </p>
      </div>

      <div style={{ display: 'flex', flexDirection: 'column', gap: '14px' }}>
        {featureDefinitions.map((item) => {
          const isEnabled = Boolean(features[item.key]);

          return (
            <div
              key={item.key}
              style={{
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center',
                padding: '16px 20px',
                borderRadius: '10px',
                background: isEnabled ? '#f0fdf4' : '#ffffff',
                border: `1px solid ${isEnabled ? '#bbf7d0' : '#e2e8f0'}`,
                boxShadow: '0 1px 2px 0 rgba(0, 0, 0, 0.05)',
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
                  <p style={{ fontSize: '0.825rem', color: '#64748b', margin: '4px 0 0 0', maxWidth: '680px', lineHeight: 1.4 }}>
                    {item.description}
                  </p>
                </div>
              </div>

              <Button
                variant={isEnabled ? 'danger' : 'primary'}
                size="sm"
                onClick={() => handleToggle(item.key)}
                isLoading={patchMutation.isPending}
              >
                {isEnabled ? 'Disable Module' : 'Activate Module'}
              </Button>
            </div>
          );
        })}
      </div>
    </Card>
  );
};
