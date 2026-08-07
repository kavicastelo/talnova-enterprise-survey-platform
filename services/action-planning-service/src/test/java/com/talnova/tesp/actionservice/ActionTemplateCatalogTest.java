package com.talnova.tesp.actionservice;

import com.talnova.tesp.actionservice.catalog.ActionTemplateCatalogServiceImpl;
import com.talnova.tesp.actionservice.domain.model.ActionTemplateDocument;
import com.talnova.tesp.actionservice.repository.ActionTemplateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActionTemplateCatalogTest {

    @Mock
    private ActionTemplateRepository templateRepository;

    private ActionTemplateCatalogServiceImpl catalogService;

    @BeforeEach
    void setUp() {
        catalogService = new ActionTemplateCatalogServiceImpl(templateRepository);
    }

    @Test
    @DisplayName("TC-ACT-401-01: Seed Daash Global action template catalog per FR-ACT-003 and US-ACT-002")
    void testSeedTemplateCatalogSuccess() {
        when(templateRepository.save(any(ActionTemplateDocument.class))).thenAnswer(i -> i.getArgument(0));

        List<ActionTemplateDocument> seeded = catalogService.seedTemplateCatalog();

        assertNotNull(seeded);
        assertEquals(3, seeded.size());
        assertEquals("TPL-COMM-001", seeded.get(0).getTemplateId());
        assertEquals("GRP-COMMUNICATION", seeded.get(0).getGroupId());
        assertTrue(seeded.get(0).getSuggestedMilestones().size() >= 3);

        verify(templateRepository, times(3)).save(any(ActionTemplateDocument.class));
    }

    @Test
    @DisplayName("TC-ACT-401-02: Query action templates by Question Group ID GRP-COMMUNICATION")
    void testGetTemplatesByGroup() {
        ActionTemplateDocument tpl = ActionTemplateDocument.builder()
                .templateId("TPL-COMM-001")
                .groupId("GRP-COMMUNICATION")
                .title("Weekly Open Floor Huddle & Town Hall")
                .build();

        when(templateRepository.findByGroupId("GRP-COMMUNICATION")).thenReturn(List.of(tpl));

        List<ActionTemplateDocument> templates = catalogService.getTemplatesByGroup("GRP-COMMUNICATION");
        assertEquals(1, templates.size());
        assertEquals("TPL-COMM-001", templates.get(0).getTemplateId());
    }
}
