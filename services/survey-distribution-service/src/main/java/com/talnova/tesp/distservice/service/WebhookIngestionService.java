package com.talnova.tesp.distservice.service;

import com.talnova.tesp.distservice.dto.DeliveryWebhookPayloadDTO;

public interface WebhookIngestionService {

    void processDeliveryWebhook(DeliveryWebhookPayloadDTO webhookPayload);
}
