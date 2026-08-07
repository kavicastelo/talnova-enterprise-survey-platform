package com.talnova.tesp.reportingservice.template;

import com.talnova.tesp.reportingservice.dto.ExecutiveReportDataDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class ReportHtmlTemplateEngineImpl implements ReportHtmlTemplateEngine {

    private static final Logger log = LoggerFactory.getLogger(ReportHtmlTemplateEngineImpl.class);

    private final TemplateEngine templateEngine;

    @Autowired
    public ReportHtmlTemplateEngineImpl(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    @Override
    public String renderExecutiveSummaryHtml(ExecutiveReportDataDTO reportData) {
        log.info("Compiling Thymeleaf white-label HTML report for company '{}' per FR-RPT-004",
                reportData.getBranding().getCompanyName());

        Context context = new Context();
        context.setVariable("reportData", reportData);

        try {
            String html = templateEngine.process("executive_summary_report", context);
            log.info("Successfully compiled executive summary HTML report (length: {} bytes)", html.length());
            return html;
        } catch (Exception e) {
            log.error("Failed to render Thymeleaf HTML report template", e);
            throw new RuntimeException("Thymeleaf HTML compilation failed", e);
        }
    }
}
