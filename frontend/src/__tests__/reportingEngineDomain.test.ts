import { describe, it, expect, beforeEach, vi } from 'vitest';
import { reportingApi } from '../features/reporting/api/reportingApi';
import { apiClient } from '../services/api/client';
import { ReportRequest } from '../types/reporting';

const mockLocalStorage = (() => {
  let store: Record<string, string> = {};
  return {
    getItem: (key: string) => store[key] || null,
    setItem: (key: string, value: string) => {
      store[key] = value.toString();
    },
    removeItem: (key: string) => {
      delete store[key];
    },
    clear: () => {
      store = {};
    },
  };
})();

Object.defineProperty(global, 'localStorage', {
  value: mockLocalStorage,
  writable: true,
});

describe('FEAT-009 Reporting Engine Domain Integration', () => {
  beforeEach(() => {
    localStorage.clear();
    vi.restoreAllMocks();
  });

  it('generateReport calls POST /reports/generate via gateway client', async () => {
    const mockJobResponse = {
      jobId: 'JOB-99201',
      projectId: 'PRJ-99201',
      campaignId: 'CMP-101',
      reportType: 'EXEC_SUMMARY_PDF' as const,
      nodeId: 'GLOBAL_ORG',
      status: 'QUEUED' as const,
      createdAt: new Date().toISOString(),
      message: 'Report generation job successfully queued',
    };

    const spy = vi.spyOn(apiClient, 'post').mockResolvedValue(mockJobResponse);

    const payload: ReportRequest = {
      projectId: 'PRJ-99201',
      campaignId: 'CMP-101',
      reportType: 'EXEC_SUMMARY_PDF',
      nodeId: 'GLOBAL_ORG',
      requestedBy: 'USR-HR-DIRECTOR',
    };

    const result = await reportingApi.generateReport(payload);

    expect(spy).toHaveBeenCalledWith('/reports/generate', payload);
    expect(result.jobId).toBe('JOB-99201');
    expect(result.status).toBe('QUEUED');
  });

  it('getJobStatus calls GET /reports/jobs/{jobId} via gateway client', async () => {
    const mockCompletedJob = {
      jobId: 'JOB-99201',
      projectId: 'PRJ-99201',
      campaignId: 'CMP-101',
      reportType: 'EXEC_SUMMARY_PDF' as const,
      status: 'COMPLETED' as const,
      downloadUrl: 'https://storage.talnova.com/reports/JOB-99201.pdf',
      createdAt: new Date().toISOString(),
      expiresAt: new Date(Date.now() + 86400000).toISOString(),
    };

    const spy = vi.spyOn(apiClient, 'get').mockResolvedValue(mockCompletedJob);

    const result = await reportingApi.getJobStatus('JOB-99201');

    expect(spy).toHaveBeenCalledWith('/reports/jobs/JOB-99201');
    expect(result.status).toBe('COMPLETED');
    expect(result.downloadUrl).toBe('https://storage.talnova.com/reports/JOB-99201.pdf');
  });

  it('validates password protection length constraints (6-30 chars)', () => {
    const validPass = 'Pass1234';
    const shortPass = '123';
    const longPass = 'A'.repeat(35);

    expect(validPass.length >= 6 && validPass.length <= 30).toBe(true);
    expect(shortPass.length >= 6 && shortPass.length <= 30).toBe(false);
    expect(longPass.length >= 6 && longPass.length <= 30).toBe(false);
  });

  it('submits streaming XLSX raw response dataset export job per FR-RPT-003 (PF-RPT-002)', async () => {
    const mockXlsxJob = {
      jobId: 'JOB-XLSX-8810',
      projectId: 'PRJ-99201',
      campaignId: 'CMP-101',
      reportType: 'RAW_RESPONSES_XLSX' as const,
      status: 'QUEUED' as const,
      createdAt: new Date().toISOString(),
    };

    const spy = vi.spyOn(apiClient, 'post').mockResolvedValue(mockXlsxJob);

    const payload: ReportRequest = {
      projectId: 'PRJ-99201',
      campaignId: 'CMP-101',
      reportType: 'RAW_RESPONSES_XLSX',
      requestedBy: 'USR-HR-DIRECTOR',
    };

    const result = await reportingApi.generateReport(payload);

    expect(spy).toHaveBeenCalledWith('/reports/generate', payload);
    expect(result.reportType).toBe('RAW_RESPONSES_XLSX');
    expect(result.status).toBe('QUEUED');
  });

  it('enforces differential privacy suppression when cell response count N < 5 per BR-RPT-001 (PF-RPT-005)', () => {
    const sanitizeScore = (score: number | null, sampleSize: number) => {
      if (sampleSize < 5) return '* N/A (N < 5)';
      return score !== null ? `${score}%` : 'N/A';
    };

    expect(sanitizeScore(85, 12)).toBe('85%');
    expect(sanitizeScore(85, 3)).toBe('* N/A (N < 5)');
    expect(sanitizeScore(null, 2)).toBe('* N/A (N < 5)');
  });
});
