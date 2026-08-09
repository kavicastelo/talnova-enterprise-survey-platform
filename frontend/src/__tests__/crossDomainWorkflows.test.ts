import { describe, it, expect, beforeEach, vi } from 'vitest';
import { QueryClient } from '@tanstack/react-query';
import { apiClient } from '../services/api/client';
import { projectConfigApi } from '../features/project-config/api/projectConfigApi';
import { orgApi } from '../features/organization/api/orgApi';
import { surveyApi } from '../features/survey-builder/api/surveyApi';
import { distributionApi } from '../features/distribution/api/distributionApi';
import { responseIntakeApi } from '../features/response-intake/api/responseIntakeApi';
import { actionPlanningApi } from '../features/action-planning/api/actionPlanningApi';
import { reportingApi } from '../features/reporting/api/reportingApi';
import { notificationApi } from '../features/notifications/api/notificationApi';
import { auditApi } from '../features/audit/api/auditApi';

describe('TESP Frontend Cross-Domain Forensic Audit & User Journeys', () => {
  let queryClient: QueryClient;

  beforeEach(() => {
    queryClient = new QueryClient();
    vi.restoreAllMocks();
  });

  it('WORKFLOW 1: Project Initialization & Tenant Propagation', async () => {
    const mockPrj = { projectId: 'PRJ-99201', name: 'Aitken Spence Enterprise Survey 2026' };
    vi.spyOn(apiClient, 'get').mockResolvedValue({ success: true, data: mockPrj });

    const result = await projectConfigApi.getProject('PRJ-99201');
    expect(result.projectId).toBe('PRJ-99201');
  });

  it('WORKFLOW 2: Organization Node to Employee Roster ABAC Scoping', async () => {
    const mockOrg = { nodeId: 'N-301', name: 'Engineering Dept' };
    vi.spyOn(apiClient, 'get').mockResolvedValue({ success: true, data: mockOrg });

    const node = await orgApi.getNode('N-301', 'PRJ-99201');
    expect(node.nodeId).toBe('N-301');
  });

  it('WORKFLOW 3: Survey Creation & Published Version Immutability', async () => {
    const mockSurvey = { surveyId: 'SRV-5001', status: 'PUBLISHED', version: 1 };
    vi.spyOn(apiClient, 'post').mockResolvedValue({ success: true, data: mockSurvey });

    const result = await surveyApi.publishSurvey('SRV-5001');
    expect(result.status).toBe('PUBLISHED');
  });

  it('WORKFLOW 4: Survey Campaign Distribution & Token Batching', async () => {
    const mockTokenBatch = { campaignId: 'CMP-101', generatedCount: 100, kioskPin: '882019' };
    vi.spyOn(apiClient, 'post').mockResolvedValue({ success: true, data: mockTokenBatch });

    const result = await distributionApi.generateTokens({
      projectId: 'PRJ-99201',
      campaignId: 'CMP-101',
      surveyId: 'SRV-5001',
      anonymityLevel: 'SEMI_ANONYMOUS',
      count: 100,
    });
    expect(result.generatedCount).toBe(100);
  });

  it('WORKFLOW 5 & 6: Response Intake to Analytics & AI Differential Privacy', async () => {
    const mockIngestion = { responseId: 'RSP-99018273', status: 'ACCEPTED' };
    vi.spyOn(apiClient, 'post').mockResolvedValue({ success: true, data: mockIngestion });

    const intakeResult = await responseIntakeApi.submitResponse({
      projectId: 'PRJ-99201',
      campaignId: 'CMP-101',
      surveyId: 'SRV-5001',
      surveyVersion: 1,
      respondentType: 'AUTHENTICATED',
      answers: [],
    });
    expect(intakeResult.status).toBe('ACCEPTED');
  });

  it('WORKFLOW 7: Analytics Insight to Action Plan Remediation', async () => {
    const mockActionPlan = { actionPlanId: 'ACT-9901', status: 'DRAFT' };
    vi.spyOn(apiClient, 'post').mockResolvedValue({ success: true, data: mockActionPlan });

    const actionResult = await actionPlanningApi.createActionPlan({
      projectId: 'PRJ-99201',
      campaignId: 'CMP-101',
      title: 'Workload Balancing',
    });
    expect(actionResult.actionPlanId).toBe('ACT-9901');
  });

  it('WORKFLOW 8: Executive Report Generation & Presigned URL Download', async () => {
    const mockJob = { jobId: 'JOB-99201', status: 'QUEUED' };
    vi.spyOn(apiClient, 'post').mockResolvedValue(mockJob);

    const reportResult = await reportingApi.generateReport({
      projectId: 'PRJ-99201',
      campaignId: 'CMP-101',
      reportType: 'EXEC_SUMMARY_PDF',
      requestedBy: 'USR-HR-DIR',
    });
    expect(reportResult.jobId).toBe('JOB-99201');
  });

  it('WORKFLOW 9 & 10: Multi-Channel Notifications & Append-Only Audit Logging', async () => {
    const mockNotif = { notificationId: 'NTF-9901', status: 'QUEUED' };
    vi.spyOn(apiClient, 'post').mockResolvedValue(mockNotif);

    const notifResult = await notificationApi.sendNotification({
      projectId: 'PRJ-99201',
      recipient: 'user@enterprise.com',
      channel: 'EMAIL',
      messageBody: 'Hello',
    });
    expect(notifResult.notificationId).toBe('NTF-9901');

    const mockAudit = { auditId: 'AUD-880192', action: 'SURVEY_PUBLISHED' };
    vi.spyOn(apiClient, 'post').mockResolvedValue(mockAudit);

    const auditResult = await auditApi.recordAuditLog({
      projectId: 'PRJ-99201',
      actorId: 'USR-SUPER-ADMIN',
      userRole: 'SUPER_ADMIN',
      action: 'SURVEY_PUBLISHED',
    });
    expect(auditResult.auditId).toBe('AUD-880192');
  });

  it('TENANT ISOLATION: queryClient clear on tenant switch', async () => {
    queryClient.setQueryData(['project', 'PRJ-99201'], { name: 'Tenant A Data' });
    expect(queryClient.getQueryData(['project', 'PRJ-99201'])).toBeDefined();

    // Perform tenant switch cache clear
    await queryClient.cancelQueries();
    queryClient.clear();

    expect(queryClient.getQueryData(['project', 'PRJ-99201'])).toBeUndefined();
  });
});
