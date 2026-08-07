package com.talnova.tesp.reportingservice.security;

import com.talnova.tesp.reportingservice.domain.model.ReportJobDocument;
import com.talnova.tesp.reportingservice.dto.ReportJobResponseDTO;

public interface ReportDownloadLinkGuardService {

    /**
     * Validates expiration status of pre-signed report download URLs and revokes access after 24 hours (86400s TTL) per BR-RPT-003.
     */
    ReportJobResponseDTO validateAndSecureDownloadLink(ReportJobDocument job);
}
