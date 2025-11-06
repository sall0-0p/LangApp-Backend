package com.lordbucket.langlearn.config.yaml;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class LessonTopicConfig {
    @JsonProperty("topicIdentifier")
    private String topicIdentifier;
    @JsonProperty("taskCount")
    private int taskCount;
}
