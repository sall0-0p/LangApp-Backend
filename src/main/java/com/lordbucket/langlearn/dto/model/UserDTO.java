package com.lordbucket.langlearn.dto.model;

import com.lordbucket.langlearn.model.Lesson;

import java.util.List;
import java.util.Optional;

public record UserDTO(
        Long id,
        String username,
        String email,
        List<CourseSummaryDTO> listOfEnrolledCourses,
        LessonSummaryDTO lastCompletedLesson,
        List<String> completedLessonIds
) {
}
