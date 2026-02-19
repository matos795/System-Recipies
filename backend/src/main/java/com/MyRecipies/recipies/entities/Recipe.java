package com.MyRecipies.recipies.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "recipes")
public class Recipe {

    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "product_id")
    private Product product;

    private LocalDateTime lastUpdateDate;
    private String description;
    private Integer amount;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private User client;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeItem> items = new ArrayList<>();

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeVersion> versions = new ArrayList<>();

    public Recipe() {
    }

    public Recipe(Product product, LocalDateTime lastUpdateDate, String description, Integer amount, User client) {
        this.product = product;
        this.lastUpdateDate = lastUpdateDate;
        this.description = description;
        this.amount = amount;
        this.client = client;
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

    public User getClient() {
        return client;
    }

    public void setClient(User client) {
        this.client = client;
    }

    public void addItem(RecipeItem item) {
    items.add(item);
    item.setRecipe(this);
}

public void removeItem(RecipeItem item) {
    items.remove(item);
    item.setRecipe(null);
}

@PrePersist
protected void onCreate() {
    this.lastUpdateDate = LocalDateTime.now(); 
}

@PreUpdate
protected void onUpdate() {
    this.lastUpdateDate = LocalDateTime.now();
}

public BigDecimal calculateTotalCost() {
    return items.stream()
            .map(RecipeItem::getTotalCost)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
}

public BigDecimal calculateUnitCost() {
    if (amount == 0) return BigDecimal.ZERO;
    return calculateTotalCost().divide(BigDecimal.valueOf(amount));
}

public BigDecimal calculateUnitProfit() {
    return product.getPrice().subtract(calculateUnitCost());
}

public BigDecimal calculateTotalProfit() {
    return calculateUnitProfit().multiply(BigDecimal.valueOf(amount));
}

public BigDecimal calculateProfitPercentage() {
    BigDecimal unitCost = calculateUnitCost();
    if (unitCost.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;

    return calculateUnitProfit().divide(unitCost).multiply(BigDecimal.valueOf(100));
}

    public List<RecipeVersion> getVersions() {
        return versions;
    }

    public void setVersions(List<RecipeVersion> versions) {
        this.versions = versions;
    }

}
