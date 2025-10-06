package com.MyRecipies.recipies.services;

import java.time.LocalDate;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import com.MyRecipies.recipies.dto.RecipeDTO;
import com.MyRecipies.recipies.dto.RecipeItemDTO;
import com.MyRecipies.recipies.entities.Ingredient;
import com.MyRecipies.recipies.entities.Product;
import com.MyRecipies.recipies.entities.Recipe;
import com.MyRecipies.recipies.entities.RecipeItem;
import com.MyRecipies.recipies.repositories.IngredientRepository;
import com.MyRecipies.recipies.repositories.ProductRepository;
import com.MyRecipies.recipies.repositories.RecipeRepository;
import com.MyRecipies.recipies.services.exceptions.DatabaseException;
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

    @Transactional(readOnly = true)
    public Page<RecipeDTO> findAll(Pageable pageable){
        Page<Recipe> recipes = recipeRepository.findAll(pageable);
        return recipes.map(x -> new RecipeDTO(x));
    }

    @Transactional(readOnly = true)
    public RecipeDTO findById(Long id){
        Recipe recipe = recipeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Recurso não encontrado!"));
        return new RecipeDTO(recipe);
    }

    @Transactional
    public RecipeDTO insert(RecipeDTO dto){
        Recipe entity = new Recipe();
        dtoToEntity(entity, dto);
        entity = recipeRepository.save(entity);
        return new RecipeDTO(entity);
    }

    @Transactional
    public RecipeDTO update(Long id, RecipeDTO dto){
        try{
        Recipe entity = recipeRepository.getReferenceById(id);
        dtoToEntity(entity, dto);
        entity = recipeRepository.save(entity);
        return new RecipeDTO(entity);
        } catch (EntityNotFoundException e) {
        throw new ResourceNotFoundException("Receita não encontrada! ID: " + id);
    }
    }

@Transactional
public void delete(Long id) {
    // Busca a receita
    Recipe recipe = recipeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Recurso não encontrado"));

    try {
        // Limpa os items para acionar orphanRemoval
        recipe.getItems().clear();

        // Pega o produto associado
        Product product = recipe.getProduct();

        // Deleta a receita
        recipeRepository.delete(recipe);

        // Deleta o produto
        if (product != null) {
            productRepository.delete(product);
        }

    } catch (DataIntegrityViolationException e) {
        throw new DatabaseException("Falha de integridade referencial");
    }
}


    private void dtoToEntity(Recipe entity, RecipeDTO dto){

        Product product;
if (entity.getProduct() != null && entity.getProduct().getId() != null) {
    Long existingProductId = entity.getProduct().getId();
    product = productRepository.getReferenceById(existingProductId);
    product.setName(dto.getProductName());
    product.setPrice(dto.getProductPrice());
    product.setImgUrl(dto.getImgUrl());
    product.setCreateDate(dto.getCreateDate() != null ? dto.getCreateDate() : LocalDate.now());
    product.setLastUpdateDate(dto.getLastUpdateDate());
} else {
    product = new Product();
    product.setName(dto.getProductName());
    product.setPrice(dto.getProductPrice());
    product.setImgUrl(dto.getImgUrl());
    product.setCreateDate(dto.getCreateDate() != null ? dto.getCreateDate() : LocalDate.now());
    product.setLastUpdateDate(dto.getLastUpdateDate());
    product = productRepository.save(product);
}

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
            item.setQuantity(itemDTO.getQuantity());

            if (itemDTO.getIngredientId() != null) {
                Ingredient ing = ingredientRepository.findById(itemDTO.getIngredientId())
                .orElseThrow(() -> new ResourceNotFoundException("Recurso não encontrado!"));
                item.setUnitCost(ing.calculateUnitCost());
                item.setIngredient(ing);
            } 
            else if(itemDTO.getSubProductId() != null) {
                Product sub = productRepository.findById(itemDTO.getSubProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Recurso não encontrado!"));
                item.setUnitCost(sub.calculateUnitCost());
                item.setSubProduct(sub);
            }
            entity.addItem(item);
        }
    }
}
