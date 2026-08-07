package com.talnova.tesp.reportingservice.pdf;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class ChromiumPdfRendererServiceImpl implements ChromiumPdfRendererService {

    private static final Logger log = LoggerFactory.getLogger(ChromiumPdfRendererServiceImpl.class);

    @Override
    public byte[] renderHtmlToPdf(String htmlContent) {
        long startTime = System.currentTimeMillis();
        log.info("Starting HTML-to-PDF compilation via OpenHTMLToPDF Headless engine per FR-RPT-002");

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(htmlContent, null);
            builder.toStream(os);
            builder.run();

            byte[] pdfBytes = os.toByteArray();
            long duration = System.currentTimeMillis() - startTime;
            log.info("Completed HTML-to-PDF compilation in {} ms (Output size: {} bytes) per SLA-RPT-01", duration, pdfBytes.length);

            if (duration > 2500) {
                log.warn("SLA BREACH WARNING: PDF compilation took {} ms (SLA Limit: 2500 ms)", duration);
            }

            return pdfBytes;
        } catch (Exception e) {
            log.error("Failed to render PDF from HTML content", e);
            throw new RuntimeException("HTML-to-PDF rendering failed", e);
        }
    }
}
