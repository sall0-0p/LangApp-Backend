package com.lordbucket.langlearn.dto.model;

import com.lordbucket.langlearn.model.enums.TaskType;

public record TaskDTO(
    Long id,
    TopicSummaryDTO topic,
    TaskType taskType,
    Object taskData
) {
}
