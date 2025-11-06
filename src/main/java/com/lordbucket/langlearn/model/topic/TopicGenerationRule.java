package com.lordbucket.langlearn.model.topic;

import com.lordbucket.langlearn.model.enums.TaskType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * A "Factory Seeder" rule.
 * e.g., "For the 'Food' topic, generate 20 TRANSLATE_TO_TARGET_MCQ tasks."
 */
@Data
@Entity
@Table(name = "topic_generation_rules")
public class TopicGenerationRule {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Topic topic;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskType taskType;

    @Column(nullable = false)
    private int count;
}