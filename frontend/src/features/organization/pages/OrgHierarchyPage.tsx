import React, { useState } from 'react';
import { PageHeader } from '../../../components/ui/PageHeader';
import { Tabs } from '../../../components/ui/Tabs';
import { Card } from '../../../components/ui/Card';
import { OrgHierarchyManager } from '../../../components/hierarchy/OrgHierarchyManager';
import { useTenant } from '../../../context/TenantContext';
import { useAnomaliesQuery } from '../api/useOrgQueries';
import { Alert } from '../../../components/ui/Alert';
import { Badge } from '../../../components/ui/Badge';
import { Button } from '../../../components/ui/Button';
import { Skeleton } from '../../../components/ui/Skeleton';

export const OrgHierarchyPage: React.FC = () => {
  const { activeProject, activeProjectId } = useTenant();
  const [activeTab, setActiveTab] = useState<string>('tree');
  const [targetFocusNodeId, setTargetFocusNodeId] = useState<string>('');

  const currentProjectId = activeProject?.projectId || activeProjectId || 'PRJ-99201';
  const { data: anomalyReport, isLoading: isAnomalyLoading, refetch: refetchAnomalies } = useAnomaliesQuery(currentProjectId);

  const tabs = [
    { id: 'tree', label: 'Org Tree Visualizer' },
    {
      id: 'anomalies',
      label: `AI Anomaly Auditor ${anomalyReport?.totalAnomaliesDetected ? `(${anomalyReport.totalAnomaliesDetected})` : ''}`,
    },
  ];

  const healthScore = anomalyReport
    ? Math.max(0, 100 - anomalyReport.totalAnomaliesDetected * 10)
    : 100;

  const criticalCount = anomalyReport
    ? anomalyReport.anomalies.filter((a) => a.severity === 'HIGH' || a.severity === 'CRITICAL').length
    : 0;

  return (
    <div>
      <PageHeader
        title="Organizational Hierarchy & Reporting Lines"
        subtitle={`Materialized path tree structure, lineage calculation, and AI structural anomaly inspection for ${currentProjectId}`}
      />

      <Tabs tabs={tabs} activeTab={activeTab} onChange={setActiveTab} />

      <div style={{ marginTop: '20px' }}>
        {activeTab === 'tree' && (
          <OrgHierarchyManager projectId={currentProjectId} initialSelectedNodeId={targetFocusNodeId} />
        )}

        {activeTab === 'anomalies' && (
          <Card variant="bordered" padding="24px">
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px', flexWrap: 'wrap', gap: '12px' }}>
              <div>
                <h3 style={{ fontSize: '1.2rem', fontWeight: 800, color: '#0f172a', margin: 0 }}>
                  AI Structural Hierarchy Anomaly Auditor
                </h3>
                <p style={{ fontSize: '0.85rem', color: '#64748b', margin: '4px 0 0 0' }}>
                  Scans organizational tree for structural anti-patterns, excessive depth (&gt; 7 levels), orphan subtrees, and single-child chains.
                </p>
              </div>
              <Button variant="secondary" size="sm" onClick={() => refetchAnomalies()}>
                🔄 Re-run Structural Scan
              </Button>
            </div>

            {isAnomalyLoading ? (
              <Skeleton height="200px" borderRadius="12px" />
            ) : !anomalyReport || anomalyReport.totalAnomaliesDetected === 0 ? (
              <Alert type="success" title="Hierarchy Health Optimal (100% Score)">
                No structural anomalies or deep nesting issues were detected in this organization workspace.
              </Alert>
            ) : (
              <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
                {/* Summary KPI Health Banner */}
                <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: '16px' }}>
                  <div style={{ background: '#f8fafc', padding: '14px 18px', borderRadius: '10px', border: '1px solid #e2e8f0' }}>
                    <div style={{ fontSize: '0.75rem', fontWeight: 700, color: '#64748b', textTransform: 'uppercase' }}>Tree Health Score</div>
                    <div style={{ fontSize: '1.5rem', fontWeight: 800, color: healthScore > 80 ? '#16a34a' : '#d97706' }}>
                      {healthScore}%
                    </div>
                  </div>
                  <div style={{ background: '#f8fafc', padding: '14px 18px', borderRadius: '10px', border: '1px solid #e2e8f0' }}>
                    <div style={{ fontSize: '0.75rem', fontWeight: 700, color: '#64748b', textTransform: 'uppercase' }}>Inspected Nodes</div>
                    <div style={{ fontSize: '1.5rem', fontWeight: 800, color: '#0f172a' }}>
                      {anomalyReport.totalNodesInspected}
                    </div>
                  </div>
                  <div style={{ background: '#f8fafc', padding: '14px 18px', borderRadius: '10px', border: '1px solid #e2e8f0' }}>
                    <div style={{ fontSize: '0.75rem', fontWeight: 700, color: '#64748b', textTransform: 'uppercase' }}>Total Anomalies</div>
                    <div style={{ fontSize: '1.5rem', fontWeight: 800, color: '#dc2626' }}>
                      {anomalyReport.totalAnomaliesDetected}
                    </div>
                  </div>
                  <div style={{ background: '#f8fafc', padding: '14px 18px', borderRadius: '10px', border: '1px solid #e2e8f0' }}>
                    <div style={{ fontSize: '0.75rem', fontWeight: 700, color: '#64748b', textTransform: 'uppercase' }}>Critical / High Issues</div>
                    <div style={{ fontSize: '1.5rem', fontWeight: 800, color: criticalCount > 0 ? '#dc2626' : '#64748b' }}>
                      {criticalCount}
                    </div>
                  </div>
                </div>

                {/* Detailed Anomaly Cards */}
                {anomalyReport.anomalies.map((detail, idx) => (
                  <div
                    key={idx}
                    style={{
                      padding: '18px',
                      borderRadius: '10px',
                      background: '#fffbeb',
                      border: '1px solid #fde68a',
                      display: 'flex',
                      flexDirection: 'column',
                      gap: '10px',
                    }}
                  >
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '8px' }}>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                        <span style={{ fontWeight: 800, color: '#92400e', fontSize: '0.95rem' }}>
                          [{detail.anomalyType}]
                        </span>
                        <span style={{ fontSize: '0.9rem', color: '#78350f' }}>Target Node:</span>
                        <code style={{ fontWeight: 700, background: '#fef3c7', padding: '2px 6px', borderRadius: '4px', color: '#92400e' }}>
                          {detail.nodeId}
                        </code>
                      </div>
                      <div style={{ display: 'flex', gap: '8px', alignItems: 'center' }}>
                        <Badge variant={detail.severity === 'HIGH' || detail.severity === 'CRITICAL' ? 'danger' : 'warning'}>
                          Severity: {detail.severity}
                        </Badge>
                        <Button
                          variant="secondary"
                          size="sm"
                          onClick={() => {
                            setTargetFocusNodeId(detail.nodeId);
                            setActiveTab('tree');
                          }}
                        >
                          🔍 Inspect Node in Tree
                        </Button>
                      </div>
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
