package com.lordbucket.langlearn.model;

import com.lordbucket.langlearn.misc.Language;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "courses")
public class Course {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true, nullable = false)
    private String identifier;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private boolean active = false;

    @Enumerated(EnumType.STRING)
    private Language originLanguage;

    @Enumerated(EnumType.STRING)
    private Language targetLanguage;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Section> sections;
}
