package com.talnova.tesp.reportingservice;

import com.talnova.tesp.reportingservice.dto.ExecutiveReportDataDTO;
import com.talnova.tesp.reportingservice.dto.ProjectBrandingDTO;
import com.talnova.tesp.reportingservice.template.ReportHtmlTemplateEngineImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReportHtmlTemplateEngineTest {

    private ReportHtmlTemplateEngineImpl templateEngineService;

    @BeforeEach
    void setUp() {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML");
        resolver.setCharacterEncoding("UTF-8");

        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(resolver);

        templateEngineService = new ReportHtmlTemplateEngineImpl(templateEngine);
    }

    @Test
    @DisplayName("TC-RPT-302-01: Compile Thymeleaf white-label HTML report injecting corporate logo and color tokens per FR-RPT-004")
    void testRenderExecutiveSummaryHtml() {
        ProjectBrandingDTO branding = ProjectBrandingDTO.builder()
                .projectId("PRJ-99201")
                .companyName("Acme Global Enterprise")
                .logoUrl("https://cdn.talnova.com/branding/acme-logo.png")
                .primaryColorHex("#0F172A")
                .secondaryColorHex("#2563EB")
                .fontFamily("Inter, sans-serif")
                .footerText("Confidential Acme Briefing")
                .build();

        ExecutiveReportDataDTO reportData = ExecutiveReportDataDTO.builder()
                .branding(branding)
                .campaignTitle("Q3 Global Engagement Survey")
                .reportDate("2026-08-01")
                .enpsScore(45.5)
                .responseRate(88.2)
                .strengths(List.of("Strong team collaboration", "High psychological safety"))
                .concerns(List.of("Resource constraints in engineering"))
                .recommendations(List.of("Initiate workload audit"))
                .build();

        String html = templateEngineService.renderExecutiveSummaryHtml(reportData);

        assertNotNull(html);
        assertTrue(html.contains("Acme Global Enterprise"));
        assertTrue(html.contains("acme-logo.png"));
        assertTrue(html.contains("#0F172A"));
        assertTrue(html.contains("Q3 Global Engagement Survey"));
        assertTrue(html.contains("Strong team collaboration"));
    }
}
