package com.daoninhthai.inventory.dto;

import com.daoninhthai.inventory.entity.TenantPlan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTenantRequest {

    private String name;
    private String subdomain;
    private TenantPlan plan;
}
