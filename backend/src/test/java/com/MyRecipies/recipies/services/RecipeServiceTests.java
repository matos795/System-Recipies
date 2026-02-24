package com.MyRecipies.recipies.services;

import static org.mockito.ArgumentMatchers.any;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.MyRecipies.recipies.dto.RecipeDTO;
import com.MyRecipies.recipies.dto.RecipeItemDTO;
import com.MyRecipies.recipies.entities.Ingredient;
import com.MyRecipies.recipies.entities.Product;
import com.MyRecipies.recipies.entities.Recipe;
import com.MyRecipies.recipies.entities.RecipeItem;
import com.MyRecipies.recipies.entities.RecipeVersion;
import com.MyRecipies.recipies.entities.User;
import com.MyRecipies.recipies.repositories.IngredientRepository;
import com.MyRecipies.recipies.repositories.ProductRepository;
import com.MyRecipies.recipies.repositories.RecipeRepository;
import com.MyRecipies.recipies.repositories.RecipeVersionRepository;
import com.MyRecipies.recipies.services.exceptions.DatabaseException;
import com.MyRecipies.recipies.services.exceptions.ResourceNotFoundException;
import com.MyRecipies.recipies.tests.Factory;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
public class RecipeServiceTests {

    @InjectMocks
    private RecipeService recipeService;

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private IngredientRepository ingredientRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private RecipeVersionRepository versionRepository;

    @Mock
    private UserService userService;

    @Mock
    private AuthService authService;

    private Long existingId;
    private Long nonExistingId;
    private Long dependentId;

    private Long clientId;
    private Recipe recipe;

    private Pageable pageable;

    private User client;

    private RecipeDTO dto;

    @BeforeEach
    public void setUp() throws Exception {

        existingId = 1L;
        nonExistingId = 1000L;
        dependentId = 3L;

        clientId = 10L;

        pageable = PageRequest.of(0, 10);

        dto = new RecipeDTO();
        dto.setItems(new ArrayList<>());
        dto.setProductName("aaaa");
        dto.setProductPrice(BigDecimal.valueOf(10.0));

        client = new User();
        client.setId(clientId);

        recipe = new Recipe();
        recipe.setProduct(Factory.createProduct());
        recipe.getProduct().setId(existingId);
        recipe.setId(existingId);
        recipe.setClient(client);
    }

    @Test
    public void findByIdShouldReturnRecipeDTOWhenIdExistsAndUserAuthorized() {

        Mockito.when(recipeRepository.findById(existingId)).thenReturn(Optional.of(recipe));
        Mockito.doNothing().when(authService).validateSelfOrAdmin(clientId);

        RecipeDTO dto = recipeService.findById(existingId);

        Assertions.assertNotNull(dto);

        Mockito.verify(recipeRepository).findById(existingId);
        Mockito.verify(authService).validateSelfOrAdmin(clientId);
    }

    @Test
    public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

