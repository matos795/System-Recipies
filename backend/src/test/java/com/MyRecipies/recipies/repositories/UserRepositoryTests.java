package com.MyRecipies.recipies.repositories;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.MyRecipies.recipies.entities.Role;
import com.MyRecipies.recipies.entities.User;
import com.MyRecipies.recipies.projections.UserDetailsProjection;
import com.MyRecipies.recipies.tests.Factory;

@DataJpaTest
public class UserRepositoryTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private Role roleClient;
    private User clientWithRoles;
    private String existingEmail;
    private String nonExistingEmail;

    @BeforeEach
    public void setUp() throws Exception {

        roleClient = new Role();
        roleClient.setAuthority("NEW_ROLE");
        testEntityManager.persist(roleClient);

        clientWithRoles = Factory.createUser();
        clientWithRoles.addRole(roleClient);
        clientWithRoles = testEntityManager.persist(clientWithRoles);

        existingEmail = clientWithRoles.getEmail();
        nonExistingEmail = "nonexist@example.com";

        testEntityManager.flush();
    }

    @Test
    public void findByEmailShouldReturnUserWhenEmailExists() {
        Optional<User> user = userRepository.findByEmail(existingEmail);

        Assertions.assertTrue(user.isPresent());
        Assertions.assertEquals(user.get().getEmail(), existingEmail);
    }

    @Test
    public void findByEmailShouldReturnEmptyWhenEmailDoesNotExist() {
        Optional<User> user = userRepository.findByEmail(nonExistingEmail);

        Assertions.assertTrue(user.isEmpty());
    }

    @Test
    public void searchUserAndRolesByEmailShouldReturnUserWithRolesWhenEmailExists() {
        List<UserDetailsProjection> list = userRepository.searchUserAndRolesByEmail(existingEmail);

        Assertions.assertFalse(list.isEmpty());
        
        for (UserDetailsProjection user : list) {
            Assertions.assertEquals(user.getAuthority(), roleClient.getAuthority());
            Assertions.assertEquals(user.getUsername(), existingEmail);
            Assertions.assertNotNull(user.getPassword());
        }
    }

    @Test
    public void searchUserAndRolesByEmailShouldReturnEmptyListWhenEmailDoesNotExist() {

        List<UserDetailsProjection> list =
                userRepository.searchUserAndRolesByEmail(nonExistingEmail);

        Assertions.assertTrue(list.isEmpty());
    }
}