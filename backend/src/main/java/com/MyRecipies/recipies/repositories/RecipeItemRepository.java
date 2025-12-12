package com.MyRecipies.recipies.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.MyRecipies.recipies.entities.RecipeItem;

public interface RecipeItemRepository extends JpaRepository<RecipeItem, Long> {

}
