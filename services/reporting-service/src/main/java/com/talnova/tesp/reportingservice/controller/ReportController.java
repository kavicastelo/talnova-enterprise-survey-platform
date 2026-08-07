package com.talnova.tesp.reportingservice.controller;

import com.talnova.tesp.reportingservice.domain.model.ReportJobDocument;
import com.talnova.tesp.reportingservice.domain.model.ReportStatus;
import com.talnova.tesp.reportingservice.dto.ReportJobResponseDTO;
import com.talnova.tesp.reportingservice.dto.ReportRequestDTO;
import com.talnova.tesp.reportingservice.queue.ReportJobQueueProducerService;
import com.talnova.tesp.reportingservice.repository.ReportJobRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reports")
@Tag(name = "Reporting Engine APIs", description = "Asynchronous white-label PDF/Excel report compilation and status tracking endpoints per FEAT-009")
public class ReportController {

    private static final Logger log = LoggerFactory.getLogger(ReportController.class);

    private final ReportJobRepository reportJobRepository;
    private final ReportJobQueueProducerService queueProducerService;

    @Autowired
    public ReportController(ReportJobRepository reportJobRepository, ReportJobQueueProducerService queueProducerService) {
        this.reportJobRepository = reportJobRepository;
        this.queueProducerService = queueProducerService;
    }

    @PostMapping("/generate")
    @Operation(summary = "Submit async report generation request per FR-RPT-001", description = "Accepts report parameters, enqueues job to Redis queue, and returns HTTP 202 Accepted with jobId in < 50ms")
    public ResponseEntity<ReportJobResponseDTO> generateReport(@Valid @RequestBody ReportRequestDTO request) {
        log.info("Received async report generation request: type={}, project={}, campaign={}",
                request.getReportType(), request.getProjectId(), request.getCampaignId());

        String jobId = "JOB-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Instant now = Instant.now();

        ReportJobDocument jobDoc = ReportJobDocument.builder()
                .projectId(request.getProjectId())
                .jobId(jobId)
                .reportType(request.getReportType())
                .campaignId(request.getCampaignId())
                .nodeId(request.getNodeId())
                .requestedBy(request.getRequestedBy())
                .status(ReportStatus.QUEUED)
                .createdAt(now)
                .build();

        // Save metadata to MongoDB
        reportJobRepository.save(jobDoc);

        // Enqueue job to Redis queue tesp:reports:queue
        queueProducerService.enqueueJob(jobDoc);

        ReportJobResponseDTO response = ReportJobResponseDTO.builder()
                .jobId(jobId)
                .projectId(request.getProjectId())
                .campaignId(request.getCampaignId())
                .reportType(request.getReportType())
                .nodeId(request.getNodeId())
                .status(ReportStatus.QUEUED)
                .createdAt(now)
                .message("Report generation job successfully queued")
                .build();

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @GetMapping("/jobs/{jobId}")
    @Operation(summary = "Fetch report job status by jobId", description = "Returns job execution status, pre-signed download URL, and expiration timestamp")
    public ResponseEntity<ReportJobResponseDTO> getJobStatusByJobId(@PathVariable String jobId) {
        log.info("Fetching report job status for jobId '{}'", jobId);

        ReportJobDocument jobDoc = reportJobRepository.findByJobId(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Report job not found for jobId: " + jobId));

        ReportJobResponseDTO response = ReportJobResponseDTO.builder()
                .jobId(jobDoc.getJobId())
                .projectId(jobDoc.getProjectId())
                .campaignId(jobDoc.getCampaignId())
                .reportType(jobDoc.getReportType())
                .nodeId(jobDoc.getNodeId())
                .status(jobDoc.getStatus())
                .downloadUrl(jobDoc.getDownloadUrl())
                .createdAt(jobDoc.getCreatedAt())
                .expiresAt(jobDoc.getExpiresAt())
                .message(jobDoc.getErrorMessage())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/jobs/{projectId}/{jobId}")
    @Operation(summary = "Fetch report job status and download URL", description = "Returns job execution status, pre-signed download URL, and expiration timestamp")
    public ResponseEntity<ReportJobResponseDTO> getJobStatus(
            @PathVariable String projectId,
            @PathVariable String jobId) {
        log.info("Fetching report job status for projectId '{}' and jobId '{}'", projectId, jobId);

        ReportJobDocument jobDoc = reportJobRepository.findByProjectIdAndJobId(projectId, jobId)
                .orElseThrow(() -> new IllegalArgumentException("Report job not found for jobId: " + jobId));

        ReportJobResponseDTO response = ReportJobResponseDTO.builder()
                .jobId(jobDoc.getJobId())
                .projectId(jobDoc.getProjectId())
                .campaignId(jobDoc.getCampaignId())
                .reportType(jobDoc.getReportType())
                .nodeId(jobDoc.getNodeId())
                .status(jobDoc.getStatus())
                .downloadUrl(jobDoc.getDownloadUrl())
                .createdAt(jobDoc.getCreatedAt())
                .expiresAt(jobDoc.getExpiresAt())
                .message(jobDoc.getErrorMessage())
                .build();

        return ResponseEntity.ok(response);
    }
}
