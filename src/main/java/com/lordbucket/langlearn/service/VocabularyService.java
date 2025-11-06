package com.lordbucket.langlearn.service;

import com.lordbucket.langlearn.config.yaml.VocabularyEntryConfig;
import com.lordbucket.langlearn.model.enums.Language;
import com.lordbucket.langlearn.model.vocabulary.Lexeme;
import com.lordbucket.langlearn.model.enums.LexemeType;
import com.lordbucket.langlearn.model.vocabulary.Sense;
import com.lordbucket.langlearn.repository.vocabulary.LexemeRepository;
import com.lordbucket.langlearn.repository.vocabulary.SenseRepository;
import com.lordbucket.langlearn.service.ai.AiEmojiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Used to manage Lexemes and Senses.
 */
@Slf4j
@Service
public class VocabularyService {
    private final LexemeRepository lexemeRepository;
    private final SenseRepository senseRepository;
    private final AiEmojiService aiEmojiService;

    public VocabularyService(LexemeRepository lexemeRepository, SenseRepository senseRepository, AiEmojiService aiEmojiService) {
        this.lexemeRepository = lexemeRepository;
        this.senseRepository = senseRepository;
        this.aiEmojiService = aiEmojiService;
    }

    public Lexeme createLexeme(String expression, Language language, LexemeType type) {
        if (lexemeRepository.findByExpressionAndLanguage(expression, language).isPresent()) {
            throw new IllegalStateException("Lexeme already exists!");
        }

        Lexeme lexeme = new Lexeme();
        lexeme.setExpression(expression);
        lexeme.setType(type);
        lexeme.setLanguage(language);
        return lexemeRepository.save(lexeme);
    }

    /**
     * Register new Sense, creates new lexeme if no corresponding lexeme is found.
     * @param vocabEntry entry of vocabulary to be created
     * @param targetLanguage target language that is learned
     * @return sense created that was created
     */
    public Sense registerSense(VocabularyEntryConfig vocabEntry, Language targetLanguage, Language originLanguage) {
        if (senseRepository.findByLexemeExpressionAndOriginLanguage(vocabEntry.getTarget(), originLanguage).isPresent()) {
            log.warn("Attempted to register sense %s (%s) that already exists, aborting.".formatted(vocabEntry.getTarget(), vocabEntry.getOrigin()));
            return null;
        }

        // Query our lexeme
        Optional<Lexeme> lexemeCandidate = lexemeRepository.findByExpressionAndLanguage(vocabEntry.getTarget(), targetLanguage);

        // Create lexeme if does not exist
        Lexeme lexeme;
        if (lexemeCandidate.isEmpty()) {
            LexemeType lexemeType;
            try {
                lexemeType = LexemeType.valueOf(vocabEntry.getType());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid vocabulary type value of %s!".formatted(vocabEntry.getType()));
            }

            lexeme = this.createLexeme(vocabEntry.getTarget(), targetLanguage, lexemeType);
        } else {
            lexeme = lexemeCandidate.get();
        }

        // Create sense
        Sense sense = new Sense();
        sense.setLexeme(lexeme);
        sense.setOriginLanguage(originLanguage);
        sense.setOriginTranslation(vocabEntry.getOrigin());
        sense.setEmoji(aiEmojiService.generateEmojiForString(vocabEntry.getOrigin()));
        return senseRepository.save(sense);
    }
}
