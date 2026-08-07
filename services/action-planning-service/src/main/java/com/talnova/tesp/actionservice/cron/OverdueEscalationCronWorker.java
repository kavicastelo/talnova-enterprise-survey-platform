package com.talnova.tesp.actionservice.cron;

public interface OverdueEscalationCronWorker {

    /**
     * Polls active milestone due dates, dispatches reminders 3 days prior, and escalates overdue milestones to HR per FR-ACT-005.
     */
    int checkAndDispatchEscalationReminders();
}
