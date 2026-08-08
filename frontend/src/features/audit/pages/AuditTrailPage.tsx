import React, { useState } from 'react';
import { PageHeader } from '../../../components/ui/PageHeader';
import { Card } from '../../../components/ui/Card';
import { Badge } from '../../../components/ui/Badge';
import { Skeleton } from '../../../components/ui/Skeleton';
import { ErrorState } from '../../../components/ui/ErrorState';
import { useTenant } from '../../../context/TenantContext';
import { useAuditLogsQuery } from '../api/useAuditQueries';
import { AuditLogResponse } from '../../../types/audit';

export const AuditTrailPage: React.FC = () => {
  const { activeProject } = useTenant();
  const [actorIdFilter, setActorIdFilter] = useState<string>('');
  const [actionFilter, setActionFilter] = useState<string>('');

  const projectId = activeProject?.projectId || 'PRJ-99201';

  const {
    data: logsData,
    isLoading,
    isError,
    refetch,
  } = useAuditLogsQuery(projectId, actorIdFilter || undefined, actionFilter || undefined);

  // Demo fallback logs
  const fallbackLogs: AuditLogResponse[] = [
    {
      auditId: 'AUD-880192',
      projectId,
      actorId: 'USR-SUPER-ADMIN',
      userRole: 'SUPER_ADMIN',
      action: 'SURVEY_PUBLISHED',
      resourceId: 'SRV-5001',
      timestamp: new Date(Date.now() - 1800000).toISOString(),
      details: { version: '1', status: 'PUBLISHED' },
    },
    {
      auditId: 'AUD-880191',
      projectId,
      actorId: 'USR-HR-DIRECTOR',
      userRole: 'HR_MANAGER',
      action: 'ACTION_PLAN_APPROVED',
      resourceId: 'ACT-9901',
      timestamp: new Date(Date.now() - 3600000).toISOString(),
      details: { status: 'APPROVED', actor: 'USR-HR-DIRECTOR' },
    },
    {
      auditId: 'AUD-880190',
      projectId,
      actorId: 'USR-HR-DIRECTOR',
      userRole: 'HR_MANAGER',
      action: 'TOKEN_BATCH_GENERATED',
      resourceId: 'CMP-101',
      timestamp: new Date(Date.now() - 7200000).toISOString(),
      details: { count: '100', anonymity: 'SEMI_ANONYMOUS' },
    },
  ];

  const logs = logsData && logsData.length > 0 ? logsData : fallbackLogs;

  return (
    <div>
      <PageHeader
        title="Immutable Platform Audit Trail"
        subtitle={`Write-once, append-only security and compliance audit log inspector for ${projectId}`}
      />

      {/* Filter Controls */}
      <Card variant="bordered" padding="20px" style={{ marginTop: '20px' }}>
        <div style={{ display: 'flex', gap: '16px', alignItems: 'center' }}>
          <div>
            <label style={{ display: 'block', fontSize: '0.75rem', fontWeight: 700, textTransform: 'uppercase', color: '#475569', marginBottom: '4px' }}>
              Filter by Actor ID
            </label>
            <input
              type="text"
              placeholder="e.g. USR-SUPER-ADMIN"
              value={actorIdFilter}
              onChange={(e) => setActorIdFilter(e.target.value)}
              style={{ padding: '8px 12px', borderRadius: '8px', border: '1px solid #cbd5e1', fontSize: '0.85rem' }}
            />
          </div>

          <div>
            <label style={{ display: 'block', fontSize: '0.75rem', fontWeight: 700, textTransform: 'uppercase', color: '#475569', marginBottom: '4px' }}>
              Filter by Action Type
            </label>
            <input
              type="text"
              placeholder="e.g. SURVEY_PUBLISHED"
              value={actionFilter}
              onChange={(e) => setActionFilter(e.target.value)}
              style={{ padding: '8px 12px', borderRadius: '8px', border: '1px solid #cbd5e1', fontSize: '0.85rem' }}
            />
          </div>
        </div>
      </Card>

      {/* Audit Log Table */}
      <div style={{ marginTop: '20px' }}>
        {isLoading ? (
          <Card variant="bordered" padding="24px">
            <Skeleton height="350px" borderRadius="12px" />
          </Card>
        ) : isError ? (
          <Card variant="bordered" padding="24px">
            <ErrorState
              title="Audit Logs Unavailable"
              message="Could not load audit log history from audit-service."
              onRetry={refetch}
            />
          </Card>
        ) : (
          <Card variant="bordered" padding="24px">
            <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '0.85rem' }}>
              <thead>
                <tr style={{ borderBottom: '2px solid #e2e8f0', textAlign: 'left', color: '#475569' }}>
                  <th style={{ padding: '12px' }}>Audit ID</th>
                  <th style={{ padding: '12px' }}>Timestamp</th>
                  <th style={{ padding: '12px' }}>Actor</th>
                  <th style={{ padding: '12px' }}>Role</th>
                  <th style={{ padding: '12px' }}>Action</th>
                  <th style={{ padding: '12px' }}>Target Resource</th>
                </tr>
              </thead>
              <tbody>
                {logs.map((log) => (
                  <tr key={log.auditId} style={{ borderBottom: '1px solid #f1f5f9' }}>
                    <td style={{ padding: '12px' }}>
                      <code style={{ color: '#2563eb', fontWeight: 700 }}>{log.auditId}</code>
                    </td>
                    <td style={{ padding: '12px', color: '#64748b' }}>
                      {new Date(log.timestamp).toLocaleString()}
                    </td>
                    <td style={{ padding: '12px', fontWeight: 700, color: '#0f172a' }}>
                      {log.actorId}
                    </td>
                    <td style={{ padding: '12px' }}>
                      <Badge variant="neutral">{log.userRole}</Badge>
                    </td>
                    <td style={{ padding: '12px' }}>
                      <Badge variant="info">{log.action}</Badge>
                    </td>
                    <td style={{ padding: '12px', fontFamily: 'monospace', color: '#475569' }}>
                      {log.resourceId || '—'}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </Card>
        )}
      </div>
    </div>
  );
};
