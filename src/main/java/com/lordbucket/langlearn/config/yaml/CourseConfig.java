package com.lordbucket.langlearn.config.yaml;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class CourseConfig {
    private String identifier;
    private String emoji;
    private String title;
    @JsonProperty("originLanguage")
    private String originLanguage;
    @JsonProperty("targetLanguage")
    private String targetLanguage;
    private List<String> sectionFiles;
}
