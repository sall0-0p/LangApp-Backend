package com.lordbucket.langlearn.service.mapper;

import com.lordbucket.langlearn.dto.model.LessonDTO;
import com.lordbucket.langlearn.dto.model.LessonSummaryDTO;
import com.lordbucket.langlearn.model.Lesson;
import com.lordbucket.langlearn.model.topic.LessonTopic;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class LessonMapper {
    private final TopicMapper topicMapper;
    private final SectionMapper sectionMapper;

    public LessonMapper(TopicMapper topicMapper, @Lazy SectionMapper sectionMapper) {
        this.topicMapper = topicMapper;
        this.sectionMapper = sectionMapper;
    }

    public LessonDTO toDTO(Lesson lesson) {
        return new LessonDTO(lesson.getIdentifier(),
                lesson.getTitle(),
                lesson.getOrderIndex(),
                // TODO: Replace with finished status tracking.
                true,
                sectionMapper.toSummaryDTO(lesson.getSection()),
                lesson.getTopicComposition()
                        .stream()
                        .map(LessonTopic::getTopic)
                        .map(topicMapper::toSummaryDTO)
                        .toList()
        );
    }

    public LessonSummaryDTO toSummaryDTO(Lesson lesson) {
        return new LessonSummaryDTO(lesson.getIdentifier(),
                lesson.getTitle(),
                lesson.getOrderIndex(),
                // TODO: Replace with finished status tracking.
                true
        );
    }
}
