package com.MyRecipies.recipies.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.MyRecipies.recipies.entities.enums.VersionActionType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
    private BigDecimal productPriceSnapshot;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VersionActionType actionType;

    @ManyToOne
    @JoinColumn(name = "recipe_product_id", nullable = false)
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

    public BigDecimal getProductPriceSnapshot() {
        return productPriceSnapshot;
    }

    public void setProductPriceSnapshot(BigDecimal productPriceSnapshot) {
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

    public VersionActionType getActionType() {
        return actionType;
    }

    public void setActionType(VersionActionType actionType) {
        this.actionType = actionType;
    }

}
