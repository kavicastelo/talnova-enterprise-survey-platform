package com.talnova.tesp.distservice;

import com.talnova.tesp.distservice.domain.model.AnonymityLevel;
import com.talnova.tesp.distservice.domain.model.CampaignStatus;
import com.talnova.tesp.distservice.domain.model.DistributionChannel;
import com.talnova.tesp.distservice.domain.model.SurveyCampaignDocument;
import com.talnova.tesp.distservice.dto.CampaignCreateDTO;
import com.talnova.tesp.distservice.dto.CampaignResponseDTO;
import com.talnova.tesp.distservice.dto.TargetAudienceDTO;
import com.talnova.tesp.distservice.exception.CampaignValidationException;
import com.talnova.tesp.distservice.mapper.CampaignMapper;
import com.talnova.tesp.distservice.repository.CampaignRepository;
import com.talnova.tesp.distservice.service.CampaignServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CampaignServiceTest {

    @Mock
    private CampaignRepository campaignRepository;

    private CampaignMapper campaignMapper;
    private CampaignServiceImpl campaignService;

    @BeforeEach
    void setUp() {
        campaignMapper = new CampaignMapper();
        campaignService = new CampaignServiceImpl(campaignRepository, campaignMapper);
    }

    @Test
    @DisplayName("TC-DST-102-01: Successfully create and launch active survey campaign")
    void testCreateCampaignSuccess() {
        String projectId = "PRJ-99201";
        String campaignId = "CMP-1001";
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(86400 * 14);

        CampaignCreateDTO dto = CampaignCreateDTO.builder()
                .projectId(projectId)
                .campaignId(campaignId)
                .surveyId("SRV-5001")
                .surveyVersion(1)
                .title("Q3 Employee Pulse Survey")
                .anonymityLevel(AnonymityLevel.SEMI_ANONYMOUS)
                .channels(List.of(DistributionChannel.EMAIL, DistributionChannel.TEAMS))
                .targetAudience(TargetAudienceDTO.builder().nodeIds(List.of("N-301")).build())
                .startDate(now)
                .expirationDate(expiry)
                .build();

        when(campaignRepository.existsByProjectIdAndCampaignIdAndIsDeletedFalse(projectId, campaignId)).thenReturn(false);
        when(campaignRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CampaignResponseDTO response = campaignService.createCampaign(dto);

        assertNotNull(response);
        assertEquals(campaignId, response.getCampaignId());
        assertEquals(CampaignStatus.ACTIVE, response.getStatus());
        assertEquals(2, response.getMetrics().getTotalTargeted());
    }

    @Test
    @DisplayName("TC-DST-102-02: Expiration date < 24 hours triggers VR-DST-003 validation failure")
    void testExpirationDateTooSoonFailsValidation() {
        CampaignCreateDTO dto = CampaignCreateDTO.builder()
                .projectId("PRJ-99201")
                .campaignId("CMP-1002")
                .surveyId("SRV-5001")
                .surveyVersion(1)
                .title("Invalid Expiration Campaign")
                .anonymityLevel(AnonymityLevel.AUTHENTICATED)
                .channels(List.of(DistributionChannel.EMAIL))
                .startDate(Instant.now())
                .expirationDate(Instant.now().plusSeconds(3600)) // Only 1 hour!
                .build();

        assertThrows(CampaignValidationException.class, () -> campaignService.createCampaign(dto));
    }

    @Test
    @DisplayName("TC-DST-102-03: Validate state transitions in Campaign State Machine")
    void testCampaignStateTransitions() {
        String projectId = "PRJ-99201";
        String campaignId = "CMP-1001";

        SurveyCampaignDocument doc = SurveyCampaignDocument.builder()
                .projectId(projectId)
                .campaignId(campaignId)
                .status(CampaignStatus.ACTIVE)
                .build();

        when(campaignRepository.findByProjectIdAndCampaignIdAndIsDeletedFalse(projectId, campaignId))
                .thenReturn(Optional.of(doc));
        when(campaignRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // ACTIVE -> PAUSED (Valid)
        CampaignResponseDTO pausedRes = campaignService.updateCampaignStatus(projectId, campaignId, CampaignStatus.PAUSED);
        assertEquals(CampaignStatus.PAUSED, pausedRes.getStatus());

        // PAUSED -> ACTIVE (Valid)
        CampaignResponseDTO activeRes = campaignService.updateCampaignStatus(projectId, campaignId, CampaignStatus.ACTIVE);
        assertEquals(CampaignStatus.ACTIVE, activeRes.getStatus());

        // ACTIVE -> COMPLETED (Valid terminal state)
        CampaignResponseDTO completedRes = campaignService.updateCampaignStatus(projectId, campaignId, CampaignStatus.COMPLETED);
        assertEquals(CampaignStatus.COMPLETED, completedRes.getStatus());

        // COMPLETED -> ACTIVE (Invalid transition from terminal state)
        assertThrows(CampaignValidationException.class, () -> campaignService.updateCampaignStatus(projectId, campaignId, CampaignStatus.ACTIVE));
    }

    @Test
    @DisplayName("TC-DST-102-04: Resolve Target Participant Employee IDs by Node Tree Scope")
    void testResolveTargetAudienceEmployees() {
        List<String> employeeIds = campaignService.resolveTargetParticipantEmployeeIds("PRJ-99201", List.of("N-301", "N-302"));
        assertEquals(4, employeeIds.size());
        assertTrue(employeeIds.contains("EMP-N-301-101"));
        assertTrue(employeeIds.contains("EMP-N-302-102"));
    }
}
