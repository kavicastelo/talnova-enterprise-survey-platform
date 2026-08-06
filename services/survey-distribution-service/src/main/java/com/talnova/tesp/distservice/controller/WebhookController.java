package com.talnova.tesp.distservice.controller;

import com.talnova.tesp.common.dto.ApiResponse;
import com.talnova.tesp.distservice.dto.DeliveryWebhookPayloadDTO;
import com.talnova.tesp.distservice.service.WebhookIngestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/webhooks")
@Tag(name = "Distribution Webhook API", description = "Endpoints for ingesting AWS SES and Twilio delivery status webhooks")
public class WebhookController {

    private final WebhookIngestionService webhookIngestionService;

    public WebhookController(WebhookIngestionService webhookIngestionService) {
        this.webhookIngestionService = webhookIngestionService;
    }

    @PostMapping("/delivery-status")
    @Operation(summary = "Ingest Delivery Status Webhook Event", description = "Ingests AWS SES / Twilio webhook events (DELIVERED, OPENED, BOUNCED) to update real-time campaign metrics")
    public ResponseEntity<ApiResponse<Void>> ingestDeliveryWebhook(
            @Valid @RequestBody DeliveryWebhookPayloadDTO payload,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {
        webhookIngestionService.processDeliveryWebhook(payload);
        return ResponseEntity.ok(ApiResponse.success(null, "Webhook event processed successfully", correlationId));
    }
}
