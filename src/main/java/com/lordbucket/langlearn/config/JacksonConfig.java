package com.lordbucket.langlearn.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class JacksonConfig {

    @Primary
    @Bean("jsonObjectMapper")
    public ObjectMapper jsonObjectMapper() {
        return new ObjectMapper();
    }

    @Bean("yamlObjectMapper")
    public ObjectMapper yamlObjectMapper() {
        // This returns a special version of ObjectMapper
        // that is configured to understand YAML.
        return new ObjectMapper(new YAMLFactory());
    }
}
