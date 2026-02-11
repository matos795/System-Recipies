package com.MyRecipies.recipies.services;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;

import com.MyRecipies.recipies.dto.UserDTO;
import com.MyRecipies.recipies.dto.UserInsertDTO;
import com.MyRecipies.recipies.entities.Role;
import com.MyRecipies.recipies.entities.User;
import com.MyRecipies.recipies.projections.UserDetailsProjection;
import com.MyRecipies.recipies.repositories.RoleRepository;
import com.MyRecipies.recipies.repositories.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
	private PasswordEncoder passwordEncoder;

    @Mock
	private RoleRepository roleRepository;

    @Test
    public void loadUserByUsernameShouldReturnUserWhenUsernameExists() {

        String username = "test@email.com";

        UserDetailsProjection projection = Mockito.mock(UserDetailsProjection.class);

        Mockito.when(projection.getPassword()).thenReturn("encodedPassword");
        Mockito.when(projection.getRoleId()).thenReturn(1L);
        Mockito.when(projection.getAuthority()).thenReturn("ROLE_CLIENT");

        Mockito.when(userRepository.searchUserAndRolesByEmail(username))
            .thenReturn(List.of(projection));

        UserDetails userDetails = userService.loadUserByUsername(username);

        Assertions.assertNotNull(userDetails);
        Assertions.assertEquals(username, userDetails.getUsername());
        Assertions.assertEquals("encodedPassword", userDetails.getPassword());
        Assertions.assertTrue(
            userDetails.getAuthorities()
                    .stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_CLIENT"))
        );

        Mockito.verify(userRepository).searchUserAndRolesByEmail(username);
    }

    @Test
    public void loadUserByUsernameShouldThrowUsernameNotFoundExceptionWhenUsernameDoesNotExist() {

        String username = "notfound@email.com";

        Mockito.when(userRepository.searchUserAndRolesByEmail(username))
            .thenReturn(List.of());

        Assertions.assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername(username);
        });

        Mockito.verify(userRepository).searchUserAndRolesByEmail(username);
    }

    @Test
    public void insertShouldSaveUserAndReturnDTO() {

        UserInsertDTO dto = new UserInsertDTO();
        dto.setName("Maria");
        dto.setEmail("maria@email.com");
        dto.setPhone("999999999");
        dto.setPassword("123456");

        Role role = new Role(1L, "ROLE_CLIENT");

        Mockito.when(passwordEncoder.encode("123456")).thenReturn("encodedPassword");
        Mockito.when(roleRepository.findByAuthority("ROLE_CLIENT")).thenReturn(role);
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        Mockito.when(userRepository.save(captor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        UserDTO result = userService.insert(dto);
        User savedUser = captor.getValue();

        Assertions.assertNotNull(result);
        Assertions.assertEquals("Maria", savedUser.getName());
        Assertions.assertEquals("maria@email.com", savedUser.getEmail());
        Assertions.assertEquals("encodedPassword", savedUser.getPassword());
        Assertions.assertTrue(savedUser.getRoles().stream().anyMatch(r -> r.getAuthority().equals("ROLE_CLIENT")));

        Mockito.verify(passwordEncoder).encode("123456");
        Mockito.verify(roleRepository).findByAuthority("ROLE_CLIENT");
        Mockito.verify(userRepository).save(Mockito.any());
    }

    @Test
    public void authenticatedShouldReturnUserWhenEmailExists() {

        String email = "user@email.com";

        User user = new User();
        user.setEmail(email);

        Jwt jwt = Mockito.mock(Jwt.class);
        Mockito.when(jwt.getClaim("username")).thenReturn(email);

        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal()).thenReturn(jwt);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        User result = userService.authenticated();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(email, result.getEmail());

        Mockito.verify(userRepository).findByEmail(email);
    }

    @Test
    public void authenticatedShouldThrowExceptionWhenEmailNotFound() {

        String email = "notfound@email.com";

        Jwt jwt = Mockito.mock(Jwt.class);
        Mockito.when(jwt.getClaim("username")).thenReturn(email);

        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal()).thenReturn(jwt);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        Assertions.assertThrows(UsernameNotFoundException.class, () -> {
            userService.authenticated();
        });

        Mockito.verify(userRepository).findByEmail(email);
    }

        @Test
    public void authenticatedShouldThrowExceptionWhenNoAuthenticationPresent() {

        SecurityContextHolder.clearContext();

        Assertions.assertThrows(UsernameNotFoundException.class, () -> {
            userService.authenticated();
        });
    }

    @Test
    public void getMeShouldReturnUserDTO() {

        User user = new User();
        user.setId(1L);
        user.setEmail("user@email.com");

        UserService spyService = Mockito.spy(userService);

        Mockito.doReturn(user).when(spyService).authenticated();

        UserDTO dto = spyService.getMe();

        Assertions.assertNotNull(dto);
        Assertions.assertEquals(user.getEmail(), dto.getEmail());

        Mockito.verify(spyService).authenticated();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }
}
