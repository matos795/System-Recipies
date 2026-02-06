package com.MyRecipies.recipies.tests;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import com.MyRecipies.recipies.entities.Ingredient;
import com.MyRecipies.recipies.entities.Product;
import com.MyRecipies.recipies.entities.Recipe;
import com.MyRecipies.recipies.entities.RecipeItem;
import com.MyRecipies.recipies.entities.Supplier;
import com.MyRecipies.recipies.entities.User;
import com.MyRecipies.recipies.entities.enums.UnitType;

public class Factory {

    private static final AtomicLong counter = new AtomicLong();

    /* ===================== USER ===================== */

    public static User createUser() {
        User client = new User();
        client.setName("John Doe");
        client.setEmail("john" + counter.incrementAndGet() + "@example.com");
        client.setPassword("password123");
        client.setPhone("123-456-7890");
        client.setBirthDate(LocalDate.of(1990, 1, 1));
        return client;
    }

    /* ===================== INGREDIENT ===================== */

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

    /* ===================== PRODUCT ===================== */

    public static Product createProduct() {
        Product product = new Product();
        product.setName("Cake");
        product.setPrice(30.0);
        product.setCreateDate(LocalDate.now());
        product.setLastUpdateDate(LocalDateTime.now());
        return product;
    }

    /* ===================== RECIPE ===================== */

    public static Recipe createRecipe(User client) {
        Recipe recipe = new Recipe();
        recipe.setClient(client);
        recipe.setDescription("Test recipe");
        recipe.setAmount(10);
        recipe.setLastUpdateDate(LocalDateTime.now());

        // product 1–1
        Product product = createProduct();
        product.setRecipe(recipe);
        recipe.setProduct(product);

        recipe.setItems(new ArrayList<>());

        return recipe;
    }

    /* ===================== RECIPE WITH ITEMS ===================== */

    public static Recipe createRecipeWithIngredients(
            User client,
            List<Ingredient> ingredients
    ) {
        Recipe recipe = createRecipe(client);

        List<RecipeItem> items = new ArrayList<>();

        for (Ingredient ing : ingredients) {
            RecipeItem item = new RecipeItem();
            item.setRecipe(recipe);
            item.setIngredient(ing);
            item.setQuantity(1.0);
            items.add(item);
        }

        recipe.setItems(items);
        return recipe;
    }

    /* ===================== SUPPLIER ===================== */

    public static Supplier createSupplier(User client) {
        Supplier supplier = new Supplier();
        supplier.setName("Sugar");
        supplier.setAddress("123 Test Street");
        supplier.setPhone("123-456-7890");
        supplier.setEmail("supplier" + counter.incrementAndGet() + "@example.com");
        supplier.setClient(client);
        return supplier;
    }

    /* ===================== XXXXXXX ===================== */


}