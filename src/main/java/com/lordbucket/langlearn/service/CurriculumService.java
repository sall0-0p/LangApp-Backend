package com.lordbucket.langlearn.service;

import com.lordbucket.langlearn.config.yaml.CourseConfig;
import com.lordbucket.langlearn.config.yaml.LessonConfig;
import com.lordbucket.langlearn.config.yaml.LessonTopicConfig;
import com.lordbucket.langlearn.config.yaml.SectionConfig;
import com.lordbucket.langlearn.model.Course;
import com.lordbucket.langlearn.model.Lesson;
import com.lordbucket.langlearn.model.Section;
import com.lordbucket.langlearn.model.enums.Language;
import com.lordbucket.langlearn.model.topic.LessonTopic;
import com.lordbucket.langlearn.model.topic.Topic;
import com.lordbucket.langlearn.repository.*;
import com.lordbucket.langlearn.repository.topic.LessonTopicRepository;
import com.lordbucket.langlearn.repository.topic.TopicRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class CurriculumService {

    private final CourseRepository courseRepository;
    private final SectionRepository sectionRepository;
    private final LessonRepository lessonRepository;
    private final TopicRepository topicRepository;
    private final LessonTopicRepository lessonTopicRepository;

    public CurriculumService(CourseRepository courseRepository,
                             SectionRepository sectionRepository,
                             LessonRepository lessonRepository,
                             TopicRepository topicRepository,
                             LessonTopicRepository lessonTopicRepository) {
        this.courseRepository = courseRepository;
        this.sectionRepository = sectionRepository;
        this.lessonRepository = lessonRepository;
        this.topicRepository = topicRepository;
        this.lessonTopicRepository = lessonTopicRepository;
    }

    /**
     * Finds or creates a Course.
     */
    @Transactional
    public Course syncCourse(CourseConfig config) {
        Course course = courseRepository.findByIdentifier(config.getIdentifier())
                .orElseGet(() -> {
                    Course newCourse = new Course();
                    newCourse.setIdentifier(config.getIdentifier());
                    return newCourse;
                });

        course.setTitle(config.getTitle());
        course.setOriginLanguage(Language.valueOf(config.getOriginLanguage().toUpperCase()));
        course.setTargetLanguage(Language.valueOf(config.getTargetLanguage().toUpperCase()));
        course.setActive(true);

        return courseRepository.save(course);
    }

    /**
     * Finds or creates a Section, linking it to its parent Course.
     */
    @Transactional
    public Section syncSection(SectionConfig config, Course course) {
        Section section = sectionRepository.findByIdentifier(config.getSectionIdentifier())
                .orElseGet(() -> {
                    Section newSection = new Section();
                    newSection.setIdentifier(config.getSectionIdentifier());
                    return newSection;
                });

        section.setTitle(config.getSectionTitle());
        // We'll just use the number of lessons for order for now.
        section.setOrderIndex(config.getLessons() != null ? config.getLessons().size() : 0);
        section.setCourse(course);

        return sectionRepository.save(section);
    }

    /**
     * Finds or creates a Lesson, linking it to its Section.
     * Most importantly, it creates the LessonTopic join entities.
     */
    @Transactional
    public Lesson syncLesson(LessonConfig config, Section section) {
        Lesson lesson = lessonRepository.findByIdentifier(config.getIdentifier())
                .orElseGet(() -> {
                    Lesson newLesson = new Lesson();
                    newLesson.setIdentifier(config.getIdentifier());
                    return newLesson;
                });

        lesson.setTitle(config.getTitle());
        lesson.setOrderIndex(config.getOrder());
        lesson.setSection(section);
        lessonRepository.save(lesson); // Save first to get an ID

        // Clear old topic composition to ensure YAML is the source of truth
        lessonTopicRepository.deleteAllByLesson(lesson);

        // Create the new "playlist" links
        if (config.getTopicComposition() != null) {
            for (LessonTopicConfig topicConfig : config.getTopicComposition()) {
                // Find the Topic that was created by TopicService
                Topic topic = topicRepository.findByIdentifier(topicConfig.getTopicIdentifier())
                        .orElseThrow(() -> new RuntimeException("Seeder Error: Topic '" + topicConfig.getTopicIdentifier() + "' not found. Ensure it is defined in a topics/*.yml file."));

                // Create the join entity that stores the "rule"
                LessonTopic lessonTopic = new LessonTopic();
                lessonTopic.setLesson(lesson);
                lessonTopic.setTopic(topic);
                lessonTopic.setTaskCount(topicConfig.getTaskCount());
                lessonTopicRepository.save(lessonTopic);
            }
        }
        return lesson;
    }
}