package com.lordbucket.langlearn.repository;

import com.lordbucket.langlearn.model.task.GeneratedTask;
import com.lordbucket.langlearn.model.topic.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GeneratedTaskRepository extends JpaRepository<GeneratedTask, Long> {
    int countByTopic(Topic topic);
}
