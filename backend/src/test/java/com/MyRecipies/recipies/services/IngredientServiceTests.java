package com.MyRecipies.recipies.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.MyRecipies.recipies.dto.IngredientDTO;
import com.MyRecipies.recipies.entities.Ingredient;
import com.MyRecipies.recipies.entities.User;
import com.MyRecipies.recipies.repositories.IngredientRepository;
import com.MyRecipies.recipies.services.exceptions.DatabaseException;
import com.MyRecipies.recipies.services.exceptions.ResourceNotFoundException;
import com.MyRecipies.recipies.tests.Factory;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
public class IngredientServiceTests {

    @InjectMocks
    private IngredientService ingredientService;

    @Mock
    private IngredientRepository ingredientRepository;

    @Mock
    private AuthService authService;

    @Mock
    private UserService userService;

    private Long existingId;
    private Long nonExistingId;
    private Long dependentId;
    private Long clientId;
    private Long clientIdNoAuthorizated;

    private Pageable pageable;

    private User user;
    private User userNoAuthorizated;
    private Ingredient ingredient;
    private IngredientDTO ingredientDTO;

    @BeforeEach
    public void setUp() throws Exception {

        existingId = 1L;
        nonExistingId = 1000L;
        dependentId = 3L;
        clientId = 10L;
        clientIdNoAuthorizated = 11L;

        pageable = PageRequest.of(0, 10);

        user = new User();
        user.setId(clientId);

        userNoAuthorizated = new User();
        userNoAuthorizated.setId(clientIdNoAuthorizated);

        ingredient = new Ingredient();
        ingredient.setId(existingId);
        ingredient.setClient(user);

        ingredientDTO = new IngredientDTO(Factory.createIngredient(user));
    }

    @Test
    public void findByClientIdShouldReturnPagedIngredientsWhenClientIdExists() {

        Mockito.when(userService.authenticated()).thenReturn(user);
        Mockito.when(ingredientRepository.findByClientId(clientId, pageable)).thenReturn(Page.empty());

        Page<IngredientDTO> page = ingredientService.findByClientId(pageable);

        Assertions.assertTrue(page.isEmpty());

        Mockito.verify(userService).authenticated();
        Mockito.verify(ingredientRepository).findByClientId(clientId, pageable);
    }

    @Test
    public void findByIdShouldReturnIngredientDTOWhenIdExistsAndUserAuthorized() {

        Mockito.when(ingredientRepository.findById(existingId)).thenReturn(Optional.of(ingredient));
        Mockito.doNothing().when(authService).validateSelfOrAdmin(clientId);

        IngredientDTO dto = ingredientService.findById(existingId);

        Assertions.assertNotNull(dto);
        Assertions.assertEquals(dto.getClient().getId(), clientId);

        Mockito.verify(ingredientRepository).findById(existingId);
        Mockito.verify(authService).validateSelfOrAdmin(clientId);
    }

    @Test
    public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

