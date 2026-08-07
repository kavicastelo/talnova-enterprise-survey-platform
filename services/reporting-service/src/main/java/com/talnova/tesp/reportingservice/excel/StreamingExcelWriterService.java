package com.talnova.tesp.reportingservice.excel;

import java.util.List;

public interface StreamingExcelWriterService {

    /**
     * Streams dataset to XLSX workbook using Apache POI SXSSFWorkbook(100) sliding 100-row memory window per BR-RPT-004.
     */
    byte[] writeStreamingWorkbook(String sheetName, List<String> headers, List<List<Object>> rows);
}
