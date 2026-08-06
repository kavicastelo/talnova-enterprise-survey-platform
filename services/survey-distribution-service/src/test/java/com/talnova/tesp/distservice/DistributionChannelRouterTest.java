package com.talnova.tesp.distservice;

import com.talnova.tesp.distservice.domain.model.AnonymityLevel;
import com.talnova.tesp.distservice.domain.model.DistributionChannel;
import com.talnova.tesp.distservice.dto.ChannelMessageDispatchDTO;
import com.talnova.tesp.distservice.dto.GeneratedTokenDTO;
import com.talnova.tesp.distservice.dto.MessageTemplateRequestDTO;
import com.talnova.tesp.distservice.service.DistributionChannelRouterImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DistributionChannelRouterTest {

    private DistributionChannelRouterImpl router;

    @BeforeEach
    void setUp() {
        router = new DistributionChannelRouterImpl();
    }

    @Test
    @DisplayName("TC-DST-401-01: Hydrate Email Template with survey URL and localized subject")
    void testBuildAndHydrateEmailMessage() {
        MessageTemplateRequestDTO request = MessageTemplateRequestDTO.builder()
                .projectId("PRJ-99201")
                .campaignId("CMP-1001")
                .campaignTitle("Q3 Employee Pulse Survey")
                .employeeId("EMP-10020")
                .recipientContact("john.doe@aitkenspence.lk")
                .channel(DistributionChannel.EMAIL)
                .locale("en-US")
                .token("TKN-HASH-999")
                .build();

        ChannelMessageDispatchDTO dispatch = router.buildAndHydrateMessage(request);

        assertNotNull(dispatch);
        assertEquals("You're Invited: Q3 Employee Pulse Survey", dispatch.getSubject());
        assertTrue(dispatch.getSurveyUrl().contains("https://surveys.aitkenspence.com/p?t=TKN-HASH-999"));
        assertTrue(dispatch.getBodyHtml().contains("Q3 Employee Pulse Survey"));
    }

    @Test
    @DisplayName("TC-DST-401-02: Hydrate SMS Template in Sinhala (si-LK)")
    void testBuildAndHydrateSmsMessageSinhala() {
        MessageTemplateRequestDTO request = MessageTemplateRequestDTO.builder()
                .projectId("PRJ-99201")
                .campaignId("CMP-1001")
                .campaignTitle("කාර්ය මණ්ඩල සමීක්ෂණය")
                .employeeId("EMP-10021")
                .recipientContact("+94771234567")
                .channel(DistributionChannel.SMS)
                .locale("si-LK")
                .token("TKN-HASH-888")
                .build();

        ChannelMessageDispatchDTO dispatch = router.buildAndHydrateMessage(request);

        assertNotNull(dispatch);
        assertTrue(dispatch.getBodyText().contains("සමීක්ෂණ ඇරයුම: කාර්ය මණ්ඩල සමීක්ෂණය"));
        assertTrue(dispatch.getBodyText().contains("https://surveys.aitkenspence.com/p?t=TKN-HASH-888"));
    }

    @Test
    @DisplayName("TC-DST-401-03: Hydrate Kiosk PIN Message")
    void testBuildAndHydrateKioskPinMessage() {
        MessageTemplateRequestDTO request = MessageTemplateRequestDTO.builder()
                .projectId("PRJ-99201")
                .campaignId("CMP-1001")
                .campaignTitle("Factory Kiosk Feedback")
                .channel(DistributionChannel.KIOSK_PIN)
                .locale("en-US")
                .kioskPin("849201")
                .build();

        ChannelMessageDispatchDTO dispatch = router.buildAndHydrateMessage(request);

        assertNotNull(dispatch);
        assertTrue(dispatch.getBodyText().contains("849201"));
        assertEquals("849201", dispatch.getKioskPin());
    }

    @Test
    @DisplayName("TC-DST-401-04: Batch Dispatch Preparation across Email & Teams")
    void testPrepareBatchDispatch() {
        GeneratedTokenDTO token1 = GeneratedTokenDTO.builder().token("T1").employeeId("E1").anonymityLevel(AnonymityLevel.SEMI_ANONYMOUS).build();
        GeneratedTokenDTO token2 = GeneratedTokenDTO.builder().token("T2").employeeId("E2").anonymityLevel(AnonymityLevel.SEMI_ANONYMOUS).build();

        List<ChannelMessageDispatchDTO> batch = router.prepareBatchDispatch(
                "PRJ-99201",
                "CMP-1001",
                "Q3 Pulse",
                List.of(token1, token2),
                List.of(DistributionChannel.EMAIL, DistributionChannel.TEAMS),
                "en-US"
        );

        assertNotNull(batch);
        assertEquals(4, batch.size(), "2 tokens x 2 channels = 4 dispatch payloads");
    }
}
