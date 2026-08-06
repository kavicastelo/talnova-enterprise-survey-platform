package com.talnova.tesp.employeeservice;

import com.talnova.tesp.employeeservice.domain.EmployeeDocument;
import com.talnova.tesp.employeeservice.domain.EmployeeStatus;
import com.talnova.tesp.employeeservice.dto.EmployeeResponseDTO;
import com.talnova.tesp.employeeservice.mapper.EmployeeMapper;
import com.talnova.tesp.employeeservice.repository.EmployeeRepository;
import com.talnova.tesp.employeeservice.security.PiiMaskingUtil;
import com.talnova.tesp.employeeservice.service.EmployeeServiceImpl;
import com.talnova.tesp.employeeservice.validation.DynamicAttributeValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PiiAnonymizationTest {

    @Mock
    private EmployeeRepository repository;

    @Mock
    private DynamicAttributeValidator validator;

    private EmployeeMapper mapper;
    private EmployeeServiceImpl employeeService;

    @BeforeEach
    void setUp() {
        mapper = new EmployeeMapper();
        employeeService = new EmployeeServiceImpl(repository, mapper, validator);
    }

    @Test
    @DisplayName("TC-EMP-801-A: PiiMaskingUtil masks email, full name, and phone number SEC-EMP-01")
    void testPiiMaskingUtil() {
        assertEquals("j***e@aitkenspence.lk", PiiMaskingUtil.maskEmail("john.doe@aitkenspence.lk"));
        assertEquals("J*** D***", PiiMaskingUtil.maskFullName("John Doe"));
        assertEquals("+94****567", PiiMaskingUtil.maskPhoneNumber("+94771234567"));
    }

    @Test
    @DisplayName("TC-EMP-801-B: forgetEmployeeProfile scrambles PII and soft deletes record for GDPR Right-to-be-Forgotten GDPR-EMP-01")
    void testForgetEmployeeProfileGdprCompliance() {
        EmployeeDocument doc = EmployeeDocument.builder()
                .id("66b26d8f8a84a51e3c8b1111")
                .projectId("PRJ-99201")
                .employeeId("EMP-10020")
                .email("john.doe@aitkenspence.lk")
                .fullName("John Doe")
                .phoneNumber("+94771234567")
                .nodeId("N-201")
                .status(EmployeeStatus.ACTIVE)
                .attributes(Map.of("TenureYears", 4))
                .isDeleted(false)
                .build();

        when(repository.findActiveEmployeeByEmployeeId("PRJ-99201", "EMP-10020"))
                .thenReturn(Optional.of(doc));

        when(repository.save(any(EmployeeDocument.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        EmployeeResponseDTO response = employeeService.forgetEmployeeProfile("PRJ-99201", "EMP-10020");

        verify(repository).save(doc);
        assertNotNull(response);

        assertTrue(doc.getEmail().startsWith("anonymized-"));
        assertTrue(doc.getEmail().endsWith("@deleted.local"));
        assertEquals("ANONYMIZED_EMPLOYEE", doc.getFullName());
        assertEquals("+00000000000", doc.getPhoneNumber());
        assertTrue(doc.getAttributes().isEmpty());
        assertEquals(EmployeeStatus.TERMINATED, doc.getStatus());
        assertTrue(doc.getIsDeleted());
    }
}
