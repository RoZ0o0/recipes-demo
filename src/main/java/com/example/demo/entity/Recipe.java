package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.time.OffsetDateTime;
import java.util.List;

@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Recipe name must not be empty")
    @Size(max = 255)
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Recipe description must not be empty")
    @Column(nullable = false)
    private String description;

    @NotNull
    @NotEmpty(message = "Recipe must have at least one ingredient")
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Ingredient> ingredients;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false, columnDefinition = "difficulty")
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private Difficulty difficulty;

    @Positive(message = "Preparation time must be at least 1 minute")
    private Integer preparationTime;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    public enum Difficulty {
        EASY, MEDIUM, HARD
    }
}
