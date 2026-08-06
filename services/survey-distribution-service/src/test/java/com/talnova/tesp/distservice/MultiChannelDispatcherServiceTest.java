package com.talnova.tesp.distservice;

import com.talnova.tesp.distservice.adapter.AwsSesEmailAdapter;
import com.talnova.tesp.distservice.adapter.KioskPinAdapter;
import com.talnova.tesp.distservice.adapter.MsTeamsAdapter;
import com.talnova.tesp.distservice.adapter.SlackAdapter;
import com.talnova.tesp.distservice.adapter.TwilioSmsAdapter;
import com.talnova.tesp.distservice.domain.model.DistributionChannel;
import com.talnova.tesp.distservice.dto.ChannelMessageDispatchDTO;
import com.talnova.tesp.distservice.dto.DispatchResultDTO;
import com.talnova.tesp.distservice.service.MultiChannelDispatcherServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MultiChannelDispatcherServiceTest {

    private MultiChannelDispatcherServiceImpl dispatcherService;

    @BeforeEach
    void setUp() {
        dispatcherService = new MultiChannelDispatcherServiceImpl(List.of(
                new AwsSesEmailAdapter(),
                new TwilioSmsAdapter(),
                new MsTeamsAdapter(),
                new SlackAdapter(),
                new KioskPinAdapter()
        ));
    }

    @Test
    @DisplayName("TC-DST-402-01: Dispatch Single Email via AWS SES Adapter")
    void testDispatchSingleMessageEmail() {
        ChannelMessageDispatchDTO msg = ChannelMessageDispatchDTO.builder()
                .campaignId("CMP-1001")
                .employeeId("EMP-10020")
                .recipientContact("user@aitkenspence.lk")
                .channel(DistributionChannel.EMAIL)
                .subject("Pulse Survey")
                .bodyHtml("<p>Hello</p>")
                .build();

        DispatchResultDTO result = dispatcherService.dispatchSingleMessage(msg);

        assertNotNull(result);
        assertEquals(DispatchResultDTO.DispatchStatus.SENT, result.getStatus());
        assertEquals(DistributionChannel.EMAIL, result.getChannel());
        assertNotNull(result.getExternalMessageId());
    }

    @Test
    @DisplayName("TC-DST-402-02: Dispatch Single SMS via Twilio Adapter")
    void testDispatchSingleMessageSms() {
        ChannelMessageDispatchDTO msg = ChannelMessageDispatchDTO.builder()
                .campaignId("CMP-1001")
                .employeeId("EMP-10021")
                .recipientContact("+94771234567")
                .channel(DistributionChannel.SMS)
                .bodyText("Survey URL: https://...")
                .build();

        DispatchResultDTO result = dispatcherService.dispatchSingleMessage(msg);

        assertNotNull(result);
        assertEquals(DispatchResultDTO.DispatchStatus.SENT, result.getStatus());
        assertEquals(DistributionChannel.SMS, result.getChannel());
    }

    @Test
    @DisplayName("TC-DST-402-03: Concurrent Multi-Channel Batch Dispatch of 100 Messages")
    void testDispatchBatchMessagesConcurrent() {
        List<ChannelMessageDispatchDTO> batch = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            DistributionChannel ch = (i % 2 == 0) ? DistributionChannel.EMAIL : DistributionChannel.TEAMS;
            batch.add(ChannelMessageDispatchDTO.builder()
                    .campaignId("CMP-1001")
                    .employeeId("EMP-" + i)
                    .recipientContact("emp" + i + "@aitkenspence.lk")
                    .channel(ch)
                    .build());
        }

        List<DispatchResultDTO> results = dispatcherService.dispatchBatchMessages(batch);

        assertNotNull(results);
        assertEquals(100, results.size());
        long sentCount = results.stream().filter(r -> r.getStatus() == DispatchResultDTO.DispatchStatus.SENT).count();
        assertEquals(100, sentCount, "All 100 messages must be dispatched with SENT status");
    }

    @Test
    @DisplayName("TC-DST-402-04: Fault Tolerant Handling of Individual Adapter Failures")
    void testFaultTolerantAdapterError() {
        ChannelMessageDispatchDTO invalidMsg = ChannelMessageDispatchDTO.builder()
                .campaignId("CMP-1001")
                .employeeId("EMP-BAD")
                .recipientContact("invalid-email-address")
                .channel(DistributionChannel.EMAIL)
                .build();

        ChannelMessageDispatchDTO validMsg = ChannelMessageDispatchDTO.builder()
                .campaignId("CMP-1001")
                .employeeId("EMP-GOOD")
                .recipientContact("good@aitkenspence.lk")
                .channel(DistributionChannel.EMAIL)
                .build();

        List<DispatchResultDTO> results = dispatcherService.dispatchBatchMessages(List.of(invalidMsg, validMsg));

        assertNotNull(results);
        assertEquals(2, results.size());
        
        DispatchResultDTO failedResult = results.stream().filter(r -> "EMP-BAD".equals(r.getEmployeeId())).findFirst().orElseThrow();
        assertEquals(DispatchResultDTO.DispatchStatus.FAILED, failedResult.getStatus());
        assertNotNull(failedResult.getErrorMessage());

        DispatchResultDTO sentResult = results.stream().filter(r -> "EMP-GOOD".equals(r.getEmployeeId())).findFirst().orElseThrow();
        assertEquals(DispatchResultDTO.DispatchStatus.SENT, sentResult.getStatus());
    }
}
