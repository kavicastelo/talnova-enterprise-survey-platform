package com.talnova.tesp.actionservice.catalog;

import com.talnova.tesp.actionservice.domain.model.ActionTemplateDocument;
import com.talnova.tesp.actionservice.repository.ActionTemplateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ActionTemplateCatalogServiceImpl implements ActionTemplateCatalogService {

    private static final Logger log = LoggerFactory.getLogger(ActionTemplateCatalogServiceImpl.class);

    private final ActionTemplateRepository templateRepository;

    @Autowired
    public ActionTemplateCatalogServiceImpl(ActionTemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
    }

    @Override
    public List<ActionTemplateDocument> seedTemplateCatalog() {
        log.info("Seeding Daash Global remedial action template catalog per FR-ACT-003 and US-ACT-002");

        List<ActionTemplateDocument> templates = List.of(
                ActionTemplateDocument.builder()
                        .templateId("TPL-COMM-001")
                        .groupId("GRP-COMMUNICATION")
                        .categoryName("Leadership Communication")
                        .title("Weekly Open Floor Huddle & Town Hall")
                        .description("Implement bi-weekly transparent executive Q&A sessions to bridge leadership visibility gap and address employee concerns.")
                        .suggestedMilestones(List.of(
                                "Establish recurring bi-weekly team huddle schedule",
                                "Create anonymous Slido question submission channel",
                                "Conduct first open-floor Q&A session with division vice president",
                                "Publish action takeaways summary document to intranet"
                        ))
                        .build(),

                ActionTemplateDocument.builder()
                        .templateId("TPL-WELL-001")
                        .groupId("GRP-WELLBEING")
                        .categoryName("Workplace Wellbeing & Burnout")
                        .title("Workload Rebalancing & No-Meeting Focus Fridays")
                        .description("Audit team task allocations, introduce focus blocks, and eliminate non-essential Friday afternoon meetings to reduce burnout risk.")
                        .suggestedMilestones(List.of(
                                "Audit active project allocations across team members",
                                "Implement 4-hour daily calendar focus blocks",
                                "Designate Friday afternoons as internal meeting-free zones",
                                "Conduct bi-weekly workload balance check-ins"
                        ))
                        .build(),

                ActionTemplateDocument.builder()
                        .templateId("TPL-LEAD-001")
                        .groupId("GRP-LEADERSHIP")
                        .categoryName("Management Recognition & Coaching")
                        .title("Structured 1-on-1 Coaching & Peer Recognition")
                        .description("Establish mandatory 30-minute weekly 1-on-1 career development check-ins and launch peer recognition cards.")
                        .suggestedMilestones(List.of(
                                "Set up bi-weekly 1-on-1 coaching calendar invites",
                                "Define clear career development goal roadmaps",
                                "Launch monthly peer shout-out recognition channel",
                                "Track quarterly employee sentiment progress"
                        ))
                        .build()
        );

        List<ActionTemplateDocument> savedList = new ArrayList<>();
        for (ActionTemplateDocument tpl : templates) {
            ActionTemplateDocument saved = templateRepository.save(tpl);
            savedList.add(saved);
            log.info("Seeded Daash Global Action Template '{}' (Group: '{}')", saved.getTemplateId(), saved.getGroupId());
        }

        return savedList;
    }

    @Override
    public List<ActionTemplateDocument> getTemplatesByGroup(String groupId) {
        return templateRepository.findByGroupId(groupId);
    }
}
