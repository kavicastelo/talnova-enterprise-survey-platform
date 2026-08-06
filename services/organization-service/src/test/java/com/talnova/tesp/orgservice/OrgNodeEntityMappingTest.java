package com.talnova.tesp.orgservice;

import com.talnova.tesp.orgservice.domain.NodeStatus;
import com.talnova.tesp.orgservice.domain.OrgNodeDocument;
import com.talnova.tesp.orgservice.dto.CreateNodeDTO;
import com.talnova.tesp.orgservice.dto.OrgNodeResponseDTO;
import com.talnova.tesp.orgservice.mapper.OrgNodeMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class OrgNodeEntityMappingTest {

    private OrgNodeMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new OrgNodeMapper();
    }

    @Test
    @DisplayName("TC-ORG-201-A: OrgNodeDocument entity maps dynamic key-value attributes and optimistic versioning")
    void testEntityDynamicAttributesAndVersioning() {
        Map<String, Object> customAttributes = new HashMap<>();
        customAttributes.put("CostCenterCode", "CC-99201");
        customAttributes.put("FacilityType", "RETAIL_STORE");
        customAttributes.put("HeadcountLimit", 150);

        Instant now = Instant.now();

        OrgNodeDocument doc = OrgNodeDocument.builder()
                .id("66b26d8f8a84a51e3c8b9999")
                .projectId("PRJ-99201")
                .nodeId("N-201")
                .name("Western Province Region")
                .type("REGION")
                .parentId("N-101")
                .path(",N-001,N-101,N-201,")
                .depth(3)
                .displayOrder(2)
                .status(NodeStatus.ACTIVE)
                .attributes(customAttributes)
                .version(1)
                .isDeleted(false)
                .createdAt(now)
                .updatedAt(now)
                .build();

        assertEquals("66b26d8f8a84a51e3c8b9999", doc.getId());
        assertEquals("PRJ-99201", doc.getProjectId());
        assertEquals("N-201", doc.getNodeId());
        assertEquals("Western Province Region", doc.getName());
        assertEquals("REGION", doc.getType());
        assertEquals("N-101", doc.getParentId());
        assertEquals(",N-001,N-101,N-201,", doc.getPath());
        assertEquals(3, doc.getDepth());
        assertEquals(2, doc.getDisplayOrder());
        assertEquals(NodeStatus.ACTIVE, doc.getStatus());
        assertEquals(1, doc.getVersion());
        assertFalse(doc.isDeleted());
        assertEquals(now, doc.getCreatedAt());
        assertEquals(now, doc.getUpdatedAt());

        assertNotNull(doc.getAttributes());
        assertEquals("CC-99201", doc.getAttributes().get("CostCenterCode"));
        assertEquals("RETAIL_STORE", doc.getAttributes().get("FacilityType"));
        assertEquals(150, doc.getAttributes().get("HeadcountLimit"));
    }

    @Test
    @DisplayName("TC-ORG-201-B: OrgNodeMapper correctly maps CreateNodeDTO to Document and ResponseDTO")
    void testMapperToDocumentAndResponse() {
        CreateNodeDTO createDTO = CreateNodeDTO.builder()
                .projectId("PRJ-99201")
                .nodeId("N-301")
                .name("Software Engineering Team")
                .type("TEAM")
                .parentId("N-201")
                .displayOrder(1)
                .attributes(Map.of("Lead", "John Doe"))
                .build();

        String computedPath = ",N-001,N-101,N-201,N-301,";
        int computedDepth = 4;

        OrgNodeDocument doc = mapper.toDocument(createDTO, computedPath, computedDepth);

        assertNotNull(doc);
        assertEquals("PRJ-99201", doc.getProjectId());
        assertEquals("N-301", doc.getNodeId());
        assertEquals(computedPath, doc.getPath());
        assertEquals(4, doc.getDepth());

        OrgNodeResponseDTO response = mapper.toResponseDTO(doc);

        assertNotNull(response);
        assertEquals("PRJ-99201", response.getProjectId());
        assertEquals("N-301", response.getNodeId());
        assertEquals("Software Engineering Team", response.getName());
        assertEquals("TEAM", response.getType());
        assertEquals("N-201", response.getParentId());
        assertEquals(computedPath, response.getPath());
        assertEquals(4, response.getDepth());
        assertEquals(NodeStatus.ACTIVE, response.getStatus());
        assertEquals("John Doe", response.getAttributes().get("Lead"));
    }
}
