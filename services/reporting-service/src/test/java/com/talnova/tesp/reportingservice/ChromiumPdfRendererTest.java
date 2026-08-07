package com.talnova.tesp.reportingservice;

import com.talnova.tesp.reportingservice.pdf.ChromiumPdfRendererServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChromiumPdfRendererTest {

    private ChromiumPdfRendererServiceImpl pdfRenderer;

    @BeforeEach
    void setUp() {
        pdfRenderer = new ChromiumPdfRendererServiceImpl();
    }

    @Test
    @DisplayName("TC-RPT-401-01: Render HTML string to PDF binary stream within < 2.5s SLA per FR-RPT-002 and SLA-RPT-01")
    void testRenderHtmlToPdfSuccess() {
        String sampleHtml = """
                <!DOCTYPE html>
                <html>
                <head>
                    <title>Executive Briefing</title>
                    <style>
                        body { font-family: sans-serif; color: #1e293b; }
                        h1 { color: #0f172a; }
                    </style>
                </head>
                <body>
                    <h1>Acme Global Enterprise</h1>
                    <p>Q3 Executive Briefing Report</p>
                </body>
                </html>
                """;

        long start = System.currentTimeMillis();
        byte[] pdfBytes = pdfRenderer.renderHtmlToPdf(sampleHtml);
        long duration = System.currentTimeMillis() - start;

        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
        // Verify PDF Header (%PDF-)
        String header = new String(pdfBytes, 0, Math.min(5, pdfBytes.length));
        assertEquals("%PDF-", header);

        // Verify SLA < 2500ms
        assertTrue(duration < 2500, "PDF rendering duration " + duration + " ms exceeded SLA limit of 2500 ms");
    }
}
