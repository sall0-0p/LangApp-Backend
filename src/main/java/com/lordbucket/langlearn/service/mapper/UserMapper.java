package com.lordbucket.langlearn.service.mapper;

import com.lordbucket.langlearn.dto.model.LessonSummaryDTO;
import com.lordbucket.langlearn.dto.model.UserDTO;
import com.lordbucket.langlearn.model.Lesson;
import com.lordbucket.langlearn.model.User;
import com.lordbucket.langlearn.model.UserLessonCompletion;
import com.lordbucket.langlearn.repository.CompletionRepository;
import org.springframework.stereotype.Component;

import javax.swing.text.html.Option;
import java.util.Optional;

@Component
public class UserMapper {
    private final CompletionRepository completionRepository;
    private final CourseMapper courseMapper;
    private final LessonMapper lessonMapper;

    public UserMapper(CompletionRepository completionRepository, CourseMapper courseMapper, LessonMapper lessonMapper) {
        this.completionRepository = completionRepository;
        this.courseMapper = courseMapper;
        this.lessonMapper = lessonMapper;
    }

    public UserDTO toDTO(User user) {
        Optional<UserLessonCompletion> lastCompletion = completionRepository.findTopByUserOrderByCompletedAtDesc(user);
        LessonSummaryDTO lastLessonDTO = lastCompletion
                .map(completion -> lessonMapper.toSummaryDTO(completion.getLesson(), user))
                .orElse(null);

        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                completionRepository.findEnrolledCoursesByUser(user)
                        .stream()
                        .map(course -> courseMapper.toSummaryDTO(course, user))
                        .toList(),
                lastLessonDTO
        );
    }
}
