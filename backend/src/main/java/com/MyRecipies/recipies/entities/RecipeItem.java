package com.MyRecipies.recipies.entities;

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
    private Double quantity;
    
    public RecipeItem() {
    }

    public RecipeItem(Long id, Recipe recipe, Product subProduct, Ingredient ingredient, Double quantity) {
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

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Double getUnitCost() {
    if (ingredient != null) {
        return ingredient.calculateUnitCost();
    }
    if (subProduct != null) {
        return subProduct.calculateUnitCost();
    }
    return 0.0;
}

public Double getTotalCost() {
    return getUnitCost() * quantity;
}

}
