package com.talnova.tesp.reportingservice.config;

import com.talnova.tesp.reportingservice.domain.model.ReportJobDocument;
import com.talnova.tesp.reportingservice.domain.model.ReportStatus;
import com.talnova.tesp.reportingservice.domain.model.ReportType;
import com.talnova.tesp.reportingservice.repository.ReportJobRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@Profile("!test")
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    private final ReportJobRepository reportJobRepository;

    public DataInitializer(ReportJobRepository reportJobRepository) {
        this.reportJobRepository = reportJobRepository;
    }

    @Override
    public void run(String... args) {
        seedReportJobs("PRJ-99201");
        seedReportJobs("PRJ-DEFAULT-001");
    }

    private void seedReportJobs(String projectId) {
        try {
            if (reportJobRepository.findByJobId("JOB-88201").isEmpty()) {
                log.info("Seeding demo report jobs JOB-88201 and JOB-88202 for project '{}'...", projectId);

                ReportJobDocument job1 = ReportJobDocument.builder()
                        .jobId("JOB-88201")
                        .projectId(projectId)
                        .campaignId("CMP-77102")
                        .reportType(ReportType.EXEC_SUMMARY_PDF)
                        .nodeId("N-001")
                        .requestedBy("usr_admin_01")
                        .status(ReportStatus.COMPLETED)
                        .downloadUrl("https://example.com/reports/JOB-88201.pdf")
                        .createdAt(Instant.now())
                        .completedAt(Instant.now())
                        .build();

                ReportJobDocument job2 = ReportJobDocument.builder()
                        .jobId("JOB-88202")
                        .projectId(projectId)
                        .campaignId("CMP-77102")
                        .reportType(ReportType.AGGREGATED_SCORES_XLSX)
                        .nodeId("N-201")
                        .requestedBy("usr_admin_01")
                        .status(ReportStatus.COMPLETED)
                        .downloadUrl("https://example.com/reports/JOB-88202.xlsx")
                        .createdAt(Instant.now())
                        .completedAt(Instant.now())
                        .build();

                reportJobRepository.save(job1);
                reportJobRepository.save(job2);
                log.info("Seeded report jobs JOB-88201 and JOB-88202 for project '{}'", projectId);
            }
        } catch (Exception ex) {
            log.debug("Notice: Report DataInitializer skipped for {}: {}", projectId, ex.getMessage());
        }
    }
}
