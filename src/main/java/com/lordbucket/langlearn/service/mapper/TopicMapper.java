package com.lordbucket.langlearn.service.mapper;

import com.lordbucket.langlearn.dto.model.TopicSummaryDTO;
import com.lordbucket.langlearn.model.topic.Topic;
import org.springframework.stereotype.Component;

@Component
public class TopicMapper {
    public TopicSummaryDTO toSummaryDTO(Topic topic) {
        return new TopicSummaryDTO(topic.getIdentifier(), topic.getTitle(), topic.getLevel());
    }
}