        Mockito.when(recipeRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            recipeService.findById(nonExistingId);
        });

        Mockito.verify(recipeRepository).findById(nonExistingId);
        Mockito.verifyNoInteractions(authService);
    }

    @Test
    public void findByIdShouldThrowExceptionWhenUserNotAuthorized() {

        Mockito.when(recipeRepository.findById(existingId)).thenReturn(Optional.of(recipe));
        Mockito.doThrow(RuntimeException.class).when(authService).validateSelfOrAdmin(clientId);

        Assertions.assertThrows(RuntimeException.class, () -> {
            recipeService.findById(existingId);
        });

        Mockito.verify(recipeRepository).findById(existingId);
        Mockito.verify(authService).validateSelfOrAdmin(clientId);
    }

    @Test
    public void deleteShouldDoNothingWhenIdExistsAndUserAuthorized() {

        Mockito.when(recipeRepository.findById(existingId)).thenReturn(Optional.of(recipe));
        Mockito.doNothing().when(authService).validateSelfOrAdmin(clientId);

        Mockito.doNothing().when(recipeRepository).delete(recipe);
        Mockito.doNothing().when(productRepository).delete(recipe.getProduct());

        Assertions.assertDoesNotThrow(() -> {
            recipeService.delete(existingId);
        });

        Mockito.verify(recipeRepository).findById(existingId);
        Mockito.verify(authService).validateSelfOrAdmin(clientId);
        Mockito.verify(recipeRepository).delete(recipe);
        Mockito.verify(productRepository).delete(recipe.getProduct());
    }

    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

        Mockito.when(recipeRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            recipeService.delete(nonExistingId);
        });

        Mockito.verify(recipeRepository).findById(nonExistingId);
    }

    @Test
    public void deleteShouldThrowDataIntegrityViolationExceptionWhenDependentId() {

        Mockito.when(recipeRepository.findById(dependentId)).thenReturn(Optional.of(recipe));
        Mockito.doNothing().when(authService).validateSelfOrAdmin(clientId);
        Mockito.doThrow(DataIntegrityViolationException.class).when(recipeRepository).delete(recipe);

        Assertions.assertThrows(DatabaseException.class, () -> {
            recipeService.delete(dependentId);
        });

        Mockito.verify(recipeRepository).findById(dependentId);
        Mockito.verify(authService).validateSelfOrAdmin(clientId);
        Mockito.verify(recipeRepository).delete(recipe);
    }

    @Test
    public void deleteShouldThrowExceptionWhenUserNotAuthorized() {

        Mockito.when(recipeRepository.findById(existingId)).thenReturn(Optional.of(recipe));
        Mockito.doThrow(RuntimeException.class).when(authService).validateSelfOrAdmin(clientId);

        Assertions.assertThrows(RuntimeException.class, () -> {
            recipeService.delete(existingId);
        });

        Mockito.verify(recipeRepository).findById(existingId);
        Mockito.verify(authService).validateSelfOrAdmin(clientId);
        Mockito.verify(recipeRepository, Mockito.never()).delete(Mockito.any());
    }

    @Test
    public void findByClientIdShouldReturnEmptyPageWhenClientHasNoRecipes() {

        User user = new User();
        user.setId(clientId);

        Mockito.when(userService.authenticated()).thenReturn(user);
        Mockito.when(recipeRepository.findByClientId(clientId, pageable)).thenReturn(Page.empty());

        Page<RecipeDTO> page = recipeService.findByClientId(pageable);

        Assertions.assertTrue(page.isEmpty());

        Mockito.verify(userService).authenticated();
        Mockito.verify(recipeRepository).findByClientId(clientId, pageable);
    }

    @Test
    public void insertShouldSaveRecipeAndReturnDTO() {

        Mockito.when(userService.authenticated()).thenReturn(client);
        Mockito.when(productRepository.save(Mockito.any())).thenAnswer(invocation -> invocation.getArgument(0));
        Mockito.when(recipeRepository.save(Mockito.any())).thenAnswer(invocation -> invocation.getArgument(0));

        RecipeDTO result = recipeService.insert(dto);

        Assertions.assertNotNull(result);

        Mockito.verify(userService).authenticated();
        Mockito.verify(recipeRepository).save(Mockito.any());
    }

    @Test
    public void insertShouldCreateVersionOne() {

        Mockito.when(userService.authenticated()).thenReturn(client);
        Mockito.when(productRepository.save(Mockito.any())).thenAnswer(invocation -> invocation.getArgument(0));
        Mockito.when(recipeRepository.save(Mockito.any())).thenAnswer(invocation -> invocation.getArgument(0));

        recipeService.insert(dto);

        Mockito.verify(versionRepository, Mockito.times(1))
                .save(Mockito.argThat(version -> version.getVersionNumber() == 1));
    }

    @Test
    public void updateShouldReturnRecipeDTOWhenIdExistsAndAuthorized() {

        Mockito.when(recipeRepository.getReferenceById(existingId))
                .thenReturn(recipe);

        Mockito.doNothing().when(authService)
                .validateSelfOrAdmin(clientId);

        Mockito.when(productRepository.getReferenceById(existingId))
                .thenReturn(recipe.getProduct());

        Mockito.when(productRepository.save(Mockito.any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Mockito.when(recipeRepository.save(Mockito.any()))
                .thenReturn(recipe);

        RecipeDTO result = recipeService.update(existingId, dto);

        Assertions.assertNotNull(result);

        Mockito.verify(recipeRepository).getReferenceById(existingId);
        Mockito.verify(authService).validateSelfOrAdmin(clientId);
        Mockito.verify(productRepository).save(Mockito.any());
        Mockito.verify(recipeRepository).save(recipe);
    }

    @Test
    public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

        Mockito.when(recipeRepository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            recipeService.update(nonExistingId, dto);
        });

        Mockito.verify(recipeRepository).getReferenceById(nonExistingId);
        Mockito.verifyNoInteractions(authService);
    }

    @Test
    public void updateShouldThrowExceptionWhenUserNotAuthorized() {

        Mockito.when(recipeRepository.getReferenceById(existingId)).thenReturn(recipe);
        Mockito.doThrow(RuntimeException.class).when(authService).validateSelfOrAdmin(clientId);

        Assertions.assertThrows(RuntimeException.class, () -> {
            recipeService.update(existingId, dto);
        });

        Mockito.verify(recipeRepository).getReferenceById(existingId);
        Mockito.verify(authService).validateSelfOrAdmin(clientId);
        Mockito.verify(recipeRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    public void updateShouldCreateNewVersion() {

        Mockito.when(recipeRepository.getReferenceById(existingId)).thenReturn(recipe);
        Mockito.doNothing().when(authService).validateSelfOrAdmin(clientId);
        Mockito.when(productRepository.getReferenceById(existingId)).thenReturn(recipe.getProduct());
        Mockito.when(productRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        Mockito.when(recipeRepository.save(any())).thenReturn(recipe);

        recipeService.update(existingId, dto);

        Mockito.verify(versionRepository).save(any(RecipeVersion.class));
    }

    @Test
    public void updateShouldIncrementVersionNumber() {

        RecipeVersion v1 = new RecipeVersion();
        v1.setVersionNumber(1);

        recipe.setVersions(new ArrayList<>());
        recipe.getVersions().add(v1);

        Mockito.when(recipeRepository.getReferenceById(existingId)).thenReturn(recipe);
        Mockito.doNothing().when(authService).validateSelfOrAdmin(clientId);
        Mockito.when(productRepository.getReferenceById(existingId)).thenReturn(recipe.getProduct());
        Mockito.when(productRepository.save(Mockito.any())).thenAnswer(invocation -> invocation.getArgument(0));
        Mockito.when(recipeRepository.save(Mockito.any())).thenReturn(recipe);

        dto.setProductName(recipe.getProduct().getName());

        recipeService.update(existingId, dto);

        Mockito.verify(versionRepository).save(Mockito.argThat(version -> version.getVersionNumber() == 2));
    }

    @Test
    public void updateShouldSnapshotOldDataBeforeChanging() {

        recipe.setDescription("Descrição Antiga");
        recipe.setAmount(5);

        Product product = recipe.getProduct();
        product.setName("Produto Antigo");
        product.setPrice(new BigDecimal("50"));

        Mockito.when(recipeRepository.getReferenceById(existingId)).thenReturn(recipe);
        Mockito.doNothing().when(authService).validateSelfOrAdmin(clientId);
        Mockito.when(productRepository.getReferenceById(existingId)).thenReturn(recipe.getProduct());
        Mockito.when(productRepository.save(Mockito.any())).thenAnswer(invocation -> invocation.getArgument(0));
        Mockito.when(recipeRepository.save(Mockito.any())).thenReturn(recipe);

        recipeService.update(existingId, dto);

        Mockito.verify(versionRepository)
                .save(Mockito.argThat(version -> version.getDescription().equals("Descrição Antiga") &&
                        version.getAmount().equals(5) &&
                        version.getProductNameSnapshot().equals("Produto Antigo") &&
                        version.getProductPriceSnapshot().compareTo(new BigDecimal("50")) == 0));
    }

    @Test
    public void updateShouldCreateVersionOneIfNoPreviousVersions() {

        recipe.setVersions(null);

        Mockito.when(recipeRepository.getReferenceById(existingId)).thenReturn(recipe);
        Mockito.doNothing().when(authService).validateSelfOrAdmin(clientId);
        Mockito.when(productRepository.getReferenceById(existingId)).thenReturn(recipe.getProduct());
        Mockito.when(productRepository.save(Mockito.any())).thenAnswer(invocation -> invocation.getArgument(0));
        Mockito.when(recipeRepository.save(Mockito.any())).thenReturn(recipe);

        recipeService.update(existingId, dto);

        Mockito.verify(versionRepository).save(Mockito.argThat(version -> version.getVersionNumber() == 1));
    }

    @Test
    public void updateShouldCreateItemSnapshots() {

        RecipeItem item = new RecipeItem();
        item.setRecipe(recipe);
        item.setQuantity(new BigDecimal("2"));
        item.setUnitCost(new BigDecimal("10"));
        item.setTotalCost(new BigDecimal("20"));
        item.setIngredient(Factory.createIngredient(client));

        List<RecipeItem> items = new ArrayList<>();
        items.add(item);
        recipe.setItems(items);

        Mockito.when(recipeRepository.getReferenceById(existingId)).thenReturn(recipe);
        Mockito.doNothing().when(authService).validateSelfOrAdmin(clientId);
        Mockito.when(productRepository.getReferenceById(existingId)).thenReturn(recipe.getProduct());
        Mockito.when(productRepository.save(Mockito.any())).thenAnswer(invocation -> invocation.getArgument(0));
        Mockito.when(recipeRepository.save(Mockito.any())).thenReturn(recipe);

        recipeService.update(existingId, dto);

        Mockito.verify(versionRepository)
                .save(Mockito.argThat(version -> version.getItems() != null &&
                        !version.getItems().isEmpty()));
    }

    @Test
    public void updateShouldNotModifyPreviousVersionList() {

        RecipeVersion v1 = new RecipeVersion();
        v1.setVersionNumber(1);

        List<RecipeVersion> versions = new ArrayList<>();
        versions.add(v1);
        recipe.setVersions(versions);

        Mockito.when(recipeRepository.getReferenceById(existingId)).thenReturn(recipe);
        Mockito.doNothing().when(authService).validateSelfOrAdmin(clientId);
        Mockito.when(productRepository.getReferenceById(existingId)).thenReturn(recipe.getProduct());
        Mockito.when(productRepository.save(Mockito.any())).thenAnswer(invocation -> invocation.getArgument(0));
        Mockito.when(recipeRepository.save(Mockito.any())).thenReturn(recipe);

        recipeService.update(existingId, dto);

        Assertions.assertEquals(1, recipe.getVersions().size());
    }

    @Test
    public void updateShouldRecalculateFinancialData() {

        Product product = recipe.getProduct();
        product.setPrice(new BigDecimal("100"));

        Mockito.when(recipeRepository.getReferenceById(existingId)).thenReturn(recipe);
        Mockito.doNothing().when(authService).validateSelfOrAdmin(clientId);
        Mockito.when(productRepository.getReferenceById(existingId)).thenReturn(recipe.getProduct());
        Mockito.when(productRepository.save(Mockito.any())).thenAnswer(invocation -> invocation.getArgument(0));
        Mockito.when(recipeRepository.save(Mockito.any())).thenReturn(recipe);

        // Configura DTO corretamente
        dto.setAmount(1);
        dto.setProductPrice(new BigDecimal("100"));

        RecipeItemDTO itemDTO = new RecipeItemDTO();
        itemDTO.setIngredientId(1L);
        itemDTO.setQuantity(new BigDecimal("1"));

        dto.setItems(new ArrayList<>());
        dto.getItems().add(itemDTO);

        // Mock do ingredientRepository
        Ingredient ing = Factory.createIngredient(client);
        ing.setPriceCost(new BigDecimal("30"));
        ing.setQuantityPerUnit(new BigDecimal("1"));

        Mockito.when(ingredientRepository.findById(1L))
                .thenReturn(Optional.of(ing));

        RecipeDTO result = recipeService.update(existingId, dto);

        Assertions.assertEquals(0, result.getCostPerUnit().compareTo(new BigDecimal("30")));
        Assertions.assertEquals(0, result.getTotalCost().compareTo(new BigDecimal("30")));
        Assertions.assertEquals(0, result.getProfit().compareTo(new BigDecimal("70")));
    }

    @Test
    public void updateShouldSnapshotItemFinancialValues() {

        RecipeItem item = new RecipeItem();
        item.setRecipe(recipe);
        item.setQuantity(new BigDecimal("2"));
        item.setUnitCost(new BigDecimal("5"));
        item.setTotalCost(new BigDecimal("10"));
        item.setIngredient(Factory.createIngredient(client));

        List<RecipeItem> items = new ArrayList<>();
        items.add(item);
        recipe.setItems(items);

        Mockito.when(recipeRepository.getReferenceById(existingId)).thenReturn(recipe);
        Mockito.doNothing().when(authService).validateSelfOrAdmin(clientId);
        Mockito.when(productRepository.getReferenceById(existingId)).thenReturn(recipe.getProduct());
        Mockito.when(productRepository.save(Mockito.any())).thenAnswer(invocation -> invocation.getArgument(0));
        Mockito.when(recipeRepository.save(Mockito.any())).thenReturn(recipe);

        recipeService.update(existingId, dto);

        Mockito.verify(versionRepository).save(
                Mockito.argThat(version -> version.getItems().get(0)
                        .getTotalCostSnapshot()
                        .compareTo(new BigDecimal("10")) == 0));
    }
}
