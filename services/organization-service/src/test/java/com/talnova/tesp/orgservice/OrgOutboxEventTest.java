package com.talnova.tesp.orgservice;

import com.talnova.tesp.orgservice.domain.OutboxEventDocument;
import com.talnova.tesp.orgservice.domain.OutboxStatus;
import com.talnova.tesp.orgservice.messaging.OutboxEventPoller;
import com.talnova.tesp.orgservice.repository.OutboxEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrgOutboxEventTest {

    @Mock
    private OutboxEventRepository outboxRepository;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    private OutboxEventPoller outboxPoller;

    @BeforeEach
    void setUp() {
        outboxPoller = new OutboxEventPoller(outboxRepository, kafkaTemplate);
        ReflectionTestUtils.setField(outboxPoller, "orgEventsTopic", "tesp.org.events.v1");
    }

    @Test
    @DisplayName("TC-ORG-501-A: OutboxEventPoller relays PENDING events to Kafka topic tesp.org.events.v1 and sets status to PUBLISHED")
    void testProcessOutboxEventsSuccess() {
        OutboxEventDocument pendingEvent = OutboxEventDocument.builder()
                .id("66b26d8f8a84a51e3c8b7777")
                .eventId("EVT-9920101")
                .projectId("PRJ-99201")
                .aggregateType("OrgNode")
                .aggregateId("N-201")
                .eventType("ORG_NODE_MOVED")
                .payload("{\"eventType\":\"ORG_NODE_MOVED\",\"nodeId\":\"N-201\",\"oldPath\":\",N-001,N-101,N-201,\",\"newPath\":\",N-001,N-102,N-201,\"}")
                .status(OutboxStatus.PENDING)
                .createdAt(Instant.now())
                .build();

        when(outboxRepository.findByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING))
                .thenReturn(List.of(pendingEvent));

        CompletableFuture future = CompletableFuture.completedFuture(null);
        when(kafkaTemplate.send(eq("tesp.org.events.v1"), eq("PRJ-99201"), anyString()))
                .thenReturn(future);

        outboxPoller.processOutboxEvents();

        verify(kafkaTemplate).send(eq("tesp.org.events.v1"), eq("PRJ-99201"), eq(pendingEvent.getPayload()));
        verify(outboxRepository).save(pendingEvent);
        assertEquals(OutboxStatus.PUBLISHED, pendingEvent.getStatus());
    }
}
