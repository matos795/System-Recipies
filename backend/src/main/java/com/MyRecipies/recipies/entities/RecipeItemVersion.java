package com.MyRecipies.recipies.entities;

import java.math.BigDecimal;

import com.MyRecipies.recipies.entities.enums.UnitType;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "recipe_item_versions")
public class RecipeItemVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long ingredientId;
    private Long subProductId;

    private String ingredientName;
    private BigDecimal quantity;
    private BigDecimal unitCostSnapshot;
    private UnitType unit;
    private BigDecimal totalCostSnapshot;

    @ManyToOne
    @JoinColumn(name = "version_id")
    private RecipeVersion version;

    public RecipeItemVersion() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public RecipeVersion getVersion() {
        return version;
    }

    public void setVersion(RecipeVersion version) {
        this.version = version;
    }

    public Long getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(Long ingredientId) {
        this.ingredientId = ingredientId;
    }

    public Long getSubProductId() {
        return subProductId;
    }

    public void setSubProductId(Long subProductId) {
        this.subProductId = subProductId;
    }


}