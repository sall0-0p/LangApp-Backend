package com.lordbucket.langlearn.model.task;

import com.lordbucket.langlearn.model.enums.TaskType;
import com.lordbucket.langlearn.model.topic.Topic;

/**
 * The "Strategy" interface.
 * Each implementation knows how to build the POJO for one specific TaskType.
 * It can do this via a database query (AI-free) or by calling the
 * AiGenerationService (AI-powered).
 */
public interface ITaskGenerator {
    /**
     * The unique TaskType this generator is responsible for.
     * This is used as a key in a Map.
     */
    TaskType getTaskType();

    /**
     * The Java class (Record/POJO) that this generator produces.
     * This is used for JSON serialization and validation.
     */
    Class<?> getResponsePojoClass();

    /**
     * The main factory method.
     * Takes the "ingredients" (the Topic) and produces the
     * "product" (the task data POJO).
     *
     * @param topic The Topic containing all context (vocabulary, level, etc.)
     * @return A Java POJO (e.g., a TranslateWordToMCAiResponse)
     * @throws Exception if generation fails (e.g., AI fails, no distractors found)
     */
    Object generateTaskPojo(Topic topic) throws Exception;
}
