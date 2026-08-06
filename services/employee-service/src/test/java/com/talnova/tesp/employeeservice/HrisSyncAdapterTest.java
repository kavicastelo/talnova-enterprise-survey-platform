package com.talnova.tesp.employeeservice;

import com.talnova.tesp.employeeservice.dto.BulkImportResultDTO;
import com.talnova.tesp.employeeservice.dto.CreateEmployeeDTO;
import com.talnova.tesp.employeeservice.dto.HrisSyncRequestDTO;
import com.talnova.tesp.employeeservice.service.BulkImportService;
import com.talnova.tesp.employeeservice.service.hris.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HrisSyncAdapterTest {

    @Mock
    private BulkImportService bulkImportService;

    private WorkdaySyncAdapter workdayAdapter;
    private SuccessFactorsSyncAdapter successFactorsAdapter;
    private HrisSyncServiceImpl hrisSyncService;

    @BeforeEach
    void setUp() {
        workdayAdapter = new WorkdaySyncAdapter();
        successFactorsAdapter = new SuccessFactorsSyncAdapter();
        hrisSyncService = new HrisSyncServiceImpl(List.of(workdayAdapter, successFactorsAdapter), bulkImportService);
    }

    @Test
    @DisplayName("TC-EMP-501-A: WorkdaySyncAdapter fetches Workday RaaS roster and maps to CreateEmployeeDTO FR-EMP-007")
    void testWorkdaySyncAdapterFetchRoster() {
        List<CreateEmployeeDTO> roster = workdayAdapter.fetchRoster("PRJ-99201", "https://workday.com/raas", "token123");

        assertNotNull(roster);
        assertFalse(roster.isEmpty());
        assertEquals("WD-9001", roster.get(0).getEmployeeId());
        assertEquals("Workday User Alpha", roster.get(0).getFullName());
        assertEquals("N-201", roster.get(0).getNodeId());
    }

    @Test
    @DisplayName("TC-EMP-501-B: HrisSyncServiceImpl orchestrates Workday sync with BulkImportService SLA-EMP-03")
    void testHrisSyncServiceImplOrchestration() {
        HrisSyncRequestDTO request = new HrisSyncRequestDTO(
                "PRJ-99201",
                HrisProvider.WORKDAY,
                "https://workday.com/raas",
                "token123",
                true
        );

        BulkImportResultDTO expectedResult = BulkImportResultDTO.builder()
                .jobId("JOB-WD-100")
                .projectId("PRJ-99201")
                .totalProcessed(2)
                .updatedCount(2)
                .build();

        when(bulkImportService.processCsvImport(eq("PRJ-99201"), any(InputStream.class), eq(true)))
                .thenReturn(expectedResult);

        BulkImportResultDTO result = hrisSyncService.syncHrisRoster(request);

        verify(bulkImportService).processCsvImport(eq("PRJ-99201"), any(InputStream.class), eq(true));
        assertNotNull(result);
        assertEquals(2, result.getTotalProcessed());
    }
}
