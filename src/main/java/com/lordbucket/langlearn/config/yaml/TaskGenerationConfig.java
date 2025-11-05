package com.lordbucket.langlearn.config.yaml;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TaskGenerationConfig {
    @JsonProperty("taskType")
    private String taskType;

    private int count;
    private int variations;
}
