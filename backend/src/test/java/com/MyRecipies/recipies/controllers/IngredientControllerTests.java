package com.MyRecipies.recipies.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.MyRecipies.recipies.config.SecurityConfig;
import com.MyRecipies.recipies.controller.IngredientController;
import com.MyRecipies.recipies.dto.IngredientDTO;
import com.MyRecipies.recipies.services.IngredientService;
import com.MyRecipies.recipies.services.exceptions.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(IngredientController.class)
@AutoConfigureMockMvc(addFilters = true)
@Import(SecurityConfig.class)
public class IngredientControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IngredientService ingredientService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Autowired
    private ObjectMapper objectMapper;

    private String jsonBody;
    private Long existingId;
    private Long nonExistingId;
    private PageImpl<IngredientDTO> page;
    private IngredientDTO ingredientDTO;

    @BeforeEach
    void setUp() throws Exception {

        existingId = 1L;
        nonExistingId = 1000L;
        
        ingredientDTO = new IngredientDTO();
        ingredientDTO.setId(existingId);

        jsonBody = objectMapper.writeValueAsString(ingredientDTO);

        page = new PageImpl<>(List.of(ingredientDTO));
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    public void findByClientIdShouldReturnPageOfIngredientsAnd200() throws Exception {
        
        Mockito.when(ingredientService.findByClientId(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/ingredients")
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].id").value(existingId));

        Mockito.verify(ingredientService).findByClientId(any(Pageable.class));
    }

    @Test
    public void findByClientIdShouldReturn401WhenNotAuthenticated() throws Exception {

        mockMvc.perform(get("/ingredients")).andExpect(status().isUnauthorized());

        Mockito.verifyNoInteractions(ingredientService);
    }

    @Test
    @WithMockUser(roles = "OTHER")
    public void findByClientIdShouldReturn403WhenUserHasWrongRole() throws Exception {

        mockMvc.perform(get("/ingredients")).andExpect(status().isForbidden());

        Mockito.verifyNoInteractions(ingredientService);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void findAllShouldReturnPageWhenUserHasAdminRole() throws Exception {

        Mockito.when(ingredientService.findAll(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/ingredients/all").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].id").value(existingId));

        Mockito.verify(ingredientService).findAll(any(Pageable.class));
    }

    @Test
    public void findAllShouldReturn401WhenNotAuthenticated() throws Exception {

        mockMvc.perform(get("/ingredients/all")).andExpect(status().isUnauthorized());

        Mockito.verifyNoInteractions(ingredientService);
    }

    @Test
    @WithMockUser(roles = "OTHER")
    public void findAllShouldReturn403WhenUserHasWrongRole() throws Exception {

        mockMvc.perform(get("/ingredients/all")).andExpect(status().isForbidden());

        Mockito.verifyNoInteractions(ingredientService);
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    public void findByIdShouldReturn200WhenIdExists() throws Exception {

        Mockito.when(ingredientService.findById(existingId)).thenReturn(ingredientDTO);

        mockMvc.perform(get("/ingredients/{id}", existingId)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(existingId));

        Mockito.verify(ingredientService).findById(existingId);
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    public void findByIdShouldReturn404WhenIdDoesNotExist() throws Exception {

        Mockito.when(ingredientService.findById(nonExistingId)).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(get("/ingredients/{id}", nonExistingId)).andExpect(status().isNotFound());

        Mockito.verify(ingredientService).findById(nonExistingId);
    }

    @Test
    public void findByIdShouldReturn401WhenNotAuthenticated() throws Exception {

        mockMvc.perform(get("/ingredients/{id}", existingId)).andExpect(status().isUnauthorized());

        Mockito.verifyNoInteractions(ingredientService);
    }

    @Test
    @WithMockUser(roles = "OTHER")
    public void findByIdShouldReturn403WhenUserHasWrongRole() throws Exception {

        mockMvc.perform(get("/ingredients/{id}", existingId)).andExpect(status().isForbidden());

        Mockito.verifyNoInteractions(ingredientService);
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    public void insertShouldReturnIngredientDTOCreated() throws Exception {

        Mockito.when(ingredientService.insert(any())).thenReturn(ingredientDTO);

        mockMvc.perform(post("/ingredients")
        .with(csrf())
        .content(jsonBody)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(existingId));

        Mockito.verify(ingredientService).insert(any());
    }

    @Test
    public void insertShouldReturn401WhenNotAuthenticated() throws Exception { 

        mockMvc.perform(post("/ingredients")
        .with(csrf())
        .content(jsonBody)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized());

        Mockito.verifyNoInteractions(ingredientService);
    }

    @Test
    @WithMockUser(roles = "OTHER")
    public void insertShouldReturn403WhenUserHasWrongRole() throws Exception {

        mockMvc.perform(post("/ingredients")
        .with(csrf())
        .content(jsonBody)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden());

        Mockito.verifyNoInteractions(ingredientService);
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    public void updateShouldReturn200WhenIdExists() throws Exception {

        Mockito.when(ingredientService.update(any(), eq(existingId))).thenReturn(ingredientDTO);

        mockMvc.perform(put("/ingredients/{id}", existingId)
        .with(csrf())
        .content(jsonBody)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(existingId));

        Mockito.verify(ingredientService).update(any(), eq(existingId));
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    public void updateShouldReturn404WhenIdDoesNotExist() throws Exception {

        Mockito.when(ingredientService.update(any(), eq(nonExistingId))).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(put("/ingredients/{id}", nonExistingId)
        .with(csrf())
        .content(jsonBody)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());

        Mockito.verify(ingredientService).update(any(), eq(nonExistingId));
    }

    @Test
    public void updateShouldReturn401WhenNotAuthenticated() throws Exception {

        mockMvc.perform(put("/ingredients/{id}", existingId)
        .with(csrf())
        .content(jsonBody)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized());

        Mockito.verifyNoInteractions(ingredientService);
    }

    @Test
    @WithMockUser(roles = "OTHER")
    public void updateShouldReturn403WhenUserHasWrongRole() throws Exception {

        mockMvc.perform(put("/ingredients/{id}", existingId)
        .with(csrf())
        .content(jsonBody)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden());

        Mockito.verifyNoInteractions(ingredientService);
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    public void deleteShouldReturnNoContentWhenIdExists() throws Exception {

        Mockito.doNothing().when(ingredientService).delete(existingId);

        mockMvc.perform(delete("/ingredients/{id}", existingId)
        .with(csrf()))
        .andExpect(status().isNoContent());

        Mockito.verify(ingredientService).delete(existingId);
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    public void deleteShouldReturn404WhenIdDoesNotExist() throws Exception {

        Mockito.doThrow(ResourceNotFoundException.class).when(ingredientService).delete(nonExistingId);

        mockMvc.perform(delete("/ingredients/{id}", nonExistingId)
        .with(csrf()))
        .andExpect(status().isNotFound());

        Mockito.verify(ingredientService).delete(nonExistingId);
    }

    @Test
    public void deleteShouldReturn401WhenNotAuthenticated() throws Exception {

        mockMvc.perform(delete("/ingredients/{id}", existingId)
        .with(csrf()))
        .andExpect(status().isUnauthorized());

        Mockito.verifyNoInteractions(ingredientService);
    }

    @Test
    @WithMockUser(roles = "OTHER")
    public void deleteShouldReturn403WhenUserHasWrongRole() throws Exception {

        mockMvc.perform(delete("/ingredients/{id}", existingId)
        .with(csrf()))
        .andExpect(status().isForbidden());

        Mockito.verifyNoInteractions(ingredientService);
    }
}
