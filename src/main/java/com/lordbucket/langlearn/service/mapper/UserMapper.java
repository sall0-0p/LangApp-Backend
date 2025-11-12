package com.lordbucket.langlearn.service.mapper;

import com.lordbucket.langlearn.dto.model.UserDTO;
import com.lordbucket.langlearn.model.User;
import com.lordbucket.langlearn.repository.CompletionRepository;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    private final CompletionRepository completionRepository;
    private final CourseMapper courseMapper;

    public UserMapper(CompletionRepository completionRepository, CourseMapper courseMapper) {
        this.completionRepository = completionRepository;
        this.courseMapper = courseMapper;
    }

    public UserDTO toDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                completionRepository.findEnrolledCoursesByUser(user)
                        .stream()
                        .map(course -> courseMapper.toSummaryDTO(course, user))
                        .toList()
        );
    }
}
