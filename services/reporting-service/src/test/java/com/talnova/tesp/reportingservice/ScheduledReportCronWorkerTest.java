package com.talnova.tesp.reportingservice;

import com.talnova.tesp.reportingservice.domain.model.ReportJobDocument;
import com.talnova.tesp.reportingservice.domain.model.ReportStatus;
import com.talnova.tesp.reportingservice.domain.model.ReportType;
import com.talnova.tesp.reportingservice.queue.ReportJobQueueProducerService;
import com.talnova.tesp.reportingservice.repository.ReportJobRepository;
import com.talnova.tesp.reportingservice.scheduler.ScheduledReportCronWorkerImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScheduledReportCronWorkerTest {

    @Mock
    private ReportJobRepository reportJobRepository;

    @Mock
    private ReportJobQueueProducerService queueProducerService;

    private ScheduledReportCronWorkerImpl cronWorker;

    @BeforeEach
    void setUp() {
        cronWorker = new ScheduledReportCronWorkerImpl(reportJobRepository, queueProducerService);
    }

    @Test
    @DisplayName("TC-RPT-701-01: Trigger Quartz scheduled cron report dispatch per FR-RPT-007 and US-RPT-003")
    void testTriggerScheduledReportSuccess() {
        when(reportJobRepository.save(any(ReportJobDocument.class))).thenAnswer(i -> i.getArgument(0));
        doNothing().when(queueProducerService).enqueueJob(any(ReportJobDocument.class));

        ReportJobDocument job = cronWorker.triggerScheduledReport(
                "PRJ-99201",
                "CMP-1001",
                "N-201",
                ReportType.DEPT_BREAKDOWN_PDF,
                "0 0 7 ? * MON"
        );

        assertNotNull(job);
        assertTrue(job.getJobId().startsWith("CRON-"));
        assertEquals("QUARTZ_SCHEDULER_CRON", job.getRequestedBy());
        assertEquals(ReportStatus.QUEUED, job.getStatus());

        verify(reportJobRepository, times(1)).save(any(ReportJobDocument.class));
        verify(queueProducerService, times(1)).enqueueJob(any(ReportJobDocument.class));
    }
}
