package com.lordbucket.langlearn.model.task;

import com.lordbucket.langlearn.model.topic.Topic;
import com.lordbucket.langlearn.model.enums.TaskType;
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
    @JoinColumn(name = "topic_id")
    private Topic topic;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TaskType taskType;

    @Column(nullable = false, columnDefinition = "text")
    private String taskData;
}
