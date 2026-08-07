package com.talnova.tesp.reportingservice.scheduler;

import com.talnova.tesp.reportingservice.domain.model.ReportJobDocument;
import com.talnova.tesp.reportingservice.domain.model.ReportType;

public interface ScheduledReportCronWorker {

    /**
     * Triggers automated scheduled report generation jobs on cron intervals per FR-RPT-007 and US-RPT-003.
     */
    ReportJobDocument triggerScheduledReport(String projectId, String campaignId, String nodeId, ReportType reportType, String cronExpression);
}
