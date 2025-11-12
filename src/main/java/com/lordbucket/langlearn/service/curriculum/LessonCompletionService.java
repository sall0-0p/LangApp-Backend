package com.lordbucket.langlearn.service.curriculum;

import com.lordbucket.langlearn.model.*;
import com.lordbucket.langlearn.repository.LessonRepository;
import com.lordbucket.langlearn.repository.CompletionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LessonCompletionService {
    private final CompletionRepository completionRepository;
    private final LessonRepository lessonRepository;

    public LessonCompletionService(CompletionRepository completionRepository, LessonRepository lessonRepository) {
        this.completionRepository = completionRepository;
        this.lessonRepository = lessonRepository;
    }

    public boolean isSectionCompleted(User user, Section section) {
        int sectionSize = section.getLessons().size();
        int completedSize = completionRepository.countCompletedLessonsForUser(user, section.getLessons());

        return sectionSize == completedSize;
    }

    public boolean isLessonCompleted(User user, Lesson lesson) {
        return completionRepository.existsByUserAndLesson(user, lesson);
    }

    public boolean isEnrolledInCourse(User user, Course course) {
        return completionRepository.hasUserCompletedLessonInCourse(user, course);
    }

    @Transactional
    public boolean completeLesson(User user, String lessonIdentifier) {
        Lesson lesson = lessonRepository.findByIdentifier(lessonIdentifier).orElseThrow();
        if (completionRepository.existsByUserAndLesson(user, lesson)) {
            return false;
        }

        UserLessonCompletion userLessonCompletion = new UserLessonCompletion();
        userLessonCompletion.setLesson(lesson);
        userLessonCompletion.setUser(user);
        userLessonCompletion.setCompletedAt(LocalDateTime.now());

        completionRepository.save(userLessonCompletion);

        return true;
    }
}
