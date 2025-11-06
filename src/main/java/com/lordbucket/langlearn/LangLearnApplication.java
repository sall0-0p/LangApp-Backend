package com.lordbucket.langlearn;

import com.lordbucket.langlearn.dto.task.TranslateWordToMCTask;
import com.lordbucket.langlearn.service.ai.AiEmojiService;
import com.lordbucket.langlearn.service.ai.AiSchemaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@SpringBootApplication
public class LangLearnApplication {
    private final AiSchemaService aiSchemaService;

    public LangLearnApplication(AiSchemaService aiSchemaService) {
        this.aiSchemaService = aiSchemaService;
    }

    public static void main(String[] args) {
        SpringApplication.run(LangLearnApplication.class, args);
    }

    @Order(1)
    @Component
    public class TestRunner implements CommandLineRunner {
        @Override
        public void run(String... args) {
            log.info(aiSchemaService.getSchema(TranslateWordToMCTask.class));
        }
    }

    @Component
    public static class HelloRunner implements CommandLineRunner {
        private final AiEmojiService aiEmojiService;

        public HelloRunner(AiEmojiService aiEmojiService) {
            this.aiEmojiService = aiEmojiService;
        }

        @Override
        public void run(String... args) {
            String emoji = aiEmojiService.generateEmojiForString("Hello World!");
            log.info(emoji);
        }
    }
}
