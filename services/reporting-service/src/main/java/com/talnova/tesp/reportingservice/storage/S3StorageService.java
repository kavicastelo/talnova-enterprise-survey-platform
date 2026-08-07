package com.talnova.tesp.reportingservice.storage;

public interface S3StorageService {

    /**
     * Uploads binary report artifact to private AWS S3 bucket ('tesp-report-exports') per FR-RPT-006.
     */
    String uploadArtifact(String objectKey, byte[] content, String contentType);

    /**
     * Generates a 24-hour pre-signed S3 download URL (86400s TTL) per BR-RPT-003.
     */
    String generatePreSignedDownloadUrl(String objectKey, long durationSeconds);
}
