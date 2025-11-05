package com.lordbucket.langlearn.repository;

import com.lordbucket.langlearn.model.Course;
import com.lordbucket.langlearn.model.Section;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SectionRepository extends JpaRepository<Section, Long> {
    Optional<Section> findByCourseAndTitle(Course course, String title);
}
