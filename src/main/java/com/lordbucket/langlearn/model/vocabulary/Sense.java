package com.lordbucket.langlearn.model.vocabulary;

import com.lordbucket.langlearn.dto.model.TaskOption;
import com.lordbucket.langlearn.model.enums.Language;
import com.lordbucket.langlearn.model.topic.Topic;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Set;

/**
 * Represents a single *meaning* of a Lexeme. This is the
 * core "vocabulary item" you will teach.
 * e.g., "the bank (finance)" for the Lexeme "die Bank"
 */
@Data
@Entity
@Table(name = "senses")
public class Sense {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String originTranslation;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Language originLanguage;

    @Column
    private String emoji;

    // The word this meaning belongs to
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lexeme_id", nullable = false)
    private Lexeme lexeme;

    // The topics this meaning appears in
    @ManyToMany(mappedBy = "senses")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Topic> topics;

    public TaskOption toTaskOption() {
        return new TaskOption(this.id,
                this.lexeme.getExpression(),
                this.originTranslation,
                this.emoji);
    }
}