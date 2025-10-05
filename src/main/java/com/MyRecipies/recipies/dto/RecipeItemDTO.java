package com.MyRecipies.recipies.dto;

import com.MyRecipies.recipies.entities.RecipeItem;

public class RecipeItemDTO {

    private Long id;
    private Long recipeId;
    private Long subProductId;
    private Long ingredientId;
    private Double quantity;

    public RecipeItemDTO() {
    }

    public RecipeItemDTO(RecipeItem item) {
        this.id = item.getId();
        this.recipeId = item.getRecipe() != null ? item.getRecipe().getId() : null;
        this.subProductId = item.getSubProduct() != null ? item.getSubProduct().getId() : null;
        this.ingredientId = item.getIngredient() != null ? item.getIngredient().getId() : null;
        this.quantity = item.getQuantity();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(Long recipeId) {
        this.recipeId = recipeId;
    }

    public Long getSubProductId() {
        return subProductId;
    }

    public void setSubProductId(Long subProductId) {
        this.subProductId = subProductId;
    }

    public Long getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(Long ingredientId) {
        this.ingredientId = ingredientId;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }
    
}
