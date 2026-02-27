package com.example.demo.specification;

import com.example.demo.TestcontainersConfiguration;
import com.example.demo.entity.Ingredient;
import com.example.demo.entity.Recipe;
import com.example.demo.repository.RecipeRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
class RecipeSpecificationsIntegrationTest {

    @Autowired
    private RecipeRepository recipeRepository;

    @BeforeEach
    void setUp() {
        recipeRepository.deleteAll();

        Recipe recipe = Recipe.builder()
                .name("Curry")
                .description("Chop onions, fry spices, add vegetables...")
                .difficulty(Recipe.Difficulty.MEDIUM)
                .preparationTime(20)
                .build();

        Ingredient ingredient = Ingredient.builder()
                .name("Egg")
                .quantity(100.0)
                .unit(Ingredient.Unit.G)
                .recipe(recipe)
                .build();

        recipe.setIngredients(List.of(ingredient));

        recipeRepository.save(recipe);

        Recipe recipe2 = Recipe.builder()
                .name("Salad")
                .description("Add vegetables...")
                .difficulty(Recipe.Difficulty.EASY)
                .preparationTime(10)
                .build();

        Ingredient ingredient2 = Ingredient.builder()
                .name("Cabbage")
                .quantity(200.0)
                .unit(Ingredient.Unit.G)
                .recipe(recipe2)
                .build();

        recipe2.setIngredients(List.of(ingredient2));

        recipeRepository.save(recipe2);
    }

    @AfterEach
    void tearDown() {
        recipeRepository.deleteAll();
    }

    @ParameterizedTest(name = "search = {0}, expectedName = {1}")
    @CsvSource({
            "Curry, Curry",
            "Egg, Curry",
            "EASY, Salad",
    })
    void searchRecipe_shouldFindByMultipleFields(String search, String expectedName) {
        Specification<Recipe> specification = RecipeSpecifications.searchRecipe(search);
        List<Recipe> results = recipeRepository.findAll(specification);

        assertThat(results.size(), equalTo(1));
        assertThat(results.getFirst().getName(), equalTo(expectedName));
    }

    @ParameterizedTest(name = "search = {0}, expectedCount = {1}")
    @CsvSource({
            "Curry, 1",
            "EASY, 1",
            "Cabbage, 1",
            "HARD, 0"
    })
    void searchRecipe_shouldReturnExpectedNumberOfMatches(String search, int expectedCount) {
        Specification<Recipe> specification = RecipeSpecifications.searchRecipe(search);
        List<Recipe> results = recipeRepository.findAll(specification);

        assertThat(results.size(), equalTo(expectedCount));
    }
}
