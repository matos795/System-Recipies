package com.MyRecipies.recipies.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.MyRecipies.recipies.entities.Recipe;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

}
