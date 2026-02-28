package com.example.demo.mapper;

import com.example.demo.entity.Ingredient;
import com.example.demo.entity.Recipe;
import com.example.demo.models.IngredientRequest;
import com.example.demo.models.IngredientUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IngredientMapperTest {

    private IngredientMapper ingredientMapper;

    @BeforeEach
    void setUp() {
        ingredientMapper = new IngredientMapperImpl();
    }

    @Test
    void toEntity_shouldMapIngredientRequestToIngredient() {
        IngredientRequest ingredientRequest = new IngredientRequest().name("Egg");
        Ingredient ingredient = ingredientMapper.toEntity(ingredientRequest);

        assertEquals("Egg", ingredient.getName());
        assertNull(ingredient.getId());
    }

    @Test
    void toEntity_shouldMapIngredientUpdateRequestToIngredient() {
        IngredientUpdateRequest ingredientUpdateRequest = new IngredientUpdateRequest().name("Egg");
        Ingredient ingredient = ingredientMapper.toEntity(ingredientUpdateRequest);

        assertEquals("Egg", ingredient.getName());
        assertNull(ingredient.getId());
    }

    @Test
    void updateFromRequest_shouldUpdateFields() {
        IngredientUpdateRequest ingredientUpdateRequest = new IngredientUpdateRequest().name("Cabbage").quantity(1.0);
        Ingredient ingredient = Ingredient.builder().id(10L).name("Egg").quantity(2.0).recipe(new Recipe()).build();

        ingredientMapper.updateFromRequest(ingredientUpdateRequest, ingredient);

        assertEquals("Cabbage", ingredient.getName());
        assertEquals(1.0, ingredient.getQuantity());
        assertEquals(10L, ingredient.getId());
        assertNotNull(ingredient.getRecipe());
    }
}
