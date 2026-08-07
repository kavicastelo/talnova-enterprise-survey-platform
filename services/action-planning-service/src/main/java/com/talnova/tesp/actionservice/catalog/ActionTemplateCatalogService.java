package com.talnova.tesp.actionservice.catalog;

import com.talnova.tesp.actionservice.domain.model.ActionTemplateDocument;

import java.util.List;

public interface ActionTemplateCatalogService {

    /**
     * Seeds Daash Global consulting action templates per FR-ACT-003 and US-ACT-002.
     */
    List<ActionTemplateDocument> seedTemplateCatalog();

    /**
     * Retrieves templates by Question Group ID.
     */
    List<ActionTemplateDocument> getTemplatesByGroup(String groupId);
}
