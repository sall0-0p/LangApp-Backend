package com.lordbucket.langlearn.dto.model;

import java.util.List;

public record UserDTO(
        Long id,
        String username,
        String email,
        List<CourseSummaryDTO> listOfEnrolledCourses
) {
}
