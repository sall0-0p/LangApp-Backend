package com.lordbucket.langlearn.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lordbucket.langlearn.model.enums.TaskType;
import com.lordbucket.langlearn.model.task.GeneratedTask;
import com.lordbucket.langlearn.model.task.ITaskGenerator;
import com.lordbucket.langlearn.model.topic.Topic;
import com.lordbucket.langlearn.model.topic.TopicGenerationRule;
import com.lordbucket.langlearn.repository.GeneratedTaskRepository;
import com.lordbucket.langlearn.service.curriculum.TaskService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * The "Factory Manager" for all tasks.
 * This service is called by the TopicSeeder.
 * It finds the correct "Strategy" (ITaskGenerator) and calls it.
 * It then converts the resulting POJO into JSON and saves it to the database.
 */
@Service
@Slf4j
public class TaskGenerationService {

    private final GeneratedTaskRepository generatedTaskRepository;
    private final ObjectMapper objectMapper;
    private final TaskService taskService;

    public TaskGenerationService(GeneratedTaskRepository generatedTaskRepository,
                                 @Qualifier("jsonObjectMapper") ObjectMapper objectMapper,
                                 TaskService taskService) {
        this.generatedTaskRepository = generatedTaskRepository;
        this.objectMapper = objectMapper;
        this.taskService = taskService;
    }

    /**
     * This is the main method called by the TopicSeederService.
     * It generates and saves a *single* task.
     */
    @Transactional
    public void generateAndSaveTask(Topic topic, TopicGenerationRule rule) {
        TaskType taskType = rule.getTaskType();

        // Find the correct strategy
        ITaskGenerator generator = taskService.getTaskGeneratorForType(taskType);
        if (generator == null) {
            log.warn("  -> No generator found for task type '{}'. Skipping.", taskType);
            return;
        }

        try {
            Object taskPojo = generator.generateTaskPojo(topic);

            if (taskPojo == null) {
                throw new Exception("Generator returned null POJO.");
            }

            // Convert the POJO to a JSON string
            String taskDataJson = objectMapper.writeValueAsString(taskPojo);

            // Save the final "Product" to the database
            GeneratedTask task = new GeneratedTask();
            task.setTopic(topic);
            task.setTaskType(taskType);
            task.setTaskData(taskDataJson);
            log.info(taskDataJson);

            // We can also link the primary sense if the generator provides it
            // (This is an advanced step for later)
            generatedTaskRepository.save(task);
        } catch (Exception e) {
            log.error("  -> FAILED to generate task of type '{}': {}", taskType, e.getMessage());
        }
    }
}