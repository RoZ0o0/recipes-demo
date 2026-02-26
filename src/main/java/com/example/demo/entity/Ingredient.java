package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @NotNull
    @NotBlank
    @Size(max = 60)
    @Column(nullable = false)
    private String name;

    @NotNull
    @Column(nullable = false)
    private Double quantity;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false, columnDefinition = "unit")
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private Unit unit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    public enum Unit {
        G, KG, ML, CUP, TBSP, TSP, PIECE
    }
}
