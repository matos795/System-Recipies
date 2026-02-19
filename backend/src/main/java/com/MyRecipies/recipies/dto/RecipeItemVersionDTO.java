package com.MyRecipies.recipies.dto;

import java.math.BigDecimal;

import com.MyRecipies.recipies.entities.RecipeItemVersion;
import com.MyRecipies.recipies.entities.enums.UnitType;

public class RecipeItemVersionDTO {

    private String ingredientName;
    private BigDecimal quantity;
    private BigDecimal unitCostSnapshot;
    private UnitType unit;
    private BigDecimal totalCostSnapshot;

    public RecipeItemVersionDTO(RecipeItemVersion entity) {
        ingredientName = entity.getIngredientName();
        quantity = entity.getQuantity();
        unitCostSnapshot = entity.getUnitCostSnapshot();
        unit = entity.getUnit();
        totalCostSnapshot = entity.getTotalCostSnapshot();
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitCostSnapshot() {
        return unitCostSnapshot;
    }

    public void setUnitCostSnapshot(BigDecimal unitCostSnapshot) {
        this.unitCostSnapshot = unitCostSnapshot;
    }

    public UnitType getUnit() {
        return unit;
    }

    public void setUnit(UnitType unit) {
        this.unit = unit;
    }

    public BigDecimal getTotalCostSnapshot() {
        return totalCostSnapshot;
    }

    public void setTotalCostSnapshot(BigDecimal totalCostSnapshot) {
        this.totalCostSnapshot = totalCostSnapshot;
    }

    
}
