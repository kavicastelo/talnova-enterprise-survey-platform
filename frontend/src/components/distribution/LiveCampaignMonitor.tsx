import React, { useState } from 'react';
import { useCampaignsQuery } from '../../features/distribution/api/useDistributionQueries';
import { CampaignMetricsCard } from './CampaignMetricsCard';
import { Card } from '../ui/Card';
import { Select } from '../ui/Select';
import { Skeleton } from '../ui/Skeleton';
import { ErrorState } from '../ui/ErrorState';
import { Button } from '../ui/Button';

interface Props {
  projectId: string;
  onLaunchNew?: () => void;
}

export const LiveCampaignMonitor: React.FC<Props> = ({ projectId, onLaunchNew }) => {
  const { data: campaigns, isLoading, isError, refetch } = useCampaignsQuery(projectId);
  const [selectedCampaignId, setSelectedCampaignId] = useState<string>('');

  if (isLoading) {
    return (
      <Card variant="bordered" padding="24px">
        <Skeleton height="200px" borderRadius="12px" />
      </Card>
    );
  }

  if (isError) {
    return (
      <Card variant="bordered" padding="24px">
        <ErrorState
          title="Campaigns Unavailable"
          message="Could not retrieve survey distribution campaigns for this project."
          onRetry={refetch}
        />
      </Card>
    );
  }

  if (!campaigns || campaigns.length === 0) {
    return (
      <Card variant="bordered" padding="32px">
        <div style={{ textAlign: 'center', padding: '20px 0' }}>
          <h3 style={{ fontSize: '1.2rem', fontWeight: 800, color: '#0f172a', margin: '0 0 8px 0' }}>
            No Active Distribution Campaigns
          </h3>
          <p style={{ fontSize: '0.875rem', color: '#64748b', marginBottom: '20px' }}>
            There are currently no survey dispatches launched for project <code>{projectId}</code>.
          </p>
          {onLaunchNew && (
            <Button variant="primary" onClick={onLaunchNew}>
              🚀 Launch New Campaign Wizard
            </Button>
          )}
        </div>
      </Card>
    );
  }

  const activeId = selectedCampaignId || campaigns[0]?.campaignId;

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
      {campaigns.length > 1 && (
        <Card variant="bordered" padding="16px">
          <Select
            label="Select Active Campaign to Monitor"
            value={activeId}
            onChange={(e) => setSelectedCampaignId(e.target.value)}
            options={campaigns.map((c) => ({
              value: c.campaignId,
              label: `${c.title} (${c.campaignId}) — Status: ${c.status}`,
            }))}
          />
        </Card>
      )}

      {activeId && <CampaignMetricsCard campaignId={activeId} projectId={projectId} />}
    </div>
  );
};
