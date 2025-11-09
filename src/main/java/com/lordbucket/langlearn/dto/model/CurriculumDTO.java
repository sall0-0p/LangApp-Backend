package com.lordbucket.langlearn.dto.model;

import com.lordbucket.langlearn.model.enums.Language;

import java.util.List;

public record CurriculumDTO(
        String identifier,
        String title,
        Language originLanguage,
        Language targetLanguage,
        List<SectionSummaryDTOwLessons> sections
) {

}
