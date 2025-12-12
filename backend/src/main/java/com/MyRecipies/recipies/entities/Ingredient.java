package com.MyRecipies.recipies.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.MyRecipies.recipies.entities.enums.UnitType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "ingredients")
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    private String brand;

    @Column(name = "price_cost", nullable = false)
    private Double priceCost;

    private String imgUrl;

    @Column(name = "create_date", nullable = false)
    private LocalDate createDate;

    private LocalDateTime lastUpdateDate;

    @Column(name = "quantity_per_unit", nullable = false)
    private Double quantityPerUnit;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UnitType unit;

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private User client;

    public Ingredient() {
    }

    public Ingredient(Long id, String name, String brand, Double priceCost, String imgUrl, LocalDate createDate,
            LocalDateTime lastUpdateDate, Double quantityPerUnit, UnitType unit, Supplier supplier, User client) {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.priceCost = priceCost;
        this.imgUrl = imgUrl;
        this.createDate = createDate;
        this.lastUpdateDate = lastUpdateDate;
        this.quantityPerUnit = quantityPerUnit;
        this.unit = unit;
        this.supplier = supplier;
        this.client = client;
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

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Double getPriceCost() {
        return priceCost;
    }

    public void setPriceCost(Double priceCost) {
        this.priceCost = priceCost;
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

    public Double getQuantityPerUnit() {
        return quantityPerUnit;
    }

    public void setQuantityPerUnit(Double quantityPerUnit) {
        this.quantityPerUnit = quantityPerUnit;
    }

    public UnitType getUnit() {
        return unit;
    }

    public void setUnit(UnitType unit) {
        this.unit = unit;
    }

    public Double getUnitCost() {
        return calculateUnitCost();
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public Double calculateUnitCost() {
    if (priceCost != null && quantityPerUnit != null && quantityPerUnit > 0) {
        return priceCost / quantityPerUnit;
    }
    return null;
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

public User getClient() {
    return client;
}

public void setClient(User client) {
    this.client = client;
}

}
