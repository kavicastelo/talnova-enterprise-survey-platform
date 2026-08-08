import { describe, it, expect, beforeEach, vi } from 'vitest';
import { employeeApi } from '../features/employee/api/employeeApi';
import { apiClient } from '../services/api/client';
import { CreateEmployeeRequest, UpdateEmployeeRequest } from '../types/employee';

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

describe('FEAT-003 Employee Management Domain Integration', () => {
  beforeEach(() => {
    localStorage.clear();
    vi.restoreAllMocks();
  });

  it('validates employee ID regex pattern correctly', () => {
    const empPattern = /^[A-Za-z0-9_-]{2,30}$/;
    expect(empPattern.test('EMP-10020')).toBe(true);
    expect(empPattern.test('E-1')).toBe(true);
    expect(empPattern.test('INVALID EMP')).toBe(false);
    expect(empPattern.test('E')).toBe(false);
  });

  it('createEmployee calls POST /employees via gateway client', async () => {
    const mockResponse = {
      id: '66b26d8f8a84a51e3c8b1111',
      projectId: 'PRJ-99201',
      employeeId: 'EMP-10020',
      fullName: 'Alexander Aitken',
      email: 'a.aitken@aitkenspence.lk',
      nodeId: 'N-201',
      status: 'ACTIVE' as const,
    };

    const spy = vi.spyOn(apiClient, 'post').mockResolvedValue({
      success: true,
      data: mockResponse,
    });

    const payload: CreateEmployeeRequest = {
      projectId: 'PRJ-99201',
      employeeId: 'EMP-10020',
      fullName: 'Alexander Aitken',
      email: 'a.aitken@aitkenspence.lk',
      nodeId: 'N-201',
    };

    const result = await employeeApi.createEmployee(payload);

    expect(spy).toHaveBeenCalledWith('/employees', payload);
    expect(result.employeeId).toBe('EMP-10020');
    expect(result.status).toBe('ACTIVE');
  });

  it('updateEmployee calls PUT /employees/{employeeId} via gateway client', async () => {
    const mockResponse = {
      projectId: 'PRJ-99201',
      employeeId: 'EMP-10020',
      fullName: 'Alexander Aitken',
      email: 'alex.a@aitkenspence.lk',
      nodeId: 'N-301',
      status: 'ACTIVE' as const,
    };

    const spy = vi.spyOn(apiClient, 'put').mockResolvedValue({
      success: true,
      data: mockResponse,
    });

    const payload: UpdateEmployeeRequest = {
      email: 'alex.a@aitkenspence.lk',
      nodeId: 'N-301',
    };

    const result = await employeeApi.updateEmployee('EMP-10020', payload, 'PRJ-99201');

    expect(spy).toHaveBeenCalledWith('/employees/EMP-10020', payload, { params: { projectId: 'PRJ-99201' } });
    expect(result.email).toBe('alex.a@aitkenspence.lk');
  });

  it('anonymizeEmployee calls POST /employees/{employeeId}/gdpr-anonymize', async () => {
    const mockResponse = {
      projectId: 'PRJ-99201',
      employeeId: 'EMP-10020',
      fullName: 'ANONYMIZED_USER_882',
      email: 'anonymized_882@gdpr.internal',
      nodeId: 'N-201',
      status: 'TERMINATED' as const,
    };

    const spy = vi.spyOn(apiClient, 'post').mockResolvedValue({
      success: true,
      data: mockResponse,
    });

    const result = await employeeApi.anonymizeEmployee('EMP-10020', 'PRJ-99201');

    expect(spy).toHaveBeenCalledWith('/employees/EMP-10020/gdpr-anonymize', {}, { params: { projectId: 'PRJ-99201' } });
    expect(result.fullName).toContain('ANONYMIZED');
  });

  it('syncHrisRoster calls POST /employees/hris/sync via gateway client', async () => {
    const mockResult = {
      jobId: 'JOB-SYNC-901',
      projectId: 'PRJ-99201',
      totalProcessed: 500,
      insertedCount: 25,
      updatedCount: 475,
      terminatedCount: 0,
      failedCount: 0,
      errors: [],
    };

    const spy = vi.spyOn(apiClient, 'post').mockResolvedValue({
      success: true,
      data: mockResult,
    });

    const result = await employeeApi.syncHrisRoster({
      projectId: 'PRJ-99201',
      provider: 'WORKDAY_RAAS',
      apiEndpoint: 'https://workday.com/api',
    });

    expect(spy).toHaveBeenCalledWith('/employees/hris/sync', {
      projectId: 'PRJ-99201',
      provider: 'WORKDAY_RAAS',
      apiEndpoint: 'https://workday.com/api',
    });
    expect(result.totalProcessed).toBe(500);
  });
});
