package com.talnova.tesp.reportingservice.storage;

import com.talnova.tesp.reportingservice.domain.model.ReportJobDocument;

public interface ReportArtifactUploaderService {

    /**
     * Uploads generated report PDF/XLSX binary artifact to private S3 bucket under key path /{projectId}/{campaignId}/{jobId}.extension per FR-RPT-006.
     */
    String uploadReportArtifact(ReportJobDocument job, byte[] artifactBytes);
}
