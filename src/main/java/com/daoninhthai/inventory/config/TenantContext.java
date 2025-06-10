package com.daoninhthai.inventory.config;

import com.daoninhthai.inventory.entity.Tenant;

public class TenantContext {

    private static final ThreadLocal<Tenant> CURRENT_TENANT = new ThreadLocal<>();

    private TenantContext() {
        // utility class
    }

    public static void setCurrentTenant(Tenant tenant) {
        CURRENT_TENANT.set(tenant);
    }

    public static Tenant getCurrentTenant() {
        return CURRENT_TENANT.get();
    }

    public static Long getCurrentTenantId() {
        Tenant tenant = CURRENT_TENANT.get();
        return tenant != null ? tenant.getId() : null;
    }

    public static void clear() {
        CURRENT_TENANT.remove();
    }
}
