import React, { useState } from 'react';
import { PageHeader } from '../../../components/ui/PageHeader';
import { Tabs } from '../../../components/ui/Tabs';
import { Card } from '../../../components/ui/Card';
import { OrgHierarchyManager } from '../../../components/hierarchy/OrgHierarchyManager';
import { useTenant } from '../../../context/TenantContext';
import { useAnomaliesQuery } from '../api/useOrgQueries';
import { Alert } from '../../../components/ui/Alert';
import { Badge } from '../../../components/ui/Badge';
import { Skeleton } from '../../../components/ui/Skeleton';

export const OrgHierarchyPage: React.FC = () => {
  const { activeProject } = useTenant();
  const [activeTab, setActiveTab] = useState<string>('tree');

  const { data: anomalyReport, isLoading: isAnomalyLoading } = useAnomaliesQuery(activeProject?.projectId);

  const tabs = [
    { id: 'tree', label: 'Org Tree Visualizer' },
    {
      id: 'anomalies',
      label: `AI Anomaly Auditor ${anomalyReport?.totalAnomaliesDetected ? `(${anomalyReport.totalAnomaliesDetected})` : ''}`,
    },
  ];

  return (
    <div>
      <PageHeader
        title="Organizational Hierarchy & Reporting Lines"
        subtitle={`Materialized path tree structure, lineage calculation, and AI structural anomaly inspection for ${activeProject?.projectId}`}
      />

      <Tabs tabs={tabs} activeTab={activeTab} onChange={setActiveTab} />

      <div style={{ marginTop: '20px' }}>
        {activeTab === 'tree' && (
          <OrgHierarchyManager projectId={activeProject?.projectId || 'PRJ-99201'} />
        )}

        {activeTab === 'anomalies' && (
          <Card variant="bordered" padding="24px">
            <div style={{ marginBottom: '20px' }}>
              <h3 style={{ fontSize: '1.2rem', fontWeight: 800, color: '#0f172a', margin: 0 }}>
                AI Structural Hierarchy Anomaly Auditor
              </h3>
              <p style={{ fontSize: '0.85rem', color: '#64748b', margin: '4px 0 0 0' }}>
                Scans organizational tree for structural anti-patterns, excessive depth (&gt; 7 levels), orphan subtrees, and single-child chains.
              </p>
            </div>

            {isAnomalyLoading ? (
              <Skeleton height="200px" borderRadius="12px" />
            ) : !anomalyReport || anomalyReport.totalAnomaliesDetected === 0 ? (
              <Alert type="success" title="Hierarchy Health Optimal">
                No structural anomalies or deep nesting issues were detected in this organization workspace.
              </Alert>
            ) : (
              <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
                <div style={{ display: 'flex', gap: '16px' }}>
                  <Badge variant="warning">Inspected: {anomalyReport.totalNodesInspected} Nodes</Badge>
                  <Badge variant="danger">Anomalies Detected: {anomalyReport.totalAnomaliesDetected}</Badge>
                </div>

                {anomalyReport.anomalies.map((detail, idx) => (
                  <div
                    key={idx}
                    style={{
                      padding: '16px',
                      borderRadius: '10px',
                      background: '#fffbeb',
                      border: '1px solid #fde68a',
                      display: 'flex',
                      flexDirection: 'column',
                      gap: '8px',
                    }}
                  >
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                      <span style={{ fontWeight: 700, color: '#92400e', fontSize: '0.95rem' }}>
                        [{detail.anomalyType}] Node <code style={{ fontWeight: 700 }}>{detail.nodeId}</code>
                      </span>
                      <Badge variant={detail.severity === 'HIGH' || detail.severity === 'CRITICAL' ? 'danger' : 'warning'}>
                        Severity: {detail.severity}
                      </Badge>
                    </div>

                    <p style={{ fontSize: '0.875rem', color: '#78350f', margin: 0 }}>
                      {detail.message}
                    </p>

                    <div style={{ fontSize: '0.825rem', color: '#92400e', background: '#fef3c7', padding: '8px 12px', borderRadius: '6px' }}>
                      <strong>AI Recommendation:</strong> {detail.recommendation}
                    </div>
                  </div>
                ))}
              </div>
            )}
          </Card>
        )}
      </div>
    </div>
  );
};
