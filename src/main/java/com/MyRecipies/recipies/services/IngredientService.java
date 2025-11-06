package com.MyRecipies.recipies.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.MyRecipies.recipies.dto.IngredientDTO;
import com.MyRecipies.recipies.entities.Ingredient;
import com.MyRecipies.recipies.entities.Supplier;
import com.MyRecipies.recipies.repositories.IngredientRepository;
import com.MyRecipies.recipies.repositories.SupplierRepository;
import com.MyRecipies.recipies.services.exceptions.DatabaseException;
import com.MyRecipies.recipies.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

@Service
public class IngredientService {

    @Autowired
    private IngredientRepository repository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @Transactional(readOnly = true)
    public Page<IngredientDTO> findAll(Pageable pageable){

        Long userId = userService.authenticated().getId();

        Page<Ingredient> ingredients = repository.findByClientId(userId, pageable);
        return ingredients.map(x -> new IngredientDTO(x));
    }

    @Transactional(readOnly = true)
        public IngredientDTO findById(Long id){
            Ingredient ingredient = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Recurso não encontrado!"));
            authService.validateSelfOrAdmin(ingredient.getClient().getId());
            return new IngredientDTO(ingredient);
        }

    @Transactional
    public IngredientDTO insert(IngredientDTO dto){
        Ingredient ingredient = new Ingredient();
        dtoToEntity(ingredient, dto);
        ingredient.setClient(userService.authenticated());
        ingredient = repository.save(ingredient);
        return new IngredientDTO(ingredient);
    }

    @Transactional
    public IngredientDTO update(IngredientDTO dto, Long id){
        try {
    Ingredient ingredient = repository.getReferenceById(id);
    authService.validateSelfOrAdmin(ingredient.getClient().getId());
    dtoToEntity(ingredient, dto);
    ingredient = repository.save(ingredient);
        return new IngredientDTO(ingredient);
    } catch (EntityNotFoundException e) {
        throw new ResourceNotFoundException("Ingrediente não encontrado! ID: " + id);
    }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
public void delete(Long id) {
	if (!repository.existsById(id)) {
		throw new ResourceNotFoundException("Recurso não encontrado");
	}
	try {
            authService.validateSelfOrAdmin(repository.getReferenceById(id).getClient().getId());
        	repository.deleteById(id);    		
	}
    	catch (DataIntegrityViolationException e) {
        	throw new DatabaseException("Falha de integridade referencial");
   	}
}

    private void dtoToEntity(Ingredient entity, IngredientDTO dto){
        entity.setName(dto.getName());
        entity.setBrand(dto.getBrand());
        entity.setPriceCost(dto.getPriceCost());
        entity.setImgUrl(dto.getImgUrl());
        entity.setQuantityPerUnit(dto.getQuantityPerUnit());
        entity.setUnit(dto.getUnit());
        
        if (dto.getSupplierId() != null) {
        Supplier supplier = supplierRepository.findById(dto.getSupplierId()).orElseThrow(() -> new ResourceNotFoundException("Recurso não encontrado!"));
        entity.setSupplier(supplier);
        } else entity.setSupplier(null);
    }
    }