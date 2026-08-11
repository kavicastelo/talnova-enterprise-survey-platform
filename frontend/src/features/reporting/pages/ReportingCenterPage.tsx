import React, { useState } from 'react';
import { PageHeader } from '../../../components/ui/PageHeader';
import { Button } from '../../../components/ui/Button';
import { Card } from '../../../components/ui/Card';
import { EmptyState } from '../../../components/ui/EmptyState';
import { Alert } from '../../../components/ui/Alert';
import { ReportExportModal } from '../../../components/reporting/ReportExportModal';
import { ReportJobTrackerCard } from '../../../components/reporting/ReportJobTrackerCard';
import { useTenant } from '../../../context/TenantContext';
import { useAuth } from '../../../context/AuthContext';

export const ReportingCenterPage: React.FC = () => {
  const { activeProject } = useTenant();
  const { user, hasRole } = useAuth();
  const [isExportModalOpen, setIsExportModalOpen] = useState<boolean>(false);
  const [activeJobIds, setActiveJobIds] = useState<string[]>([]);
  const [campaignId, setCampaignId] = useState<string>('CMP-1001');

  const projectId = activeProject?.projectId || localStorage.getItem('tesp_project_id') || 'PRJ-99201';

  // Permission check per PR-RPT-001 to PR-RPT-004
  const isAuthorized =
    !user ||
    hasRole(['EXECUTIVE', 'HR_MANAGER', 'CONSULTANT_DAASH', 'SUPER_ADMIN', 'PROJECT_ADMIN']);

  const handleJobSubmitted = (jobId: string) => {
    setActiveJobIds((prev) => [jobId, ...prev]);
  };

  if (!isAuthorized) {
    return (
      <div className="space-y-6 p-6">
        <PageHeader
          title="Reporting &amp; Executive Export Center"
          subtitle="Access restricted per PR-RPT-004"
        />
        <Card variant="bordered" padding="24px">
          <Alert type="error" title="Access Forbidden (403)">
            Your user role does not have authorization to access Reporting Engine APIs or export PDF/Excel briefings.
          </Alert>
        </Card>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <PageHeader
        title="Reporting &amp; Executive Export Center"
        subtitle={`Asynchronous white-label PDF briefings and streaming XLSX exports for ${projectId}`}
        actions={
          <Button variant="primary" onClick={() => setIsExportModalOpen(true)}>
            ➕ Generate New Report
          </Button>
        }
      />

      {/* Scope Controls */}
      <Card variant="bordered" padding="16px">
        <div className="flex flex-wrap items-center justify-between gap-4">
          <div className="flex items-center gap-3">
            <label className="block text-[11px] font-bold text-slate-600 uppercase tracking-wider">
              Target Campaign ID (VR-RPT-002)
            </label>
            <input
              type="text"
              value={campaignId}
              onChange={(e) => setCampaignId(e.target.value)}
              placeholder="e.g. CMP-1001"
              className="bg-white border border-slate-300 rounded px-3 py-1.5 text-xs text-slate-800 font-mono focus:outline-none focus:border-indigo-600 w-44"
            />
          </div>

          <div className="flex flex-wrap items-center gap-4 text-xs text-slate-500">
            <div>
              Differential Privacy (BR-RPT-001): <span className="font-bold text-amber-600">🛡️ Active (N &lt; 5 Suppressed)</span>
            </div>
            <div>
              White-Label Branding (FR-RPT-004): <span className="font-bold text-indigo-600">🎨 Active (Daash Enterprise)</span>
            </div>
            <div>
              S3 Download Expiration: <span className="font-bold text-slate-700">🔒 24 Hours (BR-RPT-003)</span>
            </div>
          </div>
        </div>
      </Card>

      <Card variant="bordered" padding="24px">
        <div className="space-y-4">
          <div>
            <h3 className="text-base font-bold text-slate-900">
              Active &amp; Recent Report Generation Jobs (Redis Worker Queue)
            </h3>
            <p className="text-xs text-slate-500 mt-0.5">
              Jobs execute asynchronously on Redis worker threads. Download links automatically expire after 24 hours per system security policies.
            </p>
          </div>

          {activeJobIds.length === 0 ? (
            <EmptyState
              title="No Active Report Generation Jobs"
              description="Click 'Generate New Report' above to submit an asynchronous executive PDF or streaming XLSX export job."
            />
          ) : (
            <div className="space-y-3">
              {activeJobIds.map((jobId) => (
                <ReportJobTrackerCard key={jobId} jobId={jobId} projectId={projectId} />
              ))}
            </div>
          )}
        </div>
      </Card>

      {/* Scheduled Cron Dispatcher Configuration (PF-RPT-004) */}
      <Card variant="bordered" padding="24px">
        <div className="space-y-4">
          <div className="flex justify-between items-center">
            <div>
              <h3 className="text-base font-bold text-slate-900">
                Automated Scheduled Email Report Dispatcher (FR-RPT-007, US-RPT-003)
              </h3>
              <p className="text-xs text-slate-500 mt-0.5">
                Quartz scheduler compiles and dispatches weekly departmental pulse PDFs to designated HR Directors.
              </p>
            </div>
            <span className="px-2.5 py-1 rounded bg-indigo-50 text-indigo-700 text-xs font-bold border border-indigo-200">
              Quartz Cron Active
            </span>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div className="p-4 rounded-xl bg-slate-50 border border-slate-200 space-y-2">
              <div className="flex justify-between items-center text-xs font-bold text-slate-800">
                <span>Weekly Pulse PDF Briefing</span>
                <span className="font-mono text-indigo-600">0 0 7 ? * MON</span>
              </div>
              <p className="text-xs text-slate-600">
                Executes every Monday at 07:00 AM UTC. Emails pre-signed S3 download URL to <code>hr-leadership@talnova.com</code>.
              </p>
              <div className="text-[11px] text-slate-500">
                Next Dispatch: <span className="font-semibold text-slate-700">Monday at 07:00 AM</span>
              </div>
            </div>

            <div className="p-4 rounded-xl bg-slate-50 border border-slate-200 space-y-2">
              <div className="flex justify-between items-center text-xs font-bold text-slate-800">
                <span>Monthly Board Raw Response XLSX</span>
                <span className="font-mono text-indigo-600">0 0 1 1 * ?</span>
              </div>
              <p className="text-xs text-slate-600">
                Executes 1st of every month at 01:00 AM UTC. Streams anonymized response dataset to S3 and notifies C-Suite executives.
              </p>
              <div className="text-[11px] text-slate-500">
                Next Dispatch: <span className="font-semibold text-slate-700">1st of Next Month</span>
              </div>
            </div>
          </div>
        </div>
      </Card>

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
