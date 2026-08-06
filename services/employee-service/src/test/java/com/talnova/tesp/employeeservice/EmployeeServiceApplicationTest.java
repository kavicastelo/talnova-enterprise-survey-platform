package com.talnova.tesp.employeeservice;

import com.talnova.tesp.employeeservice.config.MongoCsfleConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeServiceApplicationTest {

    @Test
    @DisplayName("TC-EMP-201-A: EmployeeServiceApplication main method initializes spring context DOC-002")
    void testApplicationClassInitialization() {
        assertNotNull(EmployeeServiceApplication.class);
    }

    @Test
    @DisplayName("TC-EMP-201-B: MongoCsfleConfig initializes CSFLE key vault namespace and KMS provider map FR-EMP-002")
    void testMongoCsfleConfigInitialization() {
        MongoCsfleConfig csfleConfig = new MongoCsfleConfig();
        ReflectionTestUtils.setField(csfleConfig, "kmsKeyArn", "arn:aws:kms:us-east-1:123456789012:key/tesp-csfle-key");
        ReflectionTestUtils.setField(csfleConfig, "keyVaultNamespace", "tesp_employee_db.__keyVault");

        assertEquals("arn:aws:kms:us-east-1:123456789012:key/tesp-csfle-key", csfleConfig.getKmsKeyArn());
        assertEquals("tesp_employee_db.__keyVault", csfleConfig.getKeyVaultNamespace());

        Map<String, Map<String, Object>> kmsProviders = csfleConfig.createLocalKmsProviderMap();
        assertNotNull(kmsProviders);
        assertTrue(kmsProviders.containsKey("local"));
        assertNotNull(kmsProviders.get("local").get("key"));
        assertEquals(96, ((byte[]) kmsProviders.get("local").get("key")).length);
    }
}
