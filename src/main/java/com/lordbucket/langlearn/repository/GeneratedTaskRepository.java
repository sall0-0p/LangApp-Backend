package com.lordbucket.langlearn.repository;

import com.lordbucket.langlearn.model.enums.TaskType;
import com.lordbucket.langlearn.model.task.GeneratedTask;
import com.lordbucket.langlearn.model.topic.Topic;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GeneratedTaskRepository extends JpaRepository<GeneratedTask, Long> {
    int countByTopic(Topic topic);

    /**
     * This is the "Smart" Task Fetcher for the LessonService.
     * It finds a random list of tasks that match:
     * 1. The Topic (e.g., "de-topic-food")
     * 2. The TaskType (e.g., "VOCABULARY")
     * 3. NOT in the user's history (excluded by ID list)
     *
     * We use a native query for database-agnostic RANDOM() and LIMIT.
     */
    @Query("SELECT t FROM GeneratedTask t " +
            "WHERE t.topic.id = :topicId " +
            "AND t.taskType = :taskType " +
            "ORDER BY FUNCTION('RANDOM')")
    List<GeneratedTask> findRandomTasksForTopic(
            @Param("topicId") Long topicId,
            @Param("taskType") TaskType taskType,
            @Param("limit") Pageable pageable
    );
}
