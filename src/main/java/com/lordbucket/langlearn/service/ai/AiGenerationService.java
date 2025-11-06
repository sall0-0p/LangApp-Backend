package com.lordbucket.langlearn.service.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lordbucket.langlearn.model.topic.Topic;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatModel;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class AiGenerationService {
    private final VertexAiGeminiChatModel vertexAiGeminiChat;
    private final ObjectMapper jacksonObjectMapper;
    private final int MAX_AI_ATTEMPTS = 3;
    private final AiSchemaService aiSchemaService;

    public AiGenerationService(
            VertexAiGeminiChatModel vertexAiGeminiChat,
            @Qualifier("jsonObjectMapper") ObjectMapper jacksonObjectMapper,
            AiSchemaService aiSchemaService) {
        this.vertexAiGeminiChat = vertexAiGeminiChat;
        this.jacksonObjectMapper = jacksonObjectMapper;
        this.aiSchemaService = aiSchemaService;
    }

    /**
     * Manages the retry logic.
     * Tries to get a valid, parsed POJO from the AI up to MAX_AI_ATTEMPTS times.
     * @param schema class that will be converted to json as a schema for ai, and object of this class will be returned as a response.
     * @return A valid POJO (e.g., TranslateWordToMCAiResponse) or null if all retries fail.
     */
    public Object callAiWithRetries(UserMessage userMessage, Class<?> schema, Topic topic) {
        for (int attempt = 1; attempt <= MAX_AI_ATTEMPTS; attempt++) {
            log.info("  -> Calling AI for task (Attempt {}/{})", attempt, MAX_AI_ATTEMPTS);
            try {
                return callAiOnce(userMessage, schema, topic);
            } catch (Exception e) {
                log.warn("  -> AI response validation FAILED (Attempt {}). Error: {}", attempt, e.getMessage());

                if (attempt == MAX_AI_ATTEMPTS) {
                    log.error("  -> AI failed to provide valid JSON after {} attempts. Giving up.", MAX_AI_ATTEMPTS);
                }

                try {
                    Thread.sleep(500);
                } catch (InterruptedException ie) {
                    // I do not care.
                }
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
    public Object callAiOnce(UserMessage userMessage, Class<?> schema, Topic topic) throws Exception {
        Prompt prompt = Prompt.builder()
                .messages(List.of(userMessage, this.generateSystemMessage(topic, schema)))
                .chatOptions(VertexAiGeminiChatOptions.builder()
                        .model(VertexAiGeminiChatModel.ChatModel.GEMINI_2_5_FLASH)
                        .build())
                .build();

        ChatResponse response = vertexAiGeminiChat.call(prompt);
        String rawOutput = response.getResult().getOutput().getText();

        String cleanedOutput = cleanAiOutput(rawOutput);

        return jacksonObjectMapper.readValue(cleanedOutput, schema);
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

    /**
     * Generates system message for ai based on topic and schema.
     */
    private SystemMessage generateSystemMessage(Topic topic, Class<?> schema) {
        String jsonSchema = aiSchemaService.getSchema(schema);

        String prompt = "You are a " + topic.getLanguage().toString() + " tutor. " +
                "Your task is to generate a single question " +
                "for an " + topic.getOriginLanguage() + " speaker at the " + topic.getLevel().toString() + "-level. " +
                "You MUST respond ONLY (no code-block or ```) with a JSON object matching the template, but do not send the $schema back:" + jsonSchema;

        return new SystemMessage(prompt);
    }
}
