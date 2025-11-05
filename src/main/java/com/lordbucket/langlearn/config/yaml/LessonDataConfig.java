package com.lordbucket.langlearn.config.yaml;

import lombok.Data;

import java.util.List;

@Data
public class LessonDataConfig {
    private List<VocabularyEntryConfig> vocabulary;
}
