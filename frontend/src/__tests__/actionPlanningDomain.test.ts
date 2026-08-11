import { describe, it, expect, beforeEach, vi } from 'vitest';
import { actionPlanningApi } from '../features/action-planning/api/actionPlanningApi';
import { apiClient } from '../services/api/client';
import { ActionPlanCreateRequest, ApprovalRequest } from '../types/actionPlanning';

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

describe('FEAT-010 Action Planning Domain Integration', () => {
  beforeEach(() => {
    localStorage.clear();
    vi.restoreAllMocks();
  });

  it('getKanbanBoard calls GET /actions/kanban via gateway client', async () => {
    const mockBoard = [
      {
        actionPlanId: 'ACT-9901',
        projectId: 'PRJ-99201',
        campaignId: 'CMP-101',
        nodeId: 'N-301',
        title: 'Leadership Transparency Workshops',
        baselineScore: 48.0,
        targetScore: 75.0,
        status: 'DRAFT' as const,
        assigneeId: 'EMP-1002',
      },
    ];

    const spy = vi.spyOn(apiClient, 'get').mockResolvedValue(mockBoard);

    const result = await actionPlanningApi.getKanbanBoard('PRJ-99201', 'N-301');

    expect(spy).toHaveBeenCalledWith('/actions/kanban', {
      params: { projectId: 'PRJ-99201', nodeId: 'N-301' },
    });
    expect(result.length).toBe(1);
    expect(result[0].actionPlanId).toBe('ACT-9901');
  });

  it('createActionPlan calls POST /actions via gateway client', async () => {
    const mockCreated = {
      actionPlanId: 'ACT-9902',
      projectId: 'PRJ-99201',
      campaignId: 'CMP-101',
      title: 'Workload Balancing',
      status: 'DRAFT' as const,
    };

    const spy = vi.spyOn(apiClient, 'post').mockResolvedValue(mockCreated);

    const payload: ActionPlanCreateRequest = {
      projectId: 'PRJ-99201',
      campaignId: 'CMP-101',
      title: 'Workload Balancing',
      status: 'DRAFT',
    };

    const result = await actionPlanningApi.createActionPlan(payload);

    expect(spy).toHaveBeenCalledWith('/actions', payload);
    expect(result.actionPlanId).toBe('ACT-9902');
  });

  it('approveActionPlan calls POST /actions/{id}/approve', async () => {
    const mockApproved = {
      actionPlanId: 'ACT-9901',
      status: 'APPROVED' as const,
    };

    const spy = vi.spyOn(apiClient, 'post').mockResolvedValue(mockApproved);

    const payload: ApprovalRequest = {
      actorId: 'USR-HR-DIR',
      userRole: 'HR_MANAGER',
      rationale: 'Approved via Kanban Board',
    };

    const result = await actionPlanningApi.approveActionPlan('ACT-9901', payload);

    expect(spy).toHaveBeenCalledWith('/actions/ACT-9901/approve', payload);
    expect(result.status).toBe('APPROVED');
  });

  it('rejectActionPlan calls POST /actions/{id}/reject', async () => {
    const mockRejected = {
      actionPlanId: 'ACT-9901',
      status: 'REJECTED' as const,
    };

    const spy = vi.spyOn(apiClient, 'post').mockResolvedValue(mockRejected);

    const payload: ApprovalRequest = {
      actorId: 'USR-HR-DIR',
      userRole: 'HR_MANAGER',
      rationale: 'Budget revisions required',
    };

    const result = await actionPlanningApi.rejectActionPlan('ACT-9901', payload);

    expect(spy).toHaveBeenCalledWith('/actions/ACT-9901/reject', payload);
    expect(result.status).toBe('REJECTED');
  });

  it('transitionState delegates to approveActionPlan when targetStatus is APPROVED', async () => {
    const mockApproved = {
      actionPlanId: 'ACT-9901',
      status: 'APPROVED' as const,
    };

    const spy = vi.spyOn(apiClient, 'post').mockResolvedValue(mockApproved);

    const result = await actionPlanningApi.transitionState('ACT-9901', {
      targetStatus: 'APPROVED',
      actorId: 'USR-HR-DIR',
      userRole: 'HR_MANAGER',
      comments: 'Approved via state transition dialog',
    });

    expect(spy).toHaveBeenCalledWith('/actions/ACT-9901/approve', {
      actorId: 'USR-HR-DIR',
      userRole: 'HR_MANAGER',
      rationale: 'Approved via state transition dialog',
    });
    expect(result.status).toBe('APPROVED');
  });

  it('syncToJira calls POST /actions/{id}/sync-jira', async () => {
    const mockSyncInfo = {
      system: 'JIRA',
      externalId: 'ENG-1082',
      externalUrl: 'https://jira.talnova.com/browse/ENG-1082',
    };

    const spy = vi.spyOn(apiClient, 'post').mockResolvedValue(mockSyncInfo);

    const result = await actionPlanningApi.syncToJira('ACT-9901', 'ENG');

    expect(spy).toHaveBeenCalledWith('/actions/ACT-9901/sync-jira', {}, { params: { projectKey: 'ENG' } });
    expect(result.system).toBe('JIRA');
    expect(result.externalId).toBe('ENG-1082');
  });

  it('syncToPlanner calls POST /actions/{id}/sync-ms-planner', async () => {
    const mockPlannerSyncInfo = {
      system: 'MS_PLANNER',
      externalId: 'PLN-8801',
      externalKey: 'PLN-8801',
    };

    const spy = vi.spyOn(apiClient, 'post').mockResolvedValue(mockPlannerSyncInfo);

    const result = await actionPlanningApi.syncToPlanner('ACT-9901', 'PLN-MAIN');

    expect(spy).toHaveBeenCalledWith('/actions/ACT-9901/sync-ms-planner', {}, { params: { planId: 'PLN-MAIN' } });
    expect(result.system).toBe('MS_PLANNER');
    expect(result.externalId).toBe('PLN-8801');
  });

  it('verifyActionPlan calls POST /actions/{id}/verify', async () => {
    const mockVerified = {
      actionPlanId: 'ACT-9901',
      status: 'VERIFIED' as const,
      baselineScore: 50.0,
      postActionScore: 68.0,
    };

    const spy = vi.spyOn(apiClient, 'post').mockResolvedValue(mockVerified);

    const result = await actionPlanningApi.verifyActionPlan('ACT-9901', 68.0);

    expect(spy).toHaveBeenCalledWith('/actions/ACT-9901/verify', { postActionScore: 68.0 });
    expect(result.status).toBe('VERIFIED');
    expect(result.postActionScore).toBe(68.0);
  });
});

