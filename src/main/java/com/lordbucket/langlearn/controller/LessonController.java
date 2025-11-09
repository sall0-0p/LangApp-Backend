package com.lordbucket.langlearn.controller;

import com.lordbucket.langlearn.dto.model.LessonDTO;
import com.lordbucket.langlearn.dto.model.TaskDTO;
import com.lordbucket.langlearn.model.Lesson;
import com.lordbucket.langlearn.repository.LessonRepository;
import com.lordbucket.langlearn.service.curriculum.LessonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.config.Task;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/lessons")
public class LessonController {
    private final LessonService lessonService;
    private final LessonRepository lessonRepository;

    public LessonController(LessonService lessonService, LessonRepository lessonRepository) {
        this.lessonService = lessonService;
        this.lessonRepository = lessonRepository;
    }

    @GetMapping("/{identifier}")
    public ResponseEntity<LessonDTO> getLessonDetails(@PathVariable("identifier") String identifier) {
        LessonDTO lesson = lessonService.getLessonByIdentifier(identifier);

        if (lesson == null) {
            return ResponseEntity.notFound()
                    .build();
        }

        return ResponseEntity.ok(lesson);
    }

    @GetMapping("/{identifier}/tasks")
    public ResponseEntity<List<TaskDTO>> getLessonTasks(@PathVariable("identifier") String identifier) {
        // Obtain and validate lesson by identifier.
        Optional<Lesson> lesson = lessonRepository.findByIdentifier(identifier);
        if (lesson.isEmpty()) {
            return ResponseEntity
                    .notFound()
                    .build();
        }

        // Obtain and validate tasks.
        List<TaskDTO> tasks = lessonService.getTasksForLesson(lesson.get());
        log.info(tasks.toString());
        if (tasks.isEmpty()) {
            return ResponseEntity
                    .internalServerError()
                    .build();
        }

        return ResponseEntity.ok(tasks);
    }
}
