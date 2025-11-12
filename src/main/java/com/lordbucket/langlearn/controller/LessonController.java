package com.lordbucket.langlearn.controller;

import com.lordbucket.langlearn.dto.model.LessonDTO;
import com.lordbucket.langlearn.dto.model.TaskDTO;
import com.lordbucket.langlearn.model.Lesson;
import com.lordbucket.langlearn.model.User;
import com.lordbucket.langlearn.repository.LessonRepository;
import com.lordbucket.langlearn.service.curriculum.LessonCompletionService;
import com.lordbucket.langlearn.service.curriculum.LessonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.config.Task;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/lessons")
public class LessonController {
    private final LessonService lessonService;
    private final LessonRepository lessonRepository;
    private final LessonCompletionService lessonCompletionService;

    public LessonController(LessonService lessonService, LessonRepository lessonRepository, LessonCompletionService lessonCompletionService) {
        this.lessonService = lessonService;
        this.lessonRepository = lessonRepository;
        this.lessonCompletionService = lessonCompletionService;
    }

    @GetMapping("/{identifier}")
    public ResponseEntity<LessonDTO> getLessonDetails(@PathVariable("identifier") String identifier, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        LessonDTO lesson = lessonService.getLessonByIdentifier(identifier, user);

        return ResponseEntity.ok(lesson);
    }

    @GetMapping("/{identifier}/tasks")
    public ResponseEntity<List<TaskDTO>> getLessonTasks(@PathVariable("identifier") String identifier) {
        // Obtain and validate tasks.
        List<TaskDTO> tasks = lessonService.getTasksForLesson(identifier);
        log.info(tasks.toString());
        if (tasks.isEmpty()) {
            return ResponseEntity
                    .internalServerError()
                    .build();
        }

        return ResponseEntity.ok(tasks);
    }

    @PostMapping("/{identifier}/complete")
    public ResponseEntity<?> completeLesson(@PathVariable("identifier") String identifier, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        boolean saved = lessonCompletionService.completeLesson(user, identifier);

        if (saved) {
            return ResponseEntity.ok("Successfully saved it!");
        } else {
            return ResponseEntity.ok("Failed to save, already completed lesson!");
        }
    }
}
