package com.example.demo.controller;

import com.example.demo.api.RecipeApi;
import com.example.demo.models.PaginatedRecipeResponse;
import com.example.demo.models.RecipeRequest;
import com.example.demo.models.RecipeResponse;
import com.example.demo.models.RecipeUpdateRequest;
import com.example.demo.service.RecipeService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RecipeController implements RecipeApi {
    private final RecipeService recipeService;

    @Override
    public ResponseEntity<RecipeResponse> createRecipe(RecipeRequest recipeRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(recipeService.createRecipe(recipeRequest));
    }

    @Override
    public ResponseEntity<Void> deleteRecipe(Long recipeId) {
        recipeService.deleteRecipeById(recipeId);

        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<RecipeResponse> getRecipe(Long recipeId) {
        return ResponseEntity.ok(recipeService.getRecipe(recipeId));
    }

    @Override
    public ResponseEntity<PaginatedRecipeResponse> searchRecipes(Integer page, Integer size, @Nullable String search, @Nullable String sortBy, String direction) {
        return ResponseEntity.ok(recipeService.searchRecipes(page, size, search, sortBy, direction));
    }

    @Override
    public ResponseEntity<RecipeResponse> updateRecipe(Long recipeId, RecipeUpdateRequest recipeUpdateRequest) {
        return ResponseEntity.ok(recipeService.updateRecipeById(recipeId, recipeUpdateRequest));
    }
}
