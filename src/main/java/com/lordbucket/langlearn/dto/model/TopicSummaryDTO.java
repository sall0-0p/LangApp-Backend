package com.lordbucket.langlearn.dto.model;

import com.lordbucket.langlearn.model.enums.LanguageLevel;

public record TopicSummaryDTO(
        String identifier,
        String title,
        LanguageLevel level
) {
}
