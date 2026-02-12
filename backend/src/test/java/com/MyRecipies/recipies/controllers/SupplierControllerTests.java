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
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.MyRecipies.recipies.config.SecurityConfig;
import com.MyRecipies.recipies.controller.SupplierController;
import com.MyRecipies.recipies.dto.SupplierDTO;
import com.MyRecipies.recipies.services.SupplierService;
import com.MyRecipies.recipies.services.exceptions.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(SupplierController.class)
@AutoConfigureMockMvc(addFilters = true)
@Import(SecurityConfig.class)
public class SupplierControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SupplierService supplierService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Autowired
    private ObjectMapper objectMapper;

    private String jsonBody;
    private Long existingId;
    private Long nonExistingId;
    private SupplierDTO supplierDTO;

    @BeforeEach
    void setUp() throws Exception {

        existingId = 1L;
        nonExistingId = 1000L;
        
        supplierDTO = new SupplierDTO();
        supplierDTO.setId(existingId);

        jsonBody = objectMapper.writeValueAsString(supplierDTO);
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    public void findByClientIdShouldReturnListOfSuppliersAnd200() throws Exception {
        
        Mockito.when(supplierService.findByClientId()).thenReturn(List.of(supplierDTO));

        mockMvc.perform(get("/suppliers")
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(existingId));

        Mockito.verify(supplierService).findByClientId();
    }

    @Test
    public void findByClientIdShouldReturn401WhenNotAuthenticated() throws Exception {

        mockMvc.perform(get("/suppliers")).andExpect(status().isUnauthorized());

        Mockito.verifyNoInteractions(supplierService);
    }

    @Test
    @WithMockUser(roles = "OTHER")
    public void findByClientIdShouldReturn403WhenUserHasWrongRole() throws Exception {

        mockMvc.perform(get("/suppliers")).andExpect(status().isForbidden());

        Mockito.verifyNoInteractions(supplierService);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void findAllShouldReturnListWhenUserHasAdminRole() throws Exception {

        Mockito.when(supplierService.findAll()).thenReturn(List.of(supplierDTO));

        mockMvc.perform(get("/suppliers/all").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(existingId));

        Mockito.verify(supplierService).findAll();
    }

    @Test
    public void findAllShouldReturn401WhenNotAuthenticated() throws Exception {

        mockMvc.perform(get("/suppliers/all")).andExpect(status().isUnauthorized());

        Mockito.verifyNoInteractions(supplierService);
    }

    @Test
    @WithMockUser(roles = "OTHER")
    public void findAllShouldReturn403WhenUserHasWrongRole() throws Exception {

        mockMvc.perform(get("/suppliers/all")).andExpect(status().isForbidden());

        Mockito.verifyNoInteractions(supplierService);
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    public void findByIdShouldReturn200WhenIdExists() throws Exception {

        Mockito.when(supplierService.findById(existingId)).thenReturn(supplierDTO);

        mockMvc.perform(get("/suppliers/{id}", existingId)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(existingId));

        Mockito.verify(supplierService).findById(existingId);
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    public void findByIdShouldReturn404WhenIdDoesNotExist() throws Exception {

        Mockito.when(supplierService.findById(nonExistingId)).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(get("/suppliers/{id}", nonExistingId)).andExpect(status().isNotFound());

        Mockito.verify(supplierService).findById(nonExistingId);
    }

    @Test
    public void findByIdShouldReturn401WhenNotAuthenticated() throws Exception {

        mockMvc.perform(get("/suppliers/{id}", existingId)).andExpect(status().isUnauthorized());

        Mockito.verifyNoInteractions(supplierService);
    }

    @Test
    @WithMockUser(roles = "OTHER")
    public void findByIdShouldReturn403WhenUserHasWrongRole() throws Exception {

        mockMvc.perform(get("/suppliers/{id}", existingId)).andExpect(status().isForbidden());

        Mockito.verifyNoInteractions(supplierService);
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    public void insertShouldReturnSupplierDTOCreated() throws Exception {

        Mockito.when(supplierService.insert(any())).thenReturn(supplierDTO);

        mockMvc.perform(post("/suppliers")
        .with(csrf())
        .content(jsonBody)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(existingId));

        Mockito.verify(supplierService).insert(any());
    }

    @Test
    public void insertShouldReturn401WhenNotAuthenticated() throws Exception { 

        mockMvc.perform(post("/suppliers")
        .with(csrf())
        .content(jsonBody)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized());

        Mockito.verifyNoInteractions(supplierService);
    }

    @Test
    @WithMockUser(roles = "OTHER")
    public void insertShouldReturn403WhenUserHasWrongRole() throws Exception {

        mockMvc.perform(post("/suppliers")
        .with(csrf())
        .content(jsonBody)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden());

        Mockito.verifyNoInteractions(supplierService);
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    public void updateShouldReturn200WhenIdExists() throws Exception {

        Mockito.when(supplierService.update(eq(existingId), any())).thenReturn(supplierDTO);

        mockMvc.perform(put("/suppliers/{id}", existingId)
        .with(csrf())
        .content(jsonBody)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(existingId));

        Mockito.verify(supplierService).update(eq(existingId), any());
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    public void updateShouldReturn404WhenIdDoesNotExist() throws Exception {

        Mockito.when(supplierService.update(eq(nonExistingId), any())).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(put("/suppliers/{id}", nonExistingId)
        .with(csrf())
        .content(jsonBody)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());

        Mockito.verify(supplierService).update(eq(nonExistingId), any());
    }

    @Test
    public void updateShouldReturn401WhenNotAuthenticated() throws Exception {

        mockMvc.perform(put("/suppliers/{id}", existingId)
        .with(csrf())
        .content(jsonBody)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized());

        Mockito.verifyNoInteractions(supplierService);
    }

    @Test
    @WithMockUser(roles = "OTHER")
    public void updateShouldReturn403WhenUserHasWrongRole() throws Exception {

        mockMvc.perform(put("/suppliers/{id}", existingId)
        .with(csrf())
        .content(jsonBody)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden());

        Mockito.verifyNoInteractions(supplierService);
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    public void deleteShouldReturnNoContentWhenIdExists() throws Exception {

        Mockito.doNothing().when(supplierService).delete(existingId);

        mockMvc.perform(delete("/suppliers/{id}", existingId)
        .with(csrf()))
        .andExpect(status().isNoContent());

        Mockito.verify(supplierService).delete(existingId);
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    public void deleteShouldReturn404WhenIdDoesNotExist() throws Exception {

        Mockito.doThrow(ResourceNotFoundException.class).when(supplierService).delete(nonExistingId);

        mockMvc.perform(delete("/suppliers/{id}", nonExistingId)
        .with(csrf()))
        .andExpect(status().isNotFound());

        Mockito.verify(supplierService).delete(nonExistingId);
    }

    @Test
    public void deleteShouldReturn401WhenNotAuthenticated() throws Exception {

        mockMvc.perform(delete("/suppliers/{id}", existingId)
        .with(csrf()))
        .andExpect(status().isUnauthorized());

        Mockito.verifyNoInteractions(supplierService);
    }

    @Test
    @WithMockUser(roles = "OTHER")
    public void deleteShouldReturn403WhenUserHasWrongRole() throws Exception {

        mockMvc.perform(delete("/suppliers/{id}", existingId)
        .with(csrf()))
        .andExpect(status().isForbidden());

        Mockito.verifyNoInteractions(supplierService);
    }
}
