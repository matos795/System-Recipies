package com.MyRecipies.recipies.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.MyRecipies.recipies.entities.RecipeItemVersion;
import com.MyRecipies.recipies.entities.RecipeVersion;

public class RecipeVersionDTO {

    private Long id;
    private Integer versionNumber;
    private LocalDateTime createdAt;
    private String description;
    private Integer amount;
    private String productNameSnapshot;
    private BigDecimal productPriceSnapshot;
    private BigDecimal totalCost;
    private BigDecimal profit;
    private BigDecimal margin;

    private List<RecipeItemVersionDTO> items = new ArrayList<>();

    public RecipeVersionDTO() {
    }

    public RecipeVersionDTO(RecipeVersion entity) {
        id = entity.getId();
        versionNumber = entity.getVersionNumber();
        createdAt = entity.getCreatedAt();
        description = entity.getDescription();
        amount = entity.getAmount();
        productNameSnapshot = entity.getProductNameSnapshot();
        productPriceSnapshot = entity.getProductPriceSnapshot();

        if (entity.getItems() != null) {
            for (RecipeItemVersion item : entity.getItems()) {
                items.add(new RecipeItemVersionDTO(item));
            }
        }
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

    public List<RecipeItemVersionDTO> getItems() {
        return items;
    }

    public void setItems(List<RecipeItemVersionDTO> items) {
        this.items = items;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public BigDecimal getProfit() {
        return profit;
    }

    public void setProfit(BigDecimal profit) {
        this.profit = profit;
    }

    public BigDecimal getMargin() {
        return margin;
    }

    public void setMargin(BigDecimal margin) {
        this.margin = margin;
    }
}
