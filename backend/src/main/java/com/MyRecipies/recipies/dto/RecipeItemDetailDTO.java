package com.MyRecipies.recipies.dto;

import java.math.BigDecimal;

import com.MyRecipies.recipies.entities.Ingredient;
import com.MyRecipies.recipies.entities.Product;
import com.MyRecipies.recipies.entities.RecipeItem;
import com.MyRecipies.recipies.entities.enums.UnitType;

public class RecipeItemDetailDTO {

private Long id;

    // Dados do item (ingrediente OU subproduto)
    private String name;
    private String brand;
    private String supplierName; // só se for ingrediente
    private String imgUrl;
    private UnitType unit; // un, g, ml…

    // Dados de custo
    private BigDecimal unitCost;
    private BigDecimal quantity;
    private BigDecimal totalCost;

    public RecipeItemDetailDTO() {}

    public RecipeItemDetailDTO(RecipeItem item) {

        this.id = item.getId();
        this.quantity = item.getQuantity();
        this.unitCost = item.getUnitCost();
        this.totalCost = item.getTotalCost();

        if (item.getIngredient() != null) {
            populateFromIngredient(item);
        } else if (item.getSubProduct() != null) {
            populateFromSubProduct(item);
        }
    }

    private void populateFromIngredient(RecipeItem item) {
        Ingredient ing = item.getIngredient();
        this.name = ing.getName();
        this.brand = ing.getBrand();
        this.imgUrl = ing.getImgUrl();
        this.unit = ing.getUnit();
        this.supplierName = ing.getSupplier().getName();
    }

    private void populateFromSubProduct(RecipeItem item) {
        Product sub = item.getSubProduct();
        this.name = sub.getName();
        this.brand = null; // subprodutos geralmente não têm marca
        this.imgUrl = sub.getImgUrl();
        this.supplierName = null; // subprodutos não têm fornecedor direto
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBrand() {
        return brand;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public UnitType getUnit() {
        return unit;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public BigDecimal getUnitCost() {
        return unitCost;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }
    
    public Long getSubProductId(RecipeItemDTO item) {
        return item.getSubProductId();
    }

    public Long getIngredientId(RecipeItemDTO item) {
        return item.getSubProductId();
    }
}
