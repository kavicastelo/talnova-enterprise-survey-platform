import React from 'react';
import { PageHeader } from '../../components/ui/PageHeader';
import { StatCard } from '../../components/ui/StatCard';
import { Card } from '../../components/ui/Card';
import { Badge } from '../../components/ui/Badge';
import { Button } from '../../components/ui/Button';
import { useTenant } from '../../context/TenantContext';
import { useNavigate } from 'react-router-dom';

export const ExecutiveDashboardPage: React.FC = () => {
  const { activeProject } = useTenant();
  const navigate = useNavigate();

  return (
    <div>
      <PageHeader
        title="Executive Overview Dashboard"
        subtitle={`Real-time platform metrics, campaign performance, and AI insights for tenant ${activeProject?.projectId}`}
        badge={<Badge variant="success" dot>Backend Gateway Certified</Badge>}
        actions={
          <Button variant="primary" onClick={() => navigate('/surveys')}>
            Launch New Survey
          </Button>
        }
      />

      {/* KPI Cards Grid */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(240px, 1fr))', gap: '16px', marginBottom: '24px' }}>
        <StatCard
          title="Total Active Roster"
          value="1,420"
          subtitle="Syncing with HRIS"
          trend={{ value: '12%', isPositive: true }}
          icon="👥"
        />
        <StatCard
          title="Active Campaigns"
          value="4"
          subtitle="2 scheduled dispatches"
          trend={{ value: '8%', isPositive: true }}
          icon="📋"
        />
        <StatCard
          title="Global Response Rate"
          value="84.2%"
          subtitle="Above industry benchmark"
          trend={{ value: '3.4%', isPositive: true }}
          icon="📈"
        />
        <StatCard
          title="AI Sentiment Index"
          value="7.8 / 10"
          subtitle="Positive trajectory"
          trend={{ value: '0.6', isPositive: true }}
          icon="✨"
        />
      </div>

      {/* Domain Quick Access Cards */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(320px, 1fr))', gap: '20px' }}>
        <Card title="Org Hierarchy & Census" subtitle="Explore department trees and node anomalies" action={<Button variant="ghost" size="sm" onClick={() => navigate('/organization')}>Explore →</Button>}>
          <p style={{ fontSize: '0.875rem', color: '#64748b', margin: 0 }}>
            Inspect organizational units, parent-child lineage, and detect orphan nodes or cyclic dependencies in real time.
          </p>
        </Card>

        <Card title="Analytics Engine & Heatmap" subtitle="Differential privacy sample size suppression (N < 5)" action={<Button variant="ghost" size="sm" onClick={() => navigate('/analytics')}>View Heatmap →</Button>}>
          <p style={{ fontSize: '0.875rem', color: '#64748b', margin: 0 }}>
            Query organizational scorecards and multi-demographic heatmaps protected by differential privacy algorithms.
          </p>
        </Card>
      </div>
    </div>
  );
};
