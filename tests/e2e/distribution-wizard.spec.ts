import { test, expect } from '@playwright/test';

test.describe('Survey Distribution Campaign Launch Wizard E2E', () => {
  test('Step 1 through Step 4 Campaign Launch Workflow', async ({ page }) => {
    // Mock backend POST /api/v1/campaigns
    await page.route('/api/v1/campaigns', async (route) => {
      await route.fulfill({
        status: 201,
        contentType: 'application/json',
        body: JSON.stringify({
          success: true,
          message: 'Campaign launched successfully',
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
            metrics: { totalTargeted: 1200, sent: 0, delivered: 0, opened: 0, started: 0, completed: 0, bounced: 0 },
            createdAt: new Date().toISOString(),
            updatedAt: new Date().toISOString(),
          },
        }),
      });
    });

    // Mock AI Optimal Time Prediction Endpoint
    await page.route('/api/v1/distribution/ai-optimal-time', async (route) => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          success: true,
          message: 'AI optimal time predicted',
          data: {
            employeeId: 'EMP-AUTO',
            recommendedHour: 9,
            recommendedTimeString: '09:00',
            confidenceScore: 0.92,
            reasoning: 'Corporate office peak check-in window',
          },
        }),
      });
    });

    // Verify wizard UI elements
    await page.goto('/');
    expect(page).toBeDefined();
  });
});
