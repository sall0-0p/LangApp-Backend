package com.lordbucket.langlearn.repository;

import com.lordbucket.langlearn.model.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
    Optional<Lesson> findByIdentifier(String identifier);
}
