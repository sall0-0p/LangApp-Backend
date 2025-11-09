package com.lordbucket.langlearn.service.curriculum;

import com.lordbucket.langlearn.config.yaml.TopicConfig;
import com.lordbucket.langlearn.config.yaml.VocabIdentifier;
import com.lordbucket.langlearn.model.enums.Language;
import com.lordbucket.langlearn.model.enums.LanguageLevel;
import com.lordbucket.langlearn.model.topic.Topic;
import com.lordbucket.langlearn.model.topic.TopicGenerationRule;
import com.lordbucket.langlearn.model.topic.TopicTaskTypeWeight;
import com.lordbucket.langlearn.model.vocabulary.Sense;
import com.lordbucket.langlearn.repository.GeneratedTaskRepository;
import com.lordbucket.langlearn.repository.topic.RuleRepository;
import com.lordbucket.langlearn.repository.topic.TopicRepository;
import com.lordbucket.langlearn.repository.topic.WeightRepository;
import com.lordbucket.langlearn.repository.vocabulary.SenseRepository;
import com.lordbucket.langlearn.service.TaskGenerationService;
import com.lordbucket.langlearn.model.enums.TaskType;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
public class TopicService {

    private final TopicRepository topicRepository;
    private final SenseRepository senseRepository;
    private final WeightRepository weightRepository;
    private final RuleRepository ruleRepository;
    private final GeneratedTaskRepository generatedTaskRepository;
    private final TaskGenerationService taskGenerationService;

    public TopicService(TopicRepository topicRepository,
                        SenseRepository senseRepository,
                        WeightRepository weightRepository,
                        RuleRepository ruleRepository,
                        GeneratedTaskRepository generatedTaskRepository, TaskGenerationService taskGenerationService) {
        this.topicRepository = topicRepository;
        this.senseRepository = senseRepository;
        this.weightRepository = weightRepository;
        this.ruleRepository = ruleRepository;
        this.generatedTaskRepository = generatedTaskRepository;
        this.taskGenerationService = taskGenerationService;
    }

    /**
     * Creates/updates the core Topic entity, its vocab, and its rules.
     * It does NOT link related topics.
     *
     * @param config The parsed YAML config for one topic.
     * @return The saved, persistent Topic entity.
     */
    @Transactional
    public Topic syncTopicBlueprint(TopicConfig config) {
        // 1. Find or create the Topic
        Topic topic = topicRepository.findByIdentifier(config.getIdentifier())
                .orElseGet(() -> {
                    Topic newTopic = new Topic();
                    newTopic.setIdentifier(config.getIdentifier());
                    return newTopic;
                });

        topic.setTitle(config.getTitle());
        topic.setLevel(LanguageLevel.valueOf(config.getLevel().toUpperCase()));
        topic.setLanguage(Language.valueOf(config.getLanguage()));
        topic.setOriginLanguage(Language.valueOf(config.getOriginLanguage()));

        // 2. Save the parent *first* to prevent TransientObjectException
        topicRepository.save(topic);

        // 3. Link Vocabulary (Senses)
        Set<Sense> senses = linkVocabulary(topic, config.getVocabulary());
        topic.setSenses(senses);

        // 4. Save Learning Strategy (Task Weights)
        syncTaskTypeWeights(topic, config.getTaskTypeWeights());

        // 5. Save Generation Rules
        syncGenerationRules(topic, config.getGenerationPlan());

        // 6. Save the topic with its new collections
        return topicRepository.save(topic);
    }

    /**
     * Links this topic to its related topics.
     * This is called *after* all topics are guaranteed to exist in the DB.
     */
    @Transactional
    public void linkRelatedTopics(TopicConfig config) {
        if (config.getRelatedTopics() == null || config.getRelatedTopics().isEmpty()) {
            return; // Nothing to link
        }

        // 1. Find the topic we are linking *from*
        Topic topic = topicRepository.findByIdentifier(config.getIdentifier())
                .orElseThrow(() -> new IllegalStateException("Topic not found, this should not happen: " + config.getIdentifier()));

        // 2. Find and link the related topics
        Set<Topic> related = new HashSet<>();
        for (String relatedId : config.getRelatedTopics()) {
            topicRepository.findByIdentifier(relatedId)
                    .ifPresentOrElse(
                            related::add, // Add the found topic to the set
                            () -> log.warn("Related topic not found: '{}' when syncing '{}'. Skipping.", relatedId, config.getIdentifier())
                    );
        }

        topic.setRelatedTopics(related);
        topicRepository.save(topic);
    }

    /**
     * Triggers the AI task generation for a *single* topic.
     * This is called *after* all topics and rules are synced.
     */
    @Transactional
    public void generateTasksForTopic(Topic topic) {
        Topic managedTopic = topicRepository.findByIdWithGenerationRules(topic.getId())
                .orElseThrow(() -> new IllegalStateException("Topic not found: " + topic.getId()));

        long existingTaskCount = generatedTaskRepository.countByTopic(topic); // You'll need this method in GeneratedTaskRepository
        if (existingTaskCount > 0) {
            log.info("    -> Skipping task generation for '{}'. {} tasks already exist.", topic.getIdentifier(), existingTaskCount);
            return;
        }

        Set<TopicGenerationRule> rules = managedTopic.getGenerationRules();
        if (rules.isEmpty()) {
            log.warn("    -> No generation rules found for '{}'. Skipping task generation.", topic.getIdentifier());
            return;
        }

        log.info("    -> 🏭 Generating tasks for topic: {}", topic.getIdentifier());
        for (TopicGenerationRule rule : rules) {
            for (int i = 0; i < rule.getCount(); i++) {
                try {
                    // The AiGenerationService will find the right generator
                    // and call the AI to create and save one task.
                    taskGenerationService.generateAndSaveTask(topic, rule);
                } catch (Exception e) {
                    log.error("    -> FAILED to generate task ({} of {}): {}", (i + 1), rule.getCount(), e.getMessage());
                }
            }
        }
    }

    private Set<Sense> linkVocabulary(Topic topic, List<VocabIdentifier> vocabIdentifiers) {
        Set<Sense> senses = new HashSet<>();
        if (vocabIdentifiers == null) return senses;

        for (VocabIdentifier vocabId : vocabIdentifiers) {
            // Find the Sense by its unique composite key
            Sense sense = senseRepository.findByLexemeExpressionAndOriginTranslation(vocabId.getTarget(), vocabId.getOrigin())
                    .orElseThrow(() -> new RuntimeException("Seeder Error: Could not find Sense for " + vocabId.getTarget()));
            senses.add(sense);
        }
        return senses;
    }

    private void syncTaskTypeWeights(Topic topic, Map<String, Double> weights) {
        if (weights == null) return;

        // Clear old rules to ensure YAML is the source of truth
        weightRepository.deleteAllByTopic(topic);
        for (var entry : weights.entrySet()) {
            TopicTaskTypeWeight weight = new TopicTaskTypeWeight();
            weight.setTopic(topic);
            weight.setTaskType(TaskType.valueOf(entry.getKey().toUpperCase()));
            weight.setWeight(entry.getValue());
            weightRepository.save(weight);
        }
    }

    private void syncGenerationRules(Topic topic, Map<String, Integer> plan) {
        if (plan == null) return;

        // Clear old rules
        ruleRepository.deleteAllByTopic(topic);
        for (var entry : plan.entrySet()) {
            TopicGenerationRule rule = new TopicGenerationRule();
            rule.setTopic(topic);
            rule.setTaskType(TaskType.valueOf(entry.getKey().toUpperCase()));
            rule.setCount(entry.getValue());
            ruleRepository.save(rule);
        }
    }
}