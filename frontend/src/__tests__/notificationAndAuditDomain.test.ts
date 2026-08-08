import { describe, it, expect, beforeEach, vi } from 'vitest';
import { notificationApi } from '../features/notifications/api/notificationApi';
import { auditApi } from '../features/audit/api/auditApi';
import { apiClient } from '../services/api/client';
import { NotificationRequest } from '../types/notification';
import { AuditLogRequest } from '../types/audit';

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

describe('Notifications & Audit Domain Integration', () => {
  beforeEach(() => {
    localStorage.clear();
    vi.restoreAllMocks();
  });

  it('sendNotification calls POST /notifications/send via gateway client', async () => {
    const mockResponse = {
      notificationId: 'NTF-9901',
      projectId: 'PRJ-99201',
      recipient: 'john.doe@enterprise.com',
      channel: 'EMAIL' as const,
      status: 'QUEUED' as const,
    };

    const spy = vi.spyOn(apiClient, 'post').mockResolvedValue(mockResponse);

    const payload: NotificationRequest = {
      projectId: 'PRJ-99201',
      recipient: 'john.doe@enterprise.com',
      channel: 'EMAIL',
      subject: 'Survey Invite',
      messageBody: 'Please fill out your survey',
    };

    const result = await notificationApi.sendNotification(payload);

    expect(spy).toHaveBeenCalledWith('/notifications/send', payload);
    expect(result.notificationId).toBe('NTF-9901');
    expect(result.status).toBe('QUEUED');
  });

  it('recordAuditLog calls POST /audit/logs via gateway client', async () => {
    const mockAuditResponse = {
      auditId: 'AUD-880192',
      projectId: 'PRJ-99201',
      actorId: 'USR-SUPER-ADMIN',
      userRole: 'SUPER_ADMIN',
      action: 'SURVEY_PUBLISHED',
      resourceId: 'SRV-5001',
      timestamp: new Date().toISOString(),
    };

    const spy = vi.spyOn(apiClient, 'post').mockResolvedValue(mockAuditResponse);

    const payload: AuditLogRequest = {
      projectId: 'PRJ-99201',
      actorId: 'USR-SUPER-ADMIN',
      userRole: 'SUPER_ADMIN',
      action: 'SURVEY_PUBLISHED',
      resourceId: 'SRV-5001',
    };

    const result = await auditApi.recordAuditLog(payload);

    expect(spy).toHaveBeenCalledWith('/audit/logs', payload);
    expect(result.auditId).toBe('AUD-880192');
    expect(result.action).toBe('SURVEY_PUBLISHED');
  });

  it('getAuditLogs calls GET /audit/logs via gateway client', async () => {
    const mockLogs = [
      {
        auditId: 'AUD-880192',
        projectId: 'PRJ-99201',
        actorId: 'USR-SUPER-ADMIN',
        userRole: 'SUPER_ADMIN',
        action: 'SURVEY_PUBLISHED',
        timestamp: new Date().toISOString(),
      },
    ];

    const spy = vi.spyOn(apiClient, 'get').mockResolvedValue(mockLogs);

    const result = await auditApi.getAuditLogs('PRJ-99201', 'USR-SUPER-ADMIN', 'SURVEY_PUBLISHED', 0, 20);

    expect(spy).toHaveBeenCalledWith('/audit/logs', {
      params: { projectId: 'PRJ-99201', actorId: 'USR-SUPER-ADMIN', action: 'SURVEY_PUBLISHED', page: 0, size: 20 },
    });
    expect(result.length).toBe(1);
    expect(result[0].auditId).toBe('AUD-880192');
  });
});
