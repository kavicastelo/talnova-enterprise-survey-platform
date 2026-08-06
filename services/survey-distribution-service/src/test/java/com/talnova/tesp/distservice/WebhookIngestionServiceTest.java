package com.talnova.tesp.distservice;

import com.talnova.tesp.distservice.domain.model.CampaignMetrics;
import com.talnova.tesp.distservice.domain.model.SurveyCampaignDocument;
import com.talnova.tesp.distservice.dto.DeliveryWebhookPayloadDTO;
import com.talnova.tesp.distservice.repository.CampaignRepository;
import com.talnova.tesp.distservice.service.WebhookIngestionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebhookIngestionServiceTest {

    @Mock
    private CampaignRepository campaignRepository;

    private WebhookIngestionServiceImpl webhookService;

    @BeforeEach
    void setUp() {
        webhookService = new WebhookIngestionServiceImpl(campaignRepository);
    }

    @Test
    @DisplayName("TC-DST-602-01: Ingest DELIVERED webhook event and increment campaign delivered metrics")
    void testProcessDeliveredWebhook() {
        SurveyCampaignDocument campaign = SurveyCampaignDocument.builder()
                .projectId("PRJ-99201")
                .campaignId("CMP-1001")
                .metrics(new CampaignMetrics(100, 100, 50, 20, 10, 5, 2))
                .build();

        DeliveryWebhookPayloadDTO payload = DeliveryWebhookPayloadDTO.builder()
                .projectId("PRJ-99201")
                .campaignId("CMP-1001")
                .provider(DeliveryWebhookPayloadDTO.Provider.AWS_SES)
                .eventType(DeliveryWebhookPayloadDTO.EventType.DELIVERED)
                .externalMessageId("ses-msg-101")
                .recipientContact("user@aitkenspence.lk")
                .build();

        when(campaignRepository.findByProjectIdAndCampaignIdAndIsDeletedFalse("PRJ-99201", "CMP-1001"))
                .thenReturn(Optional.of(campaign));
        when(campaignRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        webhookService.processDeliveryWebhook(payload);

        assertEquals(51, campaign.getMetrics().getDelivered());
        verify(campaignRepository, times(1)).save(campaign);
    }

    @Test
    @DisplayName("TC-DST-602-02: Ingest OPENED and BOUNCED webhook events")
    void testProcessOpenedAndBouncedWebhooks() {
        SurveyCampaignDocument campaign = SurveyCampaignDocument.builder()
                .projectId("PRJ-99201")
                .campaignId("CMP-1001")
                .metrics(new CampaignMetrics(100, 100, 50, 20, 10, 5, 2))
                .build();

        DeliveryWebhookPayloadDTO openedPayload = DeliveryWebhookPayloadDTO.builder()
                .projectId("PRJ-99201")
                .campaignId("CMP-1001")
                .provider(DeliveryWebhookPayloadDTO.Provider.TWILIO)
                .eventType(DeliveryWebhookPayloadDTO.EventType.OPENED)
                .build();

        DeliveryWebhookPayloadDTO bouncedPayload = DeliveryWebhookPayloadDTO.builder()
                .projectId("PRJ-99201")
                .campaignId("CMP-1001")
                .provider(DeliveryWebhookPayloadDTO.Provider.AWS_SES)
                .eventType(DeliveryWebhookPayloadDTO.EventType.BOUNCED)
                .build();

        when(campaignRepository.findByProjectIdAndCampaignIdAndIsDeletedFalse("PRJ-99201", "CMP-1001"))
                .thenReturn(Optional.of(campaign));

        webhookService.processDeliveryWebhook(openedPayload);
        assertEquals(21, campaign.getMetrics().getOpened());

        webhookService.processDeliveryWebhook(bouncedPayload);
        assertEquals(3, campaign.getMetrics().getBounced());
    }
}
