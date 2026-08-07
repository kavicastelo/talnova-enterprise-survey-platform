package com.talnova.tesp.actionservice.recommendation;

import com.talnova.tesp.actionservice.domain.model.ActionTemplateDocument;
import com.talnova.tesp.actionservice.repository.ActionTemplateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ActionTemplateRecommenderServiceImpl implements ActionTemplateRecommenderService {

    private static final Logger log = LoggerFactory.getLogger(ActionTemplateRecommenderServiceImpl.class);

    private final ActionTemplateRepository templateRepository;

    @Autowired
    public ActionTemplateRecommenderServiceImpl(ActionTemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
    }

    @Override
    public List<ActionTemplateDocument> getRecommendations(String groupId, String keyword) {
        log.info("Computing AI template recommendations for GroupId '{}' (Keyword filter: '{}')", groupId, keyword);

        List<ActionTemplateDocument> groupTemplates = templateRepository.findByGroupId(groupId);

        if (groupTemplates.isEmpty()) {
            log.info("No exact GroupId match for '{}'; returning all available remedial templates", groupId);
            groupTemplates = templateRepository.findAll();
        }

        if (keyword != null && !keyword.isBlank()) {
            String lowerKw = keyword.toLowerCase();
            return groupTemplates.stream()
                    .filter(t -> (t.getTitle() != null && t.getTitle().toLowerCase().contains(lowerKw)) ||
                            (t.getDescription() != null && t.getDescription().toLowerCase().contains(lowerKw)))
                    .collect(Collectors.toList());
        }

        return groupTemplates;
    }
}
