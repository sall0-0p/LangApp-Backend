package com.lordbucket.langlearn.model;

import com.lordbucket.langlearn.task.TaskType;
import jakarta.persistence.*;
import lombok.Data;

/**
 * One AIGenerated task, contains its id, lesson, data and type
 */
@Data
@Entity
@Table(name = "tasks")
public class GeneratedTask {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TaskType taskType;

    @Column(nullable = false)
    private String taskData;
}
