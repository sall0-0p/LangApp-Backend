package com.lordbucket.langlearn.repository.topic;

import com.lordbucket.langlearn.model.Lesson;
import com.lordbucket.langlearn.model.topic.LessonTopic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LessonTopicRepository extends JpaRepository<LessonTopic, Long> {
    void deleteAllByLesson(Lesson lesson);
}
