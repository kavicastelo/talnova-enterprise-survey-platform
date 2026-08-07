package com.talnova.tesp.employeeservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talnova.tesp.employeeservice.controller.EmployeeController;
import com.talnova.tesp.employeeservice.domain.EmployeeStatus;
import com.talnova.tesp.employeeservice.dto.EmployeeResponseDTO;
import com.talnova.tesp.employeeservice.exception.GlobalExceptionHandler;
import com.talnova.tesp.employeeservice.service.BulkImportService;
import com.talnova.tesp.employeeservice.service.EmployeeService;
import com.talnova.tesp.employeeservice.service.hris.HrisSyncService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = EmployeeController.class)
@ContextConfiguration(classes = {EmployeeController.class, GlobalExceptionHandler.class})
@AutoConfigureMockMvc(addFilters = false)
class EmployeeSecurityPiiAuditTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    @MockBean
    private BulkImportService bulkImportService;

    @MockBean
    private HrisSyncService hrisSyncService;

    @Test
    @DisplayName("PR-EMP-003: DEPARTMENT_MANAGER receives masked PII fields")
    void testDepartmentManagerPiiMasking() throws Exception {
        EmployeeResponseDTO mockEmp = EmployeeResponseDTO.builder()
                .id("66b26d8f8a84a51e3c8b1111")
                .projectId("PRJ-99201")
                .employeeId("EMP-101")
                .email("john.doe@company.com")
                .fullName("John Doe")
                .phoneNumber("+15551234567")
                .nodeId("N-101")
                .status(EmployeeStatus.ACTIVE)
                .attributes(Map.of("TenureYears", 5))
                .build();

        when(employeeService.getEmployeeByProjectIdAndEmployeeId("PRJ-99201", "EMP-101"))
                .thenReturn(mockEmp);

        mockMvc.perform(get("/api/v1/employees/EMP-101")
                        .header("X-Project-ID", "PRJ-99201")
                        .header("X-User-Roles", "DEPARTMENT_MANAGER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("j***e@company.com"))
                .andExpect(jsonPath("$.data.fullName").value("J*** D***"))
                .andExpect(jsonPath("$.data.phoneNumber").value("+15****567"));
    }

    @Test
    @DisplayName("PR-EMP-001: HR_MANAGER receives unmasked raw PII fields")
    void testHrManagerRawPiiAccess() throws Exception {
        EmployeeResponseDTO mockEmp = EmployeeResponseDTO.builder()
                .id("66b26d8f8a84a51e3c8b1111")
                .projectId("PRJ-99201")
                .employeeId("EMP-101")
                .email("john.doe@company.com")
                .fullName("John Doe")
                .phoneNumber("+15551234567")
                .nodeId("N-101")
                .status(EmployeeStatus.ACTIVE)
                .attributes(Map.of("TenureYears", 5))
                .build();

        when(employeeService.getEmployeeByProjectIdAndEmployeeId("PRJ-99201", "EMP-101"))
                .thenReturn(mockEmp);

        mockMvc.perform(get("/api/v1/employees/EMP-101")
                        .header("X-Project-ID", "PRJ-99201")
                        .header("X-User-Roles", "HR_MANAGER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("john.doe@company.com"))
                .andExpect(jsonPath("$.data.fullName").value("John Doe"))
                .andExpect(jsonPath("$.data.phoneNumber").value("+15551234567"));
    }

    @Test
    @DisplayName("X-Project-ID Header Fallback: Fetch employee without explicit URL parameter")
    void testProjectIdHeaderFallback() throws Exception {
        EmployeeResponseDTO mockEmp = EmployeeResponseDTO.builder()
                .id("66b26d8f8a84a51e3c8b1111")
                .projectId("PRJ-99201")
                .employeeId("EMP-101")
                .email("john.doe@company.com")
                .fullName("John Doe")
                .nodeId("N-101")
                .status(EmployeeStatus.ACTIVE)
                .build();

        when(employeeService.getEmployeeByProjectIdAndEmployeeId("PRJ-99201", "EMP-101"))
                .thenReturn(mockEmp);

        mockMvc.perform(get("/api/v1/employees/EMP-101")
                        .header("X-Project-ID", "PRJ-99201"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
