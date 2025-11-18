package com.lordbucket.langlearn.dto.model;

public record LessonSummaryDTO(
        String identifier,
        String title,
        String subtitle,
        int orderIndex,
        boolean completed
) {

}
