import React, { useState } from 'react';
import { AnonymityLevel, CampaignResponse, DistributionChannel } from '../../types/distribution';
import { useCreateCampaignMutation, usePredictOptimalTimeMutation } from '../../features/distribution/api/useDistributionQueries';
import { Card } from '../ui/Card';
import { Button } from '../ui/Button';
import { Badge } from '../ui/Badge';
import { Input } from '../ui/Input';
import { Select } from '../ui/Select';
import { Alert } from '../ui/Alert';

export interface PublishedSurveyOption {
  surveyId: string;
  title: string;
  version: number;
  questionCount: number;
  status: 'PUBLISHED' | 'ACTIVE';
}

const DEFAULT_PUBLISHED_SURVEYS: PublishedSurveyOption[] = [
  { surveyId: 'SRV-5001', title: '2026 Employee Engagement Pulse Survey', version: 1, questionCount: 18, status: 'PUBLISHED' },
  { surveyId: 'SRV-6002', title: 'Executive Leadership 360 Feedback', version: 2, questionCount: 24, status: 'PUBLISHED' },
  { surveyId: 'SRV-7003', title: 'Frontline Factory & Logistics Safety Culture Assessment', version: 1, questionCount: 12, status: 'PUBLISHED' },
];

interface Props {
  projectId: string;
  surveyId?: string;
  surveyTitle?: string;
  availableSurveys?: PublishedSurveyOption[];
  onLaunchComplete?: (campaign: CampaignResponse) => void;
  onCancel?: () => void;
  onNavigateToSurveys?: () => void;
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
  { id: 'N-304', name: 'Global R&D & Engineering', recipientCount: 950 },
  { id: 'N-305', name: 'Commercial Sales & Regional Marketing', recipientCount: 1450 },
];

const DEMOGRAPHIC_TENURES = ['< 1 Year', '1–3 Years', '3–5 Years', '5+ Years'];
const DEMOGRAPHIC_EMPLOYMENT_TYPES = ['Full-Time Regular', 'Part-Time', 'Contractor / Third-Party', 'Factory Shift Worker'];

