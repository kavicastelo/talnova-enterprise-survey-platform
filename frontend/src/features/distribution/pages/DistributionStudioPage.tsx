import React, { useState, useEffect } from 'react';
import { PageHeader } from '../../../components/ui/PageHeader';
import { Tabs } from '../../../components/ui/Tabs';
import { Card } from '../../../components/ui/Card';
import { Button } from '../../../components/ui/Button';
import { Input } from '../../../components/ui/Input';
import { Select } from '../../../components/ui/Select';
import { Badge } from '../../../components/ui/Badge';
import { Alert } from '../../../components/ui/Alert';
import { LiveCampaignMonitor } from '../../../components/distribution/LiveCampaignMonitor';
import { CampaignLaunchWizard } from '../../../components/distribution/CampaignLaunchWizard';
import { useTenant } from '../../../context/TenantContext';
import { useCampaignsQuery, useGenerateTokensMutation } from '../api/useDistributionQueries';
import { AnonymityLevel } from '../../../types/distribution';

const DEFAULT_PUBLISHED_SURVEYS = [
  { surveyId: 'SRV-5001', title: '2026 Employee Engagement Pulse Survey' },
  { surveyId: 'SRV-6002', title: 'Executive Leadership 360 Feedback' },
  { surveyId: 'SRV-7003', title: 'Frontline Factory & Logistics Safety Culture Assessment' },
];

