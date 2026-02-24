package com.MyRecipies.recipies.tests;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import com.MyRecipies.recipies.entities.Ingredient;
import com.MyRecipies.recipies.entities.Product;
import com.MyRecipies.recipies.entities.Recipe;
import com.MyRecipies.recipies.entities.RecipeItem;
import com.MyRecipies.recipies.entities.RecipeItemVersion;
import com.MyRecipies.recipies.entities.RecipeVersion;
import com.MyRecipies.recipies.entities.Supplier;
import com.MyRecipies.recipies.entities.User;
import com.MyRecipies.recipies.entities.enums.UnitType;
import com.MyRecipies.recipies.entities.enums.VersionActionType;

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
        ingredient.setPriceCost(BigDecimal.valueOf(10.0));
        ingredient.setQuantityPerUnit(BigDecimal.valueOf(1.0));
        ingredient.setUnit(UnitType.KILOGRAM);
        ingredient.setClient(client);
        return ingredient;
    }

    /* ===================== PRODUCT ===================== */

    public static Product createProduct() {
        Product product = new Product();
        product.setName("Cake");
        product.setPrice(BigDecimal.valueOf(30.0));
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
            List<Ingredient> ingredients) {
        Recipe recipe = createRecipe(client);

        List<RecipeItem> items = new ArrayList<>();

        for (Ingredient ing : ingredients) {
            RecipeItem item = new RecipeItem();
            item.setRecipe(recipe);
            item.setIngredient(ing);
            item.setQuantity(BigDecimal.valueOf(1.0));
            item.calculateSnapshot();
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

    /* ===================== RECIPE VERSIONS ===================== */

    public static RecipeVersion createRecipeVersion(Recipe recipe, int number) {
        RecipeVersion version = new RecipeVersion();
        version.setRecipe(recipe);
        version.setVersionNumber(number);
        version.setCreatedAt(LocalDateTime.now());
        version.setDescription("Teste");
        version.setAmount(1);
        version.setProductNameSnapshot("Produto");
        version.setProductPriceSnapshot(BigDecimal.TEN);
        version.setActionType(VersionActionType.CREATE); // ou UPDATE
        return version;
    }

    /* ===================== RECIPE ITEMS VERSIONS ===================== */

    public static RecipeItemVersion createRecipeItemVersion(RecipeVersion version) {
    RecipeItemVersion item = new RecipeItemVersion();
    item.setVersion(version);
    item.setIngredientName("Ingrediente");
    item.setQuantity(BigDecimal.ONE);
    item.setUnitCostSnapshot(BigDecimal.ONE);
    item.setTotalCostSnapshot(BigDecimal.ONE);
    item.setUnit(UnitType.UNIT);
    return item;
}


}