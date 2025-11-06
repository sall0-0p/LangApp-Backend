package com.lordbucket.langlearn.service.seeder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lordbucket.langlearn.config.yaml.TopicConfig;
import com.lordbucket.langlearn.model.topic.Topic;
import com.lordbucket.langlearn.repository.topic.TopicRepository;
import com.lordbucket.langlearn.service.TopicService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@Order(2)
@Slf4j
public class TopicSeederService implements CommandLineRunner {

    private final ObjectMapper yamlObjectMapper;
    private final TopicService topicService;
    private final TopicRepository topicRepository;
    private final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

    public TopicSeederService(@Qualifier("yamlObjectMapper") ObjectMapper yamlObjectMapper,
                              TopicService topicService,
                              TopicRepository topicRepository) {
        this.yamlObjectMapper = yamlObjectMapper;
        this.topicService = topicService;
        this.topicRepository = topicRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("--- 🏭 Starting Topic Seeder (Order 2) ---");

        // --- PRE-PASS: Read all YAML files into memory ---
        Resource[] resources = resolver.getResources("classpath*:courses/topics/*.y*ml");
        if (resources.length == 0) {
            log.warn("No topic files found in 'resources/courses/topics/'. Skipping.");
            return;
        }

        List<TopicConfig> allTopicConfigs = new ArrayList<>();
        for (Resource resource : resources) {
            try (InputStream is = resource.getInputStream()) {
                allTopicConfigs.add(yamlObjectMapper.readValue(is, TopicConfig.class));
            } catch (Exception e) {
                log.error("  -> FAILED to parse topic file {}: {}", resource.getFilename(), e.getMessage());
            }
        }

        // --- PASS 1: Sync all Topic blueprints, vocab, and rules ---
        log.info("  -> Pass 1: Syncing {} Topic blueprints...", allTopicConfigs.size());
        for (TopicConfig config : allTopicConfigs) {
            try {
                topicService.syncTopicBlueprint(config);
            } catch (Exception e) {
                log.error("  -> FAILED to sync blueprint for {}: {}", config.getIdentifier(), e.getMessage());
            }
        }
        log.info("  -> Pass 1: Complete.");

        // --- PASS 2: Link all related topics ---
        log.info("  -> Pass 2: Linking related topics...");
        for (TopicConfig config : allTopicConfigs) {
            try {
                topicService.linkRelatedTopics(config);
            } catch (Exception e) {
                log.error("  -> FAILED to link related topics for {}: {}", config.getIdentifier(), e.getMessage());
            }
        }
        log.info("  -> Pass 2: Complete.");

        // --- PASS 3: Run the AI Task Factory ---
        log.info("  -> Pass 3: Starting AI Task Generation...");
        // We fetch the topics from the DB to ensure we're working with persistent entities
        for (Topic topic : topicRepository.findAll()) {
            topicService.generateTasksForTopic(topic);
        }
        log.info("  -> Pass 3: Complete.");

        log.info("--- 🏭 Topic Seeder Finished ---");
    }
}