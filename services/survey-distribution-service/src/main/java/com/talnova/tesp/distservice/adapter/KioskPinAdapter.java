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
public class KioskPinAdapter implements NotificationChannelAdapter {

    private static final Logger log = LoggerFactory.getLogger(KioskPinAdapter.class);

    @Override
    public DistributionChannel getSupportedChannel() {
        return DistributionChannel.KIOSK_PIN;
    }

    @Override
    public DispatchResultDTO dispatchMessage(ChannelMessageDispatchDTO message) {
        String dispatchId = "DISP-KIOSK-" + UUID.randomUUID().toString().substring(0, 8);
        log.info("Registered Kiosk PIN '{}' dispatch for campaign '{}' (Dispatch ID: {})", message.getKioskPin(), message.getCampaignId(), dispatchId);

        return DispatchResultDTO.builder()
                .dispatchId(dispatchId)
                .campaignId(message.getCampaignId())
                .employeeId(message.getEmployeeId())
                .channel(DistributionChannel.KIOSK_PIN)
                .status(DispatchResultDTO.DispatchStatus.SENT)
                .externalMessageId("kiosk-pin-" + message.getKioskPin())
                .dispatchedAt(Instant.now())
                .build();
    }
}
