package com.lordbucket.langlearn.config.yaml;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class VocabularyEntryConfig {
    @JsonProperty("target")
    private String target;

    @JsonProperty("origin")
    private String origin;

    @JsonProperty("type")
    private String type;
}
