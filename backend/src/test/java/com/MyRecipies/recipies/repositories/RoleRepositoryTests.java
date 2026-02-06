package com.MyRecipies.recipies.repositories;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.MyRecipies.recipies.entities.Role;

@DataJpaTest
public class RoleRepositoryTests {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private Role roleClient;

    @BeforeEach
    public void setUp() throws Exception {
        roleClient = new Role();
        roleClient.setAuthority("NEW_ROLE");

        testEntityManager.persist(roleClient);
        testEntityManager.flush();
    }

    @Test
    public void findByAuthorityShouldReturnRoleWhenAuthorityExists() {
        Role role = roleRepository.findByAuthority("NEW_ROLE");

        Assertions.assertNotNull(role);
        Assertions.assertEquals("NEW_ROLE", role.getAuthority());
    }

    @Test
    public void findByAuthorityShouldReturnNullWhenAuthorityDoesNotExist() {
        Role role = roleRepository.findByAuthority("OLD_ROLE");

        Assertions.assertNull(role);
    }
}
