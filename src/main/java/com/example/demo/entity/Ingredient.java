package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Ingredient name must not be empty")
    @Size(max = 60, message = "Ingredient name must be at most 60 characters")
    @Column(nullable = false)
    private String name;

    @NotNull
    @Positive(message = "Quantity must be at least 1")
    @Column(nullable = false)
    private Double quantity;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false, columnDefinition = "unit")
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private Unit unit;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    public enum Unit {
        G, KG, ML, CUP, TBSP, TSP, PIECE
    }
}
