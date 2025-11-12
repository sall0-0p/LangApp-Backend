package com.lordbucket.langlearn.repository;

import com.lordbucket.langlearn.model.Course;
import com.lordbucket.langlearn.model.enums.Language;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {
    Optional<Course> findByIdentifier(String identifier);
    List<Course> findAllByOriginLanguage(Language language);
}
