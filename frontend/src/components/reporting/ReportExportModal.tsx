import React, { useState } from 'react';
import { useGenerateReportMutation } from '../../features/reporting/api/useReportingQueries';
import { ReportType } from '../../types/reporting';

interface ReportExportModalProps {
  isOpen: boolean;
  projectId: string;
  campaignId: string;
  onClose: () => void;
  onJobSubmitted: (jobId: string) => void;
}

export const ReportExportModal: React.FC<ReportExportModalProps> = ({
  isOpen,
  projectId,
  campaignId,
  onClose,
  onJobSubmitted,
}) => {
  const [reportType, setReportType] = useState<ReportType>('EXEC_SUMMARY_PDF');
  const [nodeId, setNodeId] = useState<string>('GLOBAL_ORG');
  const [passwordProtection, setPasswordProtection] = useState<string>('');
  const [enablePassword, setEnablePassword] = useState<boolean>(false);
  const [includeHeatmaps, setIncludeHeatmaps] = useState<boolean>(true);
  const [includeAiSummary, setIncludeAiSummary] = useState<boolean>(true);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  const generateReportMutation = useGenerateReportMutation();

  if (!isOpen) return null;

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setErrorMessage(null);

    if (enablePassword && (passwordProtection.length < 6 || passwordProtection.length > 30)) {
      setErrorMessage('Password protection must be between 6 and 30 characters (VR-RPT-004)');
      return;
    }

    generateReportMutation.mutate(
      {
        projectId,
        campaignId,
        reportType,
        nodeId,
        requestedBy: 'USR-HR-DIRECTOR',
        passwordProtection: enablePassword ? passwordProtection : undefined,
      },
      {
        onSuccess: (data) => {
          onJobSubmitted(data.jobId);
          onClose();
        },
        onError: (err: any) => {
          setErrorMessage(err?.message || 'Failed to trigger report export job');
        },
      }
    );
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/40 backdrop-blur-sm p-4">
      <div className="w-full max-w-lg rounded-xl bg-white p-6 shadow-2xl border border-slate-200">
        <div className="flex items-center justify-between border-b border-slate-200 pb-4">
          <div>
            <h2 className="text-xl font-bold text-slate-900">Export Executive & Analytical Reports</h2>
            <p className="text-xs text-slate-500">White-label PDF briefings & streaming XLSX multi-tab exports</p>
          </div>
          <button
            onClick={onClose}
            className="text-slate-400 hover:text-slate-600 text-lg font-bold"
          >
            &times;
          </button>
        </div>

        {errorMessage && (
          <div className="mt-4 rounded-lg bg-red-50 p-3 text-xs font-semibold text-red-700 border border-red-200">
            {errorMessage}
          </div>
        )}

        <form onSubmit={handleSubmit} className="mt-4 space-y-4">
          <div>
            <label className="block text-xs font-bold uppercase tracking-wider text-slate-700">
              Report Format & Document Type
            </label>
            <select
              value={reportType}
              onChange={(e) => setReportType(e.target.value as ReportType)}
              className="mt-1 w-full rounded-lg border border-slate-300 bg-white p-2.5 text-sm font-medium text-slate-900 focus:border-blue-600 focus:outline-none"
            >
              <option value="EXEC_SUMMARY_PDF">PDF — 10-Page Executive Summary Briefing</option>
              <option value="DEPT_BREAKDOWN_PDF">PDF — Departmental Breakdown Report</option>
              <option value="RAW_RESPONSES_XLSX">XLSX — Anonymized Raw Responses Workbook</option>
              <option value="AGGREGATED_SCORES_XLSX">XLSX — Department Cross-Tabulation Matrix</option>
            </select>
          </div>

          <div>
            <label className="block text-xs font-bold uppercase tracking-wider text-slate-700">
              Target Organization Node Scope
            </label>
            <select
              value={nodeId}
              onChange={(e) => setNodeId(e.target.value)}
              className="mt-1 w-full rounded-lg border border-slate-300 bg-white p-2.5 text-sm font-medium text-slate-900 focus:border-blue-600 focus:outline-none"
            >
              <option value="GLOBAL_ORG">Global Enterprise (All Nodes)</option>
              <option value="IT_DIVISION">IT & Technology Division</option>
              <option value="ENGINEERING_DEPT">Engineering Department</option>
              <option value="HR_DIVISION">Human Resources</option>
            </select>
          </div>

          <div className="space-y-2 rounded-lg bg-slate-50 p-3 border border-slate-200">
            <span className="block text-xs font-bold uppercase tracking-wider text-slate-700">
              Report Section Inclusions
            </span>
            <label className="flex items-center space-x-2 text-xs font-medium text-slate-800">
              <input
                type="checkbox"
                checked={includeHeatmaps}
                onChange={(e) => setIncludeHeatmaps(e.target.checked)}
                className="rounded border-slate-300 text-blue-600 focus:ring-blue-500"
              />
              <span>Include 2D Department Heatmaps & eNPS Scorecards</span>
            </label>
            <label className="flex items-center space-x-2 text-xs font-medium text-slate-800">
              <input
                type="checkbox"
                checked={includeAiSummary}
                onChange={(e) => setIncludeAiSummary(e.target.checked)}
                className="rounded border-slate-300 text-blue-600 focus:ring-blue-500"
              />
              <span>Include AI Executive Recommendations & Risk Flags</span>
            </label>
          </div>

          <div className="rounded-lg border border-slate-200 p-3">
            <label className="flex items-center space-x-2 text-xs font-bold uppercase tracking-wider text-slate-700">
              <input
                type="checkbox"
                checked={enablePassword}
                onChange={(e) => setEnablePassword(e.target.checked)}
                className="rounded border-slate-300 text-blue-600 focus:ring-blue-500"
              />
              <span>PDF Password Protection (VR-RPT-004)</span>
            </label>
            {enablePassword && (
              <input
                type="password"
                placeholder="Enter password (6-30 chars)"
                value={passwordProtection}
                onChange={(e) => setPasswordProtection(e.target.value)}
                className="mt-2 w-full rounded-lg border border-slate-300 p-2 text-xs text-slate-900 focus:border-blue-600 focus:outline-none"
              />
            )}
          </div>

          <div className="flex justify-end space-x-3 pt-2">
            <button
              type="button"
              onClick={onClose}
              className="rounded-lg border border-slate-300 px-4 py-2 text-xs font-semibold text-slate-700 hover:bg-slate-50"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={generateReportMutation.isPending}
              className="rounded-lg bg-blue-600 px-5 py-2 text-xs font-semibold text-white shadow-md hover:bg-blue-700 disabled:opacity-50"
            >
              {generateReportMutation.isPending ? 'Queueing Export Job...' : 'Submit Export Job (202 Accepted)'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
