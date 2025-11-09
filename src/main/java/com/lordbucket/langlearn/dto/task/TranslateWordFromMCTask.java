package com.lordbucket.langlearn.dto.task;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lordbucket.langlearn.dto.model.TaskOption;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TranslateWordFromMCTask(
        @JsonProperty("question_word")
        TaskOption questionWord,
        @JsonProperty("distractors")
        List<TaskOption> distractors
) {
}