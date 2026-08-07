package com.talnova.tesp.reportingservice.template;

import com.talnova.tesp.reportingservice.dto.ExecutiveReportDataDTO;

public interface ReportHtmlTemplateEngine {

    /**
     * Compiles white-label HTML report String injecting branding tokens, eNPS cards, and AI summaries per FR-RPT-004.
     */
    String renderExecutiveSummaryHtml(ExecutiveReportDataDTO reportData);
}
