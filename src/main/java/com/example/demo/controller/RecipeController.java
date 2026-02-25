package com.example.demo.controller;

import com.example.demo.api.RecipeApi;
import com.example.demo.models.RecipeRequest;
import com.example.demo.models.RecipeResponse;
import com.example.demo.models.RecipeUpdateRequest;
import com.example.demo.service.RecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    public ResponseEntity<List<RecipeResponse>> getRecipes() {
        return ResponseEntity.ok(recipeService.getRecipes());
    }

    @Override
    public ResponseEntity<RecipeResponse> updateRecipe(Long recipeId, RecipeUpdateRequest recipeUpdateRequest) {
        return ResponseEntity.ok(recipeService.updateRecipeById(recipeId, recipeUpdateRequest));
    }
}
