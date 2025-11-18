package com.lordbucket.langlearn.dto.model;

import com.lordbucket.langlearn.model.enums.Language;

public record CourseSummaryDTO(
        String identifier,
        String title,
        String emoji,
        Language originLanguage,
        Language targetLanguage,
        boolean isEnrolled
) {

}

