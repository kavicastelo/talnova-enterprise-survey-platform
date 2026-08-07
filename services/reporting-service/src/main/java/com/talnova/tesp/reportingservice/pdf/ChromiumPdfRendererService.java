package com.talnova.tesp.reportingservice.pdf;

public interface ChromiumPdfRendererService {

    /**
     * Renders white-label HTML content string to PDF binary byte stream in < 2.5 seconds per FR-RPT-002 and SLA-RPT-01.
     */
    byte[] renderHtmlToPdf(String htmlContent);
}
