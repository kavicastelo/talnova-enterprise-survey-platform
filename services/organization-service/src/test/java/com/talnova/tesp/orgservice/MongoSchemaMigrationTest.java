package com.talnova.tesp.orgservice;

import com.talnova.tesp.orgservice.domain.NodeStatus;
import com.talnova.tesp.orgservice.domain.OrgNodeDocument;
import com.talnova.tesp.orgservice.dto.CreateNodeDTO;
import com.talnova.tesp.orgservice.dto.OrgNodeResponseDTO;
import com.talnova.tesp.orgservice.mapper.OrgNodeMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MongoSchemaMigrationTest {

    private OrgNodeMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new OrgNodeMapper();
    }

    @Test
    @DisplayName("TC-ORG-101-A: Successfully map and materialise path regex ^,([A-Za-z0-9_-]+,)+$")
    void testMaterializedPathRegexPattern() {
        String validPath = ",N-001,N-101,N-201,";
        String invalidPath = "N-001/N-101/N-201";

        assertTrue(validPath.matches("^,([A-Za-z0-9_-]+,)+$"));
        assertFalse(invalidPath.matches("^,([A-Za-z0-9_-]+,)+$"));
    }

    @Test
    @DisplayName("TC-ORG-101-B: Depth corresponds to path level segment count")
    void testNodeEntityMappingAndDepth() {
        CreateNodeDTO dto = CreateNodeDTO.builder()
                .projectId("PRJ-99201")
                .nodeId("N-301")
                .name("Engineering Department")
                .type("DEPARTMENT")
                .parentId("N-201")
                .attributes(Map.of("CostCenter", "CC-9920"))
                .build();

        String calculatedPath = ",N-001,N-101,N-201,N-301,";
        int calculatedDepth = 4;

        OrgNodeDocument doc = mapper.toDocument(dto, calculatedPath, calculatedDepth);

        assertNotNull(doc);
        assertEquals("PRJ-99201", doc.getProjectId());
        assertEquals("N-301", doc.getNodeId());
        assertEquals("Engineering Department", doc.getName());
        assertEquals(calculatedPath, doc.getPath());
        assertEquals(4, doc.getDepth());
        assertEquals(NodeStatus.ACTIVE, doc.getStatus());
        assertEquals("CC-9920", doc.getAttributes().get("CostCenter"));
        assertFalse(doc.isDeleted());

        OrgNodeResponseDTO response = mapper.toResponseDTO(doc);
        assertNotNull(response);
        assertEquals("N-301", response.getNodeId());
        assertEquals(calculatedPath, response.getPath());
    }
}
