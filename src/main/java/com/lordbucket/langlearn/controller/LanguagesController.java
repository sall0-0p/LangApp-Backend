package com.lordbucket.langlearn.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/languages")
public class LanguagesController {
    @GetMapping
    public ResponseEntity<?> getTeapot() {
        return ResponseEntity.status(418).body("I am a teapot, bro!");
    }
}
