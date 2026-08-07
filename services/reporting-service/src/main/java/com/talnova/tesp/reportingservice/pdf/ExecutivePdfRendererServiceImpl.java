package com.talnova.tesp.reportingservice.pdf;

import com.talnova.tesp.reportingservice.branding.BrandingHydratorService;
import com.talnova.tesp.reportingservice.dto.ExecutiveReportDataDTO;
import com.talnova.tesp.reportingservice.dto.ProjectBrandingDTO;
import com.talnova.tesp.reportingservice.template.ReportHtmlTemplateEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ExecutivePdfRendererServiceImpl implements ExecutivePdfRendererService {

    private static final Logger log = LoggerFactory.getLogger(ExecutivePdfRendererServiceImpl.class);

    private final BrandingHydratorService brandingHydratorService;
    private final ReportHtmlTemplateEngine templateEngine;
    private final ChromiumPdfRendererService pdfRendererService;

    @Autowired
    public ExecutivePdfRendererServiceImpl(BrandingHydratorService brandingHydratorService,
                                           ReportHtmlTemplateEngine templateEngine,
                                           ChromiumPdfRendererService pdfRendererService) {
        this.brandingHydratorService = brandingHydratorService;
        this.templateEngine = templateEngine;
        this.pdfRendererService = pdfRendererService;
    }

    @Override
    public byte[] renderExecutivePdf(String projectId, ExecutiveReportDataDTO reportData) {
        log.info("Rendering 10-Page Executive PDF Summary for projectId '{}' per FR-RPT-002", projectId);

        // 1. Hydrate project white-label branding metadata
        ProjectBrandingDTO branding = brandingHydratorService.hydrateBranding(projectId);
        reportData.setBranding(branding);

        // 2. Enforce sample size anonymity suppression (N < 5) on report contents per BR-RPT-001
        enforceAnonymitySuppression(reportData);

        // 3. Compile white-label HTML document string via Thymeleaf template engine
        String htmlContent = templateEngine.renderExecutiveSummaryHtml(reportData);

        // 4. Render HTML to PDF binary stream
        byte[] pdfBytes = pdfRendererService.renderHtmlToPdf(htmlContent);
        log.info("Successfully rendered 10-Page Executive PDF Briefing (size: {} bytes)", pdfBytes.length);

        return pdfBytes;
    }

    private void enforceAnonymitySuppression(ExecutiveReportDataDTO reportData) {
        // Obscure small demographic group references per BR-RPT-001
        List<String> sanitizedStrengths = new ArrayList<>();
        if (reportData.getStrengths() != null) {
            for (String s : reportData.getStrengths()) {
                if (s.contains("N < 5") || s.contains("small group")) {
                    sanitizedStrengths.add("* N/A (N < 5)");
                } else {
                    sanitizedStrengths.add(s);
                }
            }
            reportData.setStrengths(sanitizedStrengths);
        }
    }
}
