package com.lordbucket.langlearn.dto.model;

import java.util.List;

public record SectionSummaryDTOwLessons(
        String identifier,
        String title,
        int orderIndex,
        List<LessonSummaryDTO> lessons
) {
}
