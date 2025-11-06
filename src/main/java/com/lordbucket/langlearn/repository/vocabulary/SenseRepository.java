package com.lordbucket.langlearn.repository.vocabulary;

import com.lordbucket.langlearn.model.enums.Language;
import com.lordbucket.langlearn.model.topic.Topic;
import com.lordbucket.langlearn.model.vocabulary.Sense;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SenseRepository extends JpaRepository<Sense, Long> {
    Optional<Sense> findByLexemeExpressionAndOriginTranslation(String expression, String originTranslation);
    Optional<Sense> findByLexemeExpressionAndOriginLanguage(String expression, Language originLanguage);

    @Query("SELECT s FROM Sense s JOIN s.topics t WHERE t = :topic")
    List<Sense> findByTopics(@Param("topic") Topic topic);

    @Query("SELECT DISTINCT s FROM Sense s JOIN s.topics t WHERE (t.id = :topicId OR t IN (SELECT rt FROM Topic ct JOIN ct.relatedTopics rt WHERE ct.id = :topicId)) AND s.id != :senseToExcludeId")
    List<Sense> findAllDistractorsForTopic(
            @Param("topicId") Long topicId,
            @Param("senseToExcludeId") Long senseToExcludeId
    );
}
