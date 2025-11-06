package com.MyRecipies.recipies.dto;

import com.MyRecipies.recipies.entities.Ingredient;

public class IngredientMinDTO {

    private Long id;
    private String name;
    private String brand;
    
    public IngredientMinDTO() {
    }

    public IngredientMinDTO(Long id, String name, String brand) {
        this.id = id;
        this.name = name;
        this.brand = brand;
    }

    public IngredientMinDTO(Ingredient entity) {
        id = entity.getId();
        name = entity.getName();
        brand = entity.getBrand();
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

}
