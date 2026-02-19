package com.MyRecipies.recipies.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.MyRecipies.recipies.entities.RecipeVersion;

public interface RecipeVersionRepository extends JpaRepository<RecipeVersion, Long> {

    List<RecipeVersion> findByRecipeIdOrderByVersionNumberDesc(Long recipeId);

    Optional<RecipeVersion> findByIdAndRecipeId(Long versionId, Long recipeId);

}


