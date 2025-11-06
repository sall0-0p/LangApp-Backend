package com.lordbucket.langlearn.model.vocabulary;

import com.lordbucket.langlearn.model.enums.Language;
import com.lordbucket.langlearn.model.enums.LexemeType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

/**
 * Represents the word/phrase itself, separate from its meaning.
 * e.g., "die Bank"
 */
@Data
@Entity
@Table(name = "lexemes")
public class Lexeme {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String expression;

    @Column(nullable = false)
    private LexemeType type;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Language language;

    @OneToMany(mappedBy = "lexeme", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Sense> meanings;
}
