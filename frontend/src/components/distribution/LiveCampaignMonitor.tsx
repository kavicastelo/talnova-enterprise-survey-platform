import React, { useState } from 'react';
import { useCampaignsQuery } from '../../features/distribution/api/useDistributionQueries';
import { CampaignMetricsCard } from './CampaignMetricsCard';
import { Card } from '../ui/Card';
import { Select } from '../ui/Select';
import { Skeleton } from '../ui/Skeleton';
import { ErrorState } from '../ui/ErrorState';
import { Button } from '../ui/Button';
import { Badge } from '../ui/Badge';
import { Input } from '../ui/Input';
import { CampaignStatus } from '../../types/distribution';

interface Props {
  projectId: string;
  onLaunchNew?: () => void;
}

export const LiveCampaignMonitor: React.FC<Props> = ({ projectId, onLaunchNew }) => {
  const { data: campaigns, isLoading, isError, refetch } = useCampaignsQuery(projectId);
  const [selectedCampaignId, setSelectedCampaignId] = useState<string>('');
  const [statusFilter, setStatusFilter] = useState<'ALL' | CampaignStatus>('ALL');
  const [searchQuery, setSearchQuery] = useState<string>('');

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
            No Active Distribution Campaigns Found
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

  // Filter campaigns by search & status
  const filteredCampaigns = campaigns.filter((c) => {
    const matchesStatus = statusFilter === 'ALL' || c.status === statusFilter;
    const matchesSearch =
      c.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
      c.campaignId.toLowerCase().includes(searchQuery.toLowerCase()) ||
      c.surveyId.toLowerCase().includes(searchQuery.toLowerCase());
    return matchesStatus && matchesSearch;
  });

  const activeCount = campaigns.filter((c) => c.status === 'ACTIVE').length;
  const pausedCount = campaigns.filter((c) => c.status === 'PAUSED').length;
  const completedCount = campaigns.filter((c) => c.status === 'COMPLETED').length;

  const activeId =
    selectedCampaignId && filteredCampaigns.some((c) => c.campaignId === selectedCampaignId)
      ? selectedCampaignId
      : filteredCampaigns[0]?.campaignId || campaigns[0]?.campaignId;

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
      {/* Top Filter & Monitor Toolbar */}
      <Card variant="bordered" padding="20px">
        <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
          {/* Header Row & Summary Pills */}
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <div>
              <h3 style={{ fontSize: '1.1rem', fontWeight: 800, color: '#0f172a', margin: 0 }}>
                📊 Real-Time Campaign Distribution Monitor
              </h3>
              <p style={{ fontSize: '0.8rem', color: '#64748b', margin: '2px 0 0 0' }}>
                Monitor delivery progress, open rates, and department response rates for {projectId}.
              </p>
            </div>

            <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
              <div style={{ display: 'flex', gap: '6px' }}>
                <Badge variant="info">Total: {campaigns.length}</Badge>
                <Badge variant="success">Active: {activeCount}</Badge>
                <Badge variant="warning">Paused: {pausedCount}</Badge>
                <Badge variant="neutral">Completed: {completedCount}</Badge>
              </div>

              {onLaunchNew && (
                <Button variant="primary" size="sm" onClick={onLaunchNew}>
                  + Launch Campaign
                </Button>
              )}
            </div>
          </div>

          {/* Filter Bar & Campaign Combobox */}
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 240px auto', gap: '12px', alignItems: 'center' }}>
            <Select
              label="Select Active Campaign to Inspect"
              value={activeId}
              onChange={(e) => setSelectedCampaignId(e.target.value)}
              options={filteredCampaigns.map((c) => ({
                value: c.campaignId,
                label: `${c.title} (${c.campaignId}) — Status: ${c.status}`,
              }))}
            />

            <Input
              placeholder="Search campaigns by name or ID..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
            />

            <div>
              <label style={{ display: 'block', fontWeight: 600, fontSize: '0.75rem', color: '#64748b', marginBottom: '4px' }}>
                Filter Status
              </label>
              <div style={{ display: 'flex', gap: '4px' }}>
                {(['ALL', 'ACTIVE', 'PAUSED', 'COMPLETED'] as const).map((st) => (
                  <Button
                    key={st}
                    variant={statusFilter === st ? 'primary' : 'outline'}
                    size="sm"
                    onClick={() => setStatusFilter(st)}
                    style={{ padding: '4px 8px', fontSize: '0.75rem' }}
                  >
                    {st}
                  </Button>
                ))}
              </div>
            </div>
          </div>
        </div>
      </Card>

      {/* Active Campaign Metric Scorecard */}
      {activeId ? (
        <CampaignMetricsCard campaignId={activeId} projectId={projectId} />
      ) : (
        <Card variant="bordered" padding="24px" style={{ textAlign: 'center', color: '#64748b' }}>
          No campaigns match the selected status or search filter.
        </Card>
      )}
    </div>
  );
};
