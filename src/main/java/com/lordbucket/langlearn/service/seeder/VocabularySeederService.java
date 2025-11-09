package com.lordbucket.langlearn.service.seeder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lordbucket.langlearn.config.yaml.VocabularyEntryConfig;
import com.lordbucket.langlearn.model.enums.Language;
import com.lordbucket.langlearn.service.curriculum.VocabularyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;

@Service
@Order(1) // <-- Runs FIRST
@Slf4j
public class VocabularySeederService implements CommandLineRunner {

    private final ObjectMapper yamlObjectMapper;
    private final VocabularyService vocabularyService;
    private final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

    public VocabularySeederService(@Qualifier("yamlObjectMapper") ObjectMapper yamlObjectMapper,
                                   VocabularyService vocabularyService) {
        this.yamlObjectMapper = yamlObjectMapper;
        this.vocabularyService = vocabularyService;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("--- 📖 Starting Vocabulary Seeder (Order 1) ---");
        Resource[] resources = resolver.getResources("classpath*:vocabulary/*.y*ml");

        if (resources.length == 0) {
            log.warn("No vocabulary files found in 'resources/vocabulary/'. Skipping.");
            return;
        }

        for (Resource resource : resources) {
            log.info("  -> Processing vocabulary file: {}", resource.getFilename());

            String filename = resource.getFilename();
            if (filename == null) continue;

            // Get the base name, e.g., "english-german"
            String baseName = filename.split("\\.")[0];
            String[] parts = baseName.split("-");

            if (parts.length != 2) {
                log.warn("    -> Skipping file: '{}'. Filename must be in 'origin-target.yml' format (e.g., 'english-german.yml').", filename);
                continue;
            }

            Language originLanguage;
            Language targetLanguage;

            try {
                originLanguage = Language.valueOf(parts[0].toUpperCase());
                targetLanguage = Language.valueOf(parts[1].toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("    -> Skipping file: '{}'. Contains invalid language codes. Could not parse '{}' or '{}'.",
                        filename, parts[0].toUpperCase(), parts[1].toUpperCase());
                continue;
            }

            try (InputStream is = resource.getInputStream()) {
                // Parse the file which is a list of entries
                List<VocabularyEntryConfig> entries = yamlObjectMapper.readValue(is, new TypeReference<>() {});

                for (VocabularyEntryConfig entry : entries) {
                    vocabularyService.registerSense(entry, targetLanguage, originLanguage);
                }
                log.info("  -> Successfully seeded {} vocabulary entries from {}", entries.size(), filename);
            } catch (Exception e) {
                log.error("  -> FAILED to seed vocabulary file {}: {}", resource.getFilename(), e.getMessage());
            }
        }
        log.info("--- 📖 Vocabulary Seeder Finished ---");
    }
}