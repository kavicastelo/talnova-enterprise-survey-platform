package com.talnova.tesp.distservice;

import com.talnova.tesp.distservice.domain.model.AnonymityLevel;
import com.talnova.tesp.distservice.dto.TokenBatchGenerationRequestDTO;
import com.talnova.tesp.distservice.dto.TokenBatchGenerationResponseDTO;
import com.talnova.tesp.distservice.service.CryptographicTokenGeneratorImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CryptographicTokenGeneratorTest {

    private CryptographicTokenGeneratorImpl tokenGenerator;

    @BeforeEach
    void setUp() {
        tokenGenerator = new CryptographicTokenGeneratorImpl();
    }

    @Test
    @DisplayName("TC-DST-201-01: Verify HMAC-SHA256 Token Determinism and Security Hashing")
    void testHmacSha256TokenGeneration() {
        String token1 = tokenGenerator.generateHmacSha256Token("EMP-10020", "SALT-KEY-1");
        String token2 = tokenGenerator.generateHmacSha256Token("EMP-10020", "SALT-KEY-1");
        String tokenDifferentSalt = tokenGenerator.generateHmacSha256Token("EMP-10020", "SALT-KEY-2");

        assertNotNull(token1);
        assertEquals(64, token1.length(), "HMAC-SHA256 hex string must be 64 characters long");
        assertEquals(token1, token2, "Identical input and salt must produce identical token");
        assertNotEquals(token1, tokenDifferentSalt, "Different salt must produce different token");
    }

    @Test
    @DisplayName("TC-DST-201-02: Verify Kiosk 6-Digit PIN Generation and Collision Avoidance")
    void testKioskPinGeneration() {
        Set<String> existingPins = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            String pin = tokenGenerator.generateKioskPin(existingPins);
            assertNotNull(pin);
            assertEquals(6, pin.length(), "PIN must be exactly 6 digits");
            assertTrue(pin.matches("^[0-9]{6}$"), "PIN must consist of digits only");
        }
        assertEquals(100, existingPins.size(), "100 generated PINs must all be unique");
    }

    @Test
    @DisplayName("TC-DST-201-03: SLA Performance Test - Generate 1,000 Tokens using Java 21 Virtual Threads")
    void testBulkTokenGenerationSLA() {
        List<String> employeeIds = new ArrayList<>();
        for (int i = 1000; i < 2000; i++) {
            employeeIds.add("EMP-" + i);
        }

        TokenBatchGenerationRequestDTO request = TokenBatchGenerationRequestDTO.builder()
                .projectId("PRJ-99201")
                .campaignId("CMP-1001")
                .anonymityLevel(AnonymityLevel.KIOSK)
                .employeeIds(employeeIds)
                .campaignSalt("SALT-PERF-TEST")
                .build();

        TokenBatchGenerationResponseDTO response = tokenGenerator.generateTokensForCampaign(request);

        assertNotNull(response);
        assertEquals(1000, response.getTotalGenerated());
        assertEquals(1000, response.getTokens().size());
        assertTrue(response.getGenerationDurationMs() < 3500, "1,000 token generation must complete within SLA < 3.5s");

        // Verify all 1,000 generated tokens and Kiosk PINs are unique
        Set<String> tokenSet = new HashSet<>();
        Set<String> pinSet = new HashSet<>();
        response.getTokens().forEach(t -> {
            tokenSet.add(t.getToken());
            pinSet.add(t.getKioskPin());
        });

        assertEquals(1000, tokenSet.size(), "All tokens must be unique");
        assertEquals(1000, pinSet.size(), "All Kiosk PINs must be unique");
    }
}
