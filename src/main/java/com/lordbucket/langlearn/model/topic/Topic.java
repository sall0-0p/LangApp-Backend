package com.lordbucket.langlearn.model.topic;

import com.lordbucket.langlearn.model.enums.Language;
import com.lordbucket.langlearn.model.enums.LanguageLevel;
import com.lordbucket.langlearn.model.task.GeneratedTask;
import com.lordbucket.langlearn.model.vocabulary.Sense;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;
import java.util.Set;
/**
 * A "bucket" of content (e.g., "A1 Food Vocab") or concepts (e.g., "A1 Past Tense").
 * This is the central hub for linking content, rules, and tasks.
 */
@Data
@Entity
@Table(name = "topics")
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true, nullable = false)
    private String identifier; // e.g., "de-topic-food"

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Language language;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Language originLanguage;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private LanguageLevel level;

    // The "meanings" this topic teaches
    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(
            name = "topic_senses",
            joinColumns = @JoinColumn(name = "topic_id"),
            inverseJoinColumns = @JoinColumn(name = "sense_id")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Sense> senses;

    @ManyToMany
    @JoinTable(
            name = "topic_related_topics",
            joinColumns = @JoinColumn(name = "topic_id"),
            inverseJoinColumns = @JoinColumn(name = "related_topic_id")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Topic> relatedTopics;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TopicTaskTypeWeight> taskTypeWeights;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TopicGenerationRule> generationRules;

    // The lessons this topic appears in
    @OneToMany(mappedBy = "topic")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<LessonTopic> lessonUsages;

    // The pool of tasks generated for this topic
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GeneratedTask> generatedTasks;
}