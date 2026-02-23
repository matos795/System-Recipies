package com.MyRecipies.recipies.repositories;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.MyRecipies.recipies.entities.Recipe;
import com.MyRecipies.recipies.entities.RecipeItemVersion;
import com.MyRecipies.recipies.entities.RecipeVersion;
import com.MyRecipies.recipies.entities.User;
import com.MyRecipies.recipies.tests.Factory;

@DataJpaTest
public class RecipeVersionRepositoryTests {

    @Autowired
    private RecipeVersionRepository versionRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private Recipe recipe;
    private User client;
    private RecipeVersion versionWithItems;
    private RecipeVersion version2;
    private RecipeItemVersion item1;

    @BeforeEach
    public void setUp() throws Exception {

        client = testEntityManager.persist(Factory.createUser());
        recipe = testEntityManager.persist(Factory.createRecipe(client));

        versionWithItems = Factory.createRecipeVersion(recipe, 1);
        version2 = Factory.createRecipeVersion(recipe, 2);

        item1 = Factory.createRecipeItemVersion(versionWithItems);

        testEntityManager.persist(versionWithItems);
        testEntityManager.persist(version2);

        testEntityManager.flush();
    }

    @Test
    public void findByRecipeIdOrderByVersionNumberDescShouldReturnOrderedVersions() {

        List<RecipeVersion> list = versionRepository.findByRecipeIdOrderByVersionNumberDesc(recipe.getId());

        Assertions.assertFalse(list.isEmpty());
        Assertions.assertEquals(2, list.size());
        Assertions.assertTrue(list.get(0).getVersionNumber() > list.get(1).getVersionNumber());
    }

    @Test
    public void findByRecipeIdOrderByVersionNumberDescShouldReturnEmptyListWhenRecipeHasNoVersions() {

        Recipe recipeWithoutVersions = testEntityManager.persist(Factory.createRecipe(client));
        testEntityManager.flush();

        List<RecipeVersion> list = versionRepository
                .findByRecipeIdOrderByVersionNumberDesc(recipeWithoutVersions.getId());

        Assertions.assertTrue(list.isEmpty());
    }

    @Test
    public void findByIdAndRecipeIdShouldReturnVersionWhenExists() {

        RecipeVersion version = versionRepository.findByIdAndRecipeId(versionWithItems.getId(), recipe.getId())
                .orElse(null);

        Assertions.assertNotNull(version);
        Assertions.assertEquals(versionWithItems.getVersionNumber(), version.getVersionNumber());
    }

    @Test
    public void findByIdAndRecipeIdShouldReturnEmptyWhenNotExists() {

        var result = versionRepository.findByIdAndRecipeId(999L, recipe.getId());

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    public void saveShouldPersistItemVersionsWhenSavingVersion() {

        versionWithItems.getItems().add(item1);

        testEntityManager.persist(versionWithItems);
        testEntityManager.flush();
        testEntityManager.clear();

        RecipeVersion saved = versionRepository.findById(versionWithItems.getId()).get();

        Assertions.assertFalse(saved.getItems().isEmpty());
    }

    @Test
    public void deleteShouldDeleteItemVersionsWhenDeletingVersion() {

        versionWithItems.getItems().add(item1);

        testEntityManager.persist(versionWithItems);
        testEntityManager.flush();

        versionRepository.delete(versionWithItems);
        testEntityManager.flush();

        Assertions.assertTrue(versionRepository.findById(versionWithItems.getId()).isEmpty());
    }

}
