package com.lordbucket.langlearn.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lordbucket.langlearn.config.yaml.*;
import com.lordbucket.langlearn.misc.Language;
import com.lordbucket.langlearn.misc.LanguageLevel;
import com.lordbucket.langlearn.task.TaskType;
import com.lordbucket.langlearn.model.Course;
import com.lordbucket.langlearn.model.Lesson;
import com.lordbucket.langlearn.model.Section;
import com.lordbucket.langlearn.repository.CourseRepository;
import com.lordbucket.langlearn.repository.LessonRepository;
import com.lordbucket.langlearn.repository.SectionRepository;
import com.lordbucket.langlearn.repository.GeneratedTaskRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * This service runs once on startup (as a CommandLineRunner).
 * It finds all .yml course files in 'resources/curriculum/', parses them,
 * and syncs the Course/Section/Lesson structure to the database.
 *
 * After syncing the "blueprints," it calls the AiGenerationService "factory"
 * to pre-generate and save the pool of tasks for each lesson.
 */
@Service
@Slf4j // Lombok annotation for logging
public class CourseSeedingService implements CommandLineRunner {
    private final ObjectMapper yamlObjectMapper;
    private final CourseRepository courseRepository;
    private final SectionRepository sectionRepository;
    private final LessonRepository lessonRepository;
    private final GeneratedTaskRepository generatedTaskRepository;
    private final AiGenerationService aiGenerationService;

    public CourseSeedingService(@Qualifier("yamlObjectMapper") ObjectMapper yamlObjectMapper,
                                   CourseRepository courseRepository,
                                   SectionRepository sectionRepository,
                                   LessonRepository lessonRepository,
                                   GeneratedTaskRepository generatedTaskRepository,
                                   AiGenerationService aiGenerationService) {
        this.yamlObjectMapper = yamlObjectMapper;
        this.courseRepository = courseRepository;
        this.sectionRepository = sectionRepository;
        this.lessonRepository = lessonRepository;
        this.generatedTaskRepository = generatedTaskRepository;
        this.aiGenerationService = aiGenerationService;
    }

    /**
     * This is the main method executed by Spring Boot on startup.
     */
    @Override
    @Transactional // Run the entire seeding process in one database transaction
    public void run(String... args) throws Exception {
        log.info("--- Starting Course Seeder ---");

        // 1. Find all course YAML files in resources/curriculum/
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath*:courses/*.y*ml");

        if (resources.length == 0) {
            log.warn("No course configuration files found in 'resources/courses/'. Skipping seeder.");
            return;
        }

        for (Resource resource : resources) {
            log.info("Processing course file: {}", resource.getFilename());
            try (InputStream is = resource.getInputStream()) {
                // 2. Parse the YAML into our Config POJO classes
                CourseConfig config = yamlObjectMapper.readValue(is, CourseConfig.class);

                // 3. Sync the "Blueprints" (Course, Section, Lesson) to the DB.
                // This method will recursively sync sections and lessons,
                // and trigger task generation.
                syncCourse(config);
            } catch (Exception e) {
                log.error("Failed to parse or seed course file {}: {}", resource.getFilename(), e.getMessage());
            }
        }

        log.info("--- Course Seeder Finished ---");
    }

    /**
     * Finds or creates a Course entity based on the YAML config.
     */
    private void syncCourse(CourseConfig config) {
        // Find or create the Course entity using its unique identifier
        Course course = courseRepository.findByIdentifier(config.getIdentifier())
                .orElse(new Course());

        // Update the entity with values from the YAML
        course.setIdentifier(config.getIdentifier());
        course.setTitle(config.getTitle());
        course.setOriginLanguage(Language.valueOf(config.getOriginLanguage()));
        course.setTargetLanguage(Language.valueOf(config.getTargetLanguage()));
        course.setActive(true); // Always set active when seeding
        courseRepository.save(course);
        log.info("  Synced Course: {}", course.getTitle());

        // Now, sync its children (Sections)
        for (SectionConfig sectionConfig : config.getSections()) {
            syncSection(course, sectionConfig);
        }
    }

    /**
     * Finds or creates a Section entity and links it to its parent Course.
     */
    private void syncSection(Course course, SectionConfig config) {
        // Find or create the Section entity.
        Section section = sectionRepository.findByCourseAndTitle(course, config.getTitle())
                .orElse(new Section());

        section.setTitle(config.getTitle());
        section.setOrderIndex(config.getOrder());
        section.setCourse(course);
        section.setLevel(LanguageLevel.valueOf(config.getLevel()));
        sectionRepository.save(section);
        log.info("    -> Synced Section: {}", section.getTitle());

        // Now, sync its children (Lessons)
        for (LessonConfig lessonConfig : config.getLessons()) {
            syncLesson(section, lessonConfig);
        }
    }

    /**
     * Finds or creates a Lesson entity and links it to its parent Section.
     * This is the final blueprint step that triggers the AI task generation.
     */
    private void syncLesson(Section section, LessonConfig config) {
        // Find or create the Lesson entity using its unique identifier
        Lesson lesson = lessonRepository.findByIdentifier(config.getIdentifier())
                .orElse(new Lesson());

        lesson.setIdentifier(config.getIdentifier());
        lesson.setTitle(config.getTitle());
        lesson.setOrderIndex(config.getOrder());
        lesson.setSection(section); // Link to the parent
        lessonRepository.save(lesson);
        log.info("      -> Synced Lesson: {}", lesson.getTitle());

        // 4. Run the "Factory" to generate tasks for this lesson
        generateTasksForLesson(lesson, config);
    }

    /**
     * This is the "Factory" part. It checks the generationPlan from the YAML
     * and calls the AiGenerationService to create the tasks.
     */
    private void generateTasksForLesson(Lesson lesson, LessonConfig config) {
        long existingTaskCount = generatedTaskRepository.countByLesson(lesson);
        if (existingTaskCount > 0) {
            log.info("      -> Skipping task generation for '{}'. {} tasks already exist.", lesson.getTitle(), existingTaskCount);
            return;
        }

        log.info("      -> Starting task generation for '{}'...", lesson.getTitle());
        Course course = lesson.getSection().getCourse();
        LessonDataConfig lessonData = config.getLessonData();

        if (lessonData == null) {
        log.warn("      -> No 'lessonData' found for '{}'. Cannot generate tasks.", lesson.getTitle());
        return;
    }

        // Loop through the "generationPlan" from the YAML
        for (TaskGenerationConfig taskConfig : config.getGenerationPlan()) {
        int taskCount = taskConfig.getCount();
        TaskType taskType = TaskType.valueOf(taskConfig.getTaskType());

        if (taskCount == 0) continue;

        log.info("        -> Generating {} tasks of type '{}'", taskCount, taskType);

        for (int i = 0; i < taskCount; i++) {
            try {
                aiGenerationService.generateAndSaveTask(
                        lesson,
                        taskType,
                        lessonData
                );

            } catch (Exception e) {
                log.error("        -> FAILED to generate task ({} of {}): {}", (i + 1), taskCount, e.getMessage());
            }
        }
    }
        log.info("      -> Finished task generation for '{}'.", lesson.getTitle());
    }
}