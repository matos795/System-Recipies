package com.MyRecipies.recipies.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.MyRecipies.recipies.entities.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

}
