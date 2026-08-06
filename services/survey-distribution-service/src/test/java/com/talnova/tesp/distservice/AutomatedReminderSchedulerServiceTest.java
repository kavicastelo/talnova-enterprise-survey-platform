package com.talnova.tesp.distservice;

import com.talnova.tesp.distservice.domain.model.AnonymityLevel;
import com.talnova.tesp.distservice.domain.model.CampaignMetrics;
import com.talnova.tesp.distservice.domain.model.CampaignStatus;
import com.talnova.tesp.distservice.domain.model.DistributionChannel;
import com.talnova.tesp.distservice.domain.model.SurveyCampaignDocument;
import com.talnova.tesp.distservice.dto.ChannelMessageDispatchDTO;
import com.talnova.tesp.distservice.dto.DispatchResultDTO;
import com.talnova.tesp.distservice.dto.ReminderNudgeTargetDTO;
import com.talnova.tesp.distservice.repository.CampaignRepository;
import com.talnova.tesp.distservice.service.AutomatedReminderSchedulerServiceImpl;
import com.talnova.tesp.distservice.service.DistributionChannelRouter;
import com.talnova.tesp.distservice.service.MultiChannelDispatcherService;
import com.talnova.tesp.distservice.service.ReminderTargetPollerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AutomatedReminderSchedulerServiceTest {

    @Mock
    private CampaignRepository campaignRepository;

    @Mock
    private ReminderTargetPollerService pollerService;

    @Mock
    private DistributionChannelRouter channelRouter;

    @Mock
    private MultiChannelDispatcherService dispatcherService;

    private AutomatedReminderSchedulerServiceImpl schedulerService;

    @BeforeEach
    void setUp() {
        schedulerService = new AutomatedReminderSchedulerServiceImpl(campaignRepository, pollerService, channelRouter, dispatcherService);
    }

    @Test
    @DisplayName("TC-DST-502-01: Automated reminder cron processes ACTIVE campaigns and dispatches nudges")
    void testExecuteScheduledReminderSequence() {
        SurveyCampaignDocument campaign = SurveyCampaignDocument.builder()
                .projectId("PRJ-99201")
                .campaignId("CMP-1001")
                .title("Q3 Pulse Survey")
                .status(CampaignStatus.ACTIVE)
                .channels(List.of(DistributionChannel.EMAIL))
                .metrics(new CampaignMetrics())
                .build();

        ReminderNudgeTargetDTO target = ReminderNudgeTargetDTO.builder()
                .projectId("PRJ-99201")
                .campaignId("CMP-1001")
                .employeeId("EMP-10020")
                .token("TKN-101")
                .anonymityLevel(AnonymityLevel.SEMI_ANONYMOUS)
                .channels(List.of(DistributionChannel.EMAIL))
                .build();

        ChannelMessageDispatchDTO msg = ChannelMessageDispatchDTO.builder()
                .campaignId("CMP-1001")
                .channel(DistributionChannel.EMAIL)
                .build();

        DispatchResultDTO result = DispatchResultDTO.builder()
                .campaignId("CMP-1001")
                .channel(DistributionChannel.EMAIL)
                .status(DispatchResultDTO.DispatchStatus.SENT)
                .build();

        when(campaignRepository.findByStatusAndIsDeletedFalse(CampaignStatus.ACTIVE)).thenReturn(List.of(campaign));
        when(pollerService.pollUncompletedReminderTargets("PRJ-99201", "CMP-1001")).thenReturn(List.of(target));
        when(channelRouter.buildAndHydrateMessage(any())).thenReturn(msg);
        when(dispatcherService.dispatchBatchMessages(anyList())).thenReturn(List.of(result));

        int count = schedulerService.executeScheduledReminderSequence();

        assertEquals(1, count);
        verify(campaignRepository, times(1)).save(campaign);
        assertEquals(1, campaign.getMetrics().getSent());
    }

    @Test
    @DisplayName("TC-DST-502-02: Returns 0 reminders when no uncompleted targets remain")
    void testProcessRemindersForCampaignNoTargets() {
        SurveyCampaignDocument campaign = SurveyCampaignDocument.builder()
                .projectId("PRJ-99201")
                .campaignId("CMP-1001")
                .status(CampaignStatus.ACTIVE)
                .build();

        when(pollerService.pollUncompletedReminderTargets("PRJ-99201", "CMP-1001")).thenReturn(List.of());

        int count = schedulerService.processRemindersForCampaign(campaign);

        assertEquals(0, count);
        verify(dispatcherService, never()).dispatchBatchMessages(anyList());
    }
}
