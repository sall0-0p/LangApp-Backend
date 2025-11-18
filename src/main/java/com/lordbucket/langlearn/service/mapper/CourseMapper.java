package com.lordbucket.langlearn.service.mapper;

import com.lordbucket.langlearn.dto.model.CourseDTO;
import com.lordbucket.langlearn.dto.model.CourseSummaryDTO;
import com.lordbucket.langlearn.dto.model.CurriculumDTO;
import com.lordbucket.langlearn.model.Course;
import com.lordbucket.langlearn.model.User;
import com.lordbucket.langlearn.service.curriculum.LessonCompletionService;
import org.springframework.stereotype.Component;

@Component
public class CourseMapper {
    private final SectionMapper sectionMapper;
    private final LessonCompletionService lessonCompletionService;

    public CourseMapper(SectionMapper sectionMapper, LessonCompletionService lessonCompletionService) {
        this.sectionMapper = sectionMapper;
        this.lessonCompletionService = lessonCompletionService;
    }

    public CourseDTO toDTO(Course course, User user) {
        return new CourseDTO(course.getIdentifier(),
                course.getTitle(),
                course.getEmoji(),
                course.getOriginLanguage(),
                course.getTargetLanguage(),
                course.getSections()
                        .stream()
                        .map(section -> sectionMapper.toSummaryDTO(section, user))
                        .toList(),
                lessonCompletionService.isEnrolledInCourse(user, course)
        );
    }

    public CourseSummaryDTO toSummaryDTO(Course course, User user) {
        return new CourseSummaryDTO(course.getIdentifier(),
                course.getTitle(),
                course.getEmoji(),
                course.getOriginLanguage(),
                course.getTargetLanguage(),
                lessonCompletionService.isEnrolledInCourse(user, course)
        );
    }
}
