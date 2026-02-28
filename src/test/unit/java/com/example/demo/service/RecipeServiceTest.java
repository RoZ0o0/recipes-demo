package com.example.demo.service;

import com.example.demo.entity.Ingredient;
import com.example.demo.entity.Recipe;
import com.example.demo.exception.IngredientNotInRecipeException;
import com.example.demo.exception.RecipeNotFoundException;
import com.example.demo.mapper.IngredientMapper;
import com.example.demo.mapper.RecipeMapper;
import com.example.demo.models.*;
import com.example.demo.repository.RecipeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipeServiceTest {

    @InjectMocks
    private RecipeService recipeService;

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private RecipeMapper recipeMapper;

    @Mock
    private IngredientMapper ingredientMapper;

    @Test
    void createRecipe_shouldSaveRecipeWithIngredientsAndReturnResponse() {
        RecipeRequest recipeRequest = new RecipeRequest();
        recipeRequest.setIngredients(List.of(new IngredientRequest()));

        Recipe mappedRecipe = new Recipe();
        Recipe savedRecipe = new Recipe();
        savedRecipe.setId(1L);
        Ingredient ingredient = new Ingredient();
        ingredient.setRecipe(savedRecipe);

        savedRecipe.setIngredients(List.of(ingredient));

        when(recipeMapper.toEntity(recipeRequest)).thenReturn(mappedRecipe);
        when(recipeRepository.save(any(Recipe.class))).thenReturn(savedRecipe);
        when(recipeMapper.toResponse(savedRecipe)).thenReturn(new RecipeResponse().id(1L));

        RecipeResponse response = recipeService.createRecipe(recipeRequest);

        assertEquals(1L, response.getId());
        assertEquals(1, savedRecipe.getIngredients().size());
        assertSame(savedRecipe, savedRecipe.getIngredients().getFirst().getRecipe());
    }

    @Test
    void deleteRecipe_shouldThrowException_whenRecipeDoesNotExist() {
        Long recipeId = 1L;
        when(recipeRepository.existsById(recipeId)).thenReturn(false);

        assertThrows(RecipeNotFoundException.class, () -> recipeService.deleteRecipeById(recipeId));
    }

    @Test
    void deleteRecipe_shouldDeleteRecipe_whenRecipeExists() {
        Long recipeId = 1L;
        when(recipeRepository.existsById(recipeId)).thenReturn(true);

        recipeService.deleteRecipeById(recipeId);

        verify(recipeRepository, times(1)).deleteById(recipeId);
    }

    @Test
    void getRecipe_shouldThrowException_whenRecipeDoesNotExist() {
        Long recipeId = 1L;
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.empty());

        assertThrows(RecipeNotFoundException.class, () -> recipeService.getRecipe(recipeId));
    }

    @Test
    void getRecipe_shouldReturnRecipe_whenRecipeExists() {
        Long recipeId = 1L;
        Recipe recipe = Recipe.builder().id(recipeId).build();

        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
        when(recipeMapper.toResponse(recipe)).thenReturn(new RecipeResponse().id(recipeId));

        RecipeResponse response = recipeService.getRecipe(recipeId);

        assertNotNull(response);
        assertEquals(recipeId, response.getId());
    }

    @Test
    void searchRecipes_shouldReturnPaginatedResponse_whenSearchIsNull() {
        Pageable pageable = PageRequest.of(0, 2);
        Recipe recipe1 = Recipe.builder().id(1L).build();
        Recipe recipe2 = Recipe.builder().id(2L).build();

        Page<Recipe> recipePage = new PageImpl<>(List.of(recipe1, recipe2), pageable, 2);
        when(recipeRepository.findAll(any(Pageable.class))).thenReturn(recipePage);
        when(recipeMapper.toResponse(recipePage)).thenReturn(new PaginatedRecipeResponse().totalElements(2));

        PaginatedRecipeResponse response = recipeService.searchRecipes(0, 2, null, null, null);

        assertNotNull(response);
        assertEquals(2, response.getTotalElements());
    }

    @Test
    void searchRecipes_shouldReturnFilteredPaginatedResponse() {
        Pageable pageable = PageRequest.of(0, 2);
        Recipe recipe1 = Recipe.builder().id(1L).build();
        Recipe recipe2 = Recipe.builder().id(2L).build();

        Page<Recipe> recipePage = new PageImpl<>(List.of(recipe1, recipe2), pageable, 2);
        when(recipeRepository.findAll(ArgumentMatchers.<Specification<Recipe>>any(), any(Pageable.class))).thenReturn(recipePage);
        when(recipeMapper.toResponse(recipePage)).thenReturn(new PaginatedRecipeResponse().totalElements(2));

        PaginatedRecipeResponse response = recipeService.searchRecipes(0, 2, "test", null, null);

        assertNotNull(response);
        assertEquals(2, response.getTotalElements());
    }

    @Test
    void searchRecipes_shouldSortDescending_whenDirectionDesc() {
        Page<Recipe> recipePage = new PageImpl<>(List.of());

        when(recipeRepository.findAll(any(Pageable.class))).thenReturn(recipePage);
        when(recipeMapper.toResponse(recipePage)).thenReturn(new PaginatedRecipeResponse());

        recipeService.searchRecipes(0, 2, null, "name", "desc");

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(recipeRepository).findAll(captor.capture());

        Pageable usedPageable = captor.getValue();

        Sort.Order order = usedPageable.getSort().getOrderFor("name");

        assertNotNull(order);
        assertEquals(Sort.Direction.DESC, order.getDirection());
    }

    @Test
    void searchRecipes_shouldUseDefaultSortField_whenSortByNull() {
        Page<Recipe> recipePage = new PageImpl<>(List.of());

        when(recipeRepository.findAll(any(Pageable.class))).thenReturn(recipePage);
        when(recipeMapper.toResponse(recipePage)).thenReturn(new PaginatedRecipeResponse());

        recipeService.searchRecipes(0, 10, null, null, "asc");

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(recipeRepository).findAll(captor.capture());

        Pageable pageableUsed = captor.getValue();
        Sort.Order order = pageableUsed.getSort().getOrderFor("name");

        assertNotNull(order);
        assertEquals(Sort.Direction.ASC, order.getDirection());
        assertEquals(0, pageableUsed.getPageNumber());
        assertEquals(10, pageableUsed.getPageSize());
    }

    @Test
    void updateRecipeById_shouldThrowException_whenRecipeDoesNotExist() {
        Long recipeId = 1L;

        RecipeUpdateRequest recipeUpdateRequest = new RecipeUpdateRequest();

        when(recipeRepository.findById(recipeId)).thenReturn(Optional.empty());

        assertThrows(RecipeNotFoundException.class, () -> recipeService.updateRecipeById(recipeId, recipeUpdateRequest));
    }

    @Test
    void updateRecipeById_shouldThrowException_whenIngredientIdNotFound() {
        Long recipeId = 1L;
        RecipeUpdateRequest recipeUpdateRequest = new RecipeUpdateRequest();
        recipeUpdateRequest.setIngredients(List.of(new IngredientUpdateRequest().id(999L)));

        Recipe recipe = new Recipe();
        recipe.setId(recipeId);
        recipe.setIngredients(List.of(Ingredient.builder().id(1L).build()));

        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));

        assertThrows(IngredientNotInRecipeException.class,
                () -> recipeService.updateRecipeById(recipeId, recipeUpdateRequest));
    }

    @Test
    void updateRecipeById_shouldUpdateRecipeAndIngredients() {
        Long recipeId = 1L;

        RecipeUpdateRequest recipeUpdateRequest = new RecipeUpdateRequest();
        IngredientUpdateRequest existingIngredientRequest = new IngredientUpdateRequest().id(10L);
        IngredientUpdateRequest newIngredientUpdateRequest = new IngredientUpdateRequest();
        recipeUpdateRequest.setIngredients(List.of(existingIngredientRequest, newIngredientUpdateRequest));

        Recipe recipe = new Recipe();
        recipe.setId(recipeId);
        Ingredient existingIngredient = Ingredient.builder().id(10L).build();
        existingIngredient.setRecipe(recipe);
        recipe.setIngredients(new ArrayList<>(List.of(existingIngredient)));

        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
        doNothing().when(recipeMapper).updateFromRequest(recipeUpdateRequest, recipe);
        doNothing().when(ingredientMapper).updateFromRequest(existingIngredientRequest, existingIngredient);
        when(ingredientMapper.toEntity(newIngredientUpdateRequest)).thenReturn(new Ingredient());
        when(recipeRepository.save(any(Recipe.class))).thenReturn(recipe);
        when(recipeMapper.toResponse(recipe)).thenReturn(new RecipeResponse().id(recipeId));

        RecipeResponse recipeResponse = recipeService.updateRecipeById(recipeId, recipeUpdateRequest);

        assertEquals(recipeId, recipeResponse.getId());
        assertEquals(2, recipe.getIngredients().size());
        assertSame(recipe, recipe.getIngredients().get(0).getRecipe());
        assertSame(recipe, recipe.getIngredients().get(1).getRecipe());

        verify(recipeRepository).findById(recipeId);
        verify(recipeRepository).save(recipe);
    }
}
