package com.lordbucket.langlearn.model.topic;


import com.lordbucket.langlearn.model.Lesson;
import jakarta.persistence.*;
import lombok.Data;

/**
 * The "Join Entity" that links a Lesson to a Topic.
 * This is the "playlist item" and stores the rules for this lesson.
 * e.g., "For the 'At the Restaurant' lesson, include 5 tasks from the 'Food' topic."
 */
@Data
@Entity
@Table(name = "lesson_topics")
public class LessonTopic {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id")
    private Topic topic;

    @Column(nullable = false)
    private int taskCount;
}