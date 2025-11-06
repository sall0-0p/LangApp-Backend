package com.lordbucket.langlearn.repository.topic;

import com.lordbucket.langlearn.model.topic.Topic;
import com.lordbucket.langlearn.model.topic.TopicGenerationRule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RuleRepository extends JpaRepository<TopicGenerationRule, Long> {
    void deleteAllByTopic(Topic topic);
}
