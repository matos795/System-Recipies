package com.MyRecipies.recipies.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import com.MyRecipies.recipies.dto.RecipeDTO;
import com.MyRecipies.recipies.dto.RecipeItemDTO;
import com.MyRecipies.recipies.dto.RecipeVersionDTO;
import com.MyRecipies.recipies.entities.Ingredient;
import com.MyRecipies.recipies.entities.Product;
import com.MyRecipies.recipies.entities.Recipe;
import com.MyRecipies.recipies.entities.RecipeItem;
import com.MyRecipies.recipies.entities.RecipeItemVersion;
import com.MyRecipies.recipies.entities.RecipeVersion;
import com.MyRecipies.recipies.entities.enums.UnitType;
import com.MyRecipies.recipies.entities.enums.VersionActionType;
import com.MyRecipies.recipies.repositories.IngredientRepository;
import com.MyRecipies.recipies.repositories.ProductRepository;
import com.MyRecipies.recipies.repositories.RecipeRepository;
import com.MyRecipies.recipies.repositories.RecipeVersionRepository;
import com.MyRecipies.recipies.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

@Service
public class RecipeService {

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RecipeVersionRepository versionRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @Transactional(readOnly = true)
    public Page<RecipeDTO> findByClientId(Pageable pageable) {
        Long userId = userService.authenticated().getId();
        Page<Recipe> recipes = recipeRepository.findByClientId(userId, pageable);
        return recipes.map(x -> new RecipeDTO(x));
    }

    @Transactional(readOnly = true)
    public Page<RecipeDTO> findAll(Pageable pageable) {
        Page<Recipe> recipes = recipeRepository.findAll(pageable);
        return recipes.map(x -> new RecipeDTO(x));
    }

    @Transactional(readOnly = true)
    public RecipeDTO findById(Long id) {

        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recurso não encontrado!"));

        authService.validateSelfOrAdmin(recipe.getClient().getId());

        if (recipe.getDeleted()) {
            throw new ResourceNotFoundException("Receita deletada");
        }

        RecipeDTO dto = new RecipeDTO(recipe);
        calculateFinancialData(recipe, dto);
        return dto;
    }

    @Transactional
    public RecipeDTO insert(RecipeDTO dto) {
        Recipe entity = new Recipe();
        dtoToEntity(entity, dto);
        entity.setClient(userService.authenticated());
        entity = recipeRepository.save(entity);
        createVersion(entity, VersionActionType.CREATE);

        RecipeDTO newDTO = new RecipeDTO(entity);
        calculateFinancialData(entity, newDTO);
        return newDTO;
    }

    @Transactional
    public RecipeDTO update(Long id, RecipeDTO dto) {
        try {

            Recipe entity = recipeRepository.getReferenceById(id);
            authService.validateSelfOrAdmin(entity.getClient().getId());

            createVersion(entity, VersionActionType.UPDATE);

            dtoToEntity(entity, dto);
            entity = recipeRepository.save(entity);

            RecipeDTO newDTO = new RecipeDTO(entity);
            calculateFinancialData(entity, newDTO);

            return newDTO;

        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Receita não encontrada! ID: " + id);
        }
    }

    @Transactional
    public void delete(Long id) {

        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recurso não encontrado"));

        authService.validateSelfOrAdmin(recipe.getClient().getId());

        createVersion(recipe, VersionActionType.DELETE);

        recipe.setDeleted(true);

        recipeRepository.save(recipe);

    }

    @Transactional(readOnly = true)
    public List<RecipeVersionDTO> findVersions(Long recipeId) {

        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new ResourceNotFoundException("Receita não encontrada"));

        authService.validateSelfOrAdmin(recipe.getClient().getId());

        List<RecipeVersion> versions = versionRepository.findByRecipeIdOrderByVersionNumberDesc(recipeId);

