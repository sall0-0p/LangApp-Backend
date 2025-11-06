package com.lordbucket.langlearn.service.ai;

import com.github.victools.jsonschema.generator.OptionPreset;
import com.github.victools.jsonschema.generator.SchemaGenerator;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder;
import com.github.victools.jsonschema.generator.SchemaVersion;
import com.github.victools.jsonschema.module.jackson.JacksonModule;
import com.github.victools.jsonschema.module.jackson.JacksonOption;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A service that generates and caches JSON schemas from Java POJOs/Records.
 * This is used to provide a "string schema" to the OpenAI API
 * to force its JSON output.
 */
@Service
@Slf4j
public class AiSchemaService {
    private final SchemaGenerator schemaGenerator;

    // A simple cache so we don't re-generate the schema for the same class
    private final Map<Class<?>, String> schemaCache = new ConcurrentHashMap<>();

    public AiSchemaService() {
        // This is a popular library for generating JSON schemas from Java classes
        JacksonModule module = new JacksonModule(
                JacksonOption.INCLUDE_ONLY_JSONPROPERTY_ANNOTATED_METHODS,
                JacksonOption.RESPECT_JSONPROPERTY_ORDER
        );
        SchemaGeneratorConfigBuilder configBuilder = new SchemaGeneratorConfigBuilder(
                SchemaVersion.DRAFT_2020_12, OptionPreset.PLAIN_JSON
        ).with(module);

        this.schemaGenerator = new SchemaGenerator(configBuilder.build());
    }

    /**
     * Gets the JSON schema for a given class.
     * Uses a cache for efficiency.
     */
    public String getSchema(Class<?> pojoClass) {
        // Check the cache first
        return schemaCache.computeIfAbsent(pojoClass, (key) -> {
            log.info("No schema in cache for {}. Generating...", pojoClass.getSimpleName());
            // If not in cache, generate it and store it
            return schemaGenerator.generateSchema(pojoClass).toString();
        });
    }
}