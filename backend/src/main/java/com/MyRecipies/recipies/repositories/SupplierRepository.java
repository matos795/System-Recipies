package com.MyRecipies.recipies.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.MyRecipies.recipies.entities.Supplier;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    @Query("SELECT s FROM Supplier s WHERE s.client.id = :clientId")
List<Supplier> findByClientId(Long clientId);
}
