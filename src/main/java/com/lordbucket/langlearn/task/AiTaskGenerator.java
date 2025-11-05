package com.lordbucket.langlearn.task;

import com.lordbucket.langlearn.config.yaml.LessonDataConfig;
import com.lordbucket.langlearn.model.Lesson;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import org.springframework.ai.chat.prompt.Prompt;

/**
 * This interface is used for generators of specific task types.
 */
public interface AiTaskGenerator {
    /**
     * Returns the specific TaskType this generator is responsible for.
     */
    TaskType getTaskType();

    /**
     * Returns the Java class (POJO/record) that the AI's JSON
     * output should be mapped to.
     */
    Class<?> getResponsePojoClass();

    /**
     * Builds the specific System and User prompt for this task.
     *
     * @param lesson     The lesson (for origin/target language, level and other specifics)
     * @param lessonData The "seed" data from the YAML (e.g., vocab list)
     * @return A Prompt object to be sent to the AI.
     */
    Prompt buildPrompt(Lesson lesson, LessonDataConfig lessonData);
}
