package com.lordbucket.langlearn.service.curriculum;

import com.lordbucket.langlearn.model.enums.TaskType;
import com.lordbucket.langlearn.model.task.ITaskGenerator;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class TaskService {
    // This will hold all our generator strategies
    private final Map<TaskType, ITaskGenerator> generators = new EnumMap<>(TaskType.class);

    // Spring injects a List of all beans that implement the interface
    private final List<ITaskGenerator> generatorBeans;

    public TaskService(List<ITaskGenerator> generatorBeans) {
        this.generatorBeans = generatorBeans;
    }

    /**
     * This method runs after the service is created.
     * It populates our strategy map for fast lookups.
     */
    @PostConstruct
    public void initGenerators() {
        for (ITaskGenerator generator : generatorBeans) {
            generators.put(generator.getTaskType(), generator);
        }
    }

    public ITaskGenerator getTaskGeneratorForType(TaskType taskType) {
        return generators.get(taskType);
    }
}