        Mockito.when(ingredientRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            ingredientService.findById(nonExistingId);
        });

        Mockito.verify(ingredientRepository).findById(nonExistingId);
        Mockito.verifyNoInteractions(authService);
    }

    @Test
    public void findByIdShouldThrowsExceptionWhenUserNotAuthorized() {
        
        ingredient.setClient(userNoAuthorizated);

        Mockito.when(ingredientRepository.findById(existingId)).thenReturn(Optional.of(ingredient));
        Mockito.doThrow(RuntimeException.class).when(authService).validateSelfOrAdmin(ingredient.getClient().getId());

        Assertions.assertThrows(RuntimeException.class, () -> {
            ingredientService.findById(existingId);
        });

        Mockito.verify(ingredientRepository).findById(existingId);
        Mockito.verify(authService).validateSelfOrAdmin(ingredient.getClient().getId());
    }

    @Test
    public void insertShouldSaveIngredientAndReturnDTO() {

        Mockito.when(userService.authenticated()).thenReturn(user);

        ArgumentCaptor<Ingredient> captor = ArgumentCaptor.forClass(Ingredient.class);
        Mockito.when(ingredientRepository.save(captor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        ingredientService.insert(ingredientDTO);

        Ingredient savedIngredient = captor.getValue();

        Assertions.assertNotNull(savedIngredient);
        Assertions.assertEquals(clientId, savedIngredient.getClient().getId());

        Mockito.verify(userService).authenticated();
        Mockito.verify(ingredientRepository).save(any());
    }

    @Test
    public void updateShouldReturnIngredientDTOWhenIdExistsAndAuthorized() {

        Mockito.when(ingredientRepository.getReferenceById(existingId)).thenReturn(ingredient);
        Mockito.doNothing().when(authService).validateSelfOrAdmin(ingredient.getClient().getId());

        ArgumentCaptor<Ingredient> captor = ArgumentCaptor.forClass(Ingredient.class);
        Mockito.when(ingredientRepository.save(captor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        ingredientService.update(ingredientDTO, existingId);

        Ingredient updatedIngredient = captor.getValue();

        Assertions.assertNotNull(updatedIngredient);
        Assertions.assertEquals(clientId, updatedIngredient.getClient().getId());

        Mockito.verify(ingredientRepository).getReferenceById(existingId);
        Mockito.verify(authService).validateSelfOrAdmin(ingredient.getClient().getId());;
        Mockito.verify(ingredientRepository).save(any());
    }

    @Test
    public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

        Mockito.when(ingredientRepository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            ingredientService.update(ingredientDTO, nonExistingId);
        });

        Mockito.verify(ingredientRepository).getReferenceById(nonExistingId);
        Mockito.verifyNoInteractions(authService);
        Mockito.verify(ingredientRepository, never()).save(any());
    }

    @Test
    public void updateShouldThrowExceptionWhenUserNotAuthorized() {

        ingredient.setClient(userNoAuthorizated);

        Mockito.when(ingredientRepository.getReferenceById(existingId)).thenReturn(ingredient);
        Mockito.doThrow(RuntimeException.class).when(authService).validateSelfOrAdmin(ingredient.getClient().getId());

        Assertions.assertThrows(RuntimeException.class, () -> {
            ingredientService.update(ingredientDTO, existingId);
        });

        Mockito.verify(ingredientRepository).getReferenceById(existingId);
        Mockito.verify(authService).validateSelfOrAdmin(ingredient.getClient().getId());
        Mockito.verify(ingredientRepository, never()).save(any());
    }

    @Test
    public void deleteShouldDoNothingWhenIdExistsAndUserAuthorized() {

        Mockito.when(ingredientRepository.findById(existingId)).thenReturn(Optional.of(ingredient));
        Mockito.doNothing().when(authService).validateSelfOrAdmin(clientId);
        Mockito.doNothing().when(ingredientRepository).deleteById(existingId);

        Assertions.assertDoesNotThrow(() -> {
            ingredientService.delete(existingId);
        });

        Mockito.verify(ingredientRepository).findById(existingId);
        Mockito.verify(authService).validateSelfOrAdmin(clientId);
        Mockito.verify(ingredientRepository).deleteById(existingId);
    }

    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

        Mockito.when(ingredientRepository.findById(nonExistingId)).thenReturn(Optional.empty());
        
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            ingredientService.delete(nonExistingId);
        });

        Mockito.verify(ingredientRepository).findById(nonExistingId);
        Mockito.verifyNoInteractions(authService);
        Mockito.verify(ingredientRepository, never()).deleteById(nonExistingId);
    }

    @Test
    public void deleteShouldThrowDataIntegrityViolationExceptionWhenDependentId() {

        Mockito.when(ingredientRepository.findById(dependentId)).thenReturn(Optional.of(ingredient));
        Mockito.doNothing().when(authService).validateSelfOrAdmin(clientId);
        Mockito.doThrow(DataIntegrityViolationException.class).when(ingredientRepository).deleteById(dependentId);
        
        Assertions.assertThrows(DatabaseException.class, () -> {
            ingredientService.delete(dependentId);
        });

        Mockito.verify(ingredientRepository).findById(dependentId);
        Mockito.verify(authService).validateSelfOrAdmin(clientId);
        Mockito.verify(ingredientRepository).deleteById(dependentId);
    }

    @Test
    public void deleteShouldThrowExceptionWhenUserNotAuthorized() {

        ingredient.setClient(userNoAuthorizated);

        Mockito.when(ingredientRepository.findById(existingId)).thenReturn(Optional.of(ingredient));
        Mockito.doThrow(RuntimeException.class).when(authService).validateSelfOrAdmin(ingredient.getClient().getId());

        Assertions.assertThrows(RuntimeException.class, () -> {
            ingredientService.delete(existingId);
        });

        Mockito.verify(ingredientRepository).findById(existingId);
        Mockito.verify(authService).validateSelfOrAdmin(ingredient.getClient().getId());
        Mockito.verify(ingredientRepository, never()).deleteById(existingId);
    }
}
