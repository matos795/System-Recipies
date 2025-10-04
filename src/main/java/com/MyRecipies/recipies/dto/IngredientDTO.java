package com.MyRecipies.recipies.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.MyRecipies.recipies.entities.Ingredient;
import com.MyRecipies.recipies.entities.enums.UnitType;

public class IngredientDTO {

    private Long id;
    private String name;
    private String brand;
    private Double priceCost;
    private String imgUrl;
    private LocalDate createDate;
    private LocalDateTime lastUpdateDate;
    private Double quantityPerUnit;
    private UnitType unit;
    private Long supplierId;
    
    public IngredientDTO() {
    }

    public IngredientDTO(Long id, String name, String brand, Double priceCost, String imgUrl, LocalDate createDate,
            LocalDateTime lastUpdateDate, Double quantityPerUnit, UnitType unit, Long supplierId) {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.priceCost = priceCost;
        this.imgUrl = imgUrl;
        this.createDate = createDate;
        this.lastUpdateDate = lastUpdateDate;
        this.quantityPerUnit = quantityPerUnit;
        this.unit = unit;
        this.supplierId = supplierId;
    }

    public IngredientDTO(Ingredient entity) {
        id = entity.getId();
        name = entity.getName();
        brand = entity.getBrand();
        priceCost = entity.getPriceCost();
        imgUrl = entity.getImgUrl();
        createDate = entity.getCreateDate();
        lastUpdateDate = entity.getLastUpdateDate();
        quantityPerUnit = entity.getQuantityPerUnit();
        unit = entity.getUnit();
        supplierId = entity.getSupplier().getId();
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

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    
}
