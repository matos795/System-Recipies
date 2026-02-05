package com.MyRecipies.recipies.tests;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicLong;

import com.MyRecipies.recipies.entities.Ingredient;
import com.MyRecipies.recipies.entities.User;
import com.MyRecipies.recipies.entities.enums.UnitType;

public class Factory {

    private static final AtomicLong counter = new AtomicLong();

    public static User createUser() {
        User client = new User();
        client.setName("John Doe");
        client.setEmail("john" + counter.incrementAndGet() + "@example.com");
        client.setPassword("password123");
        client.setPhone("123-456-7890");
        client.setBirthDate(LocalDate.of(1990, 1, 1));
        return client;
    }

    public static Ingredient createIngredient(User client) {
        Ingredient ingredient = new Ingredient();
        ingredient.setName("Sugar");
        ingredient.setBrand("Test Brand");
        ingredient.setPriceCost(10.0);
        ingredient.setQuantityPerUnit(1.0);
        ingredient.setUnit(UnitType.KILOGRAM);
        ingredient.setClient(client);
        return ingredient;
    }
}
