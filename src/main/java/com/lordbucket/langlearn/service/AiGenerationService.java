package com.lordbucket.langlearn.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lordbucket.langlearn.config.yaml.LessonDataConfig;
import com.lordbucket.langlearn.model.GeneratedTask;
import com.lordbucket.langlearn.repository.GeneratedTaskRepository;
import com.lordbucket.langlearn.task.AiTaskGenerator;
import com.lordbucket.langlearn.task.TaskType;
import com.lordbucket.langlearn.model.Lesson;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class AiGenerationService {
    private final Map<TaskType, AiTaskGenerator> generators = new EnumMap<>(TaskType.class);
    private final VertexAiGeminiChatModel chatModel;
    private static final int MAX_AI_ATTEMPTS = 3;
    private final ObjectMapper jacksonObjectMapper;
    private final GeneratedTaskRepository generatedTaskRepository;

    public AiGenerationService(
            List<AiTaskGenerator> generatorBeans,
            VertexAiGeminiChatModel vertexAiGeminiChatModel,
            @Qualifier("jsonObjectMapper") ObjectMapper jacksonObjectMapper,
            GeneratedTaskRepository generatedTaskRepository) {
        for (AiTaskGenerator generator : generatorBeans) {
            log.info("Registering AI Task Generator: {}", generator.getTaskType());
            this.generators.put(generator.getTaskType(), generator);
        }

        this.chatModel = vertexAiGeminiChatModel;
        this.jacksonObjectMapper = jacksonObjectMapper;
        this.generatedTaskRepository = generatedTaskRepository;
    }

    /**
     * Generate a task for a lesson, based on lesson, task type and related data (vocabulary)
     * @param lesson related lesson
     * @param taskType type of task
     * @param lessonData lesson data
     */
    @Transactional
    public void generateAndSaveTask(Lesson lesson, TaskType taskType, LessonDataConfig lessonData) {
        AiTaskGenerator generator = generators.get(taskType);

        if (generator == null) {
            log.error("No AI generator found for task type: {}", taskType);
            throw new IllegalArgumentException("No AI generator found for task type: " + taskType);
        }

        try {
            Object validatedPojo = getValidResponseWithRetries(generator, lesson, lessonData);

            if (validatedPojo != null) {
                saveTask(lesson, taskType, validatedPojo);
                log.info("  -> Successfully generated and saved task for lesson '{}'", lesson.getTitle());
            } else {
                log.error("  -> Failed to generate task for lesson '{}' after {} attempts. Task not saved.",
                        lesson.getTitle(), MAX_AI_ATTEMPTS);
            }
        } catch (Exception e) {
            log.error("  -> Unhandled exception during task generation for lesson '{}': {}",
                    lesson.getTitle(), e.getMessage());
        }
    }

    /**
     * Manages the retry logic.
     * Tries to get a valid, parsed POJO from the AI up to MAX_AI_ATTEMPTS times.
     *
     * @return A valid POJO (e.g., TranslateWordToMCAiResponse) or null if all retries fail.
     */
    private Object getValidResponseWithRetries(AiTaskGenerator generator, Lesson lesson, LessonDataConfig lessonData) {
        for (int attempt = 1; attempt <= MAX_AI_ATTEMPTS; attempt++) {
            log.info("  -> Calling AI for task type '{}' (Attempt {}/{})", generator.getTaskType(), attempt, MAX_AI_ATTEMPTS);
            try {
                // Try to call the AI and parse its response.
                // This method will throw an exception if validation fails.
                return callAndValidateAi(generator, lesson, lessonData);

            } catch (Exception e) {
                log.warn("  -> AI response validation FAILED (Attempt {}). Error: {}", attempt, e.getMessage());

                if (attempt == MAX_AI_ATTEMPTS) {
                    log.error("  -> AI failed to provide valid JSON after {} attempts. Giving up.", MAX_AI_ATTEMPTS);
                }

                try { Thread.sleep(500); } catch (InterruptedException ie) {}
            }
        }
        // All retries failed
        return null;
    }

    /**
     * Performs a single attempt to call the AI and validate its response.
     *
     * @return A valid, parsed POJO.
     * @throws Exception if the AI call fails or the JSON is invalid.
     */
    private Object callAndValidateAi(AiTaskGenerator generator, Lesson lesson, LessonDataConfig lessonData) throws Exception {
        Prompt prompt = generator.buildPrompt(lesson, lessonData);
        Class<?> pojoClass = generator.getResponsePojoClass();

        ChatResponse response = chatModel.call(prompt);
        String rawOutput = response.getResult().getOutput().getText();
        log.info(rawOutput);

        String cleanedOutput = cleanAiOutput(rawOutput);

        return jacksonObjectMapper.readValue(cleanedOutput, pojoClass);
    }

    /**
     * Saves the validated POJO as a JSON string in the database.
     */
    private void saveTask(Lesson lesson, TaskType taskType, Object taskPojo) throws JsonProcessingException {
        // Convert the validated POJO back into a clean JSON string
        String taskDataJson = jacksonObjectMapper.writeValueAsString(taskPojo);

        // Create and save the new task entity
        GeneratedTask task = new GeneratedTask();
        task.setLesson(lesson);
        task.setTaskType(taskType);
        log.info(taskDataJson);
        task.setTaskData(taskDataJson);

        generatedTaskRepository.save(task);
    }

    /**
     * A helper method to clean the AI's output,
     * removing markdown wrappers like ```json ... ```
     */
    private String cleanAiOutput(String rawText) {
        if (rawText == null) return "";

        int firstBrace = rawText.indexOf('{');
        int lastBrace = rawText.lastIndexOf('}');

        if (firstBrace != -1 && lastBrace != -1 && lastBrace > firstBrace) {
            return rawText.substring(firstBrace, lastBrace + 1);
        }

        return rawText;
    }
}
