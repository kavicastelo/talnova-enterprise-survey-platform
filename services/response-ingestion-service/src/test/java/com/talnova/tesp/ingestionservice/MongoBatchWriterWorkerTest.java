package com.talnova.tesp.ingestionservice;

import com.talnova.tesp.ingestionservice.domain.model.RespondentType;
import com.talnova.tesp.ingestionservice.event.SurveyResponseSubmittedEvent;
import com.talnova.tesp.ingestionservice.repository.ResponseRepository;
import com.talnova.tesp.ingestionservice.worker.MongoBatchWriterWorker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class)
class MongoBatchWriterWorkerTest {

    @Mock
    private ResponseRepository responseRepository;

    private MongoBatchWriterWorker worker;

    @BeforeEach
    void setUp() {
        worker = new MongoBatchWriterWorker(responseRepository);
    }

    @Test
    @DisplayName("TC-INT-402-01: Process batch of raw response events consumed from Kafka stream")
    void testProcessBatchSuccess() {
        List<SurveyResponseSubmittedEvent> events = new ArrayList<>();
        for (int i = 0; i < 500; i++) {
            events.add(SurveyResponseSubmittedEvent.builder()
                    .eventId("EVT-" + i)
                    .projectId("PRJ-99201")
                    .campaignId("CMP-1001")
                    .surveyId("SRV-5001")
                    .responseId("RSP-" + i)
                    .respondentType(RespondentType.SEMI_ANONYMOUS)
                    .nodeId("N-301")
                    .answerCount(3)
                    .build());
        }

        assertDoesNotThrow(() -> worker.processBatch(events));
    }
}