export const CampaignLaunchWizard: React.FC<Props> = ({
  projectId,
  surveyId: initialSurveyId,
  surveyTitle: initialSurveyTitle,
  availableSurveys = DEFAULT_PUBLISHED_SURVEYS,
  onLaunchComplete,
  onCancel,
  onNavigateToSurveys,
}) => {
  const [step, setStep] = useState<number>(1);
  const [selectedSurveyId, setSelectedSurveyId] = useState<string>(
    initialSurveyId || (availableSurveys.length > 0 ? availableSurveys[0].surveyId : '')
  );

  const selectedSurvey = availableSurveys.find((s) => s.surveyId === selectedSurveyId) || availableSurveys[0];

  const [campaignTitle, setCampaignTitle] = useState<string>(
    initialSurveyTitle
      ? `${initialSurveyTitle} - Launch Campaign`
      : selectedSurvey
      ? `${selectedSurvey.title} - Q3 Launch`
      : 'Q3 Enterprise Survey Campaign'
  );
  const [anonymityLevel, setAnonymityLevel] = useState<AnonymityLevel>('SEMI_ANONYMOUS');
  const [selectedNodeIds, setSelectedNodeIds] = useState<string[]>(['N-301', 'N-302']);
  const [selectedTenures, setSelectedTenures] = useState<string[]>([]);
  const [selectedEmpTypes, setSelectedEmpTypes] = useState<string[]>([]);
  const [nodeSearchQuery, setNodeSearchQuery] = useState<string>('');

  const [selectedChannels, setSelectedChannels] = useState<DistributionChannel[]>(['EMAIL', 'TEAMS']);
  const [expirationDate, setExpirationDate] = useState<string>(
    new Date(Date.now() + 14 * 86400 * 1000).toISOString().slice(0, 16)
  );
  const [reminderIntervalDays, setReminderIntervalDays] = useState<number>(3);
  const [aiPrediction, setAiPrediction] = useState<any | null>(null);

  const createMutation = useCreateCampaignMutation();
  const predictMutation = usePredictOptimalTimeMutation();

  const filteredNodes = ORG_NODES.filter((n) =>
    n.name.toLowerCase().includes(nodeSearchQuery.toLowerCase())
  );

  const totalBaseRecipients = selectedNodeIds.reduce((sum, nodeId) => {
    const node = ORG_NODES.find((c) => c.id === nodeId);
    return sum + (node ? node.recipientCount : 0);
  }, 0);

  // Apply demographic modifier ratio
  const demographicRatio =
    (selectedTenures.length === 0 ? 1 : selectedTenures.length / DEMOGRAPHIC_TENURES.length) *
    (selectedEmpTypes.length === 0 ? 1 : selectedEmpTypes.length / DEMOGRAPHIC_EMPLOYMENT_TYPES.length);

  const totalEstimatedRecipients = Math.max(10, Math.round(totalBaseRecipients * demographicRatio));

  const toggleNodeSelection = (nodeId: string) => {
    setSelectedNodeIds((prev) =>
      prev.includes(nodeId) ? prev.filter((id) => id !== nodeId) : [...prev, nodeId]
    );
  };

  const selectAllNodes = () => {
    setSelectedNodeIds(ORG_NODES.map((n) => n.id));
  };

  const clearAllNodes = () => {
    setSelectedNodeIds([]);
  };

  const toggleTenure = (t: string) => {
    setSelectedTenures((prev) => (prev.includes(t) ? prev.filter((x) => x !== t) : [...prev, t]));
  };

  const toggleEmpType = (e: string) => {
    setSelectedEmpTypes((prev) => (prev.includes(e) ? prev.filter((x) => x !== e) : [...prev, e]));
  };

  const toggleChannelSelection = (channel: DistributionChannel) => {
    setSelectedChannels((prev) =>
      prev.includes(channel) ? prev.filter((c) => c !== channel) : [...prev, channel]
    );
  };

  const handleSurveyChange = (surveyId: string) => {
    setSelectedSurveyId(surveyId);
    const surv = availableSurveys.find((s) => s.surveyId === surveyId);
    if (surv) {
      setCampaignTitle(`${surv.title} - Campaign`);
    }
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
        surveyId: selectedSurveyId || 'SRV-5001',
        surveyVersion: selectedSurvey?.version || 1,
        title: campaignTitle,
        anonymityLevel,
        channels: selectedChannels,
        targetAudience: {
          includedNodeIds: selectedNodeIds,
          totalRecipientCount: totalEstimatedRecipients,
          filterAttributes: {
            tenure: selectedTenures.join(','),
            employmentType: selectedEmpTypes.join(','),
          },
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

  // If no available surveys exist
  if (!availableSurveys || availableSurveys.length === 0) {
    return (
      <Card variant="bordered" padding="32px">
        <div style={{ textAlign: 'center', padding: '24px 12px' }}>
          <div style={{ fontSize: '2.5rem', marginBottom: '12px' }}>📋</div>
          <h3 style={{ fontSize: '1.25rem', fontWeight: 800, color: '#0f172a', margin: '0 0 8px 0' }}>
            No Published Surveys Available
          </h3>
          <p style={{ fontSize: '0.875rem', color: '#64748b', maxWidth: '500px', margin: '0 auto 24px auto' }}>
            You cannot launch a distribution campaign without a published survey questionnaire. Please build and publish a survey version first in the Survey Builder.
          </p>
          <div style={{ display: 'flex', justifyContent: 'center', gap: '12px' }}>
            {onCancel && (
              <Button variant="outline" onClick={onCancel}>
                Cancel
              </Button>
            )}
            <Button
              variant="primary"
              onClick={() => {
                if (onNavigateToSurveys) onNavigateToSurveys();
                else window.location.href = '/surveys';
              }}
            >
              + Go to Survey Builder & Publish Questionnaire
            </Button>
          </div>
        </div>
      </Card>
    );
  }

  const isExpirationValid = new Date(expirationDate).getTime() >= Date.now() + 24 * 3600 * 1000;

  return (
    <Card variant="bordered" padding="28px">
      <div style={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>
        {/* Visual Stepper Header */}
        <div>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
            <div>
              <h3 style={{ fontSize: '1.25rem', fontWeight: 800, color: '#0f172a', margin: 0 }}>
                🚀 Multi-Channel Survey Campaign Launch Wizard
              </h3>
              <p style={{ fontSize: '0.85rem', color: '#64748b', margin: '4px 0 0 0' }}>
                Configure target audience, multi-channel dispatch, and single-use token vaulting for <strong>{projectId}</strong>.
              </p>
            </div>
            <Badge variant="info" style={{ fontSize: '0.85rem', padding: '6px 12px' }}>
              Step {step} of 4
            </Badge>
          </div>

          {/* Stepper Steps Bar */}
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: '8px', borderTop: '1px solid #e2e8f0', paddingTop: '16px' }}>
            {[
              { num: 1, title: 'Survey & Anonymity' },
              { num: 2, title: 'Target Audience' },
              { num: 3, title: 'Channels & Schedule' },
              { num: 4, title: 'Review & Launch' },
            ].map((s) => {
              const isActive = step === s.num;
              const isCompleted = step > s.num;

              return (
                <div
                  key={s.num}
                  onClick={() => isCompleted && setStep(s.num)}
                  style={{
                    display: 'flex',
                    alignItems: 'center',
                    gap: '8px',
                    padding: '8px 12px',
                    borderRadius: '8px',
                    background: isActive ? '#eff6ff' : isCompleted ? '#f0fdf4' : '#f8fafc',
                    border: `1px solid ${isActive ? '#3b82f6' : isCompleted ? '#86efac' : '#e2e8f0'}`,
                    cursor: isCompleted ? 'pointer' : 'default',
                    transition: 'all 0.15s ease',
                  }}
                >
                  <div
                    style={{
                      width: '24px',
                      height: '24px',
                      borderRadius: '50%',
                      background: isActive ? '#2563eb' : isCompleted ? '#16a34a' : '#cbd5e1',
                      color: '#ffffff',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      fontSize: '0.75rem',
                      fontWeight: 800,
                    }}
                  >
                    {isCompleted ? '✓' : s.num}
                  </div>
                  <span
                    style={{
                      fontSize: '0.8rem',
                      fontWeight: isActive || isCompleted ? 700 : 500,
                      color: isActive ? '#1e40af' : isCompleted ? '#166534' : '#64748b',
                    }}
                  >
                    {s.title}
                  </span>
                </div>
              );
            })}
          </div>
        </div>

        {/* Step 1: Select Survey & Anonymity Tier */}
        {step === 1 && (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
            <div>
              <Select
                label="Target Published Survey Questionnaire"
                value={selectedSurveyId}
                onChange={(e) => handleSurveyChange(e.target.value)}
                options={availableSurveys.map((s) => ({
                  value: s.surveyId,
                  label: `${s.title} (ID: ${s.surveyId} | v${s.version} — ${s.questionCount} Questions)`,
                }))}
              />
              <p style={{ fontSize: '0.75rem', color: '#64748b', marginTop: '4px' }}>
                Only published survey versions with locked question structures can be launched into distribution campaigns.
              </p>
            </div>

            <Input
              label="Campaign Title"
              value={campaignTitle}
              onChange={(e) => setCampaignTitle(e.target.value)}
              placeholder="e.g. Q3 2026 All-Hands Engagement Pulse"
              required
            />

            <div>
              <label style={{ display: 'block', fontWeight: 600, fontSize: '0.85rem', color: '#334155', marginBottom: '8px' }}>
                Anonymity Protection Tier & Cryptographic Vault Security
              </label>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px' }}>
                {[
                  {
                    tier: 'SEMI_ANONYMOUS' as AnonymityLevel,
                    title: '🔒 Semi-Anonymous (Isolated Vault)',
                    desc: 'Generates single-use HMAC-SHA256 tokens stored in isolated Vault DB (tesp_vault_db). Decouples employee identity from analytics (BR-DST-002).',
                  },
                  {
                    tier: 'AUTHENTICATED' as AnonymityLevel,
                    title: '👤 Authenticated (Tracked Identity)',
                    desc: 'Direct identity tracking per respondent. Ideal for 360 executive feedback or mandatory training compliance surveys.',
                  },
                  {
                    tier: 'FULLY_ANONYMOUS' as AnonymityLevel,
                    title: '🛡️ Fully Anonymous',
                    desc: 'Zero identity mapping tables or tokens retained. Complete privacy protection for sensitive culture & whistleblowing surveys.',
                  },
                  {
                    tier: 'KIOSK' as AnonymityLevel,
                    title: '🔢 Frontline Kiosk PIN Mode',
                    desc: 'Generates 6-digit numeric PINs for deskless factory and logistics workers without corporate email accounts.',
                  },
                ].map((item) => {
                  const isSelected = anonymityLevel === item.tier;
                  return (
                    <div
                      key={item.tier}
                      onClick={() => setAnonymityLevel(item.tier)}
                      style={{
                        padding: '14px',
                        borderRadius: '8px',
                        border: `2px solid ${isSelected ? '#2563eb' : '#e2e8f0'}`,
                        background: isSelected ? '#eff6ff' : '#ffffff',
                        cursor: 'pointer',
                        transition: 'all 0.15s ease',
                      }}
                    >
                      <div style={{ fontSize: '0.9rem', fontWeight: 700, color: isSelected ? '#1e40af' : '#0f172a', marginBottom: '4px' }}>
                        {item.title}
                      </div>
                      <div style={{ fontSize: '0.78rem', color: '#64748b', lineHeight: 1.4 }}>
                        {item.desc}
                      </div>
                    </div>
                  );
                })}
              </div>
            </div>

            <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '12px', marginTop: '8px' }}>
              {onCancel && (
                <Button variant="secondary" onClick={onCancel}>
                  Cancel
                </Button>
              )}
              <Button
                variant="primary"
                disabled={!campaignTitle.trim() || !selectedSurveyId}
                onClick={() => setStep(2)}
              >
                Next: Target Audience Scope →
              </Button>
            </div>
          </div>
        )}

        {/* Step 2: Target Audience & Scope */}
        {step === 2 && (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <div>
                <h4 style={{ fontSize: '1rem', fontWeight: 700, color: '#0f172a', margin: 0 }}>
                  Target Organization Nodes & Demographic Scope (FR-DST-007)
                </h4>
                <p style={{ fontSize: '0.8rem', color: '#64748b', margin: '2px 0 0 0' }}>
                  Filter campaign recipients by organization sub-tree nodes or demographic employee attributes.
                </p>
              </div>
              <Badge variant="success" style={{ fontSize: '0.9rem', padding: '6px 12px' }}>
                👥 Targeted Headcount: {totalEstimatedRecipients.toLocaleString()} employees
              </Badge>
            </div>

            {/* Organization Node Tree Selector */}
            <div style={{ background: '#f8fafc', padding: '16px', borderRadius: '10px', border: '1px solid #e2e8f0' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '12px' }}>
                <Input
                  placeholder="Search organization departments & nodes..."
                  value={nodeSearchQuery}
                  onChange={(e) => setNodeSearchQuery(e.target.value)}
                  style={{ width: '280px' }}
                />
                <div style={{ display: 'flex', gap: '8px' }}>
                  <Button variant="outline" size="sm" onClick={selectAllNodes}>
                    Select All
                  </Button>
                  <Button variant="outline" size="sm" onClick={clearAllNodes}>
                    Clear All
                  </Button>
                </div>
              </div>

              <div style={{ display: 'flex', flexDirection: 'column', gap: '10px', maxHeight: '220px', overflowY: 'auto' }}>
                {filteredNodes.map((node) => {
                  const isChecked = selectedNodeIds.includes(node.id);
                  return (
                    <label
                      key={node.id}
                      style={{
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'space-between',
                        padding: '8px 12px',
                        borderRadius: '6px',
                        background: isChecked ? '#ffffff' : 'transparent',
                        border: `1px solid ${isChecked ? '#cbd5e1' : 'transparent'}`,
                        cursor: 'pointer',
                      }}
                    >
                      <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                        <input
                          type="checkbox"
                          checked={isChecked}
                          onChange={() => toggleNodeSelection(node.id)}
                          style={{ width: '16px', height: '16px', borderRadius: '4px' }}
                        />
                        <span style={{ fontWeight: 600, fontSize: '0.875rem', color: '#1e293b' }}>{node.name}</span>
                        <code style={{ fontSize: '0.75rem', color: '#64748b', background: '#f1f5f9', padding: '2px 6px', borderRadius: '4px' }}>
                          {node.id}
                        </code>
                      </div>
                      <span style={{ color: '#475569', fontSize: '0.8rem', fontWeight: 600 }}>
                        {node.recipientCount.toLocaleString()} employees
                      </span>
                    </label>
                  );
                })}
              </div>

              {selectedNodeIds.length === 0 && (
                <p style={{ color: '#dc2626', fontSize: '0.8rem', marginTop: '12px', fontWeight: 600 }}>
                  ⚠️ Select at least one organization node to define campaign scope.
                </p>
              )}
            </div>

            {/* Demographic Attribute Filter Tags */}
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
              <div style={{ background: '#ffffff', padding: '14px', borderRadius: '8px', border: '1px solid #e2e8f0' }}>
                <label style={{ display: 'block', fontWeight: 700, fontSize: '0.825rem', color: '#334155', marginBottom: '8px' }}>
                  Filter by Employee Tenure
                </label>
                <div style={{ display: 'flex', flexWrap: 'wrap', gap: '6px' }}>
                  {DEMOGRAPHIC_TENURES.map((t) => {
                    const active = selectedTenures.includes(t);
                    return (
                      <Badge
                        key={t}
                        variant={active ? 'info' : 'neutral'}
                        style={{ cursor: 'pointer', padding: '6px 10px' }}
                        onClick={() => toggleTenure(t)}
                      >
                        {active ? `✓ ${t}` : `+ ${t}`}
                      </Badge>
                    );
                  })}
                </div>
              </div>

              <div style={{ background: '#ffffff', padding: '14px', borderRadius: '8px', border: '1px solid #e2e8f0' }}>
                <label style={{ display: 'block', fontWeight: 700, fontSize: '0.825rem', color: '#334155', marginBottom: '8px' }}>
                  Filter by Employment Type
                </label>
                <div style={{ display: 'flex', flexWrap: 'wrap', gap: '6px' }}>
                  {DEMOGRAPHIC_EMPLOYMENT_TYPES.map((e) => {
                    const active = selectedEmpTypes.includes(e);
                    return (
                      <Badge
                        key={e}
                        variant={active ? 'info' : 'neutral'}
                        style={{ cursor: 'pointer', padding: '6px 10px' }}
                        onClick={() => toggleEmpType(e)}
                      >
                        {active ? `✓ ${e}` : `+ ${e}`}
                      </Badge>
                    );
                  })}
                </div>
              </div>
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
                Next: Distribution Channels & Schedule →
              </Button>
            </div>
          </div>
        )}

        {/* Step 3: Channels & Schedule */}
        {step === 3 && (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
            <div>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '8px' }}>
                <label style={{ fontWeight: 700, fontSize: '0.875rem', color: '#334155' }}>
                  Select Distribution Channels (FR-DST-001 | VR-DST-004)
                </label>
                <Badge variant={selectedChannels.length > 0 ? 'success' : 'danger'}>
                  {selectedChannels.length} Channel{selectedChannels.length !== 1 ? 's' : ''} Selected
                </Badge>
              </div>

              <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: '12px' }}>
                {[
                  { id: 'EMAIL' as DistributionChannel, name: '✉️ Corporate Email', desc: 'AWS SES HTML email with single-use link' },
                  { id: 'SMS' as DistributionChannel, name: '📱 Mobile SMS', desc: 'Twilio SMS with short URL link' },
                  { id: 'TEAMS' as DistributionChannel, name: '💬 Microsoft Teams', desc: 'MS Teams bot notification card' },
                  { id: 'SLACK' as DistributionChannel, name: '⚡ Slack App', desc: 'Direct message via Slack bot' },
                  { id: 'KIOSK_PIN' as DistributionChannel, name: '🔢 Frontline Kiosk PIN', desc: '6-digit PIN for factory kiosk terminals' },
                  { id: 'QR_CODE' as DistributionChannel, name: '📷 QR Code Poster', desc: 'Printable dynamic survey QR code' },
                ].map((ch) => {
                  const isSelected = selectedChannels.includes(ch.id);
                  return (
                    <div
                      key={ch.id}
                      onClick={() => toggleChannelSelection(ch.id)}
                      style={{
                        padding: '12px',
                        borderRadius: '8px',
                        border: `2px solid ${isSelected ? '#2563eb' : '#e2e8f0'}`,
                        background: isSelected ? '#eff6ff' : '#ffffff',
                        cursor: 'pointer',
                        transition: 'all 0.15s ease',
                      }}
                    >
                      <div style={{ fontWeight: 700, fontSize: '0.85rem', color: isSelected ? '#1e40af' : '#0f172a' }}>
                        {ch.name}
                      </div>
                      <div style={{ fontSize: '0.75rem', color: '#64748b', marginTop: '2px' }}>{ch.desc}</div>
                    </div>
                  );
                })}
              </div>

              {selectedChannels.length === 0 && (
                <p style={{ color: '#dc2626', fontSize: '0.8rem', marginTop: '8px', fontWeight: 600 }}>
                  ⚠️ VR-DST-004: Select at least one distribution channel to launch the campaign.
                </p>
              )}
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
              <div>
                <Input
                  label="Campaign Expiration Date (VR-DST-003)"
                  type="datetime-local"
                  value={expirationDate}
                  onChange={(e) => setExpirationDate(e.target.value)}
                />
                {!isExpirationValid ? (
                  <p style={{ color: '#dc2626', fontSize: '0.75rem', marginTop: '4px', fontWeight: 600 }}>
                    ⚠️ Expiration must be at least 24 hours in the future (VR-DST-003).
                  </p>
                ) : (
                  <p style={{ color: '#64748b', fontSize: '0.75rem', marginTop: '4px' }}>
                    Active until: {new Date(expirationDate).toLocaleString()}
                  </p>
                )}
              </div>

              <Input
                label="Reminder Nudge Frequency (Days)"
                type="number"
                value={reminderIntervalDays}
                onChange={(e) => setReminderIntervalDays(parseInt(e.target.value) || 3)}
              />
            </div>

            {/* AI Dispatch Predictor Widget */}
            <Card variant="bordered" padding="16px" style={{ background: '#f8fafc' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <div>
                  <strong style={{ fontSize: '0.9rem', color: '#0f172a' }}>⚡ AI Optimal Dispatch Hour Optimizer</strong>
                  <p style={{ fontSize: '0.8rem', color: '#64748b', margin: '2px 0 0 0' }}>
                    Analyzes historical email/Teams open patterns per department to predict peak engagement hours.
                  </p>
                </div>
                <Button variant="outline" size="sm" onClick={handlePredictAiHour} isLoading={predictMutation.isPending}>
                  Recommend Optimal Hour
                </Button>
              </div>

              {aiPrediction && (
                <div style={{ marginTop: '12px' }}>
                  <Alert type="info" title={`Recommended UTC Hour: ${aiPrediction.recommendedHourUtc}:00 UTC`}>
                    {aiPrediction.rationale || 'Optimal engagement window based on historical response data'} (+{aiPrediction.predictedOpenRateImprovementPercent || 24}% response rate boost)
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
                disabled={selectedChannels.length === 0 || !isExpirationValid}
                onClick={() => setStep(4)}
              >
                Next: Review Pre-Launch Summary →
              </Button>
            </div>
          </div>
        )}

        {/* Step 4: Pre-Launch Summary & Launch */}
        {step === 4 && (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
            <Alert type="success" title="Pre-Launch Configuration Ready">
              Cryptographic single-use survey tokens will be generated and cached in Redis with sub-millisecond validation.
            </Alert>

            {createMutation.isError && (
              <Alert type="warning" title="Campaign Launch Error">
                {(createMutation.error as any)?.message || 'Failed to initialize campaign distribution. Please review fields and retry.'}
              </Alert>
            )}

            <div style={{ background: '#f8fafc', padding: '20px', borderRadius: '10px', border: '1px solid #e2e8f0', display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px', fontSize: '0.875rem' }}>
              <div>
                <span style={{ color: '#64748b', fontSize: '0.75rem', display: 'block' }}>Target Published Survey</span>
                <strong style={{ color: '#0f172a' }}>{selectedSurvey?.title}</strong> ({selectedSurvey?.surveyId})
              </div>

              <div>
                <span style={{ color: '#64748b', fontSize: '0.75rem', display: 'block' }}>Campaign Title</span>
                <strong style={{ color: '#0f172a' }}>{campaignTitle}</strong>
              </div>

              <div>
                <span style={{ color: '#64748b', fontSize: '0.75rem', display: 'block' }}>Anonymity Tier</span>
                <Badge variant="info">{anonymityLevel}</Badge>
              </div>

              <div>
                <span style={{ color: '#64748b', fontSize: '0.75rem', display: 'block' }}>Target Audience Headcount</span>
                <strong style={{ color: '#16a34a', fontSize: '1rem' }}>{totalEstimatedRecipients.toLocaleString()} employees</strong>
              </div>

              <div>
                <span style={{ color: '#64748b', fontSize: '0.75rem', display: 'block' }}>Active Channels</span>
                <div style={{ display: 'flex', gap: '4px', flexWrap: 'wrap', marginTop: '2px' }}>
                  {selectedChannels.map((c) => (
                    <Badge key={c} variant="neutral">{c}</Badge>
                  ))}
                </div>
              </div>

              <div>
                <span style={{ color: '#64748b', fontSize: '0.75rem', display: 'block' }}>Scheduled Expiration</span>
                <strong style={{ color: '#0f172a' }}>{new Date(expirationDate).toLocaleString()}</strong>
              </div>
            </div>

            <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: '8px' }}>
              <Button variant="secondary" onClick={() => setStep(3)}>
                ← Back
              </Button>
              <Button
                variant="primary"
                isLoading={createMutation.isPending}
                onClick={handleLaunchCampaign}
                style={{ padding: '10px 24px', fontSize: '0.95rem' }}
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

