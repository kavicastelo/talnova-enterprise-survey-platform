package com.talnova.tesp.reportingservice;

import com.talnova.tesp.reportingservice.storage.S3StorageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class S3StorageServiceTest {

    @Mock
    private S3Client s3Client;

    @Mock
    private S3Presigner s3Presigner;

    private S3StorageServiceImpl s3StorageService;

    @BeforeEach
    void setUp() {
        s3StorageService = new S3StorageServiceImpl(s3Client, s3Presigner);
    }

    @Test
    @DisplayName("TC-RPT-102-01: Upload PDF report artifact to private S3 bucket")
    void testUploadArtifact() {
        byte[] pdfBytes = "PDF Binary Content".getBytes();
        String key = "PRJ-99201/CMP-1001/JOB-88102.pdf";

        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class))).thenReturn(null);

        String resultKey = s3StorageService.uploadArtifact(key, pdfBytes, "application/pdf");

        assertEquals(key, resultKey);
        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    @DisplayName("TC-RPT-102-02: Issue 24-hour pre-signed download URL per BR-RPT-003")
    void testGeneratePreSignedDownloadUrl() {
        String key = "PRJ-99201/CMP-1001/JOB-88102.pdf";

        String url = s3StorageService.generatePreSignedDownloadUrl(key, 86400L);

        assertNotNull(url);
    }
}
