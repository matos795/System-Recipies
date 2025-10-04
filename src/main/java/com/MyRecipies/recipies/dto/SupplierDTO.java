package com.MyRecipies.recipies.dto;

import com.MyRecipies.recipies.entities.Supplier;

public class SupplierDTO {

    private Long id;
    private String name;
    private String phone;
    private String email;
    private String address;
    
    public SupplierDTO() {
    }

    public SupplierDTO(Long id, String name, String phone, String email, String address) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
    }

    public SupplierDTO(Supplier entity) {
        id = entity.getId();
        name = entity.getName();
        phone = entity.getPhone();
        email = entity.getEmail();
        address = entity.getAddress();
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

    
}