        return versions.stream().map(version -> {
            RecipeVersionDTO dto = new RecipeVersionDTO(version);
            calculateVersionFinancialData(version, dto);
            return dto;
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RecipeVersionDTO findVersionById(Long recipeId, Long versionId) {

        RecipeVersion version = versionRepository.findByIdAndRecipeId(versionId, recipeId)
                .orElseThrow(() -> new ResourceNotFoundException("Versão não encontrada"));

        authService.validateSelfOrAdmin(version.getRecipe().getClient().getId());

        RecipeVersionDTO dto = new RecipeVersionDTO(version);
        calculateVersionFinancialData(version, dto);
        return dto;
    }

    @Transactional
    public RecipeDTO restoreVersion(Long recipeId, Long versionId) {

        RecipeVersion version = versionRepository.findByIdAndRecipeId(versionId, recipeId)
                .orElseThrow(() -> new ResourceNotFoundException("Versão não encontrada"));

        authService.validateSelfOrAdmin(version.getRecipe().getClient().getId());

        Recipe recipe = version.getRecipe();
        createVersion(recipe, VersionActionType.RESTORE);

        recipe.setDescription(version.getDescription());
        recipe.setAmount(version.getAmount());

        Product product = recipe.getProduct();
        product.setName(version.getProductNameSnapshot());
        product.setPrice(version.getProductPriceSnapshot());

        recipe.getItems().clear();

        for (RecipeItemVersion itemVersion : version.getItems()) {

            RecipeItem item = new RecipeItem();
            item.setRecipe(recipe);
            item.setQuantity(itemVersion.getQuantity());

            item.setUnitCost(itemVersion.getUnitCostSnapshot());
            item.setTotalCost(itemVersion.getTotalCostSnapshot());

            item.setIngredient(itemVersion.getIngredientId() != null
                    ? ingredientRepository.getReferenceById(itemVersion.getIngredientId())
                    : null);
            item.setSubProduct(itemVersion.getSubProductId() != null
                    ? productRepository.getReferenceById(itemVersion.getSubProductId())
                    : null);

            recipe.getItems().add(item);
        }

        recipe = recipeRepository.save(recipe);

        RecipeDTO dto = new RecipeDTO(recipe);
        calculateFinancialData(recipe, dto);

        return dto;
    }

    @Transactional
    public RecipeDTO refreshRecipePrices(Long recipeId) {

        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new ResourceNotFoundException("Receita não encontrada!"));

        authService.validateSelfOrAdmin(recipe.getClient().getId());

        createVersion(recipe, VersionActionType.REFRESH);

        for (RecipeItem item : recipe.getItems()) {

            if (item.getIngredient() != null) {

                Ingredient ingredient = ingredientRepository.findById(item.getIngredient().getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Ingrediente removido"));

                authService.validateSelfOrAdmin(ingredient.getClient().getId());

                item.setIngredient(ingredient);
                item.calculateSnapshot();
            } else if (item.getSubProduct() != null) {

                Product sub = productRepository.findById(item.getSubProduct().getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Produto removido"));
                authService.validateSelfOrAdmin(sub.getRecipe().getClient().getId());

                item.setSubProduct(sub);
                item.calculateSnapshot();
            }
        }

        recipeRepository.save(recipe);

        RecipeDTO dto = new RecipeDTO(recipe);
        calculateFinancialData(recipe, dto);

        return dto;
    }

    private void calculateFinancialData(Recipe entity, RecipeDTO dto) {

        BigDecimal totalCost = entity.getItems().stream()
                .map(RecipeItem::getTotalCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        totalCost = totalCost.setScale(2, RoundingMode.HALF_UP);
        dto.setTotalCost(totalCost);

        if (entity.getAmount() != null && entity.getAmount() > 0) {
            BigDecimal costPerUnit = totalCost.divide(
                    new BigDecimal(entity.getAmount()),
                    2, RoundingMode.HALF_UP);
            dto.setCostPerUnit(costPerUnit);
        }

        BigDecimal salePrice = entity.getProduct().getPrice();

        BigDecimal profit = salePrice.subtract(totalCost)
                .setScale(2, RoundingMode.HALF_UP);

        dto.setProfit(profit);

        if (salePrice.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal margin = profit
                    .divide(salePrice, 2, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal(100))
                    .setScale(2, RoundingMode.HALF_UP);

            dto.setMargin(margin);
        }
    }

    private void calculateVersionFinancialData(RecipeVersion version, RecipeVersionDTO dto) {

        BigDecimal totalCost = version.getItems().stream()
                .map(RecipeItemVersion::getTotalCostSnapshot)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        dto.setTotalCost(totalCost);

        BigDecimal salePrice = version.getProductPriceSnapshot();

        BigDecimal profit = salePrice.subtract(totalCost)
                .setScale(2, RoundingMode.HALF_UP);

        dto.setProfit(profit);

        if (salePrice.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal margin = profit
                    .divide(salePrice, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal(100))
                    .setScale(2, RoundingMode.HALF_UP);

            dto.setMargin(margin);
        }
    }

    // Método dtoToEntity completo
    private void dtoToEntity(Recipe entity, RecipeDTO dto) {

        Product product;
        if (entity.getProduct() != null && entity.getProduct().getId() != null) {
            product = productRepository.getReferenceById(entity.getProduct().getId());
        } else {
            product = new Product();
        }

        product.setName(dto.getProductName());
        product.setPrice(dto.getProductPrice());
        product.setImgUrl(dto.getImgUrl());
        product.setCreateDate(dto.getCreateDate() != null ? dto.getCreateDate() : LocalDate.now());
        product.setLastUpdateDate(LocalDateTime.now());

        product = productRepository.save(product);
        entity.setProduct(product);
        product.setRecipe(entity);

        entity.setDescription(dto.getDescription());
        entity.setAmount(dto.getAmount());

        if (entity.getItems() == null) {
            entity.setItems(new ArrayList<>());
        } else {
            entity.getItems().clear();
        }

        for (RecipeItemDTO itemDTO : dto.getItems()) {

            RecipeItem item = new RecipeItem();
            item.setRecipe(entity);

            if (itemDTO.getIngredientId() != null) {
                Ingredient ing = ingredientRepository.findById(itemDTO.getIngredientId())
                        .orElseThrow(() -> new ResourceNotFoundException("Ingrediente não encontrado!"));
                authService.validateSelfOrAdmin(ing.getClient().getId());
                item.setIngredient(ing);
            } else if (itemDTO.getSubProductId() != null) {
                Product sub = productRepository.findById(itemDTO.getSubProductId())
                        .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado!"));
                authService.validateSelfOrAdmin(sub.getRecipe().getClient().getId());
                item.setSubProduct(sub);
            }

            item.setQuantity(itemDTO.getQuantity());

            item.calculateSnapshot();

            entity.addItem(item);
        }
    }

    private void createVersion(Recipe recipe, VersionActionType actionType) {

        RecipeVersion version = new RecipeVersion();
        version.setRecipe(recipe);
        version.setCreatedAt(LocalDateTime.now());
        version.setDescription(recipe.getDescription());
        version.setAmount(recipe.getAmount());
        version.setProductNameSnapshot(recipe.getProduct().getName());
        version.setProductPriceSnapshot(recipe.getProduct().getPrice());
        version.setActionType(actionType);

        // Número da versão
        int nextVersion = recipe.getVersions() == null ? 1 : recipe.getVersions().size() + 1;
        version.setVersionNumber(nextVersion);

        for (RecipeItem item : recipe.getItems()) {

            RecipeItemVersion itemVersion = new RecipeItemVersion();
            itemVersion.setVersion(version);
            itemVersion.setIngredientName(
                    item.getIngredient() != null
                            ? item.getIngredient().getName()
                            : item.getSubProduct().getName());
            itemVersion.setQuantity(item.getQuantity());
            itemVersion.setUnit(
                    item.getIngredient() != null
                            ? item.getIngredient().getUnit()
                            : UnitType.UNIT);
            itemVersion.setUnitCostSnapshot(item.getUnitCost());
            itemVersion.setTotalCostSnapshot(item.getTotalCost());

            if (item.getIngredient() != null) {
                itemVersion.setIngredientId(item.getIngredient().getId());
            } else if (item.getSubProduct() != null) {
                itemVersion.setSubProductId(item.getSubProduct().getId());
            }

            version.getItems().add(itemVersion);
        }

        versionRepository.save(version);
    }
}
