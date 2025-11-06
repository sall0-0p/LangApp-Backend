package com.lordbucket.langlearn.config.yaml;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class LessonConfig {
    private String identifier;
    private String title;
    private int order;
    @JsonProperty("topicComposition")
    private List<LessonTopicConfig> topicComposition;
}
