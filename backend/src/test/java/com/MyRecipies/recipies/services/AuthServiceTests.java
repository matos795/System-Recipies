package com.MyRecipies.recipies.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.MyRecipies.recipies.entities.Role;
import com.MyRecipies.recipies.entities.User;
import com.MyRecipies.recipies.services.exceptions.ForbiddenException;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTests {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserService userService;

    private User user;
    private Long userId;

    @BeforeEach
    void setUp() {
        userId = 10L;

        user = new User();
        user.setId(userId);
    }

    @Test
    public void validateSelfOrAdminShouldDoNothingWhenUserIsOwner() {

        Mockito.when(userService.authenticated()).thenReturn(user);

        Assertions.assertDoesNotThrow(() -> {
            authService.validateSelfOrAdmin(userId);
        });

        Mockito.verify(userService).authenticated();
    }

    @Test
    public void validateSelfOrAdminShouldDoNothingWhenUserIsAdmin() {

        User admin = new User();
        admin.setId(99L);
        admin.addRole(new Role(1L, "ROLE_ADMIN"));

        Mockito.when(userService.authenticated()).thenReturn(admin);

        Assertions.assertDoesNotThrow(() -> {
            authService.validateSelfOrAdmin(userId);
        });

        Mockito.verify(userService).authenticated();
    }

    @Test
    public void validateSelfOrAdminShouldThrowForbiddenExceptionWhenUserIsNotOwnerNorAdmin() {

        User otherUser = new User();
        otherUser.setId(99L);

        Mockito.when(userService.authenticated()).thenReturn(otherUser);

        Assertions.assertThrows(ForbiddenException.class, () -> {
            authService.validateSelfOrAdmin(userId);
        });

        Mockito.verify(userService).authenticated();
    }
}
