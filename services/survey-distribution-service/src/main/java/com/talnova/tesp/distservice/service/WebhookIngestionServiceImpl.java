package com.talnova.tesp.distservice.service;

import com.talnova.tesp.distservice.domain.model.CampaignMetrics;
import com.talnova.tesp.distservice.domain.model.SurveyCampaignDocument;
import com.talnova.tesp.distservice.dto.DeliveryWebhookPayloadDTO;
import com.talnova.tesp.distservice.exception.CampaignNotFoundException;
import com.talnova.tesp.distservice.repository.CampaignRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WebhookIngestionServiceImpl implements WebhookIngestionService {

    private static final Logger log = LoggerFactory.getLogger(WebhookIngestionServiceImpl.class);

    private final CampaignRepository campaignRepository;

    public WebhookIngestionServiceImpl(CampaignRepository campaignRepository) {
        this.campaignRepository = campaignRepository;
    }

    @Override
    @Transactional
    public void processDeliveryWebhook(DeliveryWebhookPayloadDTO webhookPayload) {
        log.info("Processing delivery webhook event [{}] from provider {} for campaignId: {}",
                webhookPayload.getEventType(), webhookPayload.getProvider(), webhookPayload.getCampaignId());

        SurveyCampaignDocument campaign = campaignRepository.findByProjectIdAndCampaignIdAndIsDeletedFalse(webhookPayload.getProjectId(), webhookPayload.getCampaignId())
                .orElseThrow(() -> new CampaignNotFoundException("Campaign not found for ID: " + webhookPayload.getCampaignId()));

        CampaignMetrics metrics = campaign.getMetrics();
        if (metrics == null) {
            metrics = new CampaignMetrics();
            campaign.setMetrics(metrics);
        }

        switch (webhookPayload.getEventType()) {
            case DELIVERED:
                metrics.setDelivered(metrics.getDelivered() + 1);
                log.info("Incremented DELIVERED metric to {} for campaign '{}'", metrics.getDelivered(), campaign.getCampaignId());
                break;
            case OPENED:
                metrics.setOpened(metrics.getOpened() + 1);
                log.info("Incremented OPENED metric to {} for campaign '{}'", metrics.getOpened(), campaign.getCampaignId());
                break;
            case BOUNCED:
                metrics.setBounced(metrics.getBounced() + 1);
                log.info("Incremented BOUNCED metric to {} for campaign '{}'", metrics.getBounced(), campaign.getCampaignId());
                break;
            case FAILED:
                log.warn("Delivery FAILED for recipient {} on campaign '{}'", webhookPayload.getRecipientContact(), campaign.getCampaignId());
                break;
        }

        campaignRepository.save(campaign);
    }
}
