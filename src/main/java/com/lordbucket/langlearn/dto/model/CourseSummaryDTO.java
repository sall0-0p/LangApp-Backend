package com.lordbucket.langlearn.dto.model;

import com.lordbucket.langlearn.model.enums.Language;

public record CourseSummaryDTO(
        String identifier,
        String title,
        Language originLanguage,
        Language targetLanguage
) {

}

