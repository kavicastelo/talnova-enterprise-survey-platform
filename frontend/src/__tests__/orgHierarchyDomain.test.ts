import { describe, it, expect, beforeEach, vi } from 'vitest';
import { orgApi } from '../features/organization/api/orgApi';
import { apiClient } from '../services/api/client';
import { CreateNodeRequest } from '../types/organization';

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

describe('FEAT-002 Organization Hierarchy Domain Integration', () => {
  beforeEach(() => {
    localStorage.clear();
    vi.restoreAllMocks();
  });

  it('validates node ID regex pattern correctly', () => {
    const nodePattern = /^N-[A-Za-z0-9_-]{3,20}$/;
    expect(nodePattern.test('N-301')).toBe(true);
    expect(nodePattern.test('N-DEPT_ENG')).toBe(true);
    expect(nodePattern.test('INVALID')).toBe(false);
    expect(nodePattern.test('N-1')).toBe(false);
  });

  it('detects circular re-parenting move attempts correctly', () => {
    const parentPath = ',N-001,N-101,N-201,N-301,';
    const draggedNodeId = 'N-201';

    const isCircular = parentPath.includes(`,${draggedNodeId},`);
    expect(isCircular).toBe(true);
  });

  it('createNode calls POST /nodes via gateway client', async () => {
    const mockResponse = {
      id: '66b26d8f8a84a51e3c8b9999',
      projectId: 'PRJ-99201',
      nodeId: 'N-302',
      name: 'Quality Assurance Team',
      type: 'TEAM',
      parentId: 'N-201',
      path: ',N-001,N-101,N-201,N-302,',
      depth: 4,
      status: 'ACTIVE' as const,
    };

    const spy = vi.spyOn(apiClient, 'post').mockResolvedValue({
      success: true,
      data: mockResponse,
    });

    const payload: CreateNodeRequest = {
      projectId: 'PRJ-99201',
      nodeId: 'N-302',
      name: 'Quality Assurance Team',
      type: 'TEAM',
      parentId: 'N-201',
    };

    const result = await orgApi.createNode(payload);

    expect(spy).toHaveBeenCalledWith('/nodes', payload);
    expect(result.nodeId).toBe('N-302');
    expect(result.depth).toBe(4);
  });

  it('moveNode calls POST /nodes/{nodeId}/move via gateway client', async () => {
    const mockResponse = {
      projectId: 'PRJ-99201',
      nodeId: 'N-301',
      name: 'Container Logistics Team',
      type: 'TEAM',
      parentId: 'N-102',
      path: ',N-001,N-102,N-301,',
      depth: 3,
      status: 'ACTIVE' as const,
    };

    const spy = vi.spyOn(apiClient, 'post').mockResolvedValue({
      success: true,
      data: mockResponse,
    });

    const result = await orgApi.moveNode('N-301', 'N-102', 'PRJ-99201');

    expect(spy).toHaveBeenCalledWith('/nodes/N-301/move', { newParentId: 'N-102' }, { params: { projectId: 'PRJ-99201' } });
    expect(result.parentId).toBe('N-102');
  });

  it('inspectAnomalies calls GET /nodes/anomalies via gateway client', async () => {
    const mockReport = {
      projectId: 'PRJ-99201',
      totalNodesInspected: 15,
      totalAnomaliesDetected: 1,
      anomalies: [
        {
          anomalyType: 'EXCESSIVE_DEPTH',
          nodeId: 'N-999',
          severity: 'HIGH' as const,
          message: 'Node depth 8 exceeds maximum recommended depth of 7',
          recommendation: 'Re-parent subtree closer to Root',
        },
      ],
    };

    const spy = vi.spyOn(apiClient, 'get').mockResolvedValue({
      success: true,
      data: mockReport,
    });

    const result = await orgApi.inspectAnomalies('PRJ-99201');

    expect(spy).toHaveBeenCalledWith('/nodes/anomalies', { params: { projectId: 'PRJ-99201' } });
    expect(result.totalAnomaliesDetected).toBe(1);
  });
});
