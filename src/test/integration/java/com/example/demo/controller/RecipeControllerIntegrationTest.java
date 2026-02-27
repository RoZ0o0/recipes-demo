package com.example.demo.controller;

import com.example.demo.TestcontainersConfiguration;
import com.example.demo.entity.Ingredient;
import com.example.demo.entity.Recipe;
import com.example.demo.models.*;
import com.example.demo.repository.RecipeRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
class RecipeControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private RecipeRepository recipeRepository;

    private Recipe savedRecipe;

    private RequestSpecification givenAuthenticated() {
        return given().baseUri("http://localhost").port(this.port).contentType(ContentType.JSON)
                .auth().basic("admin", "password");
    }

    @BeforeEach
    void setUp() {
        recipeRepository.deleteAll();

        Recipe recipe = Recipe.builder()
                .name("Curry")
                .description("Chop onions, fry spices, add vegetables...")
                .difficulty(Recipe.Difficulty.EASY)
                .preparationTime(20)
                .build();

        Ingredient ingredient = Ingredient.builder()
                .name("Egg")
                .quantity(1.0)
                .unit(Ingredient.Unit.G)
                .recipe(recipe)
                .build();

        recipe.setIngredients(List.of(ingredient));

        savedRecipe = recipeRepository.save(recipe);
    }

    @AfterEach
    void tearDown() {
        recipeRepository.deleteAll();
    }

    @Test
    void createRecipe_shouldCreateRecipe() throws JsonProcessingException {
        RecipeRequest recipeRequest = new RecipeRequest()
                .name("Classic Pancakes")
                .description("Fluffy homemade pancakes perfect for breakfast.")
                .difficulty(Difficulty.EASY)
                .preparationTime(20);

        IngredientRequest ingredientRequest = new IngredientRequest()
                .name("Milk")
                .quantity(200.0)
                .unit(Unit.ML);

        recipeRequest.setIngredients(List.of(ingredientRequest));

        givenAuthenticated()
                .body(new ObjectMapper().writeValueAsString(recipeRequest))
            .when()
                .post("/recipe")
            .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("$", notNullValue())
                .body("name", equalTo("Classic Pancakes"));

        assertThat(recipeRepository.findAll().size(), equalTo(2));
    }

    @Test
    void createRecipe_shouldReturnBadRequest_WhenInvalidInputProvided() throws JsonProcessingException {
        RecipeRequest recipeRequest = new RecipeRequest()
                .name("Classic Pancakes")
                .description("Fluffy homemade pancakes perfect for breakfast.")
                .difficulty(Difficulty.EASY)
                .preparationTime(0);

        IngredientRequest ingredientRequest = new IngredientRequest()
                .name("Milk")
                .quantity(200.0)
                .unit(Unit.ML);

        recipeRequest.setIngredients(List.of(ingredientRequest));

        givenAuthenticated()
                .body(new ObjectMapper().writeValueAsString(recipeRequest))
            .when()
                .post("/recipe")
            .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());

        assertThat(recipeRepository.findAll().size(), equalTo(1));
    }

    @Test
    void deleteRecipe_shouldDeleteRecipe() {
        givenAuthenticated()
            .when()
                .delete("/recipe/{recipeId}", savedRecipe.getId())
            .then()
                .statusCode(HttpStatus.OK.value());

        assertThat(recipeRepository.findAll().size(), equalTo(0));
    }

    @Test
    void deleteRecipe_shouldReturnNotFound_whenRecipeDoesNotExist() {
        givenAuthenticated()
            .when()
                .delete("/recipe/2")
            .then()
                .statusCode(HttpStatus.NOT_FOUND.value());

        assertThat(recipeRepository.findAll().size(), equalTo(1));
    }

    @Test
    void getRecipe_shouldReturnRecipe() {
        givenAuthenticated()
            .when()
                .get("/recipe/{recipeId}", savedRecipe.getId())
            .then()
                .statusCode(HttpStatus.OK.value())
                .body("name", equalTo(savedRecipe.getName()));
    }

    @Test
    void getRecipe_shouldReturnNotFound_whenRecipeDoesNotExist() {
        givenAuthenticated()
            .when()
                .get("/recipe/2")
            .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void searchRecipes_shouldReturnAllRecipesPaginated_whenSearchIsNull() {
        givenAuthenticated()
                .param("page", 0)
                .param("size", 10)
            .when()
                .get("/recipe")
            .then()
                .statusCode(HttpStatus.OK.value())
                .body("content", is(not(empty())))
                .body("totalElements", equalTo(1));
    }

    @Test
    void searchRecipes_shouldReturnFilteredRecipesPaginated_whenSearchIsProvided() {
        givenAuthenticated()
                .param("page", 0)
                .param("size", 10)
                .param("search", savedRecipe.getName())
            .when()
                .get("/recipe")
            .then()
                .statusCode(HttpStatus.OK.value())
                .body("content", is(not(empty())))
                .body("totalElements", equalTo(1));
    }

    @Test
    void searchRecipes_shouldReturnBadRequest_whenPageNumberIsNotProvided() {
        givenAuthenticated()
                .param("size", 10)
            .when()
                .get("/recipe")
            .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void updateRecipe_shouldUpdateRecipe() throws JsonProcessingException {
        RecipeUpdateRequest recipeUpdateRequest = new RecipeUpdateRequest()
                .name("Salad")
                .description("Add vegetables...")
                .difficulty(Difficulty.EASY)
                .preparationTime(10);

        IngredientUpdateRequest ingredientUpdateRequest = new IngredientUpdateRequest()
                .name("Cabbage")
                .quantity(100.0)
                .unit(Unit.G);

        recipeUpdateRequest.setIngredients(List.of(ingredientUpdateRequest));

        givenAuthenticated()
                .body(new ObjectMapper().writeValueAsString(recipeUpdateRequest))
            .when()
                .put("/recipe/{recipeId}", savedRecipe.getId())
            .then()
                .statusCode(HttpStatus.OK.value())
                .body("name", equalTo("Salad"));

        assertThat(recipeRepository.findAll().getFirst().getName(), equalTo("Salad"));
    }

    @Test
    void updateRecipe_shouldUpdateIngredients() throws JsonProcessingException {
        Long existingIngredientId = savedRecipe.getIngredients().getFirst().getId();

        RecipeUpdateRequest recipeUpdateRequest = new RecipeUpdateRequest()
                .name("Salad")
                .description("Add vegetables...")
                .difficulty(Difficulty.EASY)
                .preparationTime(10);

        IngredientUpdateRequest updatedIngredient = new IngredientUpdateRequest()
                .id(existingIngredientId)
                .name("Cabbage")
                .quantity(100.0)
                .unit(Unit.G);

        IngredientUpdateRequest newIngredient = new IngredientUpdateRequest()
                .name("Carrot")
                .quantity(100.0)
                .unit(Unit.G);

        recipeUpdateRequest.setIngredients(List.of(updatedIngredient, newIngredient));

        givenAuthenticated()
                .body(new ObjectMapper().writeValueAsString(recipeUpdateRequest))
            .when()
                .put("/recipe/{recipeId}", savedRecipe.getId())
            .then()
                .statusCode(HttpStatus.OK.value())
                .body("ingredients", hasSize(2))
                .body("ingredients[0].name", equalTo("Cabbage"));
    }

    @Test
    void updateRecipe_shouldReturnNotFound_whenRecipeDoesNotExist() throws JsonProcessingException {
        RecipeUpdateRequest recipeUpdateRequest = new RecipeUpdateRequest()
                .name("Salad")
                .description("Add vegetables...")
                .difficulty(Difficulty.EASY)
                .preparationTime(10);

        IngredientUpdateRequest ingredientUpdateRequest = new IngredientUpdateRequest()
                .name("Cabbage")
                .quantity(100.0)
                .unit(Unit.G);

        recipeUpdateRequest.setIngredients(List.of(ingredientUpdateRequest));

        givenAuthenticated()
                .body(new ObjectMapper().writeValueAsString(recipeUpdateRequest))
            .when()
                .put("/recipe/2")
            .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void updateRecipe_shouldReturnBadRequest_whenIngredientNotLinkedToRecipe() throws JsonProcessingException {
        RecipeUpdateRequest recipeUpdateRequest = new RecipeUpdateRequest()
                .name("Salad")
                .description("Add vegetables...")
                .difficulty(Difficulty.EASY)
                .preparationTime(10);

        IngredientUpdateRequest updatedIngredient = new IngredientUpdateRequest()
                .id(2L)
                .name("Cabbage")
                .quantity(100.0)
                .unit(Unit.G);

        recipeUpdateRequest.setIngredients(List.of(updatedIngredient));

        givenAuthenticated()
                .body(new ObjectMapper().writeValueAsString(recipeUpdateRequest))
                .log().all()
            .when()
                .put("/recipe/{recipeId}", savedRecipe.getId())
            .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }
}
