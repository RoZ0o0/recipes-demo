package com.example.demo.mapper;

import com.example.demo.entity.Ingredient;
import com.example.demo.entity.Recipe;
import com.example.demo.models.PaginatedRecipeResponse;
import com.example.demo.models.RecipeRequest;
import com.example.demo.models.RecipeResponse;
import com.example.demo.models.RecipeUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RecipeMapperTest {

    private RecipeMapper recipeMapper;

    @BeforeEach
    void setUp() {
        recipeMapper = new RecipeMapperImpl();
    }

    @Test
    void toResponse_shouldMapRecipeToRecipeResponse() {
        Recipe recipe = Recipe.builder().id(1L).name("Salad").build();

        RecipeResponse recipeResponse = recipeMapper.toResponse(recipe);

        assertEquals(1L, recipeResponse.getId());
        assertEquals("Salad", recipeResponse.getName());
    }

    @Test
    void toResponsePage_shouldMapPagedRecipes() {
        Recipe recipe = Recipe.builder().id(1L).name("Soup").build();
        Page<Recipe> page = new PageImpl<>(List.of(recipe));

        PaginatedRecipeResponse paginatedRecipeResponse = recipeMapper.toResponse(page);

        assertEquals(1, paginatedRecipeResponse.getContent().size());
        assertEquals("Soup", paginatedRecipeResponse.getContent().getFirst().getName());
    }

    @Test
    void toEntity_shouldMapRecipeRequestToRecipe() {
        RecipeRequest recipeRequest = new RecipeRequest().name("Salad");
        Recipe recipe = recipeMapper.toEntity(recipeRequest);

        assertEquals("Salad", recipe.getName());
        assertNull(recipe.getId());
    }

    @Test
    void updateFromRequest_shouldUpdateFields() {
        RecipeUpdateRequest recipeUpdateRequest = new RecipeUpdateRequest().name("Salad").description("Chop");
        Recipe recipe = Recipe.builder().id(5L).name("Curry").description("Stir").build();

        recipeMapper.updateFromRequest(recipeUpdateRequest, recipe);

        assertEquals("Salad", recipe.getName());
        assertEquals("Chop", recipe.getDescription());
        assertEquals(5L, recipe.getId());
    }

    @Test
    void linkIngredients_shouldSetRecipeInEachIngredient() {
        Recipe recipe = new Recipe();
        Ingredient ingredient1 = new Ingredient();
        Ingredient ingredient2 = new Ingredient();
        recipe.setIngredients(new ArrayList<>(List.of(ingredient1, ingredient2)));

        recipeMapper.linkIngredients(recipe);

        assertSame(recipe, ingredient1.getRecipe());
        assertSame(recipe, ingredient2.getRecipe());
    }

    @Test
    void mapIntegerToJsonNullable_shouldReturnUndefinedForNull() {
        JsonNullable<Integer> result = recipeMapper.map((Integer) null);
        assertTrue(result.isUndefined());
    }

    @Test
    void mapIntegerToJsonNullable_shouldWrapNonNull() {
        JsonNullable<Integer> result = recipeMapper.map(42);
        assertTrue(result.isPresent());
        assertEquals(42, result.get());
    }

    @Test
    void mapJsonNullableToInteger_shouldReturnNullForNull() {
        Integer result = recipeMapper.map((JsonNullable<Integer>) null);
        assertNull(result);
    }

    @Test
    void mapJsonNullableToInteger_shouldReturnNullForUndefined() {
        Integer result = recipeMapper.map(JsonNullable.undefined());
        assertNull(result);
    }

    @Test
    void mapJsonNullableToInteger_shouldReturnValueIfPresent() {
        JsonNullable<Integer> value = JsonNullable.of(7);
        Integer result = recipeMapper.map(value);
        assertEquals(7, result);
    }
}
