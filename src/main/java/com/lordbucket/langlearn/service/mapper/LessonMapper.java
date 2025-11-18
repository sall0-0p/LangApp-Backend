package com.lordbucket.langlearn.service.mapper;

import com.lordbucket.langlearn.dto.model.LessonDTO;
import com.lordbucket.langlearn.dto.model.LessonSummaryDTO;
import com.lordbucket.langlearn.model.Lesson;
import com.lordbucket.langlearn.model.User;
import com.lordbucket.langlearn.model.topic.LessonTopic;
import com.lordbucket.langlearn.service.curriculum.LessonCompletionService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class LessonMapper {
    private final TopicMapper topicMapper;
    private final SectionMapper sectionMapper;
    private final LessonCompletionService lessonCompletionService;

    public LessonMapper(TopicMapper topicMapper, @Lazy SectionMapper sectionMapper, LessonCompletionService lessonCompletionService) {
        this.topicMapper = topicMapper;
        this.sectionMapper = sectionMapper;
        this.lessonCompletionService = lessonCompletionService;
    }

    public LessonDTO toDTO(Lesson lesson, User user) {
        return new LessonDTO(lesson.getIdentifier(),
                lesson.getTitle(),
                lesson.getSubtitle(),
                lesson.getOrderIndex(),
                // TODO: Replace with finished status tracking.
                lessonCompletionService.isLessonCompleted(user, lesson),
                sectionMapper.toSummaryDTO(lesson.getSection(), user),
                lesson.getTopicComposition()
                        .stream()
                        .map(LessonTopic::getTopic)
                        .map(topicMapper::toSummaryDTO)
                        .toList()
        );
    }

    public LessonSummaryDTO toSummaryDTO(Lesson lesson, User user) {
        return new LessonSummaryDTO(lesson.getIdentifier(),
                lesson.getTitle(),
                lesson.getSubtitle(),
                lesson.getOrderIndex(),
                // TODO: Replace with finished status tracking.
                lessonCompletionService.isLessonCompleted(user, lesson)
        );
    }
}
