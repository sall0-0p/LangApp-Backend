package com.lordbucket.langlearn.dto.model;

import com.lordbucket.langlearn.model.enums.Language;

import java.util.List;

public record CourseDTO(
        String identifier,
        String title,
        String emoji,
        Language originLanguage,
        Language targetLanguage,
        List<SectionSummaryDTO> sections,
        boolean enrolled
) {

}
