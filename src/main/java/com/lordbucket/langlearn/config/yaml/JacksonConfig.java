package com.lordbucket.langlearn.config.yaml;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class JacksonConfig {

    @Bean("jsonObjectMapper")
    public ObjectMapper jsonObjectMapper() {
        return new ObjectMapper();
    }

    @Primary
    @Bean("yamlObjectMapper")
    public ObjectMapper yamlObjectMapper() {
        // This returns a special version of ObjectMapper
        // that is configured to understand YAML.
        return new ObjectMapper(new YAMLFactory());
    }
}
