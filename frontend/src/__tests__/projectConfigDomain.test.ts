import { describe, it, expect, beforeEach, vi } from 'vitest';
import { projectConfigApi } from '../features/project-config/api/projectConfigApi';
import { apiClient } from '../services/api/client';
import { ProjectCreateRequest, FeatureFlags } from '../types/projectConfig';

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

describe('FEAT-001 Project Configuration Domain Integration', () => {
  beforeEach(() => {
    localStorage.clear();
    vi.restoreAllMocks();
  });

  it('validates project ID pattern regex correctly', () => {
    const validPattern = /^PRJ-[A-Z0-9]{4,10}$/;
    expect(validPattern.test('PRJ-99201')).toBe(true);
    expect(validPattern.test('PRJ-ALPHA')).toBe(true);
    expect(validPattern.test('INVALID-ID')).toBe(false);
    expect(validPattern.test('prj-lowercase')).toBe(false);
  });

  it('validates HEX color pattern regex correctly', () => {
    const hexPattern = /^#([A-Fa-f0-9]{6})$/;
    expect(hexPattern.test('#1E3A8A')).toBe(true);
    expect(hexPattern.test('#ffffff')).toBe(true);
    expect(hexPattern.test('blue')).toBe(false);
    expect(hexPattern.test('#123')).toBe(false);
  });

  it('createProject calls POST /projects via gateway client', async () => {
    const mockResponse = {
      projectId: 'PRJ-88102',
      name: 'Commercial Bank Pulse Audit',
      status: 'ACTIVE' as const,
      branding: {
        companyName: 'Commercial Bank PLC',
        primaryColor: '#065F46',
        secondaryColor: '#10B981',
      },
      supportedLocales: ['en-US', 'si-LK'],
      defaultLocale: 'en-US',
      features: {
        aiAnalyticsEnabled: true,
        actionPlanningEnabled: false,
        kioskModeEnabled: true,
        smsDistributionEnabled: false,
      },
    };

    const spy = vi.spyOn(apiClient, 'post').mockResolvedValue({
      success: true,
      data: mockResponse,
    });

    const payload: ProjectCreateRequest = {
      projectId: 'PRJ-88102',
      name: 'Commercial Bank Pulse Audit',
      branding: mockResponse.branding,
      supportedLocales: ['en-US', 'si-LK'],
      defaultLocale: 'en-US',
      features: mockResponse.features,
    };

    const result = await projectConfigApi.createProject(payload);

    expect(spy).toHaveBeenCalledWith('/projects', payload);
    expect(result.projectId).toBe('PRJ-88102');
    expect(result.status).toBe('ACTIVE');
  });

  it('updateFeatureFlags calls PATCH /projects/{projectId}/features', async () => {
    const mockFeatures: FeatureFlags = {
      aiAnalyticsEnabled: true,
      actionPlanningEnabled: true,
      kioskModeEnabled: true,
      smsDistributionEnabled: true,
    };

    const spy = vi.spyOn(apiClient, 'patch').mockResolvedValue({
      success: true,
      data: {
        projectId: 'PRJ-99201',
        name: 'Aitken Spence PLC',
        status: 'ACTIVE' as const,
        branding: { companyName: 'Aitken Spence PLC', primaryColor: '#1E3A8A', secondaryColor: '#3B82F6' },
        supportedLocales: ['en-US'],
        defaultLocale: 'en-US',
        features: mockFeatures,
      },
    });

    const result = await projectConfigApi.updateFeatureFlags('PRJ-99201', mockFeatures);

    expect(spy).toHaveBeenCalledWith('/projects/PRJ-99201/features', mockFeatures);
    expect(result.features.kioskModeEnabled).toBe(true);
  });

  it('validateTheme calls POST /projects/validate-theme', async () => {
    const spy = vi.spyOn(apiClient, 'post').mockResolvedValue({
      success: true,
      data: {
        passed: true,
        contrastRatio: 12.5,
        wcagLevel: 'AAA' as const,
        recommendation: 'Passes WCAG 2.1 AA and AAA standards.',
      },
    });

    const result = await projectConfigApi.validateTheme({ primaryColor: '#1E3A8A', backgroundColor: '#FFFFFF' });

    expect(spy).toHaveBeenCalledWith('/projects/validate-theme', { primaryColor: '#1E3A8A', backgroundColor: '#FFFFFF' });
    expect(result.passed).toBe(true);
    expect(result.wcagLevel).toBe('AAA');
  });
});
