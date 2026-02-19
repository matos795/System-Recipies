package com.MyRecipies.recipies.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class RecipeVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer versionNumber;
    private LocalDateTime createdAt;

    private String description;
    private Integer amount;

    private String productNameSnapshot;
    private Double productPriceSnapshot;

    @ManyToOne
    private Recipe recipe;

    @OneToMany(mappedBy = "version", cascade = CascadeType.ALL)
    private List<RecipeItemVersion> items = new ArrayList<>();

    public RecipeVersion() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(Integer versionNumber) {
        this.versionNumber = versionNumber;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getProductNameSnapshot() {
        return productNameSnapshot;
    }

    public void setProductNameSnapshot(String productNameSnapshot) {
        this.productNameSnapshot = productNameSnapshot;
    }

    public Double getProductPriceSnapshot() {
        return productPriceSnapshot;
    }

    public void setProductPriceSnapshot(Double productPriceSnapshot) {
        this.productPriceSnapshot = productPriceSnapshot;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public List<RecipeItemVersion> getItems() {
        return items;
    }

    public void setItems(List<RecipeItemVersion> items) {
        this.items = items;
    }

}

