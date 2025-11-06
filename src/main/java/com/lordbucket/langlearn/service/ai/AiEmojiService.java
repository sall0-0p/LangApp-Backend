package com.lordbucket.langlearn.service.ai;

import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatModel;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatOptions;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AiEmojiService {
    private final VertexAiGeminiChatModel chatModel;

    public AiEmojiService(VertexAiGeminiChatModel vertexAiGeminiChatModel) {
        this.chatModel = vertexAiGeminiChatModel;
    }

    /**
     * Uses Gemini to query most suitable emoji for certain expression.
     */
    public String generateEmojiForString(String expression) {
        UserMessage message = new UserMessage("Generate one emoji that is supported on most devices for this expression: '" + expression + "', send ONLY emoji.");

        Prompt prompt = Prompt.builder()
                .messages(message)
                .chatOptions(VertexAiGeminiChatOptions.builder()
                        .model(VertexAiGeminiChatModel.ChatModel.GEMINI_2_0_FLASH_LIGHT)
                        .build()
                )
                .build();

        ChatResponse response = chatModel.call(prompt);
        return response.getResult().getOutput().getText();
    }
}
