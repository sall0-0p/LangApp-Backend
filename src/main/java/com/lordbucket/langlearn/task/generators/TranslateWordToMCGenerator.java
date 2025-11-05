package com.lordbucket.langlearn.task.generators;

import com.lordbucket.langlearn.config.yaml.LessonDataConfig;
import com.lordbucket.langlearn.config.yaml.VocabularyEntryConfig;
import com.lordbucket.langlearn.dto.ai.TranslateWordToMCAiResponse;
import com.lordbucket.langlearn.model.Course;
import com.lordbucket.langlearn.model.Lesson;
import com.lordbucket.langlearn.model.Section;
import com.lordbucket.langlearn.service.AiSchemaService;
import com.lordbucket.langlearn.task.AiTaskGenerator;
import com.lordbucket.langlearn.task.TaskType;
import com.openai.models.ChatModel;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatOptions;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

/**
 * Generator for task, where user sees word in their original language,
 * and 3 choices of related words in target language.
 * */
@Slf4j
@Component
public class TranslateWordToMCGenerator implements AiTaskGenerator {
    private final Random random = new Random();
    private final AiSchemaService aiSchemaService;

    public TranslateWordToMCGenerator(AiSchemaService aiSchemaService) {
        this.aiSchemaService = aiSchemaService;
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.TranslateWordToMC;
    }

    @Override
    public Class<?> getResponsePojoClass() {
        return TranslateWordToMCAiResponse.class;
    }

    @Override
    public Prompt buildPrompt(Lesson lesson, LessonDataConfig lessonData) {
        List<VocabularyEntryConfig> vocabulary = lessonData.getVocabulary();
        VocabularyEntryConfig seed = vocabulary.get(random.nextInt(vocabulary.size()));

        String jsonSchema = aiSchemaService.getSchema(this.getResponsePojoClass());

        String originWord = seed.getOrigin();
        String targetWord = seed.getTarget();

        Section section = lesson.getSection();
        Course course = section.getCourse();
        String systemMsg = "You are a " + course.getTargetLanguage() + " tutor. " +
                        "Your task is to generate a single question, where user has to choose from 4 answers in their target language " +
                        "for an " + course.getOriginLanguage() + " speaker at the " + section.getLevel().toString() + "-level. " +
                        "You MUST respond ONLY (no codeblock or ```) with a JSON object matching the template, but do not send the $schema back:" + jsonSchema;

        String userMsg =
                "The question prompt for the user is: '" + originWord + "'.\n" +
                        "The correct answer is: '" + targetWord + "'.\n" +
                        "Generate 3 plausible but incorrect " + course.getTargetLanguage() + " distractors for the '" + targetWord + "', and their respective emojis. " +
                        "Ensure the distractors are also at the " + section.getLevel().toString() + " level.";


        Class<?> pojoClass = this.getResponsePojoClass();
        return Prompt.builder()
                .content(systemMsg + userMsg)
                .chatOptions(VertexAiGeminiChatOptions.builder()
                        .temperature(0.5)
                        .model("gemini-2.5-flash")
                        .build())
                .build();
    }
}
