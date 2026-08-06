package com.talnova.tesp.distservice;

import com.talnova.tesp.distservice.domain.model.AnonymityLevel;
import com.talnova.tesp.distservice.domain.model.CampaignStatus;
import com.talnova.tesp.distservice.domain.model.DistributionChannel;
import com.talnova.tesp.distservice.domain.model.IdentityTokenVaultDocument;
import com.talnova.tesp.distservice.domain.model.SurveyCampaignDocument;
import com.talnova.tesp.distservice.dto.ReminderNudgeTargetDTO;
import com.talnova.tesp.distservice.exception.CampaignValidationException;
import com.talnova.tesp.distservice.repository.CampaignRepository;
import com.talnova.tesp.distservice.repository.VaultRepository;
import com.talnova.tesp.distservice.service.ReminderTargetPollerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReminderTargetPollerServiceTest {

    @Mock
    private CampaignRepository campaignRepository;

    @Mock
    private VaultRepository vaultRepository;

    private ReminderTargetPollerServiceImpl pollerService;

    @BeforeEach
    void setUp() {
        pollerService = new ReminderTargetPollerServiceImpl(campaignRepository, vaultRepository);
    }

    @Test
    @DisplayName("TC-DST-501-01: Poll uncompleted targets returns active unburned token recipients")
    void testPollUncompletedReminderTargetsSuccess() {
        SurveyCampaignDocument campaign = SurveyCampaignDocument.builder()
                .projectId("PRJ-99201")
                .campaignId("CMP-1001")
                .status(CampaignStatus.ACTIVE)
                .channels(List.of(DistributionChannel.EMAIL))
                .build();

        IdentityTokenVaultDocument unburned1 = IdentityTokenVaultDocument.builder()
                .projectId("PRJ-99201")
                .campaignId("CMP-1001")
                .employeeId("EMP-10020")
                .token("TKN-101")
                .anonymityLevel(AnonymityLevel.SEMI_ANONYMOUS)
                .isBurned(false)
                .build();

        when(campaignRepository.findByProjectIdAndCampaignIdAndIsDeletedFalse("PRJ-99201", "CMP-1001"))
                .thenReturn(Optional.of(campaign));
        when(vaultRepository.findByCampaignIdAndIsBurnedFalse("CMP-1001"))
                .thenReturn(List.of(unburned1));

        List<ReminderNudgeTargetDTO> targets = pollerService.pollUncompletedReminderTargets("PRJ-99201", "CMP-1001");

        assertNotNull(targets);
        assertEquals(1, targets.size());
        assertEquals("EMP-10020", targets.get(0).getEmployeeId());
        assertEquals("TKN-101", targets.get(0).getToken());
    }

    @Test
    @DisplayName("TC-DST-501-02: Non-ACTIVE campaign throws CampaignValidationException on target polling")
    void testPollTargetsForNonActiveCampaignFails() {
        SurveyCampaignDocument pausedCampaign = SurveyCampaignDocument.builder()
                .projectId("PRJ-99201")
                .campaignId("CMP-1001")
                .status(CampaignStatus.PAUSED)
                .build();

        when(campaignRepository.findByProjectIdAndCampaignIdAndIsDeletedFalse("PRJ-99201", "CMP-1001"))
                .thenReturn(Optional.of(pausedCampaign));

        assertThrows(CampaignValidationException.class, () ->
                pollerService.pollUncompletedReminderTargets("PRJ-99201", "CMP-1001"));
    }
}
