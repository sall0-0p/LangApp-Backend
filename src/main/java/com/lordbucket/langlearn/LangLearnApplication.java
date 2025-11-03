package com.lordbucket.langlearn;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class LangLearnApplication {

    public static void main(String[] args) {
        SpringApplication.run(LangLearnApplication.class, args);
    }
}
