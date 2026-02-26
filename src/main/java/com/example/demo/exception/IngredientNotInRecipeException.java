package com.example.demo.exception;

public class IngredientNotInRecipeException extends RuntimeException {
    public IngredientNotInRecipeException(Long ingredientId, Long recipeId) {
        super("Ingredient " + ingredientId + " does not belong to recipe " + recipeId);
    }
}
