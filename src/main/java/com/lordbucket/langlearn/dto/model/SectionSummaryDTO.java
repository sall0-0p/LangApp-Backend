package com.lordbucket.langlearn.dto.model;

import java.util.List;

public record SectionSummaryDTO(
        String identifier,
        String title,
        int orderIndex,
        boolean completed,
        List<LessonSummaryDTO> lessons
) {
}
