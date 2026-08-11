import React, { useState } from 'react';
import { AnonymityLevel, CampaignResponse, DistributionChannel } from '../../types/distribution';
import { useCreateCampaignMutation, usePredictOptimalTimeMutation } from '../../features/distribution/api/useDistributionQueries';
import { Card } from '../ui/Card';
import { Button } from '../ui/Button';
import { Badge } from '../ui/Badge';
import { Input } from '../ui/Input';
import { Select } from '../ui/Select';
import { Alert } from '../ui/Alert';

interface Props {
  projectId: string;
  surveyId: string;
  surveyTitle: string;
  onLaunchComplete?: (campaign: CampaignResponse) => void;
  onCancel?: () => void;
}

interface OrgNode {
  id: string;
  name: string;
  recipientCount: number;
}

const ORG_NODES: OrgNode[] = [
  { id: 'N-301', name: 'Corporate Head Office & HR', recipientCount: 1200 },
  { id: 'N-302', name: 'Plant Operations & Logistics', recipientCount: 2800 },
  { id: 'N-303', name: 'Maritime & Freight Logistics', recipientCount: 1200 },
];

export const CampaignLaunchWizard: React.FC<Props> = ({
  projectId,
  surveyId,
  surveyTitle,
  onLaunchComplete,
  onCancel,
}) => {
  const [step, setStep] = useState<number>(1);
  const [campaignTitle, setCampaignTitle] = useState<string>(`${surveyTitle} - Launch Campaign`);
  const [anonymityLevel, setAnonymityLevel] = useState<AnonymityLevel>('SEMI_ANONYMOUS');
  const [selectedNodeIds, setSelectedNodeIds] = useState<string[]>(['N-301']);
  const [selectedChannels, setSelectedChannels] = useState<DistributionChannel[]>(['EMAIL', 'TEAMS']);
  const [expirationDate, setExpirationDate] = useState<string>(
    new Date(Date.now() + 14 * 86400 * 1000).toISOString().slice(0, 16)
  );
  const [reminderIntervalDays, setReminderIntervalDays] = useState<number>(3);
  const [aiPrediction, setAiPrediction] = useState<any | null>(null);

  const createMutation = useCreateCampaignMutation();
  const predictMutation = usePredictOptimalTimeMutation();

  const totalEstimatedRecipients = selectedNodeIds.reduce((sum, nodeId) => {
    const node = ORG_NODES.find((c) => c.id === nodeId);
    return sum + (node ? node.recipientCount : 0);
  }, 0);

  const toggleNodeSelection = (nodeId: string) => {
    setSelectedNodeIds((prev) =>
      prev.includes(nodeId) ? prev.filter((id) => id !== nodeId) : [...prev, nodeId]
    );
  };

  const toggleChannelSelection = (channel: DistributionChannel) => {
    setSelectedChannels((prev) =>
      prev.includes(channel) ? prev.filter((c) => c !== channel) : [...prev, channel]
    );
  };

  const handlePredictAiHour = () => {
    predictMutation.mutate(
      {
        projectId,
        channel: selectedChannels[0] || 'EMAIL',
        targetAudienceCount: totalEstimatedRecipients,
      },
      {
        onSuccess: (data) => setAiPrediction(data),
      }
    );
  };

  const handleLaunchCampaign = () => {
    const generatedCampaignId = `CMP-${Math.random().toString(36).substring(2, 8).toUpperCase()}`;

    createMutation.mutate(
      {
        projectId,
        campaignId: generatedCampaignId,
        surveyId,
        surveyVersion: 1,
        title: campaignTitle,
        anonymityLevel,
        channels: selectedChannels,
        targetAudience: {
          includedNodeIds: selectedNodeIds,
          totalRecipientCount: totalEstimatedRecipients,
        },
        schedule: {
          reminderIntervalDays,
        },
        startDate: new Date().toISOString(),
        expirationDate: new Date(expirationDate).toISOString(),
      },
      {
        onSuccess: (response) => {
          if (onLaunchComplete) {
            onLaunchComplete(response);
          }
        },
      }
    );
  };

  return (
    <Card variant="bordered" padding="28px">
      <div style={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>
        {/* Wizard Header Bar */}
        <div>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '8px' }}>
            <h3 style={{ fontSize: '1.25rem', fontWeight: 800, color: '#0f172a', margin: 0 }}>
              🚀 Survey Campaign Launch Wizard
            </h3>
            <Badge variant="info">Step {step} of 4</Badge>
          </div>
          <p style={{ fontSize: '0.85rem', color: '#64748b', margin: 0 }}>
            Configure multi-channel delivery, target audience node scope, and single-use token vaulting for {projectId}.
          </p>
        </div>

        {/* Step 1: Campaign Details */}
        {step === 1 && (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
            <Input
              label="Campaign Title"
              value={campaignTitle}
              onChange={(e) => setCampaignTitle(e.target.value)}
              placeholder="Q3 Employee Pulse Campaign"
              required
            />

            <Select
              label="Anonymity Protection Tier"
              value={anonymityLevel}
              onChange={(e) => setAnonymityLevel(e.target.value as AnonymityLevel)}
              options={[
                { value: 'SEMI_ANONYMOUS', label: 'Semi-Anonymous (Single-use HMAC Tokens, Isolated Vault)' },
                { value: 'AUTHENTICATED', label: 'Authenticated (Direct Employee Identity Tracking)' },
                { value: 'FULLY_ANONYMOUS', label: 'Fully Anonymous (No Employee ID retained)' },
                { value: 'KIOSK', label: 'Kiosk Mode (6-Digit Kiosk Numeric PINs)' },
              ]}
            />

            <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '12px', marginTop: '8px' }}>
              {onCancel && (
                <Button variant="secondary" onClick={onCancel}>
                  Cancel
                </Button>
              )}
              <Button
                variant="primary"
                disabled={!campaignTitle.trim()}
                onClick={() => setStep(2)}
              >
                Next: Target Audience →
              </Button>
            </div>
          </div>
        )}

        {/* Step 2: Target Audience */}
        {step === 2 && (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <h4 style={{ fontSize: '1rem', fontWeight: 700, color: '#0f172a', margin: 0 }}>
                Organization Tree Audience Scope
              </h4>
              <Badge variant="success">
                👥 Targeted Recipients: {totalEstimatedRecipients.toLocaleString()}
              </Badge>
            </div>

            <div style={{ background: '#f8fafc', padding: '16px', borderRadius: '8px', border: '1px solid #e2e8f0', display: 'flex', flexDirection: 'column', gap: '10px' }}>
              {ORG_NODES.map((node) => (
                <label key={node.id} style={{ display: 'flex', alignItems: 'center', gap: '10px', fontSize: '0.9rem', color: '#1e293b', cursor: 'pointer' }}>
                  <input
                    type="checkbox"
                    checked={selectedNodeIds.includes(node.id)}
                    onChange={() => toggleNodeSelection(node.id)}
                    style={{ width: '16px', height: '16px' }}
                  />
                  <span style={{ fontWeight: 600 }}>{node.name}</span>
                  <span style={{ color: '#64748b', fontSize: '0.8rem' }}>({node.recipientCount.toLocaleString()} employees)</span>
                </label>
              ))}
            </div>

            <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: '8px' }}>
              <Button variant="secondary" onClick={() => setStep(1)}>
                ← Back
              </Button>
              <Button
                variant="primary"
                disabled={selectedNodeIds.length === 0}
                onClick={() => setStep(3)}
              >
                Next: Channels & Schedule →
              </Button>
            </div>
          </div>
        )}

        {/* Step 3: Channels & Schedule */}
        {step === 3 && (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
            <div>
              <label style={{ display: 'block', fontWeight: 600, fontSize: '0.85rem', color: '#334155', marginBottom: '8px' }}>
                Distribution Channels (At least 1 required - VR-DST-004)
              </label>
              <div style={{ display: 'flex', flexWrap: 'wrap', gap: '8px' }}>
                {[
                  { id: 'EMAIL', label: '✉️ Email' },
                  { id: 'SMS', label: '📱 SMS' },
                  { id: 'TEAMS', label: '💬 MS Teams' },
                  { id: 'SLACK', label: '⚡ Slack' },
                  { id: 'KIOSK_PIN', label: '🔢 Kiosk PIN' },
                  { id: 'QR_CODE', label: '📷 QR Code' },
                ].map((ch) => (
                  <Button
                    key={ch.id}
                    type="button"
                    variant={selectedChannels.includes(ch.id as DistributionChannel) ? 'primary' : 'outline'}
                    size="sm"
                    onClick={() => toggleChannelSelection(ch.id as DistributionChannel)}
                  >
                    {ch.label}
                  </Button>
                ))}
              </div>
              {selectedChannels.length === 0 && (
                <p style={{ color: '#dc2626', fontSize: '0.75rem', marginTop: '4px' }}>
                  ⚠️ VR-DST-004: Select at least one distribution channel to launch the campaign.
                </p>
              )}
            </div>

            <div>
              <Input
                label="Expiration Date (Must be at least 24h in future - VR-DST-003)"
                type="datetime-local"
                value={expirationDate}
                onChange={(e) => setExpirationDate(e.target.value)}
              />
              {new Date(expirationDate).getTime() < Date.now() + 24 * 3600 * 1000 && (
                <p style={{ color: '#dc2626', fontSize: '0.75rem', marginTop: '4px' }}>
                  ⚠️ VR-DST-003: Campaign expiration date must be at least 24 hours in the future.
                </p>
              )}
            </div>

            <Input
              label="Reminder Nudge Interval (Days)"
              type="number"
              value={reminderIntervalDays}
              onChange={(e) => setReminderIntervalDays(parseInt(e.target.value) || 3)}
            />

            {/* AI Dispatch Predictor Widget */}
            <Card variant="bordered" padding="16px">
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <div>
                  <strong style={{ fontSize: '0.9rem', color: '#0f172a' }}>⚡ AI Optimal Dispatch Hour Predictor</strong>
                  <p style={{ fontSize: '0.8rem', color: '#64748b', margin: '2px 0 0 0' }}>
                    Predict optimal recipient hour to maximize survey open & response rate.
                  </p>
                </div>
                <Button variant="outline" size="sm" onClick={handlePredictAiHour} isLoading={predictMutation.isPending}>
                  Recommend Hour
                </Button>
              </div>

              {aiPrediction && (
                <div style={{ marginTop: '12px' }}>
                  <Alert type="info" title={`Recommended UTC Hour: ${aiPrediction.recommendedHourUtc}:00`}>
                    {aiPrediction.rationale} (+{aiPrediction.predictedOpenRateImprovementPercent}% open rate improvement)
                  </Alert>
                </div>
              )}
            </Card>

            <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: '8px' }}>
              <Button variant="secondary" onClick={() => setStep(2)}>
                ← Back
              </Button>
              <Button
                variant="primary"
                disabled={selectedChannels.length === 0}
                onClick={() => setStep(4)}
              >
                Next: Review & Launch →
              </Button>
            </div>
          </div>
        )}

        {/* Step 4: Summary & Launch */}
        {step === 4 && (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
            <Alert type="success" title="Pre-Launch Campaign Summary">
              Single-use HMAC-SHA256 tokens will be generated and vaulted in isolation.
            </Alert>

            <div style={{ background: '#f8fafc', padding: '16px', borderRadius: '8px', border: '1px solid #e2e8f0', display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px', fontSize: '0.85rem' }}>
              <div><strong>Campaign Title:</strong> {campaignTitle}</div>
              <div><strong>Survey ID:</strong> {surveyId}</div>
              <div><strong>Anonymity Level:</strong> {anonymityLevel}</div>
              <div><strong>Recipient Scope:</strong> {totalEstimatedRecipients.toLocaleString()} employees</div>
              <div><strong>Channels:</strong> {selectedChannels.join(', ')}</div>
              <div><strong>Expiration:</strong> {new Date(expirationDate).toLocaleString()}</div>
            </div>

            <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: '8px' }}>
              <Button variant="secondary" onClick={() => setStep(3)}>
                ← Back
              </Button>
              <Button
                variant="primary"
                isLoading={createMutation.isPending}
                onClick={handleLaunchCampaign}
              >
                🚀 Launch Campaign Now
              </Button>
            </div>
          </div>
        )}
      </div>
    </Card>
  );
};
