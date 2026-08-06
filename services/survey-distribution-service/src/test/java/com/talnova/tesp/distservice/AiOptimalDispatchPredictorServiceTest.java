package com.talnova.tesp.distservice;

import com.talnova.tesp.distservice.domain.model.DistributionChannel;
import com.talnova.tesp.distservice.dto.OptimalDispatchPredictionRequestDTO;
import com.talnova.tesp.distservice.dto.OptimalDispatchPredictionResponseDTO;
import com.talnova.tesp.distservice.service.AiOptimalDispatchPredictorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AiOptimalDispatchPredictorServiceTest {

    private AiOptimalDispatchPredictorServiceImpl predictorService;

    @BeforeEach
    void setUp() {
        predictorService = new AiOptimalDispatchPredictorServiceImpl();
    }

    @Test
    @DisplayName("TC-DST-701-01: Corporate office department predicts 09:00 AM optimal dispatch hour")
    void testPredictCorporateDepartment() {
        OptimalDispatchPredictionRequestDTO req = OptimalDispatchPredictionRequestDTO.builder()
                .projectId("PRJ-99201")
                .employeeId("EMP-10020")
                .department("Human Resources")
                .preferredChannel(DistributionChannel.EMAIL)
                .build();

        OptimalDispatchPredictionResponseDTO res = predictorService.predictOptimalDispatchHour(req);

        assertNotNull(res);
        assertEquals(9, res.getRecommendedHour());
        assertEquals("09:00", res.getRecommendedTimeString());
        assertTrue(res.getConfidenceScore() >= 0.85);
    }

    @Test
    @DisplayName("TC-DST-701-02: Operations / Factory department predicts 14:00 PM shift break window")
    void testPredictFactoryDepartment() {
        OptimalDispatchPredictionRequestDTO req = OptimalDispatchPredictionRequestDTO.builder()
                .projectId("PRJ-99201")
                .employeeId("EMP-10021")
                .department("Plant Operations & Logistics")
                .preferredChannel(DistributionChannel.EMAIL)
                .build();

        OptimalDispatchPredictionResponseDTO res = predictorService.predictOptimalDispatchHour(req);

        assertNotNull(res);
        assertEquals(14, res.getRecommendedHour());
        assertEquals("14:00", res.getRecommendedTimeString());
    }

    @Test
    @DisplayName("TC-DST-701-03: Field Sales department predicts 18:00 PM end of day window")
    void testPredictSalesFieldDepartment() {
        OptimalDispatchPredictionRequestDTO req = OptimalDispatchPredictionRequestDTO.builder()
                .projectId("PRJ-99201")
                .employeeId("EMP-10022")
                .department("Remote Sales Team")
                .preferredChannel(DistributionChannel.EMAIL)
                .build();

        OptimalDispatchPredictionResponseDTO res = predictorService.predictOptimalDispatchHour(req);

        assertNotNull(res);
        assertEquals(18, res.getRecommendedHour());
        assertEquals("18:00", res.getRecommendedTimeString());
    }
}
