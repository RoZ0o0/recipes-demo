package com.example.demo.specification;

import com.example.demo.entity.Ingredient;
import com.example.demo.entity.Recipe;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public class RecipeSpecifications {
    private RecipeSpecifications () {}

    public static Specification<Recipe> searchRecipe(String search) {
        return (r, q, cb) -> {
            String like = "%" + search.toLowerCase() + "%";

            Join<Recipe, Ingredient> ingredientJoin = r.join("ingredients");

            return cb.or(
                    cb.like(cb.lower(r.get("name")), like),
                    cb.like(
                        cb.lower(
                            cb.function("LOWER", String.class,
                                    cb.function("TEXT", String.class, r.get("difficulty"))
                            )
                        ),
                        like
                    ),
                    cb.like(cb.lower(ingredientJoin.get("name")), like)
            );
        };
    }
}
