import React, { useEffect, useState } from 'react';
import { CampaignResponse, CampaignStatus } from '../../types/distribution';
import { getCampaign, updateCampaignStatus } from '../../services/distributionApi';

interface Props {
  projectId: string;
  campaignId: string;
}

export const LiveCampaignMonitor: React.FC<Props> = ({ projectId, campaignId }) => {
  const [campaign, setCampaign] = useState<CampaignResponse | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [errorMsg, setErrorMsg] = useState<string | null>(null);
  const [isUpdatingStatus, setIsUpdatingStatus] = useState<boolean>(false);

  const fetchMetrics = async () => {
    try {
      const data = await getCampaign(projectId, campaignId);
      setCampaign(data);
      setErrorMsg(null);
    } catch (err: any) {
      setErrorMsg(err.message || 'Failed to poll campaign delivery metrics');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchMetrics();
    const intervalId = setInterval(fetchMetrics, 5000);
    return () => clearInterval(intervalId);
  }, [projectId, campaignId]);

  const handleStatusChange = async (newStatus: CampaignStatus) => {
    setIsUpdatingStatus(true);
    try {
      const updated = await updateCampaignStatus(projectId, campaignId, newStatus);
      setCampaign(updated);
    } catch (err: any) {
      setErrorMsg(err.message || 'Failed to update campaign status');
    } finally {
      setIsUpdatingStatus(false);
    }
  };

  if (loading) {
    return (
      <div style={{ padding: '40px', textAlign: 'center', color: '#64748b', fontFamily: 'system-ui, sans-serif' }}>
        🔄 Loading live campaign delivery metrics...
      </div>
    );
  }

  if (!campaign) {
    return (
      <div style={{ padding: '24px', background: '#fef2f2', color: '#991b1b', borderRadius: '8px', fontFamily: 'system-ui, sans-serif' }}>
        ⚠️ {errorMsg || 'Campaign metrics unavailable'}
      </div>
    );
  }

  const { metrics, status } = campaign;
  const deliveryRate = metrics.sent > 0 ? (metrics.delivered / metrics.sent) * 100 : 0;
  const openRate = metrics.delivered > 0 ? (metrics.opened / metrics.delivered) * 100 : 0;
  const completionRate = metrics.totalTargeted > 0 ? (metrics.completed / metrics.totalTargeted) * 100 : 0;

  const statusColors: Record<CampaignStatus, { bg: string; text: string }> = {
    DRAFT: { bg: '#f1f5f9', text: '#475569' },
    SCHEDULED: { bg: '#fef3c7', text: '#92400e' },
    ACTIVE: { bg: '#dcfce7', text: '#166534' },
    PAUSED: { bg: '#ffedd5', text: '#9a3412' },
    COMPLETED: { bg: '#e0f2fe', text: '#075985' },
    EXPIRED: { bg: '#f3e8ff', text: '#6b21a8' },
    CANCELLED: { bg: '#fee2e2', text: '#991b1b' },
  };

  return (
    <div style={{ maxWidth: '1000px', margin: '0 auto', fontFamily: 'system-ui, -apple-system, sans-serif', color: '#1e293b' }}>
      {/* Header Bar */}
      <div style={{ background: '#0f172a', borderRadius: '12px', padding: '24px', color: '#fff', marginBottom: '24px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '12px', marginBottom: '6px' }}>
            <h2 style={{ margin: 0, fontSize: '22px', fontWeight: '700' }}>📊 {campaign.title}</h2>
            <span
              style={{
                background: statusColors[status]?.bg || '#fff',
                color: statusColors[status]?.text || '#000',
                padding: '4px 12px',
                borderRadius: '20px',
                fontWeight: '700',
                fontSize: '12px',
                textTransform: 'uppercase',
              }}
            >
              ● {status}
            </span>
          </div>
          <p style={{ margin: 0, color: '#94a3b8', fontSize: '13px' }}>
            Campaign ID: <code>{campaign.campaignId}</code> | Anonymity: <strong>{campaign.anonymityLevel}</strong> | Auto-refreshing (5s)
          </p>
        </div>

        {/* Quick Campaign State Machine Transition Controls */}
        <div style={{ display: 'flex', gap: '10px' }}>
          {status === 'ACTIVE' && (
            <button
              onClick={() => handleStatusChange('PAUSED')}
              disabled={isUpdatingStatus}
              style={{ padding: '8px 16px', borderRadius: '8px', background: '#ea580c', color: '#fff', border: 'none', fontWeight: '600', cursor: 'pointer', fontSize: '13px' }}
            >
              ⏸ Pause Campaign
            </button>
          )}

          {status === 'PAUSED' && (
            <button
              onClick={() => handleStatusChange('ACTIVE')}
              disabled={isUpdatingStatus}
              style={{ padding: '8px 16px', borderRadius: '8px', background: '#16a34a', color: '#fff', border: 'none', fontWeight: '600', cursor: 'pointer', fontSize: '13px' }}
            >
              ▶ Resume Campaign
            </button>
          )}

          {(status === 'ACTIVE' || status === 'PAUSED') && (
            <button
              onClick={() => handleStatusChange('COMPLETED')}
              disabled={isUpdatingStatus}
              style={{ padding: '8px 16px', borderRadius: '8px', background: '#0284c7', color: '#fff', border: 'none', fontWeight: '600', cursor: 'pointer', fontSize: '13px' }}
            >
              ✅ Complete Campaign
            </button>
          )}
        </div>
      </div>

      {errorMsg && (
        <div style={{ background: '#fef2f2', border: '1px solid #fca5a5', color: '#991b1b', padding: '12px 16px', borderRadius: '8px', marginBottom: '20px', fontSize: '14px' }}>
          ⚠️ {errorMsg}
        </div>
      )}

      {/* Real-Time Delivery Metrics Grid */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(6, 1fr)', gap: '14px', marginBottom: '28px' }}>
        {[
          { label: 'Targeted', value: metrics.totalTargeted, color: '#475569', icon: '👥' },
          { label: 'Sent', value: metrics.sent, color: '#2563eb', icon: '📤' },
          { label: 'Delivered', value: metrics.delivered, color: '#0d9488', icon: '📬' },
          { label: 'Opened', value: metrics.opened, color: '#7c3aed', icon: '👀' },
          { label: 'Completed', value: metrics.completed, color: '#16a34a', icon: '🎉' },
          { label: 'Bounced', value: metrics.bounced, color: '#dc2626', icon: '⚠️' },
        ].map((m) => (
          <div key={m.label} style={{ background: '#fff', border: '1px solid #e2e8f0', borderRadius: '10px', padding: '16px', textAlign: 'center' }}>
            <div style={{ fontSize: '20px', marginBottom: '4px' }}>{m.icon}</div>
            <div style={{ fontSize: '22px', fontWeight: '800', color: m.color }}>{m.value.toLocaleString()}</div>
            <div style={{ fontSize: '12px', color: '#64748b', fontWeight: '600', marginTop: '2px' }}>{m.label}</div>
          </div>
        ))}
      </div>

      {/* Visual Conversion Funnel Rates */}
      <div style={{ background: '#fff', border: '1px solid #e2e8f0', borderRadius: '12px', padding: '24px' }}>
        <h3 style={{ margin: '0 0 20px 0', fontSize: '16px', fontWeight: '700' }}>📈 Delivery & Participation Response Funnel</h3>

        <div style={{ display: 'flex', flexDirection: 'column', gap: '18px' }}>
          {/* Delivery Rate */}
          <div>
            <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '13px', fontWeight: '600', marginBottom: '6px' }}>
              <span>Delivery Success Rate</span>
              <span>{deliveryRate.toFixed(1)}%</span>
            </div>
            <div style={{ width: '100%', height: '10px', background: '#f1f5f9', borderRadius: '5px', overflow: 'hidden' }}>
              <div style={{ width: `${Math.min(deliveryRate, 100)}%`, height: '100%', background: '#0d9488', transition: 'width 0.5s ease' }} />
            </div>
          </div>

          {/* Open Rate */}
          <div>
            <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '13px', fontWeight: '600', marginBottom: '6px' }}>
              <span>Recipient Open Rate</span>
              <span>{openRate.toFixed(1)}%</span>
            </div>
            <div style={{ width: '100%', height: '10px', background: '#f1f5f9', borderRadius: '5px', overflow: 'hidden' }}>
              <div style={{ width: `${Math.min(openRate, 100)}%`, height: '100%', background: '#7c3aed', transition: 'width 0.5s ease' }} />
            </div>
          </div>

          {/* Response Completion Rate */}
          <div>
            <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '13px', fontWeight: '600', marginBottom: '6px' }}>
              <span>Survey Completion Rate</span>
              <span>{completionRate.toFixed(1)}%</span>
            </div>
            <div style={{ width: '100%', height: '10px', background: '#f1f5f9', borderRadius: '5px', overflow: 'hidden' }}>
              <div style={{ width: `${Math.min(completionRate, 100)}%`, height: '100%', background: '#16a34a', transition: 'width 0.5s ease' }} />
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
