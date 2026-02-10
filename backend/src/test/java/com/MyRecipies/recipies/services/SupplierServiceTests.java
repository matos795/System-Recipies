package com.MyRecipies.recipies.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;

import java.util.List;
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

import com.MyRecipies.recipies.dto.SupplierDTO;
import com.MyRecipies.recipies.entities.Supplier;
import com.MyRecipies.recipies.entities.User;
import com.MyRecipies.recipies.repositories.SupplierRepository;
import com.MyRecipies.recipies.services.exceptions.DatabaseException;
import com.MyRecipies.recipies.services.exceptions.ResourceNotFoundException;
import com.MyRecipies.recipies.tests.Factory;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
public class SupplierServiceTests {

    @InjectMocks
    private SupplierService supplierService;

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private AuthService authService;

    @Mock
    private UserService userService;

    private Long existingId;
    private Long nonExistingId;
    private Long dependentId;
    private Long clientId;
    private Long clientIdNoAuthorizated;

    private User user;
    private User userNoAuthorizated;
    private Supplier supplier;
    private SupplierDTO supplierDTO;

    @BeforeEach
    public void setUp() throws Exception {

        existingId = 1L;
        nonExistingId = 1000L;
        dependentId = 3L;
        clientId = 10L;
        clientIdNoAuthorizated = 11L;

        user = new User();
        user.setId(clientId);

        userNoAuthorizated = new User();
        userNoAuthorizated.setId(clientIdNoAuthorizated);

        supplier = new Supplier();
        supplier.setId(existingId);
        supplier.setClient(user);

        supplierDTO = new SupplierDTO(Factory.createSupplier(user));
    }

    @Test
    public void findByClientIdShouldReturnListOfSuppliersWhenClientIdExists() {

        Mockito.when(userService.authenticated()).thenReturn(user);
        Mockito.when(supplierRepository.findByClientId(clientId)).thenReturn(List.of(supplier));

        List<SupplierDTO> list = supplierService.findByClientId();

        Assertions.assertFalse(list.isEmpty());
        Assertions.assertEquals(clientId, list.get(0).getClient().getId());

        Mockito.verify(userService).authenticated();
        Mockito.verify(supplierRepository).findByClientId(clientId);
    }

    @Test
    public void findByIdShouldReturnSupplierDTOWhenIdExistsAndUserAuthorized() {

        Mockito.when(supplierRepository.findById(existingId)).thenReturn(Optional.of(supplier));
        Mockito.doNothing().when(authService).validateSelfOrAdmin(clientId);

        SupplierDTO dto = supplierService.findById(existingId);

        Assertions.assertNotNull(dto);
        Assertions.assertEquals(dto.getClient().getId(), clientId);

        Mockito.verify(supplierRepository).findById(existingId);
        Mockito.verify(authService).validateSelfOrAdmin(clientId);
    }

    @Test
    public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

