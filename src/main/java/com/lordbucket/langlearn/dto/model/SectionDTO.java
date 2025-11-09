package com.lordbucket.langlearn.dto.model;

import java.util.List;

public record SectionDTO(
        String identifier,
        String title,
        int orderIndex,
        List<LessonSummaryDTO> lessons,
        CourseSummaryDTO course
) {
}
