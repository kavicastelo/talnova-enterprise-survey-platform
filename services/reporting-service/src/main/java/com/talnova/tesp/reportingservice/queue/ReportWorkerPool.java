package com.talnova.tesp.reportingservice.queue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talnova.tesp.reportingservice.domain.model.ReportJobDocument;
import com.talnova.tesp.reportingservice.domain.model.ReportStatus;
import com.talnova.tesp.reportingservice.repository.ReportJobRepository;
import com.talnova.tesp.reportingservice.storage.S3StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class ReportWorkerPool {

    private static final Logger log = LoggerFactory.getLogger(ReportWorkerPool.class);

    public static final String QUEUE_KEY = "tesp:reports:queue";
    public static final String DLQ_KEY = "tesp:reports:dlq";
    public static final int MAX_RETRIES = 3;

    private final ReportJobRepository reportJobRepository;
    private final S3StorageService s3StorageService;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public ReportWorkerPool(ReportJobRepository reportJobRepository,
                            S3StorageService s3StorageService,
                            StringRedisTemplate redisTemplate,
                            ObjectMapper objectMapper) {
        this.reportJobRepository = reportJobRepository;
        this.s3StorageService = s3StorageService;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Processes a dequeued report job payload.
     * Updates status from QUEUED -> PROCESSING -> COMPLETED (or FAILED -> DLQ after 3 retries).
     */
    public boolean processJob(ReportJobDocument job) {
        log.info("Worker pool picked up report job '{}' (type: {}, project: {}) per FR-RPT-001",
                job.getJobId(), job.getReportType(), job.getProjectId());

        // Update status to PROCESSING
        job.setStatus(ReportStatus.PROCESSING);
        reportJobRepository.save(job);

        int attempts = 0;
        boolean success = false;
        Exception lastException = null;

        while (attempts < MAX_RETRIES && !success) {
            attempts++;
            try {
                log.info("Executing compilation pipeline for job '{}' (Attempt {}/{})", job.getJobId(), attempts, MAX_RETRIES);

                // Simulate compilation & binary artifact generation
                String objectKey = String.format("%s/%s/%s.%s",
                        job.getProjectId(),
                        job.getCampaignId(),
                        job.getJobId(),
                        job.getReportType().name().endsWith("PDF") ? "pdf" : "xlsx");

                byte[] dummyArtifact = ("Report Artifact Content for " + job.getJobId()).getBytes();
                String contentType = job.getReportType().name().endsWith("PDF") ? "application/pdf" : "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

                s3StorageService.uploadArtifact(objectKey, dummyArtifact, contentType);
                String preSignedUrl = s3StorageService.generatePreSignedDownloadUrl(objectKey, 86400L);

                Instant now = Instant.now();
                Instant expiresAt = now.plusSeconds(86400L);

                job.setStatus(ReportStatus.COMPLETED);
                job.setDownloadUrl(preSignedUrl);
                job.setExpiresAt(expiresAt);
                job.setCompletedAt(now);
                reportJobRepository.save(job);

                log.info("Successfully completed report job '{}'. Artifact URL issued.", job.getJobId());
                success = true;
            } catch (Exception e) {
                lastException = e;
                log.warn("Attempt {}/{} failed for report job '{}': {}", attempts, MAX_RETRIES, job.getJobId(), e.getMessage());
            }
        }

        if (!success) {
            log.error("Report job '{}' failed after {} retries. Routing payload to Dead Letter Queue '{}'",
                    job.getJobId(), MAX_RETRIES, DLQ_KEY);
            job.setStatus(ReportStatus.FAILED);
            job.setErrorMessage(lastException != null ? lastException.getMessage() : "Execution failed after retries");
            reportJobRepository.save(job);

            try {
                String dlqPayload = objectMapper.writeValueAsString(job);
                redisTemplate.opsForList().rightPush(DLQ_KEY, dlqPayload);
            } catch (Exception ex) {
                log.error("Failed to push failed job '{}' to DLQ", job.getJobId(), ex);
            }
        }

        return success;
    }
}
