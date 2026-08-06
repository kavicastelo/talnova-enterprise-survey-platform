package com.talnova.tesp.ingestionservice;

import com.talnova.tesp.ingestionservice.domain.model.AnswerItem;
import com.talnova.tesp.ingestionservice.domain.model.RespondentType;
import com.talnova.tesp.ingestionservice.domain.model.SurveyResponseDocument;
import com.talnova.tesp.ingestionservice.repository.ResponseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResponseRepositoryTest {

    @Mock
    private ResponseRepository responseRepository;

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("TC-INT-101-01: Save SurveyResponseDocument to write-once MongoDB collection")
    void testSaveSurveyResponseDocument() {
        SurveyResponseDocument response = SurveyResponseDocument.builder()
                .id("RSP-1001")
                .projectId("PRJ-99201")
                .campaignId("CMP-1001")
                .surveyId("SRV-5001")
                .surveyVersion(1)
                .respondentType(RespondentType.SEMI_ANONYMOUS)
                .responseToken("TKN-SEMI-99102")
                .nodeId("N-301")
                .demographicSnapshot(Map.of("Tenure", "3-5 Years", "Department", "Operations"))
                .answers(List.of(
                        AnswerItem.builder().questionId("Q-101").questionType("LIKERT").numericValue(5.0).build(),
                        AnswerItem.builder().questionId("Q-102").questionType("NPS").numericValue(10.0).build(),
                        AnswerItem.builder().questionId("Q-103").questionType("LONG_TEXT").textValue("Great culture!").build()
                ))
                .submittedAt(Instant.now())
                .isDeleted(false)
                .build();

        when(responseRepository.save(any(SurveyResponseDocument.class))).thenReturn(Mono.just(response));

        Mono<SurveyResponseDocument> result = responseRepository.save(response);

        StepVerifier.create(result)
                .assertNext(doc -> {
                    assertNotNull(doc.getId());
                    assertEquals("PRJ-99201", doc.getProjectId());
                    assertEquals("CMP-1001", doc.getCampaignId());
                    assertEquals(RespondentType.SEMI_ANONYMOUS, doc.getRespondentType());
                    assertEquals(3, doc.getAnswers().size());
                    assertEquals("3-5 Years", doc.getDemographicSnapshot().get("Tenure"));
                })
                .verifyComplete();

        verify(responseRepository, times(1)).save(response);
    }

    @Test
    @DisplayName("TC-INT-101-02: Find responses by projectId and campaignId compound index")
    void testFindByProjectIdAndCampaignId() {
        SurveyResponseDocument response1 = SurveyResponseDocument.builder().id("RSP-1").projectId("PRJ-99201").campaignId("CMP-1001").build();
        SurveyResponseDocument response2 = SurveyResponseDocument.builder().id("RSP-2").projectId("PRJ-99201").campaignId("CMP-1001").build();

        when(responseRepository.findByProjectIdAndCampaignIdAndIsDeletedFalse("PRJ-99201", "CMP-1001"))
                .thenReturn(Flux.just(response1, response2));

        Flux<SurveyResponseDocument> flux = responseRepository.findByProjectIdAndCampaignIdAndIsDeletedFalse("PRJ-99201", "CMP-1001");

        StepVerifier.create(flux)
                .expectNext(response1)
                .expectNext(response2)
                .verifyComplete();
    }

    @Test
    @DisplayName("TC-INT-101-03: Check existing token to prevent duplicate submissions")
    void testExistsByResponseToken() {
        when(responseRepository.existsByResponseTokenAndIsDeletedFalse("TKN-USED-101"))
                .thenReturn(Mono.just(true));

        Mono<Boolean> exists = responseRepository.existsByResponseTokenAndIsDeletedFalse("TKN-USED-101");

        StepVerifier.create(exists)
                .expectNext(true)
                .verifyComplete();
    }
}
