package com.talnova.tesp.employeeservice;

import com.mongodb.client.result.UpdateResult;
import com.talnova.tesp.employeeservice.domain.EmployeeDocument;
import com.talnova.tesp.employeeservice.dto.BulkImportResultDTO;
import com.talnova.tesp.employeeservice.service.BulkImportServiceImpl;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BulkDeltaTerminationTest {

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
    @DisplayName("TC-EMP-302-A: autoTerminateMissing flag sets unlisted active employees to status TERMINATED BR-EMP-004")
    void testAutoTerminateMissingActiveEmployees() {
        String csvContent = "employeeId,fullName,nodeId\n" +
                "EMP-101,John Doe,N-201\n" +
                "EMP-102,Jane Smith,N-201\n";

        InputStream csvStream = new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));

        when(mongoOperations.bulkOps(eq(BulkOperations.BulkMode.UNORDERED), eq(EmployeeDocument.class)))
                .thenReturn(bulkOperations);

        UpdateResult updateResult = mock(UpdateResult.class);
        when(updateResult.getModifiedCount()).thenReturn(3L);

        when(mongoOperations.updateMulti(any(Query.class), any(Update.class), eq(EmployeeDocument.class)))
                .thenReturn(updateResult);

        BulkImportResultDTO result = bulkImportService.processCsvImport("PRJ-99201", csvStream, true);

        ArgumentCaptor<Query> queryCaptor = ArgumentCaptor.forClass(Query.class);
        ArgumentCaptor<Update> updateCaptor = ArgumentCaptor.forClass(Update.class);

        verify(mongoOperations).updateMulti(queryCaptor.capture(), updateCaptor.capture(), eq(EmployeeDocument.class));

        Query capturedQuery = queryCaptor.getValue();
        Update capturedUpdate = updateCaptor.getValue();

        Document queryDoc = capturedQuery.getQueryObject();
        Document updateDoc = capturedUpdate.getUpdateObject();

        assertEquals("PRJ-99201", queryDoc.getString("projectId"));
        assertEquals("ACTIVE", queryDoc.getString("status"));
        assertEquals(false, queryDoc.getBoolean("isDeleted"));

        Document employeeIdCriteria = (Document) queryDoc.get("employeeId");
        assertNotNull(employeeIdCriteria);
        assertTrue(employeeIdCriteria.containsKey("$nin"));

        Document setDoc = (Document) updateDoc.get("$set");
        assertNotNull(setDoc);
        assertEquals("TERMINATED", setDoc.getString("status"));

        assertNotNull(result);
        assertEquals(2, result.getTotalProcessed());
        assertEquals(2, result.getUpdatedCount());
        assertEquals(3, result.getTerminatedCount());
        assertEquals(0, result.getFailedCount());
    }
}
