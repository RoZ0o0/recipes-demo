package com.example.demo.service;

import com.example.demo.entity.Ingredient;
import com.example.demo.entity.Recipe;
import com.example.demo.exception.IngredientNotInRecipeException;
import com.example.demo.exception.RecipeNotFoundException;
import com.example.demo.mapper.IngredientMapper;
import com.example.demo.mapper.RecipeMapper;
import com.example.demo.models.*;
import com.example.demo.repository.RecipeRepository;
import com.example.demo.specification.RecipeSpecifications;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class RecipeService {
    private final RecipeRepository recipeRepository;
    private final RecipeMapper recipeMapper;
    private final IngredientMapper ingredientMapper;

    @Transactional
    public RecipeResponse createRecipe(RecipeRequest recipeRequest) {
        Recipe recipe = recipeMapper.toEntity(recipeRequest);

        Recipe saved = recipeRepository.save(recipe);

        return recipeMapper.toResponse(saved);
    }

    public void deleteRecipeById(Long recipeId) {
        if (!recipeRepository.existsById(recipeId)) {
            throw new RecipeNotFoundException(recipeId);
        }

        recipeRepository.deleteById(recipeId);
    }

    public RecipeResponse getRecipe(Long recipeId) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RecipeNotFoundException(recipeId));
        return recipeMapper.toResponse(recipe);
    }

    public PaginatedRecipeResponse searchRecipes(
            Integer page,
            Integer size,
            @Nullable String search,
            @Nullable String sortBy,
            String direction
    ) {
        Sort sort = Sort.by(
            direction != null && direction.equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC,
            sortBy != null ? sortBy : "name"
        );

        Pageable pageable = PageRequest.of(page, size, sort);

        if(search == null || search.trim().isEmpty()) {
            Page<Recipe> recipePage = recipeRepository.findAll(pageable);
            return recipeMapper.toResponse(recipePage);
        }

        Page<Recipe> recipePage = recipeRepository.findAll(
                RecipeSpecifications.searchRecipe(search), pageable
        );

        return recipeMapper.toResponse(recipePage);
    }

    @Transactional
    public RecipeResponse updateRecipeById(Long recipeId, RecipeUpdateRequest recipeUpdateRequest) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RecipeNotFoundException(recipeId));

        recipeMapper.updateFromRequest(recipeUpdateRequest, recipe);

        mergeIngredients(recipe, recipeUpdateRequest.getIngredients());

        return recipeMapper.toResponse(recipeRepository.save(recipe));
    }

    private void mergeIngredients(Recipe recipe, List<IngredientUpdateRequest> requests) {

        Map<Long, Ingredient> existingById = recipe.getIngredients().stream()
                .filter(i -> i.getId() != null)
                .collect(Collectors.toMap(Ingredient::getId, Function.identity()));

        List<Ingredient> result = new ArrayList<>();

        for (IngredientUpdateRequest req : requests) {

            if (req.getId() != null) {
                Ingredient existing = existingById.get(req.getId());

                if (existing == null) {
                    throw new IngredientNotInRecipeException(req.getId(), recipe.getId());
                }

                ingredientMapper.updateFromRequest(req, existing);
                result.add(existing);

            } else {
                Ingredient newIngredient = ingredientMapper.toEntity(req);
                newIngredient.setRecipe(recipe);
                result.add(newIngredient);
            }
        }

        recipe.getIngredients().clear();
        recipe.getIngredients().addAll(result);
    }
}
