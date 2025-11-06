package com.lordbucket.langlearn.repository.topic;

import com.lordbucket.langlearn.model.topic.Topic;
import com.lordbucket.langlearn.model.topic.TopicTaskTypeWeight;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeightRepository extends JpaRepository<TopicTaskTypeWeight, Long> {
    void deleteAllByTopic(Topic topic);
}
