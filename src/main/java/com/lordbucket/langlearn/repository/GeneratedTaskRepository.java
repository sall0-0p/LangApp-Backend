package com.lordbucket.langlearn.repository;

import com.lordbucket.langlearn.model.GeneratedTask;
import com.lordbucket.langlearn.model.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GeneratedTaskRepository extends JpaRepository<GeneratedTask, Long> {
    int countByLesson(Lesson lesson);
}
