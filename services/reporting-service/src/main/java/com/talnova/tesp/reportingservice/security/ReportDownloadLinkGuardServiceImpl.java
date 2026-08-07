package com.talnova.tesp.reportingservice.security;

import com.talnova.tesp.reportingservice.domain.model.ReportJobDocument;
import com.talnova.tesp.reportingservice.dto.ReportJobResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class ReportDownloadLinkGuardServiceImpl implements ReportDownloadLinkGuardService {

    private static final Logger log = LoggerFactory.getLogger(ReportDownloadLinkGuardServiceImpl.class);

    @Override
    public ReportJobResponseDTO validateAndSecureDownloadLink(ReportJobDocument job) {
        log.info("Evaluating pre-signed download URL expiration guard for jobId '{}' per BR-RPT-003", job.getJobId());

        Instant now = Instant.now();
        boolean isExpired = job.getExpiresAt() != null && now.isAfter(job.getExpiresAt());

        if (isExpired) {
            log.warn("SECURITY ALERT: Download link for report jobId '{}' has EXPIRED (ExpiresAt: {}, CurrentTime: {}) per BR-RPT-003",
                    job.getJobId(), job.getExpiresAt(), now);

            return ReportJobResponseDTO.builder()
                    .jobId(job.getJobId())
                    .projectId(job.getProjectId())
                    .campaignId(job.getCampaignId())
                    .reportType(job.getReportType())
                    .nodeId(job.getNodeId())
                    .status(job.getStatus())
                    .downloadUrl(null) // Revoke expired URL per BR-RPT-003
                    .createdAt(job.getCreatedAt())
                    .expiresAt(job.getExpiresAt())
                    .message("Pre-signed download URL expired after 24 hours (86400s TTL) per BR-RPT-003")
                    .build();
        }

        return ReportJobResponseDTO.builder()
                .jobId(job.getJobId())
                .projectId(job.getProjectId())
                .campaignId(job.getCampaignId())
                .reportType(job.getReportType())
                .nodeId(job.getNodeId())
                .status(job.getStatus())
                .downloadUrl(job.getDownloadUrl())
                .createdAt(job.getCreatedAt())
                .expiresAt(job.getExpiresAt())
                .message("Download link active")
                .build();
    }
}
