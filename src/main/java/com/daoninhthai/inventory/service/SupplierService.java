package com.daoninhthai.inventory.service;

import com.daoninhthai.inventory.entity.Supplier;
import com.daoninhthai.inventory.exception.ResourceNotFoundException;
import com.daoninhthai.inventory.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SupplierService {

    private final SupplierRepository supplierRepository;

    @Transactional(readOnly = true)
    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findByActiveTrue();
    }

    @Transactional(readOnly = true)
    public Supplier getSupplierById(Long id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", id));
    }

    @Transactional(readOnly = true)
    public List<Supplier> searchSuppliers(String name) {
        return supplierRepository.findByNameContainingIgnoreCase(name);
    }

    @Transactional
    public Supplier createSupplier(Supplier supplier) {
        supplier.setActive(true);
        Supplier saved = supplierRepository.save(supplier);
        log.info("Created supplier: {} (ID: {})", saved.getName(), saved.getId());
        return saved;
    }

    @Transactional
    public Supplier updateSupplier(Long id, Supplier request) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", id));

        if (request.getName() != null) supplier.setName(request.getName());
        if (request.getContactName() != null) supplier.setContactName(request.getContactName());
        if (request.getEmail() != null) supplier.setEmail(request.getEmail());
        if (request.getPhone() != null) supplier.setPhone(request.getPhone());
        if (request.getAddress() != null) supplier.setAddress(request.getAddress());

        Supplier updated = supplierRepository.save(supplier);
        log.info("Updated supplier: {} (ID: {})", updated.getName(), updated.getId());
        return updated;
    }

    @Transactional
    public void deleteSupplier(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", id));
        supplier.setActive(false);
        supplierRepository.save(supplier);
        log.info("Soft deleted supplier: {} (ID: {})", supplier.getName(), supplier.getId());
    }
}
