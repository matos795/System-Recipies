package com.MyRecipies.recipies.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.MyRecipies.recipies.dto.SupplierDTO;
import com.MyRecipies.recipies.entities.Supplier;
import com.MyRecipies.recipies.repositories.SupplierRepository;
import com.MyRecipies.recipies.services.exceptions.DatabaseException;
import com.MyRecipies.recipies.services.exceptions.ResourceNotFoundException;

@Service
public class SupplierService {

    @Autowired
    private SupplierRepository repository;

    @Transactional(readOnly = true)
    public List<SupplierDTO> findAll(){
        List<Supplier> suppliers = repository.findAll();
        List<SupplierDTO> listDTO = suppliers.stream().map(x -> new SupplierDTO(x)).collect(Collectors.toList());
        return listDTO;
    }

    @Transactional(readOnly = true)
    public SupplierDTO findById(Long id){
        Supplier supplier = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Recurso não encontrado!"));
        return new SupplierDTO(supplier);
    }

    @Transactional
    public SupplierDTO insert(SupplierDTO dto){
        Supplier supplier = new Supplier();
        dtoToEntity(supplier, dto);
        supplier = repository.save(supplier);
        return new SupplierDTO(supplier);
    }

    @Transactional
    public SupplierDTO update(Long id, SupplierDTO dto){
        Supplier supplier = repository.getReferenceById(id);
        dtoToEntity(supplier, dto);
        supplier = repository.save(supplier);
        return new SupplierDTO(supplier);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
public void delete(Long id) {
	if (!repository.existsById(id)) {
		throw new ResourceNotFoundException("Recurso não encontrado");
	}
	try {
        	repository.deleteById(id);    		
	}
    	catch (DataIntegrityViolationException e) {
        	throw new DatabaseException("Falha de integridade referencial");
   	}
}

    private void dtoToEntity(Supplier entity, SupplierDTO dto){
        entity.setName(dto.getName());
        entity.setPhone(dto.getPhone());
        entity.setEmail(dto.getEmail());
        entity.setAddress(dto.getAddress());
    }
}
