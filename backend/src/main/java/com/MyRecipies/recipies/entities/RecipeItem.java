package com.MyRecipies.recipies.entities;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "recipe_items")
public class RecipeItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    @ManyToOne
    @JoinColumn(name = "sub_product_id")
    private Product subProduct;

    @ManyToOne
    @JoinColumn(name = "ingredient_id")
    private Ingredient ingredient;

    @Column(nullable = false)
    private BigDecimal quantity;

    @Column(nullable = false)
    private BigDecimal unitCostSnapshot;

    @Column(nullable = false)
    private BigDecimal totalCostSnapshot;
    
    public RecipeItem() {
    }

    public RecipeItem(Long id, Recipe recipe, Product subProduct, Ingredient ingredient, BigDecimal quantity) {
        this.id = id;
        this.recipe = recipe;
        this.subProduct = subProduct;
        this.ingredient = ingredient;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public Product getSubProduct() {
        return subProduct;
    }

    public void setSubProduct(Product subProduct) {
        this.subProduct = subProduct;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public void calculateSnapshot() {
        BigDecimal unitCost = BigDecimal.ZERO;

        if (ingredient != null) {
            unitCost = ingredient.calculateUnitCost();
        } else if (subProduct != null) {
            unitCost = subProduct.calculateUnitCost();
        }

        this.unitCostSnapshot = unitCost;
        this.totalCostSnapshot = unitCost.multiply(quantity);
    }

    public BigDecimal getUnitCost() {
        return unitCostSnapshot;
    }

    public void setUnitCost(BigDecimal unitCostSnapshot) {
        this.unitCostSnapshot = unitCostSnapshot;
    }

    public BigDecimal getTotalCost() {
        return totalCostSnapshot;
    }

    public void setTotalCost(BigDecimal totalCostSnapshot) {
        this.totalCostSnapshot = totalCostSnapshot;
    }

    
}
