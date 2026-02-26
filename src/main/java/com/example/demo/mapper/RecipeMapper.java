package com.example.demo.mapper;

import com.example.demo.entity.Recipe;
import com.example.demo.models.PaginatedRecipeResponse;
import com.example.demo.models.RecipeRequest;
import com.example.demo.models.RecipeResponse;
import com.example.demo.models.RecipeUpdateRequest;
import org.mapstruct.*;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.data.domain.Page;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = IngredientMapper.class)
public interface RecipeMapper {

    RecipeResponse toResponse(Recipe recipe);

    PaginatedRecipeResponse toResponse(Page<Recipe> recipes);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Recipe toEntity(RecipeRequest recipeRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "ingredients", ignore = true)
    void updateFromRequest(RecipeUpdateRequest recipeUpdateRequest, @MappingTarget Recipe recipe);

    @AfterMapping
    default void linkIngredients(@MappingTarget Recipe recipe) {
        if (recipe.getIngredients() != null) {
            recipe.getIngredients().forEach(ingredient -> ingredient.setRecipe(recipe));
        }
    }

    default JsonNullable<Integer> map(Integer value) {
        return value == null ? JsonNullable.undefined() : JsonNullable.of(value);
    }

    default Integer map(JsonNullable<Integer> value) {
        return value != null && value.isPresent() ? value.get() : null;
    }
}
