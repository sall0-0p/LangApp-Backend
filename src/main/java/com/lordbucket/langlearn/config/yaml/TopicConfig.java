package com.lordbucket.langlearn.config.yaml;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class TopicConfig {
    private String identifier;
    private String title;
    private String language;
    private String originLanguage;
    private String level; // e.g., "A1" - will be converted to Enum
    private List<VocabIdentifier> vocabulary;
    private List<String> relatedTopics;

    @JsonProperty("taskTypeWeights")
    private Map<String, Double> taskTypeWeights;

    @JsonProperty("generationPlan")
    private Map<String, Integer> generationPlan;
}
