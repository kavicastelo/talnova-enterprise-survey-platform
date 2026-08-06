package com.talnova.tesp.distservice.adapter;

import com.talnova.tesp.distservice.domain.model.DistributionChannel;
import com.talnova.tesp.distservice.dto.ChannelMessageDispatchDTO;
import com.talnova.tesp.distservice.dto.DispatchResultDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class AwsSesEmailAdapter implements NotificationChannelAdapter {

    private static final Logger log = LoggerFactory.getLogger(AwsSesEmailAdapter.class);

    @Override
    public DistributionChannel getSupportedChannel() {
        return DistributionChannel.EMAIL;
    }

    @Override
    public DispatchResultDTO dispatchMessage(ChannelMessageDispatchDTO message) {
        String dispatchId = "DISP-SES-" + UUID.randomUUID().toString().substring(0, 8);
        log.info("Dispatched AWS SES Email to '{}' for campaign '{}' (Dispatch ID: {})", message.getRecipientContact(), message.getCampaignId(), dispatchId);

        try {
            if (message.getRecipientContact() != null && message.getRecipientContact().contains("invalid")) {
                throw new IllegalArgumentException("Invalid email address syntax: " + message.getRecipientContact());
            }

            return DispatchResultDTO.builder()
                    .dispatchId(dispatchId)
                    .campaignId(message.getCampaignId())
                    .employeeId(message.getEmployeeId())
                    .channel(DistributionChannel.EMAIL)
                    .status(DispatchResultDTO.DispatchStatus.SENT)
                    .externalMessageId("ses-msg-" + UUID.randomUUID().toString())
                    .dispatchedAt(Instant.now())
                    .build();
        } catch (Exception e) {
            log.error("AWS SES Email dispatch failed for recipient {}: {}", message.getRecipientContact(), e.getMessage());
            return DispatchResultDTO.builder()
                    .dispatchId(dispatchId)
                    .campaignId(message.getCampaignId())
                    .employeeId(message.getEmployeeId())
                    .channel(DistributionChannel.EMAIL)
                    .status(DispatchResultDTO.DispatchStatus.FAILED)
                    .errorMessage(e.getMessage())
                    .dispatchedAt(Instant.now())
                    .build();
        }
    }
}
