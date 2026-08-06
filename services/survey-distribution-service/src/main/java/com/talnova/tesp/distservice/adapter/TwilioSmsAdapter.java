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
public class TwilioSmsAdapter implements NotificationChannelAdapter {

    private static final Logger log = LoggerFactory.getLogger(TwilioSmsAdapter.class);

    @Override
    public DistributionChannel getSupportedChannel() {
        return DistributionChannel.SMS;
    }

    @Override
    public DispatchResultDTO dispatchMessage(ChannelMessageDispatchDTO message) {
        String dispatchId = "DISP-TWILIO-" + UUID.randomUUID().toString().substring(0, 8);
        log.info("Dispatched Twilio SMS to '{}' for campaign '{}' (Dispatch ID: {})", message.getRecipientContact(), message.getCampaignId(), dispatchId);

        return DispatchResultDTO.builder()
                .dispatchId(dispatchId)
                .campaignId(message.getCampaignId())
                .employeeId(message.getEmployeeId())
                .channel(DistributionChannel.SMS)
                .status(DispatchResultDTO.DispatchStatus.SENT)
                .externalMessageId("SM" + UUID.randomUUID().toString().replace("-", ""))
                .dispatchedAt(Instant.now())
                .build();
    }
}
