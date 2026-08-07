package com.talnova.tesp.actionservice;

import com.talnova.tesp.actionservice.domain.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ActionPlanRepositoryTest {

    @Test
    @DisplayName("TC-ACT-101-01: Build and verify ActionPlanDocument model, milestones, and externalSync sub-documents")
    void testActionPlanDocumentBuilder() {
        Instant now = Instant.now();

        ActionMilestone milestone = ActionMilestone.builder()
                .milestoneId("MS-001")
                .title("Schedule Weekly Open Floor Huddle")
                .completed(false)
                .dueDate(now.plusSeconds(86400L * 7))
                .assigneeId("EMP-99201")
                .build();

        ExternalSyncInfo syncInfo = ExternalSyncInfo.builder()
                .system("JIRA")
                .externalKey("ENG-402")
                .lastSyncedAt(now)
                .build();

        ActionPlanDocument doc = ActionPlanDocument.builder()
                .id("DOC-101")
                .projectId("PRJ-99201")
                .actionPlanId("ACT-901")
                .campaignId("CMP-1001")
                .nodeId("N-301")
                .groupId("GRP-COMMUNICATION")
                .title("Improve Leadership Communication")
                .description("Remedial action plan to increase communication score")
                .baselineScore(52.0)
                .targetScore(67.0)
                .status(ActionStatus.DRAFT)
                .assigneeId("EMP-99201")
                .createdBy("SYSTEM_AUTO_TRIGGER")
                .targetCompletionDate(now.plusSeconds(86400L * 30))
                .milestones(List.of(milestone))
                .externalSync(syncInfo)
                .createdAt(now)
                .build();

        assertNotNull(doc);
        assertEquals("PRJ-99201", doc.getProjectId());
        assertEquals("ACT-901", doc.getActionPlanId());
        assertEquals(ActionStatus.DRAFT, doc.getStatus());
        assertEquals(52.0, doc.getBaselineScore());
        assertEquals(1, doc.getMilestones().size());
        assertEquals("JIRA", doc.getExternalSync().getSystem());
        assertEquals("ENG-402", doc.getExternalSync().getExternalKey());
    }
}
