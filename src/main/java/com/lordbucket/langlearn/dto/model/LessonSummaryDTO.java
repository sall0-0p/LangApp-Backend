package com.lordbucket.langlearn.dto.model;

public record LessonSummaryDTO(
        String identifier,
        String title,
        int orderIndex,
        boolean completed
) {

}
