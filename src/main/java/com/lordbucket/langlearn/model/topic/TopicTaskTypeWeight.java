package com.lordbucket.langlearn.model.topic;

import com.lordbucket.langlearn.model.enums.TaskType;
import jakarta.persistence.*;
import lombok.Data;

/**
 * A "Learning Strategy" rule.
 * e.g., "For the 'Food' topic, make 80% of tasks VOCABULARY."
 */
@Data
@Entity
@Table(name = "topic_task_type_weights")
public class TopicTaskTypeWeight {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id")
    private Topic topic;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskType taskType;

    @Column(nullable = false)
    private double weight; // e.g., 0.8
}
