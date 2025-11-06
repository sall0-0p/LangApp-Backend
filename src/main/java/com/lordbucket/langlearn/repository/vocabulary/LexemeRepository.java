package com.lordbucket.langlearn.repository.vocabulary;

import com.lordbucket.langlearn.model.enums.Language;
import com.lordbucket.langlearn.model.vocabulary.Lexeme;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LexemeRepository extends JpaRepository<Lexeme, Long> {
    Optional<Lexeme> findByExpressionAndLanguage(String expression, Language language);
}
