package com.talnova.tesp.distservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talnova.tesp.distservice.controller.WebhookController;
import com.talnova.tesp.distservice.dto.DeliveryWebhookPayloadDTO;
import com.talnova.tesp.distservice.exception.GlobalExceptionHandler;
import com.talnova.tesp.distservice.service.WebhookIngestionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class WebhookControllerTest {

    private MockMvc mockMvc;

    @Mock
    private WebhookIngestionService webhookIngestionService;

    @InjectMocks
    private WebhookController webhookController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        mockMvc = MockMvcBuilders.standaloneSetup(webhookController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("TC-DST-602-03: POST /api/v1/webhooks/delivery-status returns 200 OK")
    void testIngestDeliveryWebhookEndpoint() throws Exception {
        DeliveryWebhookPayloadDTO payload = DeliveryWebhookPayloadDTO.builder()
                .projectId("PRJ-99201")
                .campaignId("CMP-1001")
                .provider(DeliveryWebhookPayloadDTO.Provider.AWS_SES)
                .eventType(DeliveryWebhookPayloadDTO.EventType.DELIVERED)
                .externalMessageId("ses-101")
                .recipientContact("user@aitkenspence.lk")
                .build();

        doNothing().when(webhookIngestionService).processDeliveryWebhook(any());

        mockMvc.perform(post("/api/v1/webhooks/delivery-status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Webhook event processed successfully"));
    }
}
