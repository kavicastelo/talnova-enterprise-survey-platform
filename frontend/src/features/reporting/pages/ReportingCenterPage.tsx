import React, { useState } from 'react';
import { PageHeader } from '../../../components/ui/PageHeader';
import { Button } from '../../../components/ui/Button';
import { Card } from '../../../components/ui/Card';
import { ReportExportModal } from '../../../components/reporting/ReportExportModal';
import { ReportJobTrackerCard } from '../../../components/reporting/ReportJobTrackerCard';
import { useTenant } from '../../../context/TenantContext';

export const ReportingCenterPage: React.FC = () => {
  const { activeProject } = useTenant();
  const [isExportModalOpen, setIsExportModalOpen] = useState<boolean>(false);
  const [activeJobIds, setActiveJobIds] = useState<string[]>(['JOB-88201', 'JOB-88202']);

  const projectId = activeProject?.projectId || 'PRJ-99201';
  const campaignId = 'CMP-101';

  const handleJobSubmitted = (jobId: string) => {
    setActiveJobIds((prev) => [jobId, ...prev]);
  };

  return (
    <div>
      <PageHeader
        title="Reporting & Executive Export Center"
        subtitle={`Asynchronous white-label PDF briefings and streaming XLSX exports for ${projectId}`}
        actions={
          <Button variant="primary" onClick={() => setIsExportModalOpen(true)}>
            ➕ Generate New Report
          </Button>
        }
      />

      <div style={{ marginTop: '24px', display: 'flex', flexDirection: 'column', gap: '20px' }}>
        <Card variant="bordered" padding="24px">
          <h3 style={{ fontSize: '1.1rem', fontWeight: 800, color: '#0f172a', margin: '0 0 16px 0' }}>
            Active & Recent Report Generation Jobs
          </h3>
          <p style={{ fontSize: '0.85rem', color: '#64748b', margin: '0 0 20px 0' }}>
            Jobs run asynchronously on backend Redis worker threads. Downloads expire in 24 hours per system security policies.
          </p>

          <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
            {activeJobIds.map((jobId) => (
              <ReportJobTrackerCard key={jobId} jobId={jobId} projectId={projectId} />
            ))}
          </div>
        </Card>
      </div>

      <ReportExportModal
        isOpen={isExportModalOpen}
        projectId={projectId}
        campaignId={campaignId}
        onClose={() => setIsExportModalOpen(false)}
        onJobSubmitted={handleJobSubmitted}
      />
    </div>
  );
};
