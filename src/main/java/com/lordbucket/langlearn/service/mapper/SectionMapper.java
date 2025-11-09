package com.lordbucket.langlearn.service.mapper;

import com.lordbucket.langlearn.dto.model.SectionDTO;
import com.lordbucket.langlearn.dto.model.SectionSummaryDTO;
import com.lordbucket.langlearn.dto.model.SectionSummaryDTOwLessons;
import com.lordbucket.langlearn.model.Section;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class SectionMapper {
    private final LessonMapper lessonMapper;
    private final CourseMapper courseMapper;

    public SectionMapper(LessonMapper lessonMapper, @Lazy CourseMapper courseMapper) {
        this.lessonMapper = lessonMapper;
        this.courseMapper = courseMapper;
    }

    public SectionDTO toDTO(Section section) {
        return new SectionDTO(section.getIdentifier(),
                section.getTitle(),
                section.getOrderIndex(),
                section.getLessons()
                        .stream()
                        .map(lessonMapper::toSummaryDTO)
                        .toList(),
                courseMapper.getSummaryDTO(section.getCourse()));
    }

    public SectionSummaryDTO toSummaryDTO(Section section) {
        return new SectionSummaryDTO(section.getIdentifier(),
                section.getTitle(),
                section.getOrderIndex()
        );
    }

    public SectionSummaryDTOwLessons getSectionSummaryDTOwLessons(Section section) {
        return new SectionSummaryDTOwLessons(section.getIdentifier(),
                section.getTitle(),
                section.getOrderIndex(),
                section.getLessons()
                        .stream()
                        .map(lessonMapper::toSummaryDTO)
                        .toList()
        );
    }
}
