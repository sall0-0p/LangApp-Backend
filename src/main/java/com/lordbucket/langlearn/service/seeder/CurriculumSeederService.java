package com.lordbucket.langlearn.service.seeder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lordbucket.langlearn.config.yaml.CourseConfig;
import com.lordbucket.langlearn.config.yaml.LessonConfig;
import com.lordbucket.langlearn.config.yaml.SectionConfig;
import com.lordbucket.langlearn.model.Course;
import com.lordbucket.langlearn.model.Section;
import com.lordbucket.langlearn.service.curriculum.CurriculumService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;

@Service
@Order(3) // <-- Runs LAST
@Slf4j
public class CurriculumSeederService implements CommandLineRunner {

    private final ObjectMapper yamlObjectMapper;
    private final CurriculumService curriculumService;
    private final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

    public CurriculumSeederService(@Qualifier("yamlObjectMapper") ObjectMapper yamlObjectMapper,
                                   CurriculumService curriculumService) {
        this.yamlObjectMapper = yamlObjectMapper;
        this.curriculumService = curriculumService;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("--- 🎵 Starting Curriculum Linker (Order 3) ---");

        Resource masterConfigResource = resolver.getResource("classpath:courses/courses.yml");
        if (!masterConfigResource.exists()) {
            log.warn("Master 'courses.yml' not found. Skipping curriculum linking.");
            return;
        }

        List<CourseConfig> courseConfigs;
        try (InputStream is = masterConfigResource.getInputStream()) {
            courseConfigs = yamlObjectMapper.readValue(is, new TypeReference<>() {});
        } catch (Exception e) {
            log.error("Failed to parse master 'courses.yml': {}", e.getMessage());
            return;
        }

        for (CourseConfig courseConfig : courseConfigs) {
            try {
                Course course = curriculumService.syncCourse(courseConfig);
                log.info("  -> Synced Course: {}", course.getTitle());
                int sectionOrder = 1;

                for (String sectionFileName : courseConfig.getSectionFiles()) {
                    Resource sectionResource = resolver.getResource("classpath:courses/sections/" + sectionFileName);
                    if (!sectionResource.exists()) {
                        log.warn("    -> Section file not found: {}", sectionFileName);
                        continue;
                    }

                    try (InputStream is = sectionResource.getInputStream()) {
                        SectionConfig sectionConfig = yamlObjectMapper.readValue(is, SectionConfig.class);

                        Section section = curriculumService.syncSection(sectionConfig, course, sectionOrder);
                        log.info("    -> Synced Section: {}", section.getTitle());

                        sectionOrder++;

                        for (LessonConfig lessonConfig : sectionConfig.getLessons()) {
                            curriculumService.syncLesson(lessonConfig, section);
                            log.info("      -> Synced Lesson: {}", lessonConfig.getTitle());
                        }
                    } catch (Exception e) {
                        log.error("      -> FAILED to sync section file {}: {}", sectionFileName, e.getMessage());
                    }
                }
            } catch (Exception e) {
                log.error("  -> FAILED to sync course {}: {}", courseConfig.getIdentifier(), e.getMessage());
            }
        }
        log.info("--- 🎵 Curriculum Linker Finished ---");
    }
}