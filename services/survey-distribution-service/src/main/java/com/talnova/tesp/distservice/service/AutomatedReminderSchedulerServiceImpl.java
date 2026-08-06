package com.talnova.tesp.distservice.service;

import com.talnova.tesp.distservice.domain.model.CampaignStatus;
import com.talnova.tesp.distservice.domain.model.DistributionChannel;
import com.talnova.tesp.distservice.domain.model.SurveyCampaignDocument;
import com.talnova.tesp.distservice.dto.ChannelMessageDispatchDTO;
import com.talnova.tesp.distservice.dto.DispatchResultDTO;
import com.talnova.tesp.distservice.dto.MessageTemplateRequestDTO;
import com.talnova.tesp.distservice.dto.ReminderNudgeTargetDTO;
import com.talnova.tesp.distservice.repository.CampaignRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AutomatedReminderSchedulerServiceImpl implements AutomatedReminderSchedulerService {

    private static final Logger log = LoggerFactory.getLogger(AutomatedReminderSchedulerServiceImpl.class);

    private final CampaignRepository campaignRepository;
    private final ReminderTargetPollerService pollerService;
    private final DistributionChannelRouter channelRouter;
    private final MultiChannelDispatcherService dispatcherService;

    public AutomatedReminderSchedulerServiceImpl(CampaignRepository campaignRepository, ReminderTargetPollerService pollerService, DistributionChannelRouter channelRouter, MultiChannelDispatcherService dispatcherService) {
        this.campaignRepository = campaignRepository;
        this.pollerService = pollerService;
        this.channelRouter = channelRouter;
        this.dispatcherService = dispatcherService;
    }

    @Override
    @Scheduled(cron = "${tesp.distribution.reminder-cron:0 0 9 * * *}")
    public int executeScheduledReminderSequence() {
        log.info("Triggering scheduled automated reminder sequence execution...");
        List<SurveyCampaignDocument> activeCampaigns = campaignRepository.findByStatusAndIsDeletedFalse(CampaignStatus.ACTIVE);
        log.info("Found {} ACTIVE survey campaigns eligible for reminder nudges", activeCampaigns.size());

        int totalRemindersDispatched = 0;
        for (SurveyCampaignDocument campaign : activeCampaigns) {
            try {
                totalRemindersDispatched += processRemindersForCampaign(campaign);
            } catch (Exception e) {
                log.error("Failed executing scheduled reminder sequence for campaign '{}': {}", campaign.getCampaignId(), e.getMessage(), e);
            }
        }

        log.info("Automated reminder sequence complete. Total reminders dispatched: {}", totalRemindersDispatched);
        return totalRemindersDispatched;
    }

    @Override
    public int processRemindersForCampaign(SurveyCampaignDocument campaign) {
        log.info("Processing automated reminder nudges for campaign '{}' ({})", campaign.getCampaignId(), campaign.getTitle());

        List<ReminderNudgeTargetDTO> uncompletedTargets = pollerService.pollUncompletedReminderTargets(campaign.getProjectId(), campaign.getCampaignId());
        if (uncompletedTargets.isEmpty()) {
            log.info("No uncompleted recipients found for campaign '{}'. Skipping nudge dispatch.", campaign.getCampaignId());
            return 0;
        }

        List<ChannelMessageDispatchDTO> nudgeMessages = new ArrayList<>();
        for (ReminderNudgeTargetDTO target : uncompletedTargets) {
            for (DistributionChannel channel : campaign.getChannels()) {
                MessageTemplateRequestDTO req = MessageTemplateRequestDTO.builder()
                        .projectId(campaign.getProjectId())
                        .campaignId(campaign.getCampaignId())
                        .campaignTitle(campaign.getTitle())
                        .employeeId(target.getEmployeeId())
                        .recipientContact(target.getEmployeeId() != null ? target.getEmployeeId() + "@aitkenspence.lk" : "anonymous")
                        .channel(channel)
                        .locale("en-US")
                        .token(target.getToken())
                        .kioskPin(target.getKioskPin())
                        .build();

                nudgeMessages.add(channelRouter.buildAndHydrateMessage(req));
            }
        }

        List<DispatchResultDTO> results = dispatcherService.dispatchBatchMessages(nudgeMessages);
        long successCount = results.stream().filter(r -> r.getStatus() == DispatchResultDTO.DispatchStatus.SENT).count();

        // Update campaign metrics
        if (campaign.getMetrics() != null) {
            campaign.getMetrics().setSent(campaign.getMetrics().getSent() + (int) successCount);
            campaignRepository.save(campaign);
        }

        log.info("Successfully dispatched {} reminder nudges for campaign '{}'", successCount, campaign.getCampaignId());
        return (int) successCount;
    }
}
