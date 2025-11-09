package com.lordbucket.langlearn.service.curriculum;

import com.lordbucket.langlearn.dto.model.CourseDTO;
import com.lordbucket.langlearn.dto.model.CourseSummaryDTO;
import com.lordbucket.langlearn.dto.model.CurriculumDTO;
import com.lordbucket.langlearn.model.Course;
import com.lordbucket.langlearn.model.enums.Language;
import com.lordbucket.langlearn.repository.CourseRepository;
import com.lordbucket.langlearn.service.mapper.CourseMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class CourseService {
    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;

    public CourseService(CourseRepository courseRepository, CourseMapper courseMapper) {
        this.courseRepository = courseRepository;
        this.courseMapper = courseMapper;
    }

    /**
     * Retrieves list of available origin languages.
     */
    public List<Language> getAvailableLanguages() {
        return Arrays
                .stream(Language.values())
                .toList();
    }

    /**
     * Retrieves list of all (active) courses.
     */
    @Transactional
    public List<CourseSummaryDTO> getAllActiveCourses() {
        return courseRepository
                .findAll()
                .stream()
                .map(courseMapper::getSummaryDTO)
                .toList();
    }

    /**
     * Retrieves list of all courses with given origin language.
     */
    @Transactional
    public List<CourseSummaryDTO> getAllCoursesForLanguage(Language language) {
        return courseRepository
                .findAllByOriginLanguage(language)
                .stream()
                .map(courseMapper::getSummaryDTO)
                .toList();
    }

    /**
     * Returns details about the course (without full list of lessons).
     */
    @Transactional
    public CourseDTO getCourseDetails(String identifier) {
        Optional<Course> course = courseRepository.findByIdentifier(identifier);

        // Return course DTO or null.
        return course.map(courseMapper::getDTO).orElse(null);
    }

    /**
     * Returns details about the course (with full list of lessons).
     */
    @Transactional
    public CurriculumDTO getCourseCurriculum(String identifier) {
        Optional<Course> course = courseRepository.findByIdentifier(identifier);

        // Return course curriculum or null.
        return course.map(courseMapper::getCurriculumDTO).orElse(null);
    }
}
