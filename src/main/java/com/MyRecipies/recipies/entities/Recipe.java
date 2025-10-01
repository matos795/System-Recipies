package com.MyRecipies.recipies.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "recipes")
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @MapsId
    private Product product;

    private LocalDateTime lastUpdateDate;
    private String description;
    private Integer amount;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeItem> items = new ArrayList<>();

    public Recipe() {
    }

    public Recipe(Product product, LocalDateTime lastUpdateDate, String description, Integer amount) {
        this.product = product;
        this.lastUpdateDate = lastUpdateDate;
        this.description = description;
        this.amount = amount;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public LocalDateTime getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(LocalDateTime lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
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

    public List<RecipeItem> getItems() {
        return items;
    }

    public void setItems(List<RecipeItem> items) {
        this.items = items;
        if (items != null) {
        for (RecipeItem item : items) {
            item.setRecipe(this);
        }
    }
    }


    public void addItem(RecipeItem item) {
    items.add(item);
    item.setRecipe(this);
}

public void removeItem(RecipeItem item) {
    items.remove(item);
    item.setRecipe(null);
}

}
