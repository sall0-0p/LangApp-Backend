package com.lordbucket.langlearn.service.curriculum;

import com.lordbucket.langlearn.dto.model.LessonDTO;
import com.lordbucket.langlearn.dto.model.TaskDTO;
import com.lordbucket.langlearn.model.Lesson;
import com.lordbucket.langlearn.model.User;
import com.lordbucket.langlearn.model.enums.TaskType;
import com.lordbucket.langlearn.model.task.GeneratedTask;
import com.lordbucket.langlearn.model.topic.LessonTopic;
import com.lordbucket.langlearn.model.topic.TopicTaskTypeWeight;
import com.lordbucket.langlearn.repository.GeneratedTaskRepository;
import com.lordbucket.langlearn.repository.LessonRepository;
import com.lordbucket.langlearn.service.mapper.LessonMapper;
import com.lordbucket.langlearn.service.mapper.TaskMapper;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class LessonService {
    private final LessonRepository lessonRepository;
    private final LessonMapper lessonMapper;
    private final GeneratedTaskRepository taskRepository;
    private final TaskMapper taskMapper;

    public LessonService(LessonRepository lessonRepository, LessonMapper lessonMapper, GeneratedTaskRepository generatedTaskRepository, TaskMapper taskMapper) {
        this.lessonRepository = lessonRepository;
        this.lessonMapper = lessonMapper;
        this.taskRepository = generatedTaskRepository;
        this.taskMapper = taskMapper;
    }

    @Transactional
    public LessonDTO getLessonByIdentifier(String identifier, User user) {
        Optional<Lesson> lesson = lessonRepository.findByIdentifier(identifier);

        return lesson.map(lesson1 -> lessonMapper.toDTO(lesson1, user)).orElseThrow();
    }

    /**
     * Returns list of generated tasks in database for specific lesson. Abides by rules like task counts and weights.
     * @param identifier - identifier of lesson tasks are going to be generated for
     */
    @Transactional
    public List<TaskDTO> getTasksForLesson(String identifier) {
        Lesson lesson = lessonRepository.findByIdentifier(identifier)
                .orElseThrow();

        Set<LessonTopic> compositionRules = lesson.getTopicComposition();
        List<GeneratedTask> finalTaskList = new ArrayList<>();

        for (LessonTopic rule : compositionRules) {
            int totalTasksToFetch = rule.getTaskCount();
            Set<TopicTaskTypeWeight> weights = rule.getTopic().getTaskTypeWeights();
            Map<TaskType, Integer> tasksToFetch = calculateTaskCounts(totalTasksToFetch, weights);
            for (Map.Entry<TaskType, Integer> entry : tasksToFetch.entrySet()) {
                TaskType type = entry.getKey();
                int limit = entry.getValue();

                if (limit == 0) continue;

                List<GeneratedTask> tasks = taskRepository.findRandomTasksForTopic(
                        rule.getTopic().getId(),
                        type,
                        Pageable.ofSize(limit)
                );
                finalTaskList.addAll(tasks);
            }
        }

        log.info(finalTaskList.toString());
        Collections.shuffle(finalTaskList);
        return finalTaskList
                .stream()
                .map(taskMapper::toDTO)
                .toList();
    }

    private Map<TaskType, Integer> calculateTaskCounts(int totalTaskCount, Set<TopicTaskTypeWeight> weights) {
        Map<TaskType, Integer> counts = new EnumMap<>(TaskType.class);
        int tasksAllocated = 0;

        for (TopicTaskTypeWeight weight : weights) {
            int count = (int) Math.floor(totalTaskCount * weight.getWeight());
            counts.put(weight.getTaskType(), count);
            tasksAllocated += count;
        }

        // Handle rounding errors (e.g., if total=5, 80/20 -> 4 + 1 = 5)
        // This just assigns the "leftover" tasks to the highest-weighted topic
        int remainder = totalTaskCount - tasksAllocated;
        if (remainder > 0 && !weights.isEmpty()) {
            TaskType highestWeightType = weights.stream()
                    .max(Comparator.comparing(TopicTaskTypeWeight::getWeight))
                    .get()
                    .getTaskType();
            counts.put(highestWeightType, counts.getOrDefault(highestWeightType, 0) + remainder);
        }

        return counts;
    }
}
