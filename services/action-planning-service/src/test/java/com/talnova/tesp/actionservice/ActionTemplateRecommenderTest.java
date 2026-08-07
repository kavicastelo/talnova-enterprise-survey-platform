package com.talnova.tesp.actionservice;

import com.talnova.tesp.actionservice.domain.model.ActionTemplateDocument;
import com.talnova.tesp.actionservice.recommendation.ActionTemplateRecommenderServiceImpl;
import com.talnova.tesp.actionservice.repository.ActionTemplateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActionTemplateRecommenderTest {

    @Mock
    private ActionTemplateRepository templateRepository;

    private ActionTemplateRecommenderServiceImpl recommenderService;

    @BeforeEach
    void setUp() {
        recommenderService = new ActionTemplateRecommenderServiceImpl(templateRepository);
    }

    @Test
    @DisplayName("TC-ACT-402-01: Recommend AI action templates for communication deficit per FR-ACT-003")
    void testGetRecommendationsForGroup() {
        ActionTemplateDocument tpl = ActionTemplateDocument.builder()
                .templateId("TPL-COMM-001")
                .groupId("GRP-COMMUNICATION")
                .title("Weekly Open Floor Huddle & Town Hall")
                .description("Implement bi-weekly transparent executive Q&A sessions")
                .build();

        when(templateRepository.findByGroupId("GRP-COMMUNICATION")).thenReturn(List.of(tpl));

        List<ActionTemplateDocument> recs = recommenderService.getRecommendations("GRP-COMMUNICATION", "huddle");

        assertNotNull(recs);
        assertEquals(1, recs.size());
        assertEquals("TPL-COMM-001", recs.get(0).getTemplateId());
        assertTrue(recs.get(0).getTitle().contains("Huddle"));
    }
}
