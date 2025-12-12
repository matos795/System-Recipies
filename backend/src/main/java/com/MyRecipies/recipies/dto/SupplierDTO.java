package com.MyRecipies.recipies.dto;

import java.util.ArrayList;
import java.util.List;

import com.MyRecipies.recipies.entities.Ingredient;
import com.MyRecipies.recipies.entities.Supplier;
import jakarta.validation.constraints.NotBlank;

public class SupplierDTO {

    private Long id;
    @NotBlank
    private String name;
    private String phone;
    private String email;
    private String address;

    private List<IngredientMinDTO> ingredients = new ArrayList<>();

    private ClientDTO client;
    
    public SupplierDTO() {
    }

    public SupplierDTO(Long id, String name, String phone, String email, String address, List<IngredientMinDTO> ingredients, ClientDTO client) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.ingredients = ingredients;
        this.client = client;
    }

    public SupplierDTO(Supplier entity) {
        id = entity.getId();
        name = entity.getName();
        phone = entity.getPhone();
        email = entity.getEmail();
        address = entity.getAddress();
        for (Ingredient ingredient : entity.getIngredients()) {
            ingredients.add(new IngredientMinDTO(ingredient));
        }
        if (entity.getClient() != null) {
            client = new ClientDTO(entity.getClient());
        } else {
            client = null;
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public List<IngredientMinDTO> getIngredients() {
        return ingredients;
    }

    public ClientDTO getClient() {
        return client;
    }
}
