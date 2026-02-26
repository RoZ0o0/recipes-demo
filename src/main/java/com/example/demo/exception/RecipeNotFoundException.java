package com.example.demo.exception;

public class RecipeNotFoundException extends RuntimeException {
    public RecipeNotFoundException(Long recipeId) {
        super(String.format("Recipe with id '%s' does not exist.", recipeId));
    }
}
