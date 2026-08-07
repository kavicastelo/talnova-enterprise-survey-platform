package com.talnova.tesp.orgservice;

import com.talnova.tesp.orgservice.domain.NodeStatus;
import com.talnova.tesp.orgservice.domain.OrgNodeDocument;
import com.talnova.tesp.orgservice.repository.OrgNodeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SubTreeScopeHeaderAuditTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrgNodeRepository orgNodeRepository;

    @Test
    @DisplayName("X-Project-ID Header Fallback: Request succeeds without explicit query parameter")
    void testProjectIdHeaderFallback() throws Exception {
        OrgNodeDocument mockNode = OrgNodeDocument.builder()
                .projectId("PRJ-99201")
                .nodeId("N-201")
                .name("Engineering")
                .type("DEPARTMENT")
                .path(",N-001,N-101,N-201,")
                .depth(3)
                .status(NodeStatus.ACTIVE)
                .isDeleted(false)
                .build();

        when(orgNodeRepository.findActiveNodeByNodeId("PRJ-99201", "N-201"))
                .thenReturn(Optional.of(mockNode));
        when(orgNodeRepository.findActiveSubTreeByPathPrefix("PRJ-99201", ",N-001,N-101,N-201,"))
                .thenReturn(List.of(mockNode));

        mockMvc.perform(get("/api/v1/nodes/N-201/subtree")
                        .header("X-Project-ID", "PRJ-99201"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("BR-ORG-004 ABAC Interceptor Guard: Access denied for node outside X-Node-Scope")
    void testAbacSubTreeScopeViolation() throws Exception {
        OrgNodeDocument outsideNode = OrgNodeDocument.builder()
                .projectId("PRJ-99201")
                .nodeId("N-999")
                .name("Unauthorized Branch")
                .type("BRANCH")
                .path(",N-001,N-102,N-999,")
                .depth(3)
                .status(NodeStatus.ACTIVE)
                .isDeleted(false)
                .build();

        when(orgNodeRepository.findActiveNodeByNodeId("PRJ-99201", "N-999"))
                .thenReturn(Optional.of(outsideNode));

        mockMvc.perform(get("/api/v1/nodes/N-999/subtree")
                        .header("X-Project-ID", "PRJ-99201")
                        .header("X-Node-Scope", ",N-001,N-101,"))
                .andExpect(status().isForbidden());
    }
}
