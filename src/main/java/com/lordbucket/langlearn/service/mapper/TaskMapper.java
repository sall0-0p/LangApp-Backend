package com.lordbucket.langlearn.service.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lordbucket.langlearn.dto.model.TaskDTO;
import com.lordbucket.langlearn.model.task.GeneratedTask;
import com.lordbucket.langlearn.service.curriculum.TaskService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {
    private final TopicMapper topicMapper;
    private final ObjectMapper jsonObjectMapper;
    private final TaskService taskService;

    public TaskMapper(TopicMapper topicMapper, @Qualifier("jsonObjectMapper") ObjectMapper jsonObjectMapper, TaskService taskService) {
        this.jsonObjectMapper = jsonObjectMapper;
        this.taskService = taskService;
        this.topicMapper = topicMapper;
    }

    public TaskDTO toDTO(GeneratedTask task) {
        Object taskData;
        try {
            taskData = jsonObjectMapper.readValue(task.getTaskData(), taskService.getTaskGeneratorForType(task.getTaskType()).getResponsePojoClass());
        } catch(Exception e) {
            taskData = null;
        }

        return new TaskDTO(task.getId(),
                topicMapper.toSummaryDTO(task.getTopic()),
                task.getTaskType(),
                taskData
        );
    }
}
