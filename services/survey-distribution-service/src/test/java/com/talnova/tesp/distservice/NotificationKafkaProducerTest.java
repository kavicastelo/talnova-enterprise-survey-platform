package com.talnova.tesp.distservice;

import com.talnova.tesp.distservice.domain.model.DistributionChannel;
import com.talnova.tesp.distservice.dto.ChannelMessageDispatchDTO;
import com.talnova.tesp.distservice.dto.NotificationDispatchEventDTO;
import com.talnova.tesp.distservice.messaging.NotificationKafkaProducerImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationKafkaProducerTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    private NotificationKafkaProducerImpl producer;

    @BeforeEach
    void setUp() {
        producer = new NotificationKafkaProducerImpl(kafkaTemplate);
    }

    @Test
    @DisplayName("TC-DST-601-01: Publish notification dispatch event to Kafka topic with campaignId key")
    void testPublishDispatchEvent() {
        ChannelMessageDispatchDTO msg = ChannelMessageDispatchDTO.builder()
                .projectId("PRJ-99201")
                .campaignId("CMP-1001")
                .employeeId("EMP-10020")
                .recipientContact("john@aitkenspence.lk")
                .channel(DistributionChannel.EMAIL)
                .subject("Q3 Pulse Survey")
                .bodyText("Link: https://...")
                .surveyUrl("https://surveys.aitkenspence.com/p?t=TKN-1")
                .build();

        producer.publishDispatchEvent(msg);

        verify(kafkaTemplate, times(1)).send(
                eq(NotificationKafkaProducerImpl.DISPATCH_EVENTS_TOPIC),
                eq("CMP-1001"),
                any(NotificationDispatchEventDTO.class)
        );
    }

    @Test
    @DisplayName("TC-DST-601-02: Publish batch dispatch events to Kafka")
    void testPublishBatchDispatchEvents() {
        ChannelMessageDispatchDTO msg1 = ChannelMessageDispatchDTO.builder().campaignId("CMP-1001").channel(DistributionChannel.EMAIL).build();
        ChannelMessageDispatchDTO msg2 = ChannelMessageDispatchDTO.builder().campaignId("CMP-1001").channel(DistributionChannel.SMS).build();

        producer.publishBatchDispatchEvents(List.of(msg1, msg2));

        verify(kafkaTemplate, times(2)).send(
                eq(NotificationKafkaProducerImpl.DISPATCH_EVENTS_TOPIC),
                eq("CMP-1001"),
                any(NotificationDispatchEventDTO.class)
        );
    }

    @Test
    @DisplayName("TC-DST-601-03: Convert ChannelMessageDispatchDTO to NotificationDispatchEventDTO")
    void testToEventDTO() {
        ChannelMessageDispatchDTO msg = ChannelMessageDispatchDTO.builder()
                .projectId("PRJ-99201")
                .campaignId("CMP-1001")
                .employeeId("EMP-10020")
                .channel(DistributionChannel.KIOSK_PIN)
                .kioskPin("849201")
                .build();

        NotificationDispatchEventDTO event = producer.toEventDTO(msg);

        assertNotNull(event);
        assertNotNull(event.getEventId());
        assertEquals("CMP-1001", event.getCampaignId());
        assertEquals("849201", event.getKioskPin());
    }
}
