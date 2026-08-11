import React, { useState } from 'react';
import { PageHeader } from '../../../components/ui/PageHeader';
import { Tabs } from '../../../components/ui/Tabs';
import { Card } from '../../../components/ui/Card';
import { Button } from '../../../components/ui/Button';
import { Input } from '../../../components/ui/Input';
import { Select } from '../../../components/ui/Select';
import { LiveCampaignMonitor } from '../../../components/distribution/LiveCampaignMonitor';
import { CampaignLaunchWizard } from '../../../components/distribution/CampaignLaunchWizard';
import { useTenant } from '../../../context/TenantContext';
import { useGenerateTokensMutation } from '../api/useDistributionQueries';

export const DistributionStudioPage: React.FC = () => {
  const { activeProject } = useTenant();
  const [activeTab, setActiveTab] = useState<string>('campaigns');

  // Token Generator Form State
  const [tokenForm, setTokenForm] = useState({
    campaignId: 'CMP-1001',
    surveyId: 'SRV-5001',
    anonymityLevel: 'SEMI_ANONYMOUS' as const,
    count: 100,
    generateKioskPin: true,
  });

  const generateTokensMutation = useGenerateTokensMutation();

  const handleGenerateTokensSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    generateTokensMutation.mutate({
      projectId: activeProject?.projectId || 'PRJ-99201',
      campaignId: tokenForm.campaignId,
      surveyId: tokenForm.surveyId,
      anonymityLevel: tokenForm.anonymityLevel,
      count: tokenForm.count,
      generateKioskPin: tokenForm.generateKioskPin,
    });
  };

  const tabs = [
    { id: 'campaigns', label: 'Active Campaign Monitor' },
    { id: 'wizard', label: 'Launch New Campaign Wizard' },
    { id: 'tokens', label: 'Single-Use Token Vault' },
  ];

  return (
    <div>
      <PageHeader
        title="Survey Campaign Distribution & Token Vault Studio"
        subtitle={`Dispatch multi-channel surveys, generate single-use HMAC-SHA256 tokens, and predict optimal dispatch hours for ${activeProject?.projectId}`}
      />

      <Tabs tabs={tabs} activeTab={activeTab} onChange={setActiveTab} />

      <div style={{ marginTop: '20px' }}>
        {activeTab === 'campaigns' && (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
            <LiveCampaignMonitor
              projectId={activeProject?.projectId || 'PRJ-99201'}
              onLaunchNew={() => setActiveTab('wizard')}
            />
          </div>
        )}

        {activeTab === 'wizard' && (
          <CampaignLaunchWizard
            projectId={activeProject?.projectId || 'PRJ-99201'}
            surveyId="SRV-5001"
            surveyTitle="2026 Employee Engagement Pulse"
            onLaunchComplete={() => setActiveTab('campaigns')}
          />
        )}

        {activeTab === 'tokens' && (
          <Card variant="bordered" padding="24px">
            <div style={{ marginBottom: '20px' }}>
              <h3 style={{ fontSize: '1.2rem', fontWeight: 800, color: '#0f172a', margin: 0 }}>
                Single-Use Cryptographic HMAC-SHA256 Token Vault Generator
              </h3>
              <p style={{ fontSize: '0.85rem', color: '#64748b', margin: '4px 0 0 0' }}>
                Pre-vault cryptographic tokens using Java 21 Virtual Threads and Redis Lua atomic single-use burn (BR-DST-001).
              </p>
            </div>

            <form onSubmit={handleGenerateTokensSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '16px', maxWidth: '600px' }}>
              <Input
                label="Campaign ID"
                value={tokenForm.campaignId}
                onChange={(e) => setTokenForm({ ...tokenForm, campaignId: e.target.value })}
                required
              />

              <Input
                label="Survey ID"
                value={tokenForm.surveyId}
                onChange={(e) => setTokenForm({ ...tokenForm, surveyId: e.target.value })}
                required
              />

              <Select
                label="Anonymity Level"
                value={tokenForm.anonymityLevel}
                onChange={(e) => setTokenForm({ ...tokenForm, anonymityLevel: e.target.value as any })}
                options={[
                  { value: 'SEMI_ANONYMOUS', label: 'Semi-Anonymous' },
                  { value: 'AUTHENTICATED', label: 'Authenticated' },
                  { value: 'FULLY_ANONYMOUS', label: 'Fully Anonymous' },
                  { value: 'KIOSK', label: 'Kiosk Mode' },
                ]}
              />

              <Input
                label="Token Count to Generate"
                type="number"
                value={tokenForm.count}
                onChange={(e) => setTokenForm({ ...tokenForm, count: parseInt(e.target.value) || 100 })}
                required
              />

              <label style={{ display: 'flex', alignItems: 'center', gap: '8px', fontSize: '0.85rem', color: '#334155', cursor: 'pointer' }}>
                <input
                  type="checkbox"
                  checked={tokenForm.generateKioskPin}
                  onChange={(e) => setTokenForm({ ...tokenForm, generateKioskPin: e.target.checked })}
                  style={{ width: '16px', height: '16px', borderRadius: '4px' }}
                />
                <span>Generate 6-digit Kiosk PINs for frontline workers</span>
              </label>

              <div style={{ display: 'flex', justifyContent: 'flex-start', marginTop: '8px' }}>
                <Button type="submit" variant="primary" isLoading={generateTokensMutation.isPending}>
                  🔒 Generate Token Batch Now
                </Button>
              </div>

              {generateTokensMutation.data && (
                <Card variant="bordered" padding="16px" style={{ background: '#f0fdf4', borderColor: '#bbf7d0', marginTop: '16px' }}>
                  <h4 style={{ fontSize: '0.95rem', fontWeight: 700, color: '#166534', margin: '0 0 8px 0' }}>
                    ✓ Token Batch Generated & Vaulted Successfully
                  </h4>
                  <div style={{ fontSize: '0.8rem', color: '#15803d', display: 'flex', flexDirection: 'column', gap: '4px' }}>
                    <div>Total Tokens Cached in Redis: <strong>{generateTokensMutation.data.generatedCount}</strong></div>
                    {generateTokensMutation.data.kioskPin && (
                      <div>Sample Frontline Kiosk PIN: <code style={{ background: '#dcfce7', padding: '2px 6px', borderRadius: '4px', fontWeight: 800 }}>{generateTokensMutation.data.kioskPin}</code></div>
                    )}
                    {generateTokensMutation.data.tokens?.length > 0 && (
                      <div>Sample HMAC Token: <code style={{ fontSize: '0.75rem' }}>{generateTokensMutation.data.tokens[0]}</code></div>
                    )}
                  </div>
                </Card>
              )}
            </form>
          </Card>
        )}
      </div>
    </div>
  );
};
