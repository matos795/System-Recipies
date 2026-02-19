package com.MyRecipies.recipies.tests;

import java.util.ArrayList;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.MyRecipies.recipies.dto.RecipeDTO;
import com.MyRecipies.recipies.entities.Recipe;
import com.MyRecipies.recipies.entities.User;
import com.MyRecipies.recipies.repositories.RecipeRepository;
import com.MyRecipies.recipies.repositories.UserRepository;
import com.MyRecipies.recipies.services.RecipeService;
import com.MyRecipies.recipies.services.UserService;

import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class RecipeIntegrationTest {

    @Autowired
    private RecipeService recipeService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    @MockitoBean
    private UserService userService;

    @Test
    public void insertShouldPersistRecipe() {

    User user = new User();
    user.setEmail("test@test.com");
    user.setPassword("123456");
    user = userRepository.save(user);

    Mockito.when(userService.authenticated()).thenReturn(user);

    RecipeDTO dto = new RecipeDTO();
    dto.setProductName("Bolo");
    dto.setProductPrice(10.0);
    dto.setItems(new ArrayList<>());

    RecipeDTO result = recipeService.insert(dto);

    Assertions.assertNotNull(result.getId());
    Assertions.assertEquals("Bolo", result.getProductName());
    Assertions.assertEquals(10.0, result.getProductPrice());

    Recipe recipe = recipeRepository.findById(result.getId()).orElseThrow();

    Assertions.assertEquals("Bolo", recipe.getProduct().getName());
    Assertions.assertEquals(10.0, recipe.getProduct().getPrice());
    Assertions.assertEquals(user.getId(), recipe.getClient().getId());

}

}

