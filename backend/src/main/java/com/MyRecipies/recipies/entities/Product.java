package com.MyRecipies.recipies.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private BigDecimal price;

    private String imgUrl;

    @Column(name = "create_date", nullable = false)
    private LocalDate createDate;
    private LocalDateTime lastUpdateDate;

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL)
    private Recipe recipe;

    public Product() {
    }

    public Product(Long id, String name, BigDecimal price, String imgUrl, LocalDate createDate,
            LocalDateTime lastUpdateDate, Recipe recipe) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imgUrl = imgUrl;
        this.createDate = createDate;
        this.lastUpdateDate = lastUpdateDate;
        this.recipe = recipe;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public LocalDate getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDate createDate) {
        this.createDate = createDate;
    }

    public LocalDateTime getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(LocalDateTime lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    @PrePersist
    protected void onCreate() {
        this.createDate = LocalDate.now();
        this.lastUpdateDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.lastUpdateDate = LocalDateTime.now();
    }

    public BigDecimal calculateUnitCost() {
        if (this.recipe == null)
            return BigDecimal.ZERO; // produtos sem receita não têm custo calculado

        BigDecimal totalCost = this.recipe.calculateTotalCost();
        if (this.recipe.getAmount() == 0)
            return BigDecimal.ZERO;

        return totalCost.divide(BigDecimal.valueOf(this.recipe.getAmount()));
    }

}
