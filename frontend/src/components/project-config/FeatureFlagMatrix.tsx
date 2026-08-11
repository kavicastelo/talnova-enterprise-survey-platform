import React, { useState, useMemo } from 'react';
import { FeatureFlags } from '../../types/projectConfig';
import { useUpdateFeatureFlagsMutation } from '../../features/project-config/api/useProjectConfigQueries';
import { Card } from '../ui/Card';
import { Badge } from '../ui/Badge';
import { Button } from '../ui/Button';
import { Input } from '../ui/Input';
import { EmptyState } from '../ui/EmptyState';

interface Props {
  projectId: string;
  initialFeatures: FeatureFlags;
  onUpdate?: (updated: FeatureFlags) => void;
}

type ModuleCategory = 'ALL' | 'ANALYTICS' | 'CHANNELS' | 'INTEGRATIONS';

export const FeatureFlagMatrix: React.FC<Props> = ({ projectId, initialFeatures, onUpdate }) => {
  const [features, setFeatures] = useState<FeatureFlags>(initialFeatures);
  const [searchTerm, setSearchTerm] = useState('');
  const [activeCategory, setActiveCategory] = useState<ModuleCategory>('ALL');

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

  const featureDefinitions: Array<{
    key: keyof FeatureFlags;
    label: string;
    description: string;
    icon: string;
    category: 'ANALYTICS' | 'CHANNELS' | 'INTEGRATIONS';
  }> = [
    {
      key: 'aiAnalyticsEnabled',
      label: 'AI Analytics & Sentiment NLP Engine',
      description: 'Enables qualitative open-text comment analysis, sentiment scoring, topic clustering, and LLM executive summary generation.',
      icon: '✨',
      category: 'ANALYTICS',
    },
    {
      key: 'actionPlanningEnabled',
      label: 'Action Planning Kanban Module',
      description: 'Enables manager action planning, approval workflows, and two-way integration sync with Jira and MS Planner.',
      icon: '🎯',
      category: 'ANALYTICS',
    },
    {
      key: 'kioskModeEnabled',
      label: 'Factory Kiosk Mode Interface',
      description: 'Enables touch-optimized 6-digit PIN screen player and offline IndexedDB response queueing for factory floor tablets.',
      icon: '🏬',
      category: 'CHANNELS',
    },
    {
      key: 'smsDistributionEnabled',
      label: 'SMS Distribution Channel',
      description: 'Enables SMS dispatch targeting for mobile workforce survey invitations via SMS gateway.',
      icon: '📲',
      category: 'CHANNELS',
    },
    {
      key: 'emailDistributionEnabled',
      label: 'Email Distribution Channel',
      description: 'Enables automated email invitation and reminder dispatch with unique single-use magic tokens.',
      icon: '✉️',
      category: 'CHANNELS',
    },
    {
      key: 'teamsDistributionEnabled',
      label: 'Microsoft Teams Integration',
      description: 'Enables survey notification cards and in-chat response intake inside Microsoft Teams.',
      icon: '💬',
      category: 'CHANNELS',
    },
    {
      key: 'slackDistributionEnabled',
      label: 'Slack Enterprise App',
      description: 'Enables Slack bot survey delivery and real-time manager alerts.',
      icon: '⚡',
      category: 'CHANNELS',
    },
    {
      key: 'hrisSyncEnabled',
      label: 'Automated HRIS Roster Sync',
      description: 'Enables scheduled nightly employee roster synchronization with Workday, SAP SuccessFactors, and BambooHR.',
      icon: '🔄',
      category: 'INTEGRATIONS',
    },
    {
      key: 'gdprAnonymizationEnabled',
      label: 'GDPR Anonymization Engine',
      description: 'Enables differential privacy thresholds (k-anonymity = 5) and automatic PII redaction.',
      icon: '🛡️',
      category: 'INTEGRATIONS',
    },
  ];

  const activeCount = useMemo(() => {
    return featureDefinitions.filter((item) => Boolean(features[item.key])).length;
  }, [features]);

  const disabledCount = featureDefinitions.length - activeCount;

  const filteredDefinitions = useMemo(() => {
    return featureDefinitions.filter((item) => {
      const matchesCategory = activeCategory === 'ALL' || item.category === activeCategory;
      const matchesSearch =
        item.label.toLowerCase().includes(searchTerm.toLowerCase()) ||
        item.description.toLowerCase().includes(searchTerm.toLowerCase());
      return matchesCategory && matchesSearch;
    });
  }, [searchTerm, activeCategory]);

  const handleClearFilters = () => {
    setSearchTerm('');
    setActiveCategory('ALL');
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
      {/* KPI Stats Summary Header */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '16px' }}>
        <Card variant="bordered" padding="16px">
          <div style={{ fontSize: '0.75rem', fontWeight: 700, color: '#64748b', textTransform: 'uppercase' }}>
            Total Modules
          </div>
          <div style={{ fontSize: '1.75rem', fontWeight: 800, color: '#0f172a', marginTop: '4px' }}>
            {featureDefinitions.length}
          </div>
        </Card>
        <Card variant="bordered" padding="16px">
          <div style={{ fontSize: '0.75rem', fontWeight: 700, color: '#16a34a', textTransform: 'uppercase' }}>
            Active Subscription Modules
          </div>
          <div style={{ fontSize: '1.75rem', fontWeight: 800, color: '#15803d', marginTop: '4px' }}>
            {activeCount}
          </div>
        </Card>
        <Card variant="bordered" padding="16px">
          <div style={{ fontSize: '0.75rem', fontWeight: 700, color: '#64748b', textTransform: 'uppercase' }}>
            Disabled Modules
          </div>
          <div style={{ fontSize: '1.75rem', fontWeight: 800, color: '#475569', marginTop: '4px' }}>
            {disabledCount}
          </div>
        </Card>
      </div>

      <Card variant="bordered" padding="24px">
        <div style={{ marginBottom: '20px' }}>
          <h3 style={{ fontSize: '1.2rem', fontWeight: 800, color: '#0f172a', margin: 0 }}>
            Tenant Feature Flag Subscription Matrix
          </h3>
          <p style={{ fontSize: '0.85rem', color: '#64748b', margin: '4px 0 0 0' }}>
            Enable or disable platform domain modules for project <code style={{ fontWeight: 700, color: '#1d4ed8' }}>{projectId}</code> (BR-CFG-004). Changes apply immediately.
          </p>
        </div>

        {/* Filter Controls Bar */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: '16px', marginBottom: '20px' }}>
          <div style={{ display: 'flex', gap: '12px', flexWrap: 'wrap', justifyContent: 'space-between', alignItems: 'center' }}>
            <div style={{ display: 'flex', gap: '6px', flexWrap: 'wrap' }}>
              {[
                { id: 'ALL', label: 'All Modules' },
                { id: 'ANALYTICS', label: 'AI & Analytics' },
                { id: 'CHANNELS', label: 'Distribution Channels' },
                { id: 'INTEGRATIONS', label: 'Roster & Privacy' },
              ].map((cat) => (
                <button
                  key={cat.id}
                  type="button"
                  onClick={() => setActiveCategory(cat.id as ModuleCategory)}
                  style={{
                    padding: '6px 14px',
                    borderRadius: '6px',
                    fontSize: '0.8rem',
                    fontWeight: 600,
                    cursor: 'pointer',
                    border: activeCategory === cat.id ? '1px solid #2563eb' : '1px solid #cbd5e1',
                    background: activeCategory === cat.id ? '#eff6ff' : '#ffffff',
                    color: activeCategory === cat.id ? '#1d4ed8' : '#64748b',
                  }}
                >
                  {cat.label}
                </button>
              ))}
            </div>

            <div style={{ width: '260px' }}>
              <Input
                placeholder="Search modules..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
              />
            </div>
          </div>
        </div>

        {/* Feature List */}
        {filteredDefinitions.length === 0 ? (
          <EmptyState
            title="No Matching Feature Modules"
            description="No subscription feature flags match your search term or category filter."
            action={
              <Button variant="secondary" size="sm" onClick={handleClearFilters}>
                Clear Active Filters
              </Button>
            }
          />
        ) : (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '14px' }}>
            {filteredDefinitions.map((item) => {
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
        )}
      </Card>
    </div>
  );
};

