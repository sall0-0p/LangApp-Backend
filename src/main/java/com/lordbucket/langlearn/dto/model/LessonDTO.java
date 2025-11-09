package com.lordbucket.langlearn.dto.model;

import java.util.List;

public record LessonDTO(
        String identifier,
        String title,
        int orderIndex,
        boolean finished,
        SectionSummaryDTO section,
        List<TopicSummaryDTO> topics
) {

}
