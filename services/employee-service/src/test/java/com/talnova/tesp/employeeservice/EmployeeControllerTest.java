package com.talnova.tesp.employeeservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talnova.tesp.employeeservice.controller.EmployeeController;
import com.talnova.tesp.employeeservice.domain.EmployeeStatus;
import com.talnova.tesp.employeeservice.dto.BulkImportResultDTO;
import com.talnova.tesp.employeeservice.dto.CreateEmployeeDTO;
import com.talnova.tesp.employeeservice.dto.EmployeeResponseDTO;
import com.talnova.tesp.employeeservice.dto.HrisSyncRequestDTO;
import com.talnova.tesp.employeeservice.dto.UpdateEmployeeDTO;
import com.talnova.tesp.employeeservice.exception.DuplicateEmployeeIdException;
import com.talnova.tesp.employeeservice.exception.GlobalExceptionHandler;
import com.talnova.tesp.employeeservice.service.BulkImportService;
import com.talnova.tesp.employeeservice.service.EmployeeService;
import com.talnova.tesp.employeeservice.service.hris.HrisProvider;
import com.talnova.tesp.employeeservice.service.hris.HrisSyncService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = EmployeeController.class)
@ContextConfiguration(classes = {EmployeeController.class, GlobalExceptionHandler.class})
@AutoConfigureMockMvc(addFilters = false)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmployeeService employeeService;

    @MockBean
    private BulkImportService bulkImportService;

    @MockBean
    private HrisSyncService hrisSyncService;

    private CreateEmployeeDTO createDTO;
    private EmployeeResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        createDTO = CreateEmployeeDTO.builder()
                .projectId("PRJ-99201")
                .employeeId("EMP-10020")
                .email("john.doe@aitkenspence.lk")
                .fullName("John Doe")
                .phoneNumber("+94771234567")
                .nodeId("N-201")
                .matrixNodeIds(List.of("N-301"))
                .status(EmployeeStatus.ACTIVE)
                .attributes(Map.of("Tenure", "3-5 Years", "Gender", "Male"))
                .build();

        responseDTO = EmployeeResponseDTO.builder()
                .id("66b26d8f8a84a51e3c8b1111")
                .projectId("PRJ-99201")
                .employeeId("EMP-10020")
                .email("john.doe@aitkenspence.lk")
                .fullName("John Doe")
                .phoneNumber("+94771234567")
                .nodeId("N-201")
                .matrixNodeIds(List.of("N-301"))
                .status(EmployeeStatus.ACTIVE)
                .attributes(Map.of("Tenure", "3-5 Years", "Gender", "Male"))
                .build();
    }

    @Test
    @DisplayName("TC-EMP-202-A: POST /api/v1/employees creates profile and returns 201 Created with Location header")
    void testCreateEmployeeSuccess() throws Exception {
        when(employeeService.createEmployee(any(CreateEmployeeDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/v1/employees/EMP-10020"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.employeeId").value("EMP-10020"))
                .andExpect(jsonPath("$.data.nodeId").value("N-201"))
                .andExpect(jsonPath("$.data.status").value("ACTIVE"));
    }

    @Test
    @DisplayName("TC-EMP-202-B: GET /api/v1/employees/EMP-10020 returns employee details")
    void testGetEmployeeSuccess() throws Exception {
        when(employeeService.getEmployeeByProjectIdAndEmployeeId("PRJ-99201", "EMP-10020")).thenReturn(responseDTO);

        mockMvc.perform(get("/api/v1/employees/EMP-10020")
                        .param("projectId", "PRJ-99201"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.employeeId").value("EMP-10020"))
                .andExpect(jsonPath("$.data.fullName").value("John Doe"));
    }

    @Test
    @DisplayName("TC-EMP-202-C: PUT /api/v1/employees/EMP-10020 updates profile and returns 200 OK")
    void testUpdateEmployeeSuccess() throws Exception {
        UpdateEmployeeDTO updateDTO = UpdateEmployeeDTO.builder()
                .fullName("John Doe Updated")
                .nodeId("N-202")
                .build();

        EmployeeResponseDTO updatedResponse = EmployeeResponseDTO.builder()
                .id("66b26d8f8a84a51e3c8b1111")
                .projectId("PRJ-99201")
                .employeeId("EMP-10020")
                .fullName("John Doe Updated")
                .nodeId("N-202")
                .status(EmployeeStatus.ACTIVE)
                .build();

        when(employeeService.updateEmployee(eq("PRJ-99201"), eq("EMP-10020"), any(UpdateEmployeeDTO.class)))
                .thenReturn(updatedResponse);

        mockMvc.perform(put("/api/v1/employees/EMP-10020")
                        .param("projectId", "PRJ-99201")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.fullName").value("John Doe Updated"))
                .andExpect(jsonPath("$.data.nodeId").value("N-202"));
    }

    @Test
    @DisplayName("TC-EMP-202-D: Duplicate employeeId creation returns 400 Bad Request RFC 7807")
    void testDuplicateEmployeeIdReturnsBadRequest() throws Exception {
        when(employeeService.createEmployee(any(CreateEmployeeDTO.class)))
                .thenThrow(new DuplicateEmployeeIdException("EMP-10020"));

        mockMvc.perform(post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Duplicate Employee Identifier"));
    }

    @Test
    @DisplayName("TC-EMP-301-A: POST /api/v1/employees/bulk-import ingests CSV roster and returns summary report")
    void testBulkImportCsvSuccess() throws Exception {
        MockMultipartFile csvFile = new MockMultipartFile(
                "file",
                "roster.csv",
                "text/csv",
                "employeeId,fullName,email,nodeId\nEMP-101,John Doe,john@test.com,N-201".getBytes()
        );

        BulkImportResultDTO resultDTO = BulkImportResultDTO.builder()
                .jobId("JOB-1001")
                .projectId("PRJ-99201")
                .totalProcessed(1)
                .insertedCount(0)
                .updatedCount(1)
                .failedCount(0)
                .build();

        when(bulkImportService.processCsvImport(eq("PRJ-99201"), any(InputStream.class), eq(false)))
                .thenReturn(resultDTO);

        mockMvc.perform(multipart("/api/v1/employees/bulk-import")
                        .file(csvFile)
                        .param("projectId", "PRJ-99201"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalProcessed").value(1))
                .andExpect(jsonPath("$.data.updatedCount").value(1));
    }

    @Test
    @DisplayName("TC-EMP-501-A: POST /api/v1/employees/hris/sync triggers automated HRIS sync and returns summary report")
    void testSyncHrisRosterSuccess() throws Exception {
        HrisSyncRequestDTO syncRequest = new HrisSyncRequestDTO(
                "PRJ-99201",
                HrisProvider.WORKDAY,
                "https://workday.enterprise.com/raas/roster",
                "token-12345",
                true
        );

        BulkImportResultDTO resultDTO = BulkImportResultDTO.builder()
                .jobId("JOB-WD-5001")
                .projectId("PRJ-99201")
                .totalProcessed(2)
                .updatedCount(2)
                .terminatedCount(0)
                .failedCount(0)
                .build();

        when(hrisSyncService.syncHrisRoster(any(HrisSyncRequestDTO.class))).thenReturn(resultDTO);

        mockMvc.perform(post("/api/v1/employees/hris/sync")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(syncRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.jobId").value("JOB-WD-5001"))
                .andExpect(jsonPath("$.data.totalProcessed").value(2));
    }
}
