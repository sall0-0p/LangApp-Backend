package com.lordbucket.langlearn.service.generator;

import com.lordbucket.langlearn.model.enums.TaskType;
import com.lordbucket.langlearn.model.task.ITaskGenerator;
import com.lordbucket.langlearn.model.topic.Topic;
import com.lordbucket.langlearn.model.vocabulary.Sense;
import com.lordbucket.langlearn.repository.vocabulary.SenseRepository;
import com.lordbucket.langlearn.dto.task.TranslateWordFromMCTask;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Component
public class TranslateWordFromMCGenerator implements ITaskGenerator {
    private final SenseRepository senseRepository;
    private final Random random = new Random();

    public TranslateWordFromMCGenerator(SenseRepository senseRepository) {
        this.senseRepository = senseRepository;
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.TRANSLATE_WORD_FROM_MC;
    }

    @Override
    public Class<?> getResponsePojoClass() {
        return TranslateWordFromMCTask.class;
    }

    @Override
    public Object generateTaskPojo(Topic topic) throws Exception {
        List<Sense> senses = senseRepository.findByTopics(topic);
        if (senses.isEmpty()) {
            throw new Exception("No 'senses' (vocabulary) found for topic: " + topic.getIdentifier());
        }

        // Pick a correct answer
        Sense correctAnswer = senses.get(random.nextInt(senses.size()));

        // Getting distractors
        List<Sense> allDistractors = senseRepository.findAllDistractorsForTopic(
                topic.getId(),
                correctAnswer.getId()
        );

        if (allDistractors.size() < 3) {
            throw new Exception("Not enough distractors found for topic: " + topic.getIdentifier() + ". Needs at least 3.");
        }

        Collections.shuffle(allDistractors);
        List<Sense> distractors = allDistractors.subList(0, 3);

        if (distractors.size() < 3) {
            throw new Exception("Not enough distractors found for topic: " + topic.getIdentifier());
        }

        // Build the POJO
        return new TranslateWordFromMCTask(
                correctAnswer.getLexeme().getExpression(),
                correctAnswer.getOriginTranslation(),
                distractors.stream().map(Sense::toTaskOption).collect(Collectors.toList())
        );
    }
}
