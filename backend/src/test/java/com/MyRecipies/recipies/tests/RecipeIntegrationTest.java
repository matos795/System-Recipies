package com.MyRecipies.recipies.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.MyRecipies.recipies.dto.RecipeDTO;
import com.MyRecipies.recipies.dto.RecipeItemDTO;
import com.MyRecipies.recipies.dto.RecipeVersionDTO;
import com.MyRecipies.recipies.entities.Ingredient;
import com.MyRecipies.recipies.entities.Recipe;
import com.MyRecipies.recipies.entities.User;
import com.MyRecipies.recipies.entities.enums.UnitType;
import com.MyRecipies.recipies.repositories.IngredientRepository;
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

    @Autowired
    private IngredientRepository ingredientRepository;

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
        dto.setProductPrice(BigDecimal.valueOf(10.0));
        dto.setItems(new ArrayList<>());

        RecipeDTO result = recipeService.insert(dto);

        Assertions.assertNotNull(result.getId());
        Assertions.assertEquals("Bolo", result.getProductName());
        Assertions.assertEquals(BigDecimal.valueOf(10.0), result.getProductPrice());

        Recipe recipe = recipeRepository.findById(result.getId()).orElseThrow();

        Assertions.assertEquals("Bolo", recipe.getProduct().getName());
        Assertions.assertEquals(BigDecimal.valueOf(10.0), recipe.getProduct().getPrice());
        Assertions.assertEquals(user.getId(), recipe.getClient().getId());

    }

    @Test
    public void shouldInsertUpdateAndCreateVersionCorrectly() {

        // ========================
        // 1️⃣ Criar usuário real
        // ========================
        User user = new User();
        user.setName("Teste");
        user.setEmail("teste@email.com");
        user.setPassword("123");
        user = userRepository.save(user);

        Mockito.when(userService.authenticated()).thenReturn(user);

        // ========================
        // 2️⃣ Criar ingrediente real
        // ========================
        Ingredient ingredient = new Ingredient();
        ingredient.setName("Farinha");
        ingredient.setPriceCost(new BigDecimal("10"));
        ingredient.setQuantityPerUnit(new BigDecimal("1"));
        ingredient.setUnit(UnitType.KILOGRAM);
        ingredient.setClient(user);

        ingredient = ingredientRepository.save(ingredient);

        // ========================
        // 3️⃣ Criar DTO para insert
        // ========================
        RecipeDTO dto = new RecipeDTO();
        dto.setDescription("Receita Original");
        dto.setAmount(1);
        dto.setProductName("Bolo");
        dto.setProductPrice(new BigDecimal("50"));

        RecipeItemDTO itemDTO = new RecipeItemDTO();
        itemDTO.setIngredientId(ingredient.getId());
        itemDTO.setQuantity(new BigDecimal("1"));

        dto.setItems(List.of(itemDTO));

        // ========================
        // 4️⃣ Insert
        // ========================
        RecipeDTO inserted = recipeService.insert(dto);

        assertNotNull(inserted.getId());
        assertEquals(0, inserted.getTotalCost().compareTo(new BigDecimal("10")));
        assertEquals(0, inserted.getProfit().compareTo(new BigDecimal("40")));

        // ========================
        // 5️⃣ Update (novo preço)
        // ========================
        dto.setProductPrice(new BigDecimal("100"));

        RecipeDTO updated = recipeService.update(inserted.getId(), dto);

        assertEquals(0, updated.getProfit().compareTo(new BigDecimal("90")));

        // ========================
        // 6️⃣ Buscar versões
        // ========================
        List<RecipeVersionDTO> versions = recipeService.findVersions(inserted.getId());

        assertEquals(2, versions.size());

        RecipeVersionDTO firstVersion = versions.get(1); // versão antiga

        assertEquals("Receita Original", firstVersion.getDescription());
        assertEquals(0,
                firstVersion.getProductPriceSnapshot()
                        .compareTo(new BigDecimal("50")));

        assertEquals(1, firstVersion.getItems().size());
        assertEquals(0,
                firstVersion.getItems().get(0)
                        .getTotalCostSnapshot()
                        .compareTo(new BigDecimal("10")));
    }

    @Test
    public void shouldRestorePreviousVersionCorrectly() {

        User user = new User();
        user.setName("Teste");
        user.setEmail("teste@email.com");
        user.setPassword("123");
        user = userRepository.save(user);

        Mockito.when(userService.authenticated()).thenReturn(user);

        RecipeDTO dto = new RecipeDTO();
        dto.setDescription("Original");
        dto.setAmount(1);
        dto.setProductName("Bolo");
        dto.setProductPrice(new BigDecimal("50"));

        RecipeDTO inserted = recipeService.insert(dto);

        dto.setDescription("Modificado");
        dto.setProductPrice(new BigDecimal("100"));

        recipeService.update(inserted.getId(), dto);

        List<RecipeVersionDTO> versions = recipeService.findVersions(inserted.getId());

        Long versionToRestore = versions.get(1).getId(); // versão antiga

        RecipeDTO restored = recipeService.restoreVersion(inserted.getId(), versionToRestore);

        assertEquals("Original", restored.getDescription());
        assertEquals(0,
                restored.getProductPrice().compareTo(new BigDecimal("50")));
    }
}
