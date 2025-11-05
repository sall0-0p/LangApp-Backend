package com.lordbucket.langlearn.config.yaml;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class SectionConfig {
    private String title;
    private int order;

    @JsonProperty("lessons")
    private List<LessonConfig> lessons;

    @JsonProperty("level")
    private String level;
}
