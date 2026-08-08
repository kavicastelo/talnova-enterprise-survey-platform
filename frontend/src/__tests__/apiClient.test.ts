import { describe, it, expect, beforeEach } from 'vitest';
import { generateCorrelationId, axiosInstance } from '../services/api/client';

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

describe('Centralized API Client Infrastructure', () => {
  beforeEach(() => {
    localStorage.clear();
  });

  it('should generate correlation ID with CORR- prefix', () => {
    const correlationId = generateCorrelationId();
    expect(correlationId).toMatch(/^CORR-[A-Z0-9]{8}$/);
  });

  it('should attach X-Project-ID header from localStorage or fallback', async () => {
    localStorage.setItem('tesp_project_id', 'PRJ-TEST-100');
    localStorage.setItem('tesp_auth_token', 'JWT-SECRET-KEY');

    const handlers = (axiosInstance.interceptors.request as any).handlers;
    const handler = handlers[0];
    const config = await handler.fulfilled({
      headers: new Map(),
    } as any);

    expect(config.headers.get('X-Project-ID')).toBe('PRJ-TEST-100');
    expect(config.headers.get('Authorization')).toBe('Bearer JWT-SECRET-KEY');
    expect(config.headers.get('X-Correlation-ID')).toMatch(/^CORR-/);
  });

  it('should default X-Project-ID to PRJ-99201 if empty in localStorage', async () => {
    const handlers = (axiosInstance.interceptors.request as any).handlers;
    const handler = handlers[0];
    const config = await handler.fulfilled({
      headers: new Map(),
    } as any);

    expect(config.headers.get('X-Project-ID')).toBe('PRJ-99201');
  });
});
