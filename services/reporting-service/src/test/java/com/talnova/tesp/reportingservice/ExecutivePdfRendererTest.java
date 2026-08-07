package com.talnova.tesp.reportingservice;

import com.talnova.tesp.reportingservice.branding.BrandingHydratorServiceImpl;
import com.talnova.tesp.reportingservice.dto.ExecutiveReportDataDTO;
import com.talnova.tesp.reportingservice.dto.ProjectBrandingDTO;
import com.talnova.tesp.reportingservice.pdf.ChromiumPdfRendererServiceImpl;
import com.talnova.tesp.reportingservice.pdf.ExecutivePdfRendererServiceImpl;
import com.talnova.tesp.reportingservice.template.ReportHtmlTemplateEngineImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExecutivePdfRendererTest {

    private ExecutivePdfRendererServiceImpl executivePdfRenderer;

    @BeforeEach
    void setUp() {
        BrandingHydratorServiceImpl brandingHydrator = new BrandingHydratorServiceImpl();

        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML");
        resolver.setCharacterEncoding("UTF-8");

        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(resolver);
        ReportHtmlTemplateEngineImpl htmlTemplateEngine = new ReportHtmlTemplateEngineImpl(templateEngine);

        ChromiumPdfRendererServiceImpl pdfRenderer = new ChromiumPdfRendererServiceImpl();

        executivePdfRenderer = new ExecutivePdfRendererServiceImpl(brandingHydrator, htmlTemplateEngine, pdfRenderer);
    }

    @Test
    @DisplayName("TC-RPT-402-01: Render 10-page Executive PDF Briefing with N < 5 anonymity suppression per FR-RPT-002 and BR-RPT-001")
    void testRenderExecutivePdfSuccess() {
        ExecutiveReportDataDTO reportData = ExecutiveReportDataDTO.builder()
                .campaignTitle("Q3 Executive Briefing Report")
                .reportDate("2026-08-01")
                .enpsScore(52.0)
                .responseRate(91.4)
                .strengths(List.of("Strong Leadership", "small group feedback"))
                .concerns(List.of("Resource Bottlenecks"))
                .recommendations(List.of("Optimize Team Capacity"))
                .build();

        byte[] pdfBytes = executivePdfRenderer.renderExecutivePdf("PRJ-99201", reportData);

        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
        String header = new String(pdfBytes, 0, Math.min(5, pdfBytes.length));
        assertEquals("%PDF-", header);

        // Verify N < 5 suppression replaced small group text
        assertTrue(reportData.getStrengths().contains("* N/A (N < 5)"));
    }
}
