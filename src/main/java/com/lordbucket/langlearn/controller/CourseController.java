package com.lordbucket.langlearn.controller;

import com.lordbucket.langlearn.dto.model.CourseDTO;
import com.lordbucket.langlearn.dto.model.CourseSummaryDTO;
import com.lordbucket.langlearn.dto.model.CurriculumDTO;
import com.lordbucket.langlearn.service.curriculum.CourseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {
    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping()
    public ResponseEntity<List<CourseSummaryDTO>> getAllCourses() {
        List<CourseSummaryDTO> body = courseService.getAllActiveCourses();
        return ResponseEntity.ok(body);
    }

    @GetMapping("/{identifier}")
    public ResponseEntity<CourseDTO> getCourseDetails(@PathVariable(name = "identifier") String identifier) {
        CourseDTO course = courseService.getCourseDetails(identifier);

        // If course not found, return 404.
        if (course == null) {
            return ResponseEntity
                    .notFound()
                    .build();
        }

        return ResponseEntity.ok(course);
    }

    @GetMapping("/c/{identifier}")
    public ResponseEntity<CurriculumDTO> getCourseCurriculum(@PathVariable(name = "identifier") String identifier) {
        CurriculumDTO curriculum = courseService.getCourseCurriculum(identifier);

        // If course not found, return 404.
        if (curriculum == null) {
            return ResponseEntity
                    .notFound()
                    .build();
        }

        return ResponseEntity.ok(curriculum);
    }
}
