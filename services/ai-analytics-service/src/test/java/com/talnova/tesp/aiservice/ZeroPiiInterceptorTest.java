package com.talnova.tesp.aiservice;

import com.talnova.tesp.aiservice.exception.PiiSecurityException;
import com.talnova.tesp.aiservice.pii.PiiSanitizerEngineImpl;
import com.talnova.tesp.aiservice.pii.ZeroPiiInterceptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ZeroPiiInterceptorTest {

    private ZeroPiiInterceptor interceptor;

    @BeforeEach
    void setUp() {
        interceptor = new ZeroPiiInterceptor(new PiiSanitizerEngineImpl());
    }

    @Test
    @DisplayName("TC-AI-202-01: Interceptor throws PiiSecurityException when unmasked email exists in outbound payload")
    void testAssertZeroPiiThrowsExceptionForUnmaskedPii() {
        String unsafePayload = "Analyze this response from john.doe@acme.com";

        PiiSecurityException exception = assertThrows(
                PiiSecurityException.class,
                () -> interceptor.assertZeroPii(unsafePayload)
        );

        assertTrue(exception.getMessage().contains("BR-AI-001"));
    }

    @Test
    @DisplayName("TC-AI-202-02: Interceptor succeeds when outbound payload is properly masked")
    void testAssertZeroPiiPassesForSanitizedPayload() {
        String safePayload = "Analyze this response from [MASKED_NAME] at [MASKED_EMAIL]";

        assertDoesNotThrow(() -> interceptor.assertZeroPii(safePayload));
    }
}
