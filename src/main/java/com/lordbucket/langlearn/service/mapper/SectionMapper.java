package com.lordbucket.langlearn.service.mapper;

import com.lordbucket.langlearn.dto.model.SectionDTO;
import com.lordbucket.langlearn.dto.model.SectionSummaryDTO;
import com.lordbucket.langlearn.dto.model.SectionSummaryDTOwLessons;
import com.lordbucket.langlearn.model.Section;
import com.lordbucket.langlearn.model.User;
import com.lordbucket.langlearn.service.curriculum.LessonCompletionService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class SectionMapper {
    private final LessonMapper lessonMapper;
    private final CourseMapper courseMapper;
    private final LessonCompletionService lessonCompletionService;

    public SectionMapper(LessonMapper lessonMapper, @Lazy CourseMapper courseMapper, LessonCompletionService lessonCompletionService) {
        this.lessonMapper = lessonMapper;
        this.courseMapper = courseMapper;
        this.lessonCompletionService = lessonCompletionService;
    }

    public SectionDTO toDTO(Section section, User user) {
        return new SectionDTO(section.getIdentifier(),
                section.getTitle(),
                section.getOrderIndex(),
                lessonCompletionService.isSectionCompleted(user, section),
                section.getLessons()
                        .stream()
                        .map(lesson -> lessonMapper.toSummaryDTO(lesson, user))
                        .toList(),
                courseMapper.toSummaryDTO(section.getCourse(), user));
    }

    public SectionSummaryDTO toSummaryDTO(Section section, User user) {
        return new SectionSummaryDTO(section.getIdentifier(),
                section.getTitle(),
                section.getOrderIndex(),
                lessonCompletionService.isSectionCompleted(user, section)
        );
    }

    public SectionSummaryDTOwLessons getSectionSummaryDTOwLessons(Section section, User user) {
        return new SectionSummaryDTOwLessons(section.getIdentifier(),
                section.getTitle(),
                section.getOrderIndex(),
                section.getLessons()
                        .stream()
                        .map(lesson -> lessonMapper.toSummaryDTO(lesson, user))
                        .toList()
        );
    }
}
