package com.lordbucket.langlearn.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "user_lesson_completions",
uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "lesson_id"}),
})
public class UserLessonCompletion {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime completedAt;
}
