package com.talnova.tesp.orgservice.config;

import com.talnova.tesp.orgservice.domain.NodeStatus;
import com.talnova.tesp.orgservice.domain.OrgNodeDocument;
import com.talnova.tesp.orgservice.repository.OrgNodeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collections;

@Component
@Profile("!test")
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    private final OrgNodeRepository nodeRepository;

    public DataInitializer(OrgNodeRepository nodeRepository) {
        this.nodeRepository = nodeRepository;
    }

    @Override
    public void run(String... args) {
        seedNodes("PRJ-99201");
        seedNodes("PRJ-DEFAULT-001");
    }

    private void seedNodes(String projectId) {
        try {
            if (!nodeRepository.existsByProjectIdAndNodeIdAndIsDeletedFalse(projectId, "N-001")) {
                log.info("Seeding org tree hierarchy for project '{}'...", projectId);

                OrgNodeDocument root = OrgNodeDocument.builder()
                        .projectId(projectId)
                        .nodeId("N-001")
                        .name("Corporate HQ")
                        .type("ROOT")
                        .parentId(null)
                        .path("/N-001")
                        .depth(1)
                        .displayOrder(1)
                        .status(NodeStatus.ACTIVE)
                        .attributes(Collections.emptyMap())
                        .isDeleted(false)
                        .createdAt(Instant.now())
                        .updatedAt(Instant.now())
                        .build();

                OrgNodeDocument dept = OrgNodeDocument.builder()
                        .projectId(projectId)
                        .nodeId("N-201")
                        .name("Human Resources & Talent Strategy")
                        .type("DEPARTMENT")
                        .parentId("N-001")
                        .path("/N-001/N-201")
                        .depth(2)
                        .displayOrder(1)
                        .status(NodeStatus.ACTIVE)
                        .attributes(Collections.emptyMap())
                        .isDeleted(false)
                        .createdAt(Instant.now())
                        .updatedAt(Instant.now())
                        .build();

                OrgNodeDocument team = OrgNodeDocument.builder()
                        .projectId(projectId)
                        .nodeId("N-301")
                        .name("Talent Acquisition & Engagement")
                        .type("TEAM")
                        .parentId("N-201")
                        .path("/N-001/N-201/N-301")
                        .depth(3)
                        .displayOrder(1)
                        .status(NodeStatus.ACTIVE)
                        .attributes(Collections.emptyMap())
                        .isDeleted(false)
                        .createdAt(Instant.now())
                        .updatedAt(Instant.now())
                        .build();

                nodeRepository.save(root);
                nodeRepository.save(dept);
                nodeRepository.save(team);

                log.info("Seeded 3 org nodes (N-001, N-201, N-301) for project '{}'", projectId);
            }
        } catch (Exception ex) {
            log.debug("Notice: Org DataInitializer skipped for {}: {}", projectId, ex.getMessage());
        }
    }
}
