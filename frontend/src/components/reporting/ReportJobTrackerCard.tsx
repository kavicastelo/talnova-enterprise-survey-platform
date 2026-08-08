import React from 'react';
import { useReportJobStatusQuery } from '../../features/reporting/api/useReportingQueries';
import { Card } from '../ui/Card';
import { Badge } from '../ui/Badge';
import { Button } from '../ui/Button';

interface ReportJobTrackerCardProps {
  jobId: string;
  projectId?: string;
}

export const ReportJobTrackerCard: React.FC<ReportJobTrackerCardProps> = ({ jobId, projectId = 'PRJ-99201' }) => {
  const { data: job, isLoading, isError } = useReportJobStatusQuery(jobId, projectId);

  if (isLoading) {
    return (
      <Card variant="bordered" padding="16px">
        <div style={{ fontSize: '0.85rem', color: '#64748b', textAlign: 'center' }}>
          🔄 Polling report job status ({jobId})...
        </div>
      </Card>
    );
  }

  if (isError || !job) {
    return (
      <Card variant="bordered" padding="16px">
        <div style={{ fontSize: '0.85rem', color: '#991b1b', textAlign: 'center' }}>
          ⚠️ Could not retrieve report job status ({jobId})
        </div>
      </Card>
    );
  }

  const isComplete = job.status === 'COMPLETED';
  const isFailed = job.status === 'FAILED';
  const isPending = job.status === 'QUEUED' || job.status === 'PROCESSING';

  return (
    <Card variant="bordered" padding="20px">
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
            <span style={{ fontWeight: 800, fontSize: '0.95rem', color: '#0f172a' }}>
              📄 Report Job <code>{job.jobId}</code>
            </span>
            <Badge
              variant={
                isComplete
                  ? 'success'
                  : isFailed
                  ? 'danger'
                  : isPending
                  ? 'warning'
                  : 'neutral'
              }
              dot
            >
              {job.status}
            </Badge>
          </div>
          <p style={{ fontSize: '0.8rem', color: '#64748b', margin: '4px 0 0 0' }}>
            Type: <strong>{job.reportType}</strong> | Campaign: <code>{job.campaignId}</code>
          </p>
        </div>

        <div>
          {isComplete && job.downloadUrl ? (
            <a href={job.downloadUrl} target="_blank" rel="noopener noreferrer" style={{ textDecoration: 'none' }}>
              <Button variant="primary" size="sm">
                ⬇️ Download Report
              </Button>
            </a>
          ) : isPending ? (
            <Button variant="secondary" size="sm" isLoading>
              Compiling Document...
            </Button>
          ) : (
            <Button variant="outline" size="sm" disabled>
              Failed
            </Button>
          )}
        </div>
      </div>
    </Card>
  );
};
