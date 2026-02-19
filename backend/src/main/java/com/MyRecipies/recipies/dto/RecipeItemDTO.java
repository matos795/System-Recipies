package com.MyRecipies.recipies.dto;

import java.math.BigDecimal;

import com.MyRecipies.recipies.entities.RecipeItem;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class RecipeItemDTO {

    private Long subProductId;
    private Long ingredientId;
    @NotNull
    @Positive
    private BigDecimal quantity;

    public RecipeItemDTO() {
    }

    public RecipeItemDTO(RecipeItem item) {
        this.subProductId = item.getSubProduct() != null ? item.getSubProduct().getId() : null;
        this.ingredientId = item.getIngredient() != null ? item.getIngredient().getId() : null;
        this.quantity = item.getQuantity();
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

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }
    
}
