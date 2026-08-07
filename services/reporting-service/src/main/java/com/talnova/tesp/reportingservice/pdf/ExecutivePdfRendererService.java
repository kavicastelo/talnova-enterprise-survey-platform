package com.talnova.tesp.reportingservice.pdf;

import com.talnova.tesp.reportingservice.dto.ExecutiveReportDataDTO;

public interface ExecutivePdfRendererService {

    /**
     * Compiles a 10-page Executive Briefing PDF incorporating white-label branding, eNPS cards, and N < 5 anonymity suppression per FR-RPT-002 and BR-RPT-001.
     */
    byte[] renderExecutivePdf(String projectId, ExecutiveReportDataDTO reportData);
}
