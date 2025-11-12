package com.lordbucket.langlearn.service.curriculum;

import com.lordbucket.langlearn.dto.model.CourseDTO;
import com.lordbucket.langlearn.dto.model.CourseSummaryDTO;
import com.lordbucket.langlearn.dto.model.CurriculumDTO;
import com.lordbucket.langlearn.model.Course;
import com.lordbucket.langlearn.model.User;
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
    public List<CourseSummaryDTO> getAllActiveCourses(User user) {
        return courseRepository
                .findAll()
                .stream()
                .map(course -> courseMapper.toSummaryDTO(course, user))
                .toList();
    }

    /**
     * Retrieves list of all courses with given origin language.
     */
    @Transactional
    public List<CourseSummaryDTO> getAllCoursesForLanguage(Language language, User user) {
        return courseRepository
                .findAllByOriginLanguage(language)
                .stream()
                .map(course -> courseMapper.toSummaryDTO(course, user))
                .toList();
    }

    /**
     * Returns details about the course (without full list of lessons).
     */
    @Transactional
    public CourseDTO getCourseDetails(String identifier, User user) {
        Optional<Course> course = courseRepository.findByIdentifier(identifier);

        // Return course DTO or null.
        if (course.isPresent()) {
            return course.map(course1 -> courseMapper.toDTO(course.get(), user)).get();
        } else {
            return null;
        }
    }

    /**
     * Returns details about the course (with full list of lessons).
     */
    @Transactional
    public CurriculumDTO getCourseCurriculum(String identifier, User user) {
        Optional<Course> course = courseRepository.findByIdentifier(identifier);

        // Return course curriculum or null.
        if (course.isPresent()) {
            return course.map(course1 -> courseMapper.getCurriculumDTO(course1, user)).orElse(null);
        } else {
            return null;
        }
    }
}