export const DistributionStudioPage: React.FC = () => {
  const { activeProject } = useTenant();
  const [activeTab, setActiveTab] = useState<string>('campaigns');

  const { data: campaigns } = useCampaignsQuery(activeProject?.projectId);

  // Token Generator Form State
  const [tokenForm, setTokenForm] = useState<{
    campaignId: string;
    surveyId: string;
    anonymityLevel: AnonymityLevel;
    count: number;
    generateKioskPin: boolean;
  }>({
    campaignId: 'CMP-1001',
    surveyId: 'SRV-5001',
    anonymityLevel: 'SEMI_ANONYMOUS',
    count: 100,
    generateKioskPin: true,
  });

  const [copiedField, setCopiedField] = useState<string | null>(null);

  // Sync selected campaign when campaigns data loads
  useEffect(() => {
    if (campaigns && campaigns.length > 0) {
      const first = campaigns[0];
      setTokenForm((prev) => ({
        ...prev,
        campaignId: first.campaignId,
        surveyId: first.surveyId || 'SRV-5001',
        anonymityLevel: first.anonymityLevel || 'SEMI_ANONYMOUS',
      }));
    }
  }, [campaigns]);

  const generateTokensMutation = useGenerateTokensMutation();

  const handleCampaignSelectChange = (campaignId: string) => {
    const found = campaigns?.find((c) => c.campaignId === campaignId);
    setTokenForm((prev) => ({
      ...prev,
      campaignId,
      surveyId: found?.surveyId || prev.surveyId,
      anonymityLevel: found?.anonymityLevel || prev.anonymityLevel,
    }));
  };

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

  const copyToClipboard = (text: string, label: string) => {
    navigator.clipboard.writeText(text);
    setCopiedField(label);
    setTimeout(() => setCopiedField(null), 2500);
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
            onLaunchComplete={() => setActiveTab('campaigns')}
            onCancel={() => setActiveTab('campaigns')}
          />
        )}

        {activeTab === 'tokens' && (
          <Card variant="bordered" padding="28px">
            <div style={{ marginBottom: '24px' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                <h3 style={{ fontSize: '1.25rem', fontWeight: 800, color: '#0f172a', margin: 0 }}>
                  🔑 Single-Use Cryptographic HMAC-SHA256 Token Vault Generator
                </h3>
                <Badge variant="indigo">FR-DST-002 | BR-DST-001</Badge>
              </div>
              <p style={{ fontSize: '0.85rem', color: '#64748b', margin: '4px 0 0 0' }}>
                Pre-vault cryptographic tokens stored in isolated Vault DB (<code>tesp_vault_db</code>) and cached in Redis with atomic single-use burn protection.
              </p>
            </div>

            {/* Contextual Empty State if no campaigns exist */}
            {(!campaigns || campaigns.length === 0) ? (
              <Card variant="bordered" padding="24px" style={{ background: '#f8fafc', textAlign: 'center' }}>
                <div style={{ fontSize: '2rem', marginBottom: '8px' }}>🚀</div>
                <h4 style={{ fontSize: '1.05rem', fontWeight: 700, color: '#0f172a', margin: '0 0 6px 0' }}>
                  No Active Campaigns Found for Project {activeProject?.projectId}
                </h4>
                <p style={{ fontSize: '0.85rem', color: '#64748b', maxWidth: '540px', margin: '0 auto 16px auto' }}>
                  Cryptographic tokens must be linked to an active or scheduled survey campaign. Launch a new campaign to initialize single-use tokens automatically or pre-vault token batches.
                </p>
                <Button variant="primary" onClick={() => setActiveTab('wizard')}>
                  + Launch New Campaign Wizard
                </Button>
              </Card>
            ) : (
              <form onSubmit={handleGenerateTokensSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '20px', maxWidth: '680px' }}>
                {/* Human-Readable Campaign Selector */}
                <div>
                  <Select
                    label="Target Distribution Campaign"
                    value={tokenForm.campaignId}
                    onChange={(e) => handleCampaignSelectChange(e.target.value)}
                    options={campaigns.map((c) => ({
                      value: c.campaignId,
                      label: `${c.title} (ID: ${c.campaignId} | Status: ${c.status})`,
                    }))}
                  />
                  <p style={{ fontSize: '0.75rem', color: '#64748b', marginTop: '4px' }}>
                    Select an existing campaign to associate generated token hashes.
                  </p>
                </div>

                {/* Human-Readable Survey Selector */}
                <div>
                  <Select
                    label="Target Published Survey Questionnaire"
                    value={tokenForm.surveyId}
                    onChange={(e) => setTokenForm({ ...tokenForm, surveyId: e.target.value })}
                    options={DEFAULT_PUBLISHED_SURVEYS.map((s) => ({
                      value: s.surveyId,
                      label: `${s.title} (${s.surveyId})`,
                    }))}
                  />
                </div>

                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
                  <Select
                    label="Anonymity Tier Protection"
                    value={tokenForm.anonymityLevel}
                    onChange={(e) => setTokenForm({ ...tokenForm, anonymityLevel: e.target.value as any })}
                    options={[
                      { value: 'SEMI_ANONYMOUS', label: 'Semi-Anonymous (Isolated Vault)' },
                      { value: 'AUTHENTICATED', label: 'Authenticated (Tracked Identity)' },
                      { value: 'FULLY_ANONYMOUS', label: 'Fully Anonymous' },
                      { value: 'KIOSK', label: 'Kiosk Frontline Mode' },
                    ]}
                  />

                  <Input
                    label="Token Count Batch Size (1 to 10,000)"
                    type="number"
                    value={tokenForm.count}
                    onChange={(e) =>
                      setTokenForm({
                        ...tokenForm,
                        count: Math.min(10000, Math.max(1, parseInt(e.target.value) || 1)),
                      })
                    }
                    required
                  />
                </div>

                <div style={{ background: '#f8fafc', padding: '12px 16px', borderRadius: '8px', border: '1px solid #e2e8f0' }}>
                  <label style={{ display: 'flex', alignItems: 'center', gap: '10px', fontSize: '0.875rem', color: '#334155', cursor: 'pointer' }}>
                    <input
                      type="checkbox"
                      checked={tokenForm.generateKioskPin}
                      onChange={(e) => setTokenForm({ ...tokenForm, generateKioskPin: e.target.checked })}
                      style={{ width: '18px', height: '18px', borderRadius: '4px' }}
                    />
                    <span style={{ fontWeight: 600 }}>Generate 6-digit Kiosk PINs for deskless frontline workers</span>
                  </label>
                  <p style={{ fontSize: '0.75rem', color: '#64748b', margin: '4px 0 0 28px' }}>
                    Creates 6-digit numeric PINs for plant factory and maritime logistics terminals without corporate email access.
                  </p>
                </div>

                <div style={{ display: 'flex', justifyContent: 'flex-start', marginTop: '4px' }}>
                  <Button type="submit" variant="primary" isLoading={generateTokensMutation.isPending} style={{ padding: '10px 24px' }}>
                    🔒 Generate & Vault Token Batch Now
                  </Button>
                </div>

                {/* Error Banner */}
                {generateTokensMutation.isError && (
                  <Alert type="warning" title="Token Generation Error">
                    {(generateTokensMutation.error as any)?.message || 'Failed to generate token batch. Please check campaign status and try again.'}
                  </Alert>
                )}

                {/* Token Results Card */}
                {generateTokensMutation.data && (
                  <Card variant="bordered" padding="20px" style={{ background: '#f0fdf4', borderColor: '#bbf7d0', marginTop: '12px' }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '12px' }}>
                      <h4 style={{ fontSize: '1rem', fontWeight: 800, color: '#166534', margin: 0 }}>
                        ✓ Cryptographic Token Batch Generated & Vaulted
                      </h4>
                      <Badge variant="success">Redis Cached (TTL = Expiration Date)</Badge>
                    </div>

                    <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px', fontSize: '0.85rem', color: '#15803d', marginBottom: '12px' }}>
                      <div>
                        Target Campaign: <strong>{generateTokensMutation.data.campaignId}</strong>
                      </div>
                      <div>
                        Total Cached Tokens: <strong>{generateTokensMutation.data.generatedCount.toLocaleString()}</strong>
                      </div>
                      <div>
                        Vault Isolation: <code style={{ background: '#dcfce7', padding: '2px 6px', borderRadius: '4px' }}>tesp_vault_db</code>
                      </div>
                      <div>
                        Single-Use Burn: <span style={{ fontWeight: 600 }}>Active (BR-DST-001)</span>
                      </div>
                    </div>

                    {generateTokensMutation.data.kioskPin && (
                      <div style={{ marginTop: '12px', paddingTop: '12px', borderTop: '1px solid #bbf7d0', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                        <div>
                          <span style={{ fontSize: '0.78rem', color: '#166534', display: 'block' }}>Sample Frontline Kiosk PIN:</span>
                          <code style={{ fontSize: '1.1rem', background: '#dcfce7', color: '#14532d', padding: '4px 10px', borderRadius: '6px', fontWeight: 800, letterSpacing: '2px' }}>
                            {generateTokensMutation.data.kioskPin}
                          </code>
                        </div>
                        <Button
                          variant="outline"
                          size="sm"
                          onClick={() => copyToClipboard(generateTokensMutation.data!.kioskPin!, 'kioskPin')}
                        >
                          {copiedField === 'kioskPin' ? '✓ Copied!' : '📋 Copy PIN'}
                        </Button>
                      </div>
                    )}

                    {generateTokensMutation.data.tokens?.length > 0 && (
                      <div style={{ marginTop: '12px', paddingTop: '12px', borderTop: '1px solid #bbf7d0', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                        <div style={{ maxWidth: '420px', overflow: 'hidden', textOverflow: 'ellipsis' }}>
                          <span style={{ fontSize: '0.78rem', color: '#166534', display: 'block' }}>Sample Single-Use HMAC Token:</span>
                          <code style={{ fontSize: '0.75rem', background: '#dcfce7', color: '#14532d', padding: '4px 8px', borderRadius: '4px', wordBreak: 'break-all' }}>
                            {generateTokensMutation.data.tokens[0]}
                          </code>
                        </div>
                        <Button
                          variant="outline"
                          size="sm"
                          onClick={() => copyToClipboard(generateTokensMutation.data!.tokens[0], 'hmacToken')}
                        >
                          {copiedField === 'hmacToken' ? '✓ Copied!' : '📋 Copy Token'}
                        </Button>
                      </div>
                    )}
                  </Card>
                )}
              </form>
            )}
          </Card>
        )}
      </div>
    </div>
  );
};