        Mockito.when(supplierRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            supplierService.findById(nonExistingId);
        });

        Mockito.verify(supplierRepository).findById(nonExistingId);
        Mockito.verifyNoInteractions(authService);
    }

    @Test
    public void findByIdShouldThrowsExceptionWhenUserNotAuthorized() {
        
        supplier.setClient(userNoAuthorizated);

        Mockito.when(supplierRepository.findById(existingId)).thenReturn(Optional.of(supplier));
        Mockito.doThrow(RuntimeException.class).when(authService).validateSelfOrAdmin(supplier.getClient().getId());

        Assertions.assertThrows(RuntimeException.class, () -> {
            supplierService.findById(existingId);
        });

        Mockito.verify(supplierRepository).findById(existingId);
        Mockito.verify(authService).validateSelfOrAdmin(supplier.getClient().getId());
    }

    @Test
    public void insertShouldSaveSupplierAndReturnDTO() {

        Mockito.when(userService.authenticated()).thenReturn(user);

        ArgumentCaptor<Supplier> captor = ArgumentCaptor.forClass(Supplier.class);
        Mockito.when(supplierRepository.save(captor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        supplierService.insert(supplierDTO);

        Supplier savedSupplier = captor.getValue();

        Assertions.assertNotNull(savedSupplier);
        Assertions.assertEquals(clientId, savedSupplier.getClient().getId());

        Mockito.verify(userService).authenticated();
        Mockito.verify(supplierRepository).save(any());
    }

    @Test
    public void updateShouldReturnSupplierDTOWhenIdExistsAndAuthorized() {

        Mockito.when(supplierRepository.getReferenceById(existingId)).thenReturn(supplier);
        Mockito.doNothing().when(authService).validateSelfOrAdmin(supplier.getClient().getId());

        ArgumentCaptor<Supplier> captor = ArgumentCaptor.forClass(Supplier.class);
        Mockito.when(supplierRepository.save(captor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        supplierService.update(existingId, supplierDTO);

        Supplier updatedSupplier = captor.getValue();

        Assertions.assertNotNull(updatedSupplier);
        Assertions.assertEquals(clientId, updatedSupplier.getClient().getId());

        Mockito.verify(supplierRepository).getReferenceById(existingId);
        Mockito.verify(authService).validateSelfOrAdmin(supplier.getClient().getId());;
        Mockito.verify(supplierRepository).save(any());
    }

    @Test
    public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

        Mockito.when(supplierRepository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            supplierService.update(nonExistingId, supplierDTO);
        });

        Mockito.verify(supplierRepository).getReferenceById(nonExistingId);
        Mockito.verifyNoInteractions(authService);
        Mockito.verify(supplierRepository, never()).save(any());
    }

    @Test
    public void updateShouldThrowExceptionWhenUserNotAuthorized() {

        supplier.setClient(userNoAuthorizated);

        Mockito.when(supplierRepository.getReferenceById(existingId)).thenReturn(supplier);
        Mockito.doThrow(RuntimeException.class).when(authService).validateSelfOrAdmin(supplier.getClient().getId());

        Assertions.assertThrows(RuntimeException.class, () -> {
            supplierService.update(existingId, supplierDTO);
        });

        Mockito.verify(supplierRepository).getReferenceById(existingId);
        Mockito.verify(authService).validateSelfOrAdmin(supplier.getClient().getId());
        Mockito.verify(supplierRepository, never()).save(any());
    }

    @Test
    public void deleteShouldDoNothingWhenIdExistsAndUserAuthorized() {

        Mockito.when(supplierRepository.findById(existingId)).thenReturn(Optional.of(supplier));
        Mockito.doNothing().when(authService).validateSelfOrAdmin(clientId);
        Mockito.doNothing().when(supplierRepository).deleteById(existingId);

        Assertions.assertDoesNotThrow(() -> {
            supplierService.delete(existingId);
        });

        Mockito.verify(supplierRepository).findById(existingId);
        Mockito.verify(authService).validateSelfOrAdmin(clientId);
        Mockito.verify(supplierRepository).deleteById(existingId);
    }

    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

        Mockito.when(supplierRepository.findById(nonExistingId)).thenReturn(Optional.empty());
        
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            supplierService.delete(nonExistingId);
        });

        Mockito.verify(supplierRepository).findById(nonExistingId);
        Mockito.verifyNoInteractions(authService);
        Mockito.verify(supplierRepository, never()).deleteById(nonExistingId);
    }

    @Test
    public void deleteShouldThrowDataIntegrityViolationExceptionWhenDependentId() {

        Mockito.when(supplierRepository.findById(dependentId)).thenReturn(Optional.of(supplier));
        Mockito.doNothing().when(authService).validateSelfOrAdmin(clientId);
        Mockito.doThrow(DataIntegrityViolationException.class).when(supplierRepository).deleteById(dependentId);
        
        Assertions.assertThrows(DatabaseException.class, () -> {
            supplierService.delete(dependentId);
        });

        Mockito.verify(supplierRepository).findById(dependentId);
        Mockito.verify(authService).validateSelfOrAdmin(clientId);
        Mockito.verify(supplierRepository).deleteById(dependentId);
    }

    @Test
    public void deleteShouldThrowExceptionWhenUserNotAuthorized() {

        supplier.setClient(userNoAuthorizated);

        Mockito.when(supplierRepository.findById(existingId)).thenReturn(Optional.of(supplier));
        Mockito.doThrow(RuntimeException.class).when(authService).validateSelfOrAdmin(supplier.getClient().getId());

        Assertions.assertThrows(RuntimeException.class, () -> {
            supplierService.delete(existingId);
        });

        Mockito.verify(supplierRepository).findById(existingId);
        Mockito.verify(authService).validateSelfOrAdmin(supplier.getClient().getId());
        Mockito.verify(supplierRepository, never()).deleteById(existingId);
    }
}
