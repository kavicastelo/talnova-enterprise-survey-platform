package com.talnova.tesp.reportingservice.storage;

import com.talnova.tesp.reportingservice.domain.model.ReportJobDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReportArtifactUploaderServiceImpl implements ReportArtifactUploaderService {

    private static final Logger log = LoggerFactory.getLogger(ReportArtifactUploaderServiceImpl.class);

    private final S3StorageService s3StorageService;

    @Autowired
    public ReportArtifactUploaderServiceImpl(S3StorageService s3StorageService) {
        this.s3StorageService = s3StorageService;
    }

    @Override
    public String uploadReportArtifact(ReportJobDocument job, byte[] artifactBytes) {
        if (artifactBytes == null || artifactBytes.length == 0) {
            throw new IllegalArgumentException("Artifact binary content cannot be null or empty");
        }

        boolean isPdf = job.getReportType() != null && job.getReportType().name().endsWith("PDF");
        String extension = isPdf ? "pdf" : "xlsx";
        String contentType = isPdf ? "application/pdf" : "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

        String objectKey = String.format("%s/%s/%s.%s",
                job.getProjectId(),
                job.getCampaignId(),
                job.getJobId(),
                extension);

        log.info("Uploading report artifact for jobId '{}' to S3 key path '{}' (size: {} bytes) per FR-RPT-006",
                job.getJobId(), objectKey, artifactBytes.length);

        return s3StorageService.uploadArtifact(objectKey, artifactBytes, contentType);
    }
}
