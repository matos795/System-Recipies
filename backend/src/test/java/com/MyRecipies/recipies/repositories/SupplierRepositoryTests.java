package com.MyRecipies.recipies.repositories;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.MyRecipies.recipies.entities.Supplier;
import com.MyRecipies.recipies.entities.User;
import com.MyRecipies.recipies.tests.Factory;

@DataJpaTest
public class SupplierRepositoryTests {

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private User clientWithSuppliers;
    private User clientWithoutSuppliers;

    private Supplier supplier1;
    private Supplier supplier2;

    @BeforeEach
    public void setUp() throws Exception {

        clientWithSuppliers = testEntityManager.persist(Factory.createUser());
        clientWithoutSuppliers = testEntityManager.persist(Factory.createUser());

        supplier1 = Factory.createSupplier(clientWithSuppliers);
        supplier2 = Factory.createSupplier(clientWithSuppliers);

        testEntityManager.persist(supplier1);
        testEntityManager.persist(supplier2);

        testEntityManager.flush();
    }

    @Test
    public void findByClientIdShouldReturnListWhenClientIdExists () {
        List<Supplier> list = supplierRepository.findByClientId(clientWithSuppliers.getId());

        Assertions.assertFalse(list.isEmpty());

        for (Supplier supplier : list) {
            Assertions.assertEquals(clientWithSuppliers.getId(), supplier.getClient().getId());
        }
    }

    @Test
    public void findByClientIdShouldReturnEmptyListWhenExistsWithoutSuppliers () {
        List<Supplier> list = supplierRepository.findByClientId(clientWithoutSuppliers.getId());

        Assertions.assertTrue(list.isEmpty());
    }
}
