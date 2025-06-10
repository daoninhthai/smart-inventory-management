package com.daoninhthai.inventory.controller;

import com.daoninhthai.inventory.entity.Tenant;
import com.daoninhthai.inventory.service.TenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/tenants")
@RequiredArgsConstructor
public class TenantController {

    private final TenantService tenantService;

    @GetMapping
    public ResponseEntity<List<Tenant>> getAllTenants() {
        return ResponseEntity.ok(tenantService.getAll());
    }

    @GetMapping("/active")
    public ResponseEntity<List<Tenant>> getActiveTenants() {
        return ResponseEntity.ok(tenantService.getAllActive());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tenant> getTenantById(@PathVariable Long id) {
        return ResponseEntity.ok(tenantService.getById(id));
    }

    @PostMapping
    public ResponseEntity<Tenant> createTenant(@RequestBody Tenant tenant) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tenantService.create(tenant));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tenant> updateTenant(@PathVariable Long id, @RequestBody Tenant tenant) {
        return ResponseEntity.ok(tenantService.update(id, tenant));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivateTenant(@PathVariable Long id) {
        tenantService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}
