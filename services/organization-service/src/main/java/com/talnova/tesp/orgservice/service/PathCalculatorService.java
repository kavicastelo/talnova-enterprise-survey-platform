package com.talnova.tesp.orgservice.service;

import com.talnova.tesp.orgservice.domain.OrgNodeDocument;
import com.talnova.tesp.orgservice.exception.NodeValidationException;
import com.talnova.tesp.orgservice.repository.OrgNodeRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PathCalculatorService {

    private final OrgNodeRepository repository;

    public PathCalculatorService(OrgNodeRepository repository) {
        this.repository = repository;
    }

    public ComputedPathResult calculatePathAndDepth(String projectId, String nodeId, String parentId) {
        if (parentId == null || parentId.trim().isEmpty()) {
            String rootPath = "," + nodeId + ",";
            return new ComputedPathResult(rootPath, 1);
        }

        Optional<OrgNodeDocument> parentOpt = repository.findActiveNodeByNodeId(projectId, parentId);
        if (parentOpt.isEmpty()) {
            throw new NodeValidationException("Parent node '" + parentId + "' does not exist in project '" + projectId + "'");
        }

        OrgNodeDocument parent = parentOpt.get();
        String childPath = parent.getPath() + nodeId + ",";
        int childDepth = parent.getDepth() + 1;

        return new ComputedPathResult(childPath, childDepth);
    }

    public static class ComputedPathResult {
        private final String path;
        private final int depth;

        public ComputedPathResult(String path, int depth) {
            this.path = path;
            this.depth = depth;
        }

        public String getPath() { return path; }
        public int getDepth() { return depth; }
    }
}
