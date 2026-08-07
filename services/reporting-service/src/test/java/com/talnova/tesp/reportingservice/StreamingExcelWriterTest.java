package com.talnova.tesp.reportingservice;

import com.talnova.tesp.reportingservice.excel.StreamingExcelWriterServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StreamingExcelWriterTest {

    private StreamingExcelWriterServiceImpl excelWriter;

    @BeforeEach
    void setUp() {
        excelWriter = new StreamingExcelWriterServiceImpl();
    }

    @Test
    @DisplayName("TC-RPT-501-01: Export 1,000 response rows via SXSSFWorkbook(100) streaming buffer per BR-RPT-004")
    void testWriteStreamingWorkbook() {
        List<String> headers = List.of("Response ID", "Question Group", "Score", "Anonymized Tag");
        List<List<Object>> rows = new ArrayList<>();

        for (int i = 1; i <= 1000; i++) {
            rows.add(List.of("RSP-" + i, "Culture & Values", 4.5, "ENG-DEPT"));
        }

        byte[] xlsxBytes = excelWriter.writeStreamingWorkbook("Anonymized Responses", headers, rows);

        assertNotNull(xlsxBytes);
        assertTrue(xlsxBytes.length > 0);
    }
}
