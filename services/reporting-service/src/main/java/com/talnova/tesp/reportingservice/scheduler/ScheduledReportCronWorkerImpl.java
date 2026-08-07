package com.talnova.tesp.reportingservice.scheduler;

import com.talnova.tesp.reportingservice.domain.model.ReportJobDocument;
import com.talnova.tesp.reportingservice.domain.model.ReportStatus;
import com.talnova.tesp.reportingservice.domain.model.ReportType;
import com.talnova.tesp.reportingservice.queue.ReportJobQueueProducerService;
import com.talnova.tesp.reportingservice.repository.ReportJobRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class ScheduledReportCronWorkerImpl implements ScheduledReportCronWorker {

    private static final Logger log = LoggerFactory.getLogger(ScheduledReportCronWorkerImpl.class);

    private final ReportJobRepository reportJobRepository;
    private final ReportJobQueueProducerService queueProducerService;

    @Autowired
    public ScheduledReportCronWorkerImpl(ReportJobRepository reportJobRepository, ReportJobQueueProducerService queueProducerService) {
        this.reportJobRepository = reportJobRepository;
        this.queueProducerService = queueProducerService;
    }

    @Override
    public ReportJobDocument triggerScheduledReport(String projectId, String campaignId, String nodeId, ReportType reportType, String cronExpression) {
        log.info("Quartz Scheduled Cron triggered for project '{}', campaign '{}', node '{}' (cron: '{}') per FR-RPT-007",
                projectId, campaignId, nodeId, cronExpression);

        String jobId = "CRON-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Instant now = Instant.now();

        ReportJobDocument jobDoc = ReportJobDocument.builder()
                .projectId(projectId)
                .jobId(jobId)
                .reportType(reportType != null ? reportType : ReportType.DEPT_BREAKDOWN_PDF)
                .campaignId(campaignId)
                .nodeId(nodeId)
                .requestedBy("QUARTZ_SCHEDULER_CRON")
                .status(ReportStatus.QUEUED)
                .createdAt(now)
                .build();

        // 1. Save metadata to Mongo
        reportJobRepository.save(jobDoc);

        // 2. Enqueue job to Redis worker queue
        queueProducerService.enqueueJob(jobDoc);
        log.info("Successfully enqueued automated scheduled report job '{}'", jobId);

        return jobDoc;
    }
}
