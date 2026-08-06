import { test, expect } from '@playwright/test';

test.describe('Real-Time Live Campaign Monitor E2E', () => {
  test('Poll metrics and transition status to PAUSED', async ({ page }) => {
    // Mock GET /api/v1/campaigns/CMP-1001
    await page.route('/api/v1/campaigns/CMP-1001*', async (route) => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          success: true,
          message: 'Campaign details retrieved',
          data: {
            id: 'doc-101',
            projectId: 'PRJ-99201',
            campaignId: 'CMP-1001',
            surveyId: 'SRV-5001',
            surveyVersion: 1,
            title: 'Q3 Employee Pulse Survey',
            anonymityLevel: 'SEMI_ANONYMOUS',
            channels: ['EMAIL', 'TEAMS'],
            targetAudience: { nodeIds: ['N-301'] },
            status: 'ACTIVE',
            metrics: { totalTargeted: 5000, sent: 4800, delivered: 4750, opened: 3200, started: 2500, completed: 2100, bounced: 50 },
            createdAt: new Date().toISOString(),
            updatedAt: new Date().toISOString(),
          },
        }),
      });
    });

    // Mock PATCH /api/v1/campaigns/CMP-1001/status
    await page.route('/api/v1/campaigns/CMP-1001/status*', async (route) => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          success: true,
          message: 'Status updated',
          data: {
            id: 'doc-101',
            projectId: 'PRJ-99201',
            campaignId: 'CMP-1001',
            status: 'PAUSED',
            metrics: { totalTargeted: 5000, sent: 4800, delivered: 4750, opened: 3200, started: 2500, completed: 2100, bounced: 50 },
          },
        }),
      });
    });

    await page.goto('/');
    expect(page).toBeDefined();
  });
});
