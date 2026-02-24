package com.MyRecipies.recipies.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

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
import com.MyRecipies.recipies.controller.RecipeController;
import com.MyRecipies.recipies.dto.RecipeDTO;
import com.MyRecipies.recipies.services.RecipeService;
import com.MyRecipies.recipies.services.exceptions.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RecipeController.class)
@AutoConfigureMockMvc(addFilters = true)
@Import(SecurityConfig.class)
public class RecipeControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RecipeService recipeService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Autowired
    private ObjectMapper objectMapper;

    private String jsonBody;
    private Long existingId;
    private Long nonExistingId;
    private PageImpl<RecipeDTO> page;
    private RecipeDTO recipeDTO;

    @BeforeEach
    void setUp() throws Exception {

        existingId = 1L;
        nonExistingId = 1000L;
        
        recipeDTO = new RecipeDTO();
        recipeDTO.setId(existingId);

        jsonBody = objectMapper.writeValueAsString(recipeDTO);

        page = new PageImpl<>(List.of(recipeDTO));
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    public void findByClientIdShouldReturnPageOfRecipesAnd200() throws Exception {
        
        Mockito.when(recipeService.findByClientId(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/recipes").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andExpect(jsonPath("$.content[0].id").value(existingId));

        Mockito.verify(recipeService).findByClientId(any(Pageable.class));
    }

    @Test
    public void findByClientIdShouldReturn401WhenNotAuthenticated() throws Exception {

        mockMvc.perform(get("/recipes")).andExpect(status().isUnauthorized());

        Mockito.verifyNoInteractions(recipeService);
    }

    @Test
    @WithMockUser(roles = "OTHER")
    public void findByClientIdShouldReturn403WhenUserHasWrongRole() throws Exception {

        mockMvc.perform(get("/recipes")).andExpect(status().isForbidden());

        Mockito.verifyNoInteractions(recipeService);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void findAllShouldReturnPageWhenUserHasAdminRole() throws Exception {

        Mockito.when(recipeService.findAll(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/recipes/all").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());

        Mockito.verify(recipeService).findAll(any(Pageable.class));
    }

    @Test
    public void findAllShouldReturn401WhenNotAuthenticated() throws Exception {

        mockMvc.perform(get("/recipes/all")).andExpect(status().isUnauthorized());

        Mockito.verifyNoInteractions(recipeService);
    }

    @Test
    @WithMockUser(roles = "OTHER")
    public void findAllShouldReturn403WhenUserHasWrongRole() throws Exception {

        mockMvc.perform(get("/recipes/all")).andExpect(status().isForbidden());

        Mockito.verifyNoInteractions(recipeService);
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    public void findByIdShouldReturn200WhenIdExists() throws Exception {

        Mockito.when(recipeService.findById(existingId)).thenReturn(recipeDTO);

        mockMvc.perform(get("/recipes/{id}", existingId).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(existingId));

        Mockito.verify(recipeService).findById(existingId);
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    public void findByIdShouldReturn404WhenIdDoesNotExist() throws Exception {

        Mockito.when(recipeService.findById(nonExistingId)).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(get("/recipes/{id}", nonExistingId)).andExpect(status().isNotFound());

        Mockito.verify(recipeService).findById(nonExistingId);
    }

    @Test
    public void findByIdShouldReturn401WhenNotAuthenticated() throws Exception {

        mockMvc.perform(get("/recipes/{id}", existingId)).andExpect(status().isUnauthorized());

        Mockito.verifyNoInteractions(recipeService);
    }

    @Test
    @WithMockUser(roles = "OTHER")
    public void findByIdShouldReturn403WhenUserHasWrongRole() throws Exception {

        mockMvc.perform(get("/recipes/{id}", existingId)).andExpect(status().isForbidden());

        Mockito.verifyNoInteractions(recipeService);
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    public void insertShouldReturnRecipeDTOCreated() throws Exception {

        Mockito.when(recipeService.insert(any())).thenReturn(recipeDTO);

        mockMvc.perform(post("/recipes")
        .with(csrf())
        .content(jsonBody)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(existingId));

        Mockito.verify(recipeService).insert(any());
    }

    @Test
    public void insertShouldReturn401WhenNotAuthenticated() throws Exception { 

        mockMvc.perform(post("/recipes")
        .with(csrf())
        .content(jsonBody)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized());

        Mockito.verifyNoInteractions(recipeService);
    }

    @Test
    @WithMockUser(roles = "OTHER")
    public void insertShouldReturn403WhenUserHasWrongRole() throws Exception {

        mockMvc.perform(post("/recipes")
        .content(jsonBody)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden());

        Mockito.verifyNoInteractions(recipeService);
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    public void updateShouldReturn200WhenIdExists() throws Exception {

        Mockito.when(recipeService.update(eq(existingId), any())).thenReturn(recipeDTO);

        mockMvc.perform(put("/recipes/{id}", existingId)
        .with(csrf())
        .content(jsonBody)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

        Mockito.verify(recipeService).update(eq(existingId), any());
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    public void updateShouldReturn404WhenIdDoesNotExist() throws Exception {

        Mockito.when(recipeService.update(eq(nonExistingId), any())).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(put("/recipes/{id}", nonExistingId)
        .with(csrf())
        .content(jsonBody)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());

        Mockito.verify(recipeService).update(eq(nonExistingId), any());
    }

    @Test
    public void updateShouldReturn401WhenNotAuthenticated() throws Exception {

        mockMvc.perform(put("/recipes/{id}", existingId)
        .with(csrf())
        .content(jsonBody)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized());

        Mockito.verifyNoInteractions(recipeService);
    }

    @Test
    @WithMockUser(roles = "OTHER")
    public void updateShouldReturn403WhenUserHasWrongRole() throws Exception {

        mockMvc.perform(put("/recipes/{id}", existingId)
        .with(csrf())
        .content(jsonBody)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden());

        Mockito.verifyNoInteractions(recipeService);
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    public void deleteShouldReturnNoContentWhenIdExists() throws Exception {

        Mockito.doNothing().when(recipeService).delete(existingId);

        mockMvc.perform(delete("/recipes/{id}", existingId)
        .with(csrf()))
        .andExpect(status().isNoContent());

        Mockito.verify(recipeService).delete(existingId);
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    public void deleteShouldReturn404WhenIdDoesNotExist() throws Exception {

        Mockito.doThrow(ResourceNotFoundException.class).when(recipeService).delete(nonExistingId);

        mockMvc.perform(delete("/recipes/{id}", nonExistingId)
        .with(csrf()))
        .andExpect(status().isNotFound());

        Mockito.verify(recipeService).delete(nonExistingId);
    }

    @Test
    public void deleteShouldReturn401WhenNotAuthenticated() throws Exception {

        mockMvc.perform(delete("/recipes/{id}", existingId)
        .with(csrf()))
        .andExpect(status().isUnauthorized());

        Mockito.verifyNoInteractions(recipeService);
    }

    @Test
    @WithMockUser(roles = "OTHER")
    public void deleteShouldReturn403WhenUserHasWrongRole() throws Exception {

        mockMvc.perform(delete("/recipes/{id}", existingId)
        .with(csrf()))
        .andExpect(status().isForbidden());

        Mockito.verifyNoInteractions(recipeService);
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    public void findVersionsShouldReturnListOfVersionsWhenIdExists() throws Exception {

        mockMvc.perform(get("/recipes/{id}/versions", existingId)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

        Mockito.verify(recipeService).findVersions(existingId);
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    public void findVersionsShouldReturn404WhenIdDoesNotExist() throws Exception {

        Mockito.when(recipeService.findVersions(nonExistingId)).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(get("/recipes/{id}/versions", nonExistingId)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());

        Mockito.verify(recipeService).findVersions(nonExistingId);
    }

    @Test
    public void findVersionsShouldReturn401WhenNotAuthenticated() throws Exception {

        mockMvc.perform(get("/recipes/{id}/versions", existingId)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized());

        Mockito.verifyNoInteractions(recipeService);
    }

    @Test
    @WithMockUser(roles = "OTHER")
    public void findVersionsShouldReturn403WhenUserHasWrongRole() throws Exception {

        mockMvc.perform(get("/recipes/{id}/versions", existingId)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden());

        Mockito.verifyNoInteractions(recipeService);
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    public void findVersionsShouldReturnEmptyListWhenRecipeHasNoVersions() throws Exception {
        Mockito.when(recipeService.findVersions(existingId)).thenReturn(List.of());

        mockMvc.perform(get("/recipes/{id}/versions", existingId)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isEmpty());

        Mockito.verify(recipeService).findVersions(existingId);
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    public void findVersionsByIdShouldReturnVersionWhenExists() throws Exception {

        Long versionId = 1L;

        mockMvc.perform(get("/recipes/{id}/versions/{versionId}", existingId, versionId)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

        Mockito.verify(recipeService).findVersionById(existingId, versionId);
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    public void findVersionsByIdShouldReturn404WhenVersionDoesNotExist() throws Exception {

        Long versionId = 1000L;

        Mockito.when(recipeService.findVersionById(existingId, versionId)).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(get("/recipes/{id}/versions/{versionId}", existingId, versionId)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());

        Mockito.verify(recipeService).findVersionById(existingId, versionId);
}

    @Test
    public void findVersionsByIdShouldReturn401WhenNotAuthenticated() throws Exception {

        Long versionId = 1L;

        mockMvc.perform(get("/recipes/{id}/versions/{versionId}", existingId, versionId)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized());

        Mockito.verifyNoInteractions(recipeService);
    }

    @Test
    @WithMockUser(roles = "OTHER")
    public void findVersionsByIdShouldReturn403WhenUserHasWrongRole() throws Exception {

        Long versionId = 1L;

        mockMvc.perform(get("/recipes/{id}/versions/{versionId}", existingId, versionId)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden());

        Mockito.verifyNoInteractions(recipeService);
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    public void restoreVersionShouldReturnRestoredVersionWhenVersionExists() throws Exception {

        Long versionId = 1L;

        mockMvc.perform(get("/recipes/{id}/versions/{versionId}/restore", existingId, versionId)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

        Mockito.verify(recipeService).restoreVersion(existingId, versionId);
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    public void restoreVersionShouldReturn404WhenVersionDoesNotExist() throws Exception {

        Long versionId = 1000L;

        Mockito.when(recipeService.restoreVersion(existingId, versionId)).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(get("/recipes/{id}/versions/{versionId}/restore", existingId, versionId)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());

        Mockito.verify(recipeService).restoreVersion(existingId, versionId);
    }

    @Test
    public void restoreVersionShouldReturn401WhenNotAuthenticated() throws Exception {

        Long versionId = 1L;

        mockMvc.perform(get("/recipes/{id}/versions/{versionId}/restore", existingId, versionId)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized());

        Mockito.verifyNoInteractions(recipeService);
    }

    @Test
    @WithMockUser(roles = "OTHER")
    public void restoreVersionShouldReturn403WhenUserHasWrongRole() throws Exception {

        Long versionId = 1L;

        mockMvc.perform(get("/recipes/{id}/versions/{versionId}/restore", existingId, versionId)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden());

        Mockito.verifyNoInteractions(recipeService);
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    public void refreshRecipePricesShouldReturnUpdatedRecipe() throws Exception {

        mockMvc.perform(put("/recipes/{id}/refresh-prices", existingId)
        .with(csrf())
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

        Mockito.verify(recipeService).refreshRecipePrices(existingId);
    }

    @Test
    public void refreshRecipePricesShouldReturn401WhenNotAuthenticated() throws Exception {

        mockMvc.perform(put("/recipes/{id}/refresh-prices", existingId)
        .with(csrf())
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized());

        Mockito.verifyNoInteractions(recipeService);
    }

    @Test
    @WithMockUser(roles = "OTHER")
    public void refreshRecipePricesShouldReturn403WhenUserHasWrongRole() throws Exception {

        mockMvc.perform(put("/recipes/{id}/refresh-prices", existingId)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden());

        Mockito.verifyNoInteractions(recipeService);
    }
}