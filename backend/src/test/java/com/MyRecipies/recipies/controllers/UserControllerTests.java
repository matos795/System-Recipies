package com.MyRecipies.recipies.controllers;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.MyRecipies.recipies.config.SecurityConfig;
import com.MyRecipies.recipies.controller.UserController;
import com.MyRecipies.recipies.dto.UserDTO;
import com.MyRecipies.recipies.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = true)
@Import(SecurityConfig.class)
public class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Autowired
    private ObjectMapper objectMapper;

    private String jsonBody;
    private Long existingId;
    private UserDTO userDTO;

    @BeforeEach
    void setUp() throws Exception {

        existingId = 1L;
        
        userDTO = new UserDTO();
        userDTO.setId(existingId);

        jsonBody = objectMapper.writeValueAsString(userDTO);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void findAllShouldReturnListWhenUserHasAdminRole() throws Exception {

        Mockito.when(userService.findAll()).thenReturn(List.of(userDTO));

        mockMvc.perform(get("/users").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(existingId));

        Mockito.verify(userService).findAll();
    }

    @Test
    public void findAllShouldReturn401WhenNotAuthenticated() throws Exception {

        mockMvc.perform(get("/users")).andExpect(status().isUnauthorized());

        Mockito.verifyNoInteractions(userService);
    }

    @Test
    @WithMockUser(roles = "OTHER")
    public void findAllShouldReturn403WhenUserHasWrongRole() throws Exception {

        mockMvc.perform(get("/users")).andExpect(status().isForbidden());

        Mockito.verifyNoInteractions(userService);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void insertShouldReturnUserDTOCreatedWhenHasAdminRole() throws Exception {

        Mockito.when(userService.insert(any())).thenReturn(userDTO);

        mockMvc.perform(post("/users")
        .with(csrf())
        .content(jsonBody)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(existingId));

        Mockito.verify(userService).insert(any());
    }

    @Test
    public void insertShouldReturn401WhenNotAuthenticated() throws Exception { 

        mockMvc.perform(post("/users")
        .with(csrf())
        .content(jsonBody)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized());

        Mockito.verifyNoInteractions(userService);
    }

    @Test
    @WithMockUser(roles = "OTHER")
    public void insertShouldReturn403WhenUserHasWrongRole() throws Exception {

        mockMvc.perform(post("/users")
        .with(csrf())
        .content(jsonBody)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden());

        Mockito.verifyNoInteractions(userService);
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    public void getMeShouldReturn200WhenIdExists() throws Exception {

        Mockito.when(userService.getMe()).thenReturn(userDTO);

        mockMvc.perform(get("/users/me")
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(existingId));

        Mockito.verify(userService).getMe();
    }

    @Test
    public void getMeShouldReturn401WhenNotAuthenticated() throws Exception {

        mockMvc.perform(get("/users/me")).andExpect(status().isUnauthorized());

        Mockito.verifyNoInteractions(userService);
    }

    @Test
    @WithMockUser(roles = "OTHER")
    public void getMeShouldReturn403WhenUserHasWrongRole() throws Exception {

        mockMvc.perform(get("/users/me")).andExpect(status().isForbidden());

        Mockito.verifyNoInteractions(userService);
    }
}
