package com.lordbucket.langlearn.dto.model;

public record SectionSummaryDTO(
        String identifier,
        String title,
        int orderIndex,
        boolean completed
) {
}
