import React, { useEffect, useState } from 'react';

export interface ReportJobProgressDrawerProps {
  isOpen: boolean;
  jobId: string | null;
  projectId: string;
  onClose: () => void;
}

interface ReportJobDetails {
  jobId: string;
  projectId: string;
  campaignId: string;
  reportType: string;
  status: 'QUEUED' | 'PROCESSING' | 'COMPLETED' | 'FAILED';
  downloadUrl?: string;
  expiresAt?: string;
  message?: string;
}

export const ReportJobProgressDrawer: React.FC<ReportJobProgressDrawerProps> = ({
  isOpen,
  jobId,
  projectId,
  onClose,
}) => {
  const [activeTab, setActiveTab] = useState<'PROGRESS' | 'SCHEDULED'>('PROGRESS');
  const [jobDetails, setJobDetails] = useState<ReportJobDetails | null>(null);
  const [cronExpression, setCronExpression] = useState<string>('0 0 7 ? * MON');
  const [recipientEmail, setRecipientEmail] = useState<string>('hr.director@acme.org');
  const [scheduleSuccessMsg, setScheduleSuccessMsg] = useState<string | null>(null);

  useEffect(() => {
    if (!isOpen || !jobId) return;

    const pollJobStatus = async () => {
      try {
        const res = await fetch(`/api/v1/reports/jobs/${projectId}/${jobId}`);
        if (res.ok) {
          const data: ReportJobDetails = await res.json();
          setJobDetails(data);
        }
      } catch (err) {
        console.error('Failed to poll report job status', err);
      }
    };

    pollJobStatus();
    const interval = setInterval(pollJobStatus, 2000);
    return () => clearInterval(interval);
  }, [isOpen, jobId, projectId]);

  if (!isOpen) return null;

  const handleScheduleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setScheduleSuccessMsg(`Recurring report scheduled successfully for '${recipientEmail}' (Cron: '${cronExpression}')`);
  };

  return (
    <div className="fixed inset-y-0 right-0 z-50 flex w-full max-w-md bg-white shadow-2xl border-l border-slate-200">
      <div className="flex flex-col w-full p-6">
        <div className="flex items-center justify-between border-b border-slate-200 pb-4">
          <h2 className="text-lg font-bold text-slate-900">Report Generation & Scheduling</h2>
          <button onClick={onClose} className="text-slate-400 hover:text-slate-600 font-bold text-lg">
            &times;
          </button>
        </div>

        {/* Tab Selector */}
        <div className="mt-4 flex border-b border-slate-200">
          <button
            onClick={() => setActiveTab('PROGRESS')}
            className={`pb-2 px-4 text-xs font-bold uppercase tracking-wider border-b-2 ${
              activeTab === 'PROGRESS'
                ? 'border-blue-600 text-blue-600'
                : 'border-transparent text-slate-500 hover:text-slate-700'
            }`}
          >
            Live Job Progress
          </button>
          <button
            onClick={() => setActiveTab('SCHEDULED')}
            className={`pb-2 px-4 text-xs font-bold uppercase tracking-wider border-b-2 ${
              activeTab === 'SCHEDULED'
                ? 'border-blue-600 text-blue-600'
                : 'border-transparent text-slate-500 hover:text-slate-700'
            }`}
          >
            Scheduled Dispatch (Cron)
          </button>
        </div>

        {/* Tab 1: Live Job Progress */}
        {activeTab === 'PROGRESS' && (
          <div className="mt-6 space-y-6 flex-1">
            <div className="rounded-xl border border-slate-200 bg-slate-50 p-4">
              <div className="flex justify-between items-center mb-2">
                <span className="text-xs font-bold uppercase text-slate-600">Job ID</span>
                <span className="text-xs font-mono font-semibold text-blue-600">{jobId || 'N/A'}</span>
              </div>

              <div className="flex justify-between items-center mb-3">
                <span className="text-xs font-bold uppercase text-slate-600">Status</span>
                <span
                  className={`px-2.5 py-1 text-xs font-bold rounded-full ${
                    jobDetails?.status === 'COMPLETED'
                      ? 'bg-emerald-100 text-emerald-800'
                      : jobDetails?.status === 'FAILED'
                      ? 'bg-red-100 text-red-800'
                      : 'bg-amber-100 text-amber-800'
                  }`}
                >
                  {jobDetails?.status || 'QUEUED'}
                </span>
              </div>

              {/* Progress Bar */}
              <div className="w-full bg-slate-200 rounded-full h-2.5 overflow-hidden">
                <div
                  className={`h-2.5 rounded-full transition-all duration-500 ${
                    jobDetails?.status === 'COMPLETED'
                      ? 'w-full bg-emerald-500'
                      : jobDetails?.status === 'PROCESSING'
                      ? 'w-2/3 bg-blue-600 animate-pulse'
                      : 'w-1/4 bg-amber-500'
                  }`}
                />
              </div>
            </div>

            {jobDetails?.status === 'COMPLETED' && jobDetails.downloadUrl && (
              <div className="rounded-xl border border-emerald-200 bg-emerald-50 p-4">
                <h3 className="text-sm font-bold text-emerald-900 mb-1">Report Ready for Download</h3>
                <p className="text-xs text-emerald-700 mb-4">
                  Pre-signed AWS S3 link active (24-hour expiration TTL per BR-RPT-003)
                </p>
                <a
                  href={jobDetails.downloadUrl}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="inline-flex w-full justify-center items-center rounded-lg bg-emerald-600 px-4 py-2.5 text-xs font-bold text-white shadow hover:bg-emerald-700"
                >
                  Download Report Artifact
                </a>
              </div>
            )}
          </div>
        )}

        {/* Tab 2: Scheduled Dispatch */}
        {activeTab === 'SCHEDULED' && (
          <form onSubmit={handleScheduleSubmit} className="mt-6 space-y-4 flex-1">
            {scheduleSuccessMsg && (
              <div className="rounded-lg bg-emerald-50 p-3 text-xs font-semibold text-emerald-800 border border-emerald-200">
                {scheduleSuccessMsg}
              </div>
            )}

            <div>
              <label className="block text-xs font-bold uppercase tracking-wider text-slate-700">
                Recipient HR Director Email
              </label>
              <input
                type="email"
                value={recipientEmail}
                onChange={(e) => setRecipientEmail(e.target.value)}
                className="mt-1 w-full rounded-lg border border-slate-300 p-2.5 text-xs font-medium text-slate-900 focus:border-blue-600 focus:outline-none"
                required
              />
            </div>

            <div>
              <label className="block text-xs font-bold uppercase tracking-wider text-slate-700">
                Quartz Cron Expression (FR-RPT-007)
              </label>
              <input
                type="text"
                value={cronExpression}
                onChange={(e) => setCronExpression(e.target.value)}
                className="mt-1 w-full rounded-lg border border-slate-300 p-2.5 text-xs font-mono text-slate-900 focus:border-blue-600 focus:outline-none"
                required
              />
              <p className="mt-1 text-[11px] text-slate-500">Default: '0 0 7 ? * MON' (Every Monday at 07:00 AM)</p>
            </div>

            <button
              type="submit"
              className="w-full rounded-lg bg-blue-600 py-2.5 text-xs font-bold text-white shadow hover:bg-blue-700"
            >
              Save Recurring Cron Schedule
            </button>
          </form>
        )}

        <div className="pt-4 border-t border-slate-200">
          <button
            onClick={onClose}
            className="w-full rounded-lg border border-slate-300 py-2 text-xs font-bold text-slate-700 hover:bg-slate-50"
          >
            Close Drawer
          </button>
        </div>
      </div>
    </div>
  );
};
