package com.lordbucket.langlearn.repository.topic;

import com.lordbucket.langlearn.model.topic.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TopicRepository extends JpaRepository<Topic, Long> {
    Optional<Topic> findByIdentifier(String identifier);

    @Query("SELECT t FROM Topic t LEFT JOIN FETCH t.generationRules WHERE t.id = :id")
    Optional<Topic> findByIdWithGenerationRules(@Param("id") Long id);
}
