package com.lordbucket.langlearn.dto.model;

import java.util.List;

public record LessonDTO(
        String identifier,
        String title,
        String subtitle,
        int orderIndex,
        boolean completed,
        SectionSummaryDTO section,
        List<TopicSummaryDTO> topics
) {

}
