package com.lordbucket.langlearn.config.yaml;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class SectionConfig {
    @JsonProperty("sectionIdentifier")
    private String sectionIdentifier;
    @JsonProperty("sectionTitle")
    private String sectionTitle;
    @JsonProperty("sectionLevel")
    private String sectionLevel; // Kept as String for parsing
    private List<LessonConfig> lessons;
}
