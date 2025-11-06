package com.lordbucket.langlearn.dto.task;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TranslateWordToMCTask(
        @JsonProperty("question_prompt")
        String questionPrompt,
        @JsonProperty("correct_answer")
        String correctAnswer,
        @JsonProperty("distractors")
        List<TaskOption> distractors
) {
}

