package com.talnova.tesp.employeeservice;

import com.talnova.tesp.employeeservice.domain.EmployeeDocument;
import com.talnova.tesp.employeeservice.dto.BulkImportResultDTO;
import com.talnova.tesp.employeeservice.service.BulkImportServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoOperations;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BulkCsvImportTest {

    @Mock
    private MongoOperations mongoOperations;

    @Mock
    private BulkOperations bulkOperations;

    private BulkImportServiceImpl bulkImportService;

    @BeforeEach
    void setUp() {
        bulkImportService = new BulkImportServiceImpl(mongoOperations);
    }

    @Test
    @DisplayName("TC-EMP-301-A: processCsvImport streams CSV records and executes 5,000 batch unordered upserts FR-EMP-003")
    void testProcessCsvImportSuccess() {
        String csvContent = "employeeId,fullName,email,phoneNumber,nodeId,matrixNodeIds,status,TenureYears,Gender\n" +
                "EMP-101,John Doe,john@test.com,+94771234567,N-201,N-301,ACTIVE,3,Male\n" +
                "EMP-102,Jane Smith,jane@test.com,+94771234568,N-201,,ACTIVE,5,Female\n" +
                "EMP-103,Robert Paul,robert@test.com,+94771234569,N-202,N-401,ACTIVE,2,Male\n" +
                "EMP-104,Alice Cooper,alice@test.com,+94771234570,N-202,,ACTIVE,1,Female\n" +
                "EMP-105,Charlie Brown,charlie@test.com,+94771234571,N-203,N-301,ACTIVE,8,Male\n";

        InputStream csvStream = new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));

        when(mongoOperations.bulkOps(eq(BulkOperations.BulkMode.UNORDERED), eq(EmployeeDocument.class)))
                .thenReturn(bulkOperations);

        BulkImportResultDTO result = bulkImportService.processCsvImport("PRJ-99201", csvStream, false);

        verify(mongoOperations).bulkOps(eq(BulkOperations.BulkMode.UNORDERED), eq(EmployeeDocument.class));
        verify(bulkOperations, times(5)).upsert(any(), any());
        verify(bulkOperations).execute();

        assertNotNull(result);
        assertEquals("PRJ-99201", result.getProjectId());
        assertEquals(5, result.getTotalProcessed());
        assertEquals(5, result.getUpdatedCount());
        assertEquals(0, result.getFailedCount());
        assertTrue(result.getErrors().isEmpty());
    }
}
