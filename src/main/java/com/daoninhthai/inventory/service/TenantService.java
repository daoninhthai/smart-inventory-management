package com.daoninhthai.inventory.service;

import com.daoninhthai.inventory.config.TenantContext;
import com.daoninhthai.inventory.entity.Tenant;
import com.daoninhthai.inventory.exception.DuplicateResourceException;
import com.daoninhthai.inventory.exception.ResourceNotFoundException;
import com.daoninhthai.inventory.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TenantService {

    private final TenantRepository tenantRepository;

    @Transactional
    public Tenant create(Tenant tenant) {
        if (tenantRepository.existsBySubdomain(tenant.getSubdomain())) {
            throw new DuplicateResourceException("Tenant", "subdomain", tenant.getSubdomain());
        }

        if (tenant.getDbSchema() == null) {
            tenant.setDbSchema("tenant_" + tenant.getSubdomain().toLowerCase().replaceAll("[^a-z0-9]", "_"));
        }

        Tenant saved = tenantRepository.save(tenant);
        log.info("Created tenant: name={}, subdomain={}, plan={}",
                saved.getName(), saved.getSubdomain(), saved.getPlan());
        return saved;
    }

    @Transactional(readOnly = true)
    public Tenant getById(Long id) {
        return tenantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant", "id", id));
    }

    @Transactional(readOnly = true)
    public Tenant getBySubdomain(String subdomain) {
        return tenantRepository.findBySubdomain(subdomain)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant", "subdomain", subdomain));
    }

    @Transactional(readOnly = true)
    public List<Tenant> getAllActive() {
        return tenantRepository.findByActiveTrue();
    }

    @Transactional(readOnly = true)
    public List<Tenant> getAll() {
        return tenantRepository.findAll();
    }

    @Transactional
    public Tenant update(Long id, Tenant update) {
        Tenant existing = tenantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant", "id", id));

        if (update.getName() != null) existing.setName(update.getName());
        if (update.getActive() != null) existing.setActive(update.getActive());
        if (update.getPlan() != null) existing.setPlan(update.getPlan());

        Tenant saved = tenantRepository.save(existing);
        log.info("Updated tenant: id={}, name={}", saved.getId(), saved.getName());
        return saved;
    }

    @Transactional
    public void deactivate(Long id) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant", "id", id));
        tenant.setActive(false);
        tenantRepository.save(tenant);
        log.info("Deactivated tenant: id={}, name={}", tenant.getId(), tenant.getName());
    }

    public Tenant getCurrentTenant() {
        Tenant current = TenantContext.getCurrentTenant();
        if (current == null) {
            throw new IllegalStateException("No tenant context available");
        }
        return current;
    }
}
