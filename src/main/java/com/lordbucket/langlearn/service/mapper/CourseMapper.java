package com.lordbucket.langlearn.service.mapper;

import com.lordbucket.langlearn.dto.model.CourseDTO;
import com.lordbucket.langlearn.dto.model.CourseSummaryDTO;
import com.lordbucket.langlearn.dto.model.CurriculumDTO;
import com.lordbucket.langlearn.model.Course;
import org.springframework.stereotype.Component;

@Component
public class CourseMapper {
    private final SectionMapper sectionMapper;

    public CourseMapper(SectionMapper sectionMapper) {
        this.sectionMapper = sectionMapper;
    }

    public CourseDTO getDTO(Course course) {
        return new CourseDTO(course.getIdentifier(),
                course.getTitle(),
                course.getOriginLanguage(),
                course.getTargetLanguage(),
                course.getSections()
                        .stream()
                        .map(sectionMapper::toSummaryDTO)
                        .toList()
        );
    }

    public CourseSummaryDTO getSummaryDTO(Course course) {
        return new CourseSummaryDTO(course.getIdentifier(),
                course.getTitle(),
                course.getOriginLanguage(),
                course.getTargetLanguage()
        );
    }

    public CurriculumDTO getCurriculumDTO(Course course) {
        return new CurriculumDTO(course.getIdentifier(),
                course.getTitle(),
                course.getOriginLanguage(),
                course.getTargetLanguage(),
                course.getSections()
                        .stream()
                        .map(sectionMapper::getSectionSummaryDTOwLessons)
                        .toList()
        );
    }
}
