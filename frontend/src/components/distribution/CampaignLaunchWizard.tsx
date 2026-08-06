import React, { useState } from 'react';
import { AnonymityLevel, CampaignCreate, CampaignResponse, DistributionChannel, OptimalDispatchPrediction } from '../../types/distribution';
import { createCampaign, predictOptimalDispatchTime } from '../../services/distributionApi';

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
  children?: OrgNode[];
}

const ORG_TREE: OrgNode[] = [
  {
    id: 'N-100',
    name: 'Aitken Spence Enterprise',
    recipientCount: 5200,
    children: [
      { id: 'N-301', name: 'Corporate Head Office & HR', recipientCount: 1200 },
      { id: 'N-302', name: 'Plant Operations & Logistics', recipientCount: 2800 },
      { id: 'N-303', name: 'Maritime & Freight Logistics', recipientCount: 1200 },
    ],
  },
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
  const [reminderFrequencyDays, setReminderFrequencyDays] = useState<number>(3);
  const [aiPrediction, setAiPrediction] = useState<OptimalDispatchPrediction | null>(null);
  const [isPredictingAi, setIsPredictingAi] = useState<boolean>(false);
  const [isSubmitting, setIsSubmitting] = useState<boolean>(false);
  const [errorMsg, setErrorMsg] = useState<string | null>(null);

  const totalEstimatedRecipients = selectedNodeIds.reduce((sum, nodeId) => {
    const node = ORG_TREE[0].children?.find((c) => c.id === nodeId);
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

  const handlePredictAiHour = async () => {
    setIsPredictingAi(true);
    try {
      const pred = await predictOptimalDispatchTime(projectId, 'EMP-AUTO', 'Corporate Head Office');
      setAiPrediction(pred);
    } catch (err: any) {
      setErrorMsg(err.message || 'AI prediction failed');
    } finally {
      setIsPredictingAi(false);
    }
  };

  const handleLaunchCampaign = async () => {
    setIsSubmitting(true);
    setErrorMsg(null);

    const generatedCampaignId = `CMP-${Math.random().toString(36).substring(2, 8).toUpperCase()}`;

    const payload: CampaignCreate = {
      projectId,
      campaignId: generatedCampaignId,
      surveyId,
      surveyVersion: 1,
      title: campaignTitle,
      anonymityLevel,
      channels: selectedChannels,
      targetAudience: {
        nodeIds: selectedNodeIds,
      },
      schedule: {
        reminderFrequencyDays,
      },
      expirationDate: new Date(expirationDate).toISOString(),
    };

    try {
      const response = await createCampaign(payload);
      if (onLaunchComplete) {
        onLaunchComplete(response);
      }
    } catch (err: any) {
      setErrorMsg(err.message || 'Failed to launch survey campaign');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div style={{ maxWidth: '900px', margin: '0 auto', fontFamily: 'system-ui, -apple-system, sans-serif', color: '#1e293b' }}>
      {/* Wizard Header Progress Bar */}
      <div style={{ background: '#0f172a', borderRadius: '12px', padding: '24px', color: '#fff', marginBottom: '24px' }}>
        <h2 style={{ margin: '0 0 8px 0', fontSize: '22px', fontWeight: '700' }}>🚀 Survey Campaign Launch Wizard</h2>
        <p style={{ margin: '0 0 20px 0', color: '#94a3b8', fontSize: '14px' }}>Configure multi-channel delivery, target audience node scope, and single-use token vaulting.</p>
        
        <div style={{ display: 'flex', justifyContent: 'space-between', position: 'relative' }}>
          {[
            { num: 1, label: 'Metadata' },
            { num: 2, label: 'Target Audience' },
            { num: 3, label: 'Channels & Schedule' },
            { num: 4, label: 'Review & Launch' },
          ].map((s) => (
            <div key={s.num} style={{ display: 'flex', alignItems: 'center', gap: '8px', zIndex: 2 }}>
              <div
                style={{
                  width: '32px',
                  height: '32px',
                  borderRadius: '50%',
                  background: step >= s.num ? '#38bdf8' : '#334155',
                  color: step >= s.num ? '#0f172a' : '#94a3b8',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  fontWeight: '700',
                  fontSize: '14px',
                }}
              >
                {s.num}
              </div>
              <span style={{ fontSize: '13px', color: step >= s.num ? '#f8fafc' : '#64748b', fontWeight: step === s.num ? '700' : '400' }}>
                {s.label}
              </span>
            </div>
          ))}
        </div>
      </div>

      {errorMsg && (
        <div style={{ background: '#fef2f2', border: '1px solid #fca5a5', color: '#991b1b', padding: '12px 16px', borderRadius: '8px', marginBottom: '20px', fontSize: '14px' }}>
          ⚠️ {errorMsg}
        </div>
      )}

      {/* Step 1: Campaign Metadata & Anonymity Tier */}
      {step === 1 && (
        <div style={{ background: '#fff', border: '1px solid #e2e8f0', borderRadius: '12px', padding: '28px' }}>
          <h3 style={{ margin: '0 0 16px 0', fontSize: '18px', fontWeight: '700' }}>Step 1: Campaign Details & Anonymity Tier</h3>
          
          <div style={{ marginBottom: '20px' }}>
            <label style={{ display: 'block', fontWeight: '600', marginBottom: '6px', fontSize: '14px' }}>Campaign Title</label>
            <input
              type="text"
              value={campaignTitle}
              onChange={(e) => setCampaignTitle(e.target.value)}
              style={{ width: '100%', padding: '10px 14px', borderRadius: '8px', border: '1px solid #cbd5e1', fontSize: '14px' }}
            />
          </div>

          <div style={{ marginBottom: '24px' }}>
            <label style={{ display: 'block', fontWeight: '600', marginBottom: '10px', fontSize: '14px' }}>Anonymity Tier Protection</label>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px' }}>
              {[
                { id: 'AUTHENTICATED', name: 'Authenticated', desc: 'Identified employee responses with identity tracking.' },
                { id: 'SEMI_ANONYMOUS', name: 'Semi-Anonymous', desc: 'Single-use HMAC tokens. Identity stored in isolated Vault only.' },
                { id: 'FULLY_ANONYMOUS', name: 'Fully Anonymous', desc: 'No employee ID links retained in any database.' },
                { id: 'KIOSK', name: 'Kiosk 6-Digit PIN', desc: 'Frontline factory kiosk dispatch with numeric PINs.' },
              ].map((tier) => (
                <div
                  key={tier.id}
                  onClick={() => setAnonymityLevel(tier.id as AnonymityLevel)}
                  style={{
                    border: anonymityLevel === tier.id ? '2px solid #0284c7' : '1px solid #e2e8f0',
                    background: anonymityLevel === tier.id ? '#f0f9ff' : '#fff',
                    borderRadius: '10px',
                    padding: '14px',
                    cursor: 'pointer',
                  }}
                >
                  <div style={{ fontWeight: '700', fontSize: '14px', color: anonymityLevel === tier.id ? '#0369a1' : '#1e293b', marginBottom: '4px' }}>
                    {tier.name}
                  </div>
                  <div style={{ fontSize: '12px', color: '#64748b' }}>{tier.desc}</div>
                </div>
              ))}
            </div>
          </div>

          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '12px' }}>
            {onCancel && (
              <button onClick={onCancel} style={{ padding: '10px 18px', borderRadius: '8px', border: '1px solid #cbd5e1', background: '#fff', cursor: 'pointer' }}>
                Cancel
              </button>
            )}
            <button
              onClick={() => setStep(2)}
              disabled={!campaignTitle.trim()}
              style={{ padding: '10px 24px', borderRadius: '8px', border: 'none', background: '#0284c7', color: '#fff', fontWeight: '600', cursor: 'pointer' }}
            >
              Next: Select Target Audience →
            </button>
          </div>
        </div>
      )}

      {/* Step 2: Target Audience Node Tree Selector */}
      {step === 2 && (
        <div style={{ background: '#fff', border: '1px solid #e2e8f0', borderRadius: '12px', padding: '28px' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
            <h3 style={{ margin: 0, fontSize: '18px', fontWeight: '700' }}>Step 2: Organization Node Tree Scope</h3>
            <span style={{ background: '#e0f2fe', color: '#0369a1', padding: '6px 14px', borderRadius: '20px', fontWeight: '700', fontSize: '13px' }}>
              👥 Targeted Recipients: {totalEstimatedRecipients.toLocaleString()}
            </span>
          </div>

          <div style={{ border: '1px solid #e2e8f0', borderRadius: '10px', padding: '16px', marginBottom: '24px', background: '#f8fafc' }}>
            <div style={{ fontWeight: '600', marginBottom: '12px', fontSize: '14px' }}>{ORG_TREE[0].name}</div>
            <div style={{ paddingLeft: '20px', display: 'flex', flexDirection: 'column', gap: '10px' }}>
              {ORG_TREE[0].children?.map((child) => (
                <label key={child.id} style={{ display: 'flex', alignItems: 'center', gap: '10px', fontSize: '14px', cursor: 'pointer' }}>
                  <input
                    type="checkbox"
                    checked={selectedNodeIds.includes(child.id)}
                    onChange={() => toggleNodeSelection(child.id)}
                    style={{ width: '16px', height: '16px' }}
                  />
                  <span style={{ fontWeight: '500' }}>{child.name}</span>
                  <span style={{ color: '#64748b', fontSize: '12px' }}>({child.recipientCount.toLocaleString()} employees)</span>
                </label>
              ))}
            </div>
          </div>

          <div style={{ display: 'flex', justifyContent: 'space-between' }}>
            <button onClick={() => setStep(1)} style={{ padding: '10px 18px', borderRadius: '8px', border: '1px solid #cbd5e1', background: '#fff', cursor: 'pointer' }}>
              ← Back
            </button>
            <button
              onClick={() => setStep(3)}
              disabled={selectedNodeIds.length === 0}
              style={{ padding: '10px 24px', borderRadius: '8px', border: 'none', background: '#0284c7', color: '#fff', fontWeight: '600', cursor: 'pointer' }}
            >
              Next: Channels & Schedule →
            </button>
          </div>
        </div>
      )}

      {/* Step 3: Multi-Channel & Schedule Config */}
      {step === 3 && (
        <div style={{ background: '#fff', border: '1px solid #e2e8f0', borderRadius: '12px', padding: '28px' }}>
          <h3 style={{ margin: '0 0 16px 0', fontSize: '18px', fontWeight: '700' }}>Step 3: Multi-Channel Delivery & Schedule</h3>

          <div style={{ marginBottom: '20px' }}>
            <label style={{ display: 'block', fontWeight: '600', marginBottom: '10px', fontSize: '14px' }}>Distribution Channels</label>
            <div style={{ display: 'flex', flexWrap: 'wrap', gap: '10px' }}>
              {[
                { id: 'EMAIL', icon: '✉️', label: 'Email (AWS SES)' },
                { id: 'SMS', icon: '📱', label: 'SMS (Twilio)' },
                { id: 'TEAMS', icon: '💬', label: 'MS Teams' },
                { id: 'SLACK', icon: '⚡', label: 'Slack' },
                { id: 'KIOSK_PIN', icon: '🔢', label: 'Kiosk 6-Digit PIN' },
                { id: 'QR_CODE', icon: '📷', label: 'QR Code Poster' },
              ].map((ch) => (
                <button
                  key={ch.id}
                  type="button"
                  onClick={() => toggleChannelSelection(ch.id as DistributionChannel)}
                  style={{
                    padding: '8px 16px',
                    borderRadius: '20px',
                    border: selectedChannels.includes(ch.id as DistributionChannel) ? '2px solid #0284c7' : '1px solid #cbd5e1',
                    background: selectedChannels.includes(ch.id as DistributionChannel) ? '#e0f2fe' : '#fff',
                    color: selectedChannels.includes(ch.id as DistributionChannel) ? '#0369a1' : '#475569',
                    fontWeight: '600',
                    fontSize: '13px',
                    cursor: 'pointer',
                    display: 'flex',
                    alignItems: 'center',
                    gap: '6px',
                  }}
                >
                  <span>{ch.icon}</span> {ch.label}
                </button>
              ))}
            </div>
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px', marginBottom: '20px' }}>
            <div>
              <label style={{ display: 'block', fontWeight: '600', marginBottom: '6px', fontSize: '14px' }}>Campaign Expiration Date</label>
              <input
                type="datetime-local"
                value={expirationDate}
                onChange={(e) => setExpirationDate(e.target.value)}
                style={{ width: '100%', padding: '10px', borderRadius: '8px', border: '1px solid #cbd5e1', fontSize: '14px' }}
              />
            </div>
            <div>
              <label style={{ display: 'block', fontWeight: '600', marginBottom: '6px', fontSize: '14px' }}>Reminder Nudge Frequency (Days)</label>
              <input
                type="number"
                min="1"
                max="14"
                value={reminderFrequencyDays}
                onChange={(e) => setReminderFrequencyDays(parseInt(e.target.value) || 3)}
                style={{ width: '100%', padding: '10px', borderRadius: '8px', border: '1px solid #cbd5e1', fontSize: '14px' }}
              />
            </div>
          </div>

          {/* AI Peak Hour Prediction Widget */}
          <div style={{ background: '#f8fafc', border: '1px dashed #94a3b8', borderRadius: '10px', padding: '16px', marginBottom: '24px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <div>
                <span style={{ fontWeight: '700', fontSize: '14px' }}>⚡ AI Optimal Dispatch Hour Predictor</span>
                <p style={{ margin: '2px 0 0 0', color: '#64748b', fontSize: '12px' }}>Analyze department shift patterns to recommend maximum response rate window.</p>
              </div>
              <button
                type="button"
                onClick={handlePredictAiHour}
                disabled={isPredictingAi}
                style={{ padding: '8px 16px', borderRadius: '8px', background: '#7c3aed', color: '#fff', border: 'none', fontWeight: '600', fontSize: '13px', cursor: 'pointer' }}
              >
                {isPredictingAi ? 'Predicting...' : 'Recommend Time'}
              </button>
            </div>

            {aiPrediction && (
              <div style={{ marginTop: '12px', background: '#f3e8ff', border: '1px solid #d8b4fe', padding: '10px 14px', borderRadius: '8px', fontSize: '13px', color: '#581c87' }}>
                🎯 Recommended Hour: <strong>{aiPrediction.recommendedTimeString}</strong> (Confidence: {(aiPrediction.confidenceScore * 100).toFixed(0)}%)
                <br />
                <span style={{ fontSize: '12px', color: '#6b21a8' }}>{aiPrediction.reasoning}</span>
              </div>
            )}
          </div>

          <div style={{ display: 'flex', justifyContent: 'space-between' }}>
            <button onClick={() => setStep(2)} style={{ padding: '10px 18px', borderRadius: '8px', border: '1px solid #cbd5e1', background: '#fff', cursor: 'pointer' }}>
              ← Back
            </button>
            <button
              onClick={() => setStep(4)}
              disabled={selectedChannels.length === 0}
              style={{ padding: '10px 24px', borderRadius: '8px', border: 'none', background: '#0284c7', color: '#fff', fontWeight: '600', cursor: 'pointer' }}
            >
              Next: Review & Launch →
            </button>
          </div>
        </div>
      )}

      {/* Step 4: Summary Review & Token Vaulting Audit */}
      {step === 4 && (
        <div style={{ background: '#fff', border: '1px solid #e2e8f0', borderRadius: '12px', padding: '28px' }}>
          <h3 style={{ margin: '0 0 16px 0', fontSize: '18px', fontWeight: '700' }}>Step 4: Campaign Summary Audit & Pre-Vaulting</h3>

          <div style={{ background: '#f8fafc', border: '1px solid #cbd5e1', borderRadius: '10px', padding: '20px', marginBottom: '24px' }}>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px', fontSize: '14px' }}>
              <div><strong>Campaign Title:</strong> {campaignTitle}</div>
              <div><strong>Survey ID:</strong> {surveyId}</div>
              <div><strong>Anonymity Tier:</strong> {anonymityLevel}</div>
              <div><strong>Recipient Scope:</strong> {totalEstimatedRecipients.toLocaleString()} employees</div>
              <div><strong>Channels:</strong> {selectedChannels.join(', ')}</div>
              <div><strong>Expiration Date:</strong> {new Date(expirationDate).toLocaleString()}</div>
            </div>
          </div>

          <div style={{ background: '#f0fdf4', border: '1px solid #86efac', padding: '12px 16px', borderRadius: '8px', color: '#166534', fontSize: '13px', marginBottom: '24px' }}>
            🔒 Single-use HMAC-SHA256 tokens will be generated via Java 21 Virtual Threads and pre-vaulted in isolated <code>tesp_vault_db</code> upon launch.
          </div>

          <div style={{ display: 'flex', justifyContent: 'space-between' }}>
            <button onClick={() => setStep(3)} style={{ padding: '10px 18px', borderRadius: '8px', border: '1px solid #cbd5e1', background: '#fff', cursor: 'pointer' }}>
              ← Back
            </button>
            <button
              onClick={handleLaunchCampaign}
              disabled={isSubmitting}
              style={{ padding: '12px 28px', borderRadius: '8px', border: 'none', background: '#16a34a', color: '#fff', fontWeight: '700', fontSize: '15px', cursor: 'pointer' }}
            >
              {isSubmitting ? 'Launching Campaign...' : '🚀 Launch Campaign Now'}
            </button>
          </div>
        </div>
      )}
    </div>
  );
};
