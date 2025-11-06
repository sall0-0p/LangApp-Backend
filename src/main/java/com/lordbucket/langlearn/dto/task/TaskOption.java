package com.lordbucket.langlearn.dto.task;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TaskOption(
        @JsonProperty("sense_id") Long senseId,
        @JsonProperty("expression") String expression,
        @JsonProperty("origin") String origin,
        @JsonProperty("emoji") String emoji
) {
}