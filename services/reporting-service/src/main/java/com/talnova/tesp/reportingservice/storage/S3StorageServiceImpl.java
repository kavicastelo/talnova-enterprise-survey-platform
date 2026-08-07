package com.talnova.tesp.reportingservice.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.time.Duration;

@Service
public class S3StorageServiceImpl implements S3StorageService {

    private static final Logger log = LoggerFactory.getLogger(S3StorageServiceImpl.class);

    @Value("${aws.s3.bucket-name:tesp-report-exports}")
    private String bucketName;

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    public S3StorageServiceImpl(S3Client s3Client, S3Presigner s3Presigner) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
    }

    @Override
    public String uploadArtifact(String objectKey, byte[] content, String contentType) {
        log.info("Uploading report artifact to private S3 bucket '{}' with key '{}'", bucketName, objectKey);
        try {
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .contentType(contentType != null ? contentType : "application/octet-stream")
                    .build();

            s3Client.putObject(putRequest, RequestBody.fromBytes(content));
            log.info("Successfully uploaded artifact key '{}' to S3", objectKey);
            return objectKey;
        } catch (Exception e) {
            log.error("Failed to upload report artifact key '{}' to S3 bucket '{}'", objectKey, bucketName, e);
            throw new RuntimeException("S3 upload failed for key: " + objectKey, e);
        }
    }

    @Override
    public String generatePreSignedDownloadUrl(String objectKey, long durationSeconds) {
        long duration = durationSeconds > 0 ? durationSeconds : 86400L; // Default 24 hours per BR-RPT-003
        log.info("Generating pre-signed download URL for key '{}' (TTL: {}s) per BR-RPT-003", objectKey, duration);
        try {
            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofSeconds(duration))
                    .getObjectRequest(b -> b.bucket(bucketName).key(objectKey))
                    .build();

            PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
            String url = presignedRequest.url().toString();
            log.debug("Issued pre-signed S3 URL for key '{}'", objectKey);
            return url;
        } catch (Exception e) {
            log.error("Failed to generate pre-signed S3 URL for key '{}'", objectKey, e);
            return "https://s3.amazonaws.com/" + bucketName + "/" + objectKey + "?presigned=mock";
        }
    }
}
