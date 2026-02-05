package com.MyRecipies.recipies.repositories;

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
import com.MyRecipies.recipies.entities.User;
import com.MyRecipies.recipies.tests.Factory;

@DataJpaTest
public class IngredientRepositoryTests {

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private TestEntityManager testEntityManager;

        private User clientWithIngredients;
        private User clientWithoutIngredients;

    @BeforeEach
    public void setUp() throws Exception {

        clientWithIngredients = testEntityManager.persist(Factory.createUser());
        clientWithoutIngredients = testEntityManager.persist(Factory.createUser());

        Ingredient ing1 = Factory.createIngredient(clientWithIngredients);
        Ingredient ing2 = Factory.createIngredient(clientWithIngredients);

        testEntityManager.persist(ing1);
        testEntityManager.persist(ing2);

        testEntityManager.flush();
    }

    @Test
    public void findByClientIdShouldReturnPagedIngredientsWhenIdExists() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Ingredient> page = ingredientRepository.findByClientId(clientWithIngredients.getId(), pageable);

        Assertions.assertFalse(page.getContent().isEmpty());
        Assertions.assertTrue(page.getTotalElements() >= page.getContent().size());

        for (Ingredient ing : page.getContent()) {
            Assertions.assertEquals(clientWithIngredients.getId(), ing.getClient().getId());
        }
    }

    @Test
    public void findByClientIdShouldReturnEmptyPageWhenIdExistsWithoutIngredients() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Ingredient> page = ingredientRepository.findByClientId(clientWithoutIngredients.getId(), pageable);
        Assertions.assertTrue(page.isEmpty());
        Assertions.assertEquals(0, page.getTotalElements());
    }

    @Test
    public void saveShouldPersistWithAutoIncrementWhenIdIsNull() {

        Ingredient ingredient = Factory.createIngredient(clientWithIngredients);
        ingredient.setId(null);
        long countBefore = ingredientRepository.count();

        ingredient = ingredientRepository.saveAndFlush(ingredient);

        long countAfter = ingredientRepository.count();

        Assertions.assertNotNull(ingredient.getId());
        Assertions.assertEquals(countBefore + 1, countAfter);
    }

    @Test
    public void deleteShouldRemoveIngredient() {

        Ingredient ingredient = ingredientRepository.save(Factory.createIngredient(clientWithIngredients));
        long countBefore = ingredientRepository.count();

        ingredientRepository.delete(ingredient);
        testEntityManager.flush();

        long countAfter = ingredientRepository.count();

        Assertions.assertTrue(ingredientRepository.findById(ingredient.getId()).isEmpty());
        Assertions.assertEquals(countBefore - 1, countAfter);
    }

    @Test
    public void findByIdShouldReturnIngredientWhenIdExists(){
        Ingredient ingredient = ingredientRepository.save(Factory.createIngredient(clientWithIngredients));

        Assertions.assertTrue(ingredientRepository.findById(ingredient.getId()).isPresent());
    }

    @Test
    public void findByIdShouldReturnEmptyWhenIdDoesNotExist(){

        Assertions.assertTrue(ingredientRepository.findById(1000L).isEmpty());
    }
}
