package com.MyRecipies.recipies.repositories;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.MyRecipies.recipies.entities.Ingredient;
import com.MyRecipies.recipies.entities.Recipe;
import com.MyRecipies.recipies.entities.User;
import com.MyRecipies.recipies.tests.Factory;

@DataJpaTest
public class RecipeRepositoryTests {

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private Pageable pageable;
    private User clientWithRecipes;
    private User clientWithoutRecipes;
    private Recipe recipeWithItems;
    private Recipe recipeWithoutItems;

    @BeforeEach
    public void setUp() throws Exception {
        pageable = PageRequest.of(0, 10);

        clientWithRecipes = testEntityManager.persist(Factory.createUser());
        clientWithoutRecipes = testEntityManager.persist(Factory.createUser());

        Ingredient ing1 = Factory.createIngredient(clientWithRecipes);
        Ingredient ing2 = Factory.createIngredient(clientWithRecipes);

        recipeWithoutItems = Factory.createRecipe(clientWithRecipes);
        recipeWithItems = Factory.createRecipeWithIngredients(clientWithRecipes, List.of(ing1, ing2));

        testEntityManager.persist(ing1);
        testEntityManager.persist(ing2);

        testEntityManager.persist(recipeWithoutItems);
        testEntityManager.persist(recipeWithItems);

        testEntityManager.flush();
    }

    @Test
    public void findByClientIdShouldReturnPagedRecipesWhenClientIdExists() {
        Page<Recipe> page = recipeRepository.findByClientId(clientWithRecipes.getId(), pageable);

        Assertions.assertFalse(page.getContent().isEmpty());
        Assertions.assertTrue(page.getTotalElements() >= page.getContent().size());

        for (Recipe recipe : page.getContent()) {
            Assertions.assertEquals(recipe.getClient().getId(), clientWithRecipes.getId());
        }
    }

    @Test
    public void findByClientIdShouldReturnEmptyPageWhenIdExistsWithoutRecipes() {
        Page<Recipe> page = recipeRepository.findByClientId(clientWithoutRecipes.getId(), pageable);

        Assertions.assertTrue(page.getContent().isEmpty());
        Assertions.assertEquals(0, page.getTotalElements());
    }

    @Test
    public void findByClientIdShouldReturnPagedRecipesWithItemsWhenHasAnyItem() {

        Page<Recipe> page =
                recipeRepository.findByClientId(clientWithRecipes.getId(), pageable);

        Assertions.assertFalse(page.isEmpty());

        Recipe recipeFromDb = page.getContent()
                .stream()
                .filter(recipe -> recipe.getId().equals(recipeWithItems.getId()))
                .findFirst()
                .orElseThrow(() ->
                    new AssertionError("Recipe with items not found in page")
                );

        Assertions.assertNotNull(recipeFromDb.getItems());
        Assertions.assertFalse(recipeFromDb.getItems().isEmpty());

        recipeFromDb.getItems().forEach(item -> {
            Assertions.assertEquals(
                recipeFromDb.getId(),
                item.getRecipe().getId()
            );
        });
    }
}
