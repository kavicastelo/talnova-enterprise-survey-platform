package com.talnova.tesp.reportingservice.excel;

import java.util.List;

public interface MultiTabExcelExporterService {

    /**
     * Generates a multi-tab XLSX workbook containing Anonymized Raw Responses (Sheet 1) and Department Cross-Tabulation Matrix with N < 5 suppression (Sheet 2) per FR-RPT-003 and BR-RPT-001.
     */
    byte[] generateMultiTabReport(List<List<Object>> rawResponses, List<List<Object>> crossTabMatrix);
}
