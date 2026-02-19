package com.MyRecipies.recipies.entities;

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

    private String ingredientName;
    private Double quantity;
    private Double unitCostSnapshot;
    private UnitType unit;
    private Double totalCostSnapshot;

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

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Double getUnitCostSnapshot() {
        return unitCostSnapshot;
    }

    public void setUnitCostSnapshot(Double unitCostSnapshot) {
        this.unitCostSnapshot = unitCostSnapshot;
    }

    public UnitType getUnit() {
        return unit;
    }

    public void setUnit(UnitType unit) {
        this.unit = unit;
    }

    public Double getTotalCostSnapshot() {
        return totalCostSnapshot;
    }

    public void setTotalCostSnapshot(Double totalCostSnapshot) {
        this.totalCostSnapshot = totalCostSnapshot;
    }

    public RecipeVersion getVersion() {
        return version;
    }

    public void setVersion(RecipeVersion version) {
        this.version = version;
    }

    
}