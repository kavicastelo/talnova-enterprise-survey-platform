import React, { useState } from 'react';
import { PageHeader } from '../../../components/ui/PageHeader';
import { Card } from '../../../components/ui/Card';
import { Badge } from '../../../components/ui/Badge';
import { Button } from '../../../components/ui/Button';
import { useTenant } from '../../../context/TenantContext';
import { NotificationChannel, NotificationResponse } from '../../../types/notification';
import { useSendNotificationMutation } from '../api/useNotificationQueries';

export const NotificationLogsPage: React.FC = () => {
  const { activeProject } = useTenant();
  const [recipient, setRecipient] = useState<string>('employee@enterprise.com');
  const [channel, setChannel] = useState<NotificationChannel>('EMAIL');
  const [subject, setSubject] = useState<string>('Q3 Employee Engagement Survey Invitation');
  const [messageBody, setMessageBody] = useState<string>('Please complete your annual employee engagement survey.');

  const sendMutation = useSendNotificationMutation();

  const projectId = activeProject?.projectId || 'PRJ-99201';

  // Demo dispatch history
  const [dispatchHistory, setDispatchHistory] = useState<NotificationResponse[]>([
    {
      notificationId: 'NTF-9901',
      projectId,
      recipient: 'john.doe@enterprise.com',
      channel: 'EMAIL',
      status: 'DELIVERED',
      sentAt: new Date(Date.now() - 3600000).toISOString(),
    },
    {
      notificationId: 'NTF-9902',
      projectId,
      recipient: '+1-555-0192',
      channel: 'SMS',
      status: 'DELIVERED',
      sentAt: new Date(Date.now() - 7200000).toISOString(),
    },
    {
      notificationId: 'NTF-9903',
      projectId,
      recipient: 'teams-channel-hr',
      channel: 'TEAMS',
      status: 'SENT',
      sentAt: new Date(Date.now() - 14400000).toISOString(),
    },
  ]);

  const handleSend = (e: React.FormEvent) => {
    e.preventDefault();
    sendMutation.mutate(
      {
        projectId,
        recipient,
        channel,
        subject,
        messageBody,
      },
      {
        onSuccess: (data) => {
          setDispatchHistory((prev) => [data, ...prev]);
        },
      }
    );
  };

  return (
    <div>
      <PageHeader
        title="Multi-Channel Notification Dispatcher & Logs"
        subtitle={`Dispatch and monitor Kafka event notifications (Email, SMS, Teams, Slack, Kiosk PIN) for ${projectId}`}
      />

      <div style={{ marginTop: '20px', display: 'grid', gridTemplateColumns: '1fr 2fr', gap: '20px' }}>
        {/* Test Dispatch Form Card */}
        <Card variant="bordered" padding="24px">
          <h3 style={{ fontSize: '1.1rem', fontWeight: 800, color: '#0f172a', margin: '0 0 16px 0' }}>
            Dispatch Notification
          </h3>

          <form onSubmit={handleSend} style={{ display: 'flex', flexDirection: 'column', gap: '14px' }}>
            <div>
              <label style={{ display: 'block', fontSize: '0.75rem', fontWeight: 700, textTransform: 'uppercase', color: '#475569', marginBottom: '4px' }}>
                Channel
              </label>
              <select
                value={channel}
                onChange={(e) => setChannel(e.target.value as NotificationChannel)}
                style={{ width: '100%', padding: '8px 12px', borderRadius: '8px', border: '1px solid #cbd5e1', fontSize: '0.85rem' }}
              >
                <option value="EMAIL">Email Dispatcher</option>
                <option value="SMS">SMS Gateway</option>
                <option value="TEAMS">Microsoft Teams Webhook</option>
                <option value="SLACK">Slack Integration</option>
                <option value="KIOSK_PIN">Kiosk Numeric PIN</option>
              </select>
            </div>

            <div>
              <label style={{ display: 'block', fontSize: '0.75rem', fontWeight: 700, textTransform: 'uppercase', color: '#475569', marginBottom: '4px' }}>
                Recipient Address / Identifier
              </label>
              <input
                type="text"
                required
                value={recipient}
                onChange={(e) => setRecipient(e.target.value)}
                style={{ width: '100%', padding: '8px 12px', borderRadius: '8px', border: '1px solid #cbd5e1', fontSize: '0.85rem' }}
              />
            </div>

            {channel === 'EMAIL' && (
              <div>
                <label style={{ display: 'block', fontSize: '0.75rem', fontWeight: 700, textTransform: 'uppercase', color: '#475569', marginBottom: '4px' }}>
                  Email Subject
                </label>
                <input
                  type="text"
                  required
                  value={subject}
                  onChange={(e) => setSubject(e.target.value)}
                  style={{ width: '100%', padding: '8px 12px', borderRadius: '8px', border: '1px solid #cbd5e1', fontSize: '0.85rem' }}
                />
              </div>
            )}

            <div>
              <label style={{ display: 'block', fontSize: '0.75rem', fontWeight: 700, textTransform: 'uppercase', color: '#475569', marginBottom: '4px' }}>
                Message Payload
              </label>
              <textarea
                rows={3}
                required
                value={messageBody}
                onChange={(e) => setMessageBody(e.target.value)}
                style={{ width: '100%', padding: '8px 12px', borderRadius: '8px', border: '1px solid #cbd5e1', fontSize: '0.85rem' }}
              />
            </div>

            <Button variant="primary" type="submit" isLoading={sendMutation.isPending}>
              🚀 Dispatch Notification (202 Accepted)
            </Button>
          </form>
        </Card>

        {/* Live Delivery Status Log */}
        <Card variant="bordered" padding="24px">
          <h3 style={{ fontSize: '1.1rem', fontWeight: 800, color: '#0f172a', margin: '0 0 16px 0' }}>
            Multi-Channel Dispatch Logs
          </h3>

          <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
            {dispatchHistory.map((item) => (
              <div
                key={item.notificationId}
                style={{
                  background: '#ffffff',
                  border: '1px solid #e2e8f0',
                  borderRadius: '10px',
                  padding: '16px',
                  display: 'flex',
                  justifyContent: 'space-between',
                  alignItems: 'center',
                }}
              >
                <div>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                    <code style={{ fontSize: '0.85rem', fontWeight: 800, color: '#0f172a' }}>
                      {item.notificationId}
                    </code>
                    <Badge variant="neutral">{item.channel}</Badge>
                    <Badge
                      variant={
                        item.status === 'DELIVERED'
                          ? 'success'
                          : item.status === 'SENT'
                          ? 'info'
                          : item.status === 'QUEUED'
                          ? 'warning'
                          : 'danger'
                      }
                      dot
                    >
                      {item.status}
                    </Badge>
                  </div>
                  <div style={{ fontSize: '0.8rem', color: '#64748b', marginTop: '4px' }}>
                    Recipient: <strong>{item.recipient}</strong>
                    {item.sentAt && ` | Sent: ${new Date(item.sentAt).toLocaleTimeString()}`}
                  </div>
                </div>
              </div>
            ))}
          </div>
        </Card>
      </div>
    </div>
  );
};
