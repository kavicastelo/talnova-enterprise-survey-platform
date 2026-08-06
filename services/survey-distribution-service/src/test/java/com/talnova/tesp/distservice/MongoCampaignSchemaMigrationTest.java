package com.talnova.tesp.distservice;

import com.talnova.tesp.distservice.domain.model.AnonymityLevel;
import com.talnova.tesp.distservice.domain.model.CampaignMetrics;
import com.talnova.tesp.distservice.domain.model.CampaignStatus;
import com.talnova.tesp.distservice.domain.model.DistributionChannel;
import com.talnova.tesp.distservice.domain.model.SurveyCampaignDocument;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MongoCampaignSchemaMigrationTest {

    @Test
    @DisplayName("TC-DST-101-01: Verify SurveyCampaignDocument MongoDB Collection & Compound Indexes Annotations")
    void testSurveyCampaignDocumentMetadataAndIndexes() {
        Class<SurveyCampaignDocument> docClass = SurveyCampaignDocument.class;

        assertTrue(docClass.isAnnotationPresent(Document.class), "SurveyCampaignDocument must be annotated with @Document");
        Document docAnn = docClass.getAnnotation(Document.class);
        assertEquals("survey_campaigns", docAnn.collection(), "Target MongoDB collection must be 'survey_campaigns'");

        assertTrue(docClass.isAnnotationPresent(CompoundIndexes.class), "SurveyCampaignDocument must be annotated with @CompoundIndexes");
        CompoundIndexes indexesAnn = docClass.getAnnotation(CompoundIndexes.class);
        CompoundIndex[] indexes = indexesAnn.value();

        assertEquals(2, indexes.length, "Must contain exactly 2 compound indexes");
        
        boolean foundUniqueIdx = false;
        boolean foundStatusIdx = false;

        for (CompoundIndex idx : indexes) {
            if ("idx_project_campaign_unique".equals(idx.name())) {
                foundUniqueIdx = true;
                assertTrue(idx.unique(), "idx_project_campaign_unique index must be unique");
                assertEquals("{'projectId': 1, 'campaignId': 1}", idx.def());
            } else if ("idx_project_status".equals(idx.name())) {
                foundStatusIdx = true;
                assertEquals("{'projectId': 1, 'status': 1}", idx.def());
            }
        }

        assertTrue(foundUniqueIdx, "Unique index idx_project_campaign_unique must be declared");
        assertTrue(foundStatusIdx, "Status index idx_project_status must be declared");
    }

    @Test
    @DisplayName("TC-DST-101-02: Verify SurveyCampaignDocument Entity Construction & Enum Mapping")
    void testSurveyCampaignDocumentInstantiation() {
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(86400 * 14);

        SurveyCampaignDocument doc = SurveyCampaignDocument.builder()
                .projectId("PRJ-99201")
                .campaignId("CMP-1001")
                .surveyId("SRV-5001")
                .surveyVersion(1)
                .title("Q3 Employee Pulse Survey")
                .anonymityLevel(AnonymityLevel.SEMI_ANONYMOUS)
                .channels(List.of(DistributionChannel.EMAIL, DistributionChannel.TEAMS))
                .status(CampaignStatus.DRAFT)
                .startDate(now)
                .expirationDate(expiry)
                .metrics(CampaignMetrics.builder().totalTargeted(5000).sent(0).delivered(0).completed(0).build())
                .build();

        assertEquals("PRJ-99201", doc.getProjectId());
        assertEquals("CMP-1001", doc.getCampaignId());
        assertEquals("SRV-5001", doc.getSurveyId());
        assertEquals(1, doc.getSurveyVersion());
        assertEquals(AnonymityLevel.SEMI_ANONYMOUS, doc.getAnonymityLevel());
        assertEquals(2, doc.getChannels().size());
        assertEquals(CampaignStatus.DRAFT, doc.getStatus());
        assertEquals(5000, doc.getMetrics().getTotalTargeted());
        assertFalse(doc.isDeleted());
    }
}
