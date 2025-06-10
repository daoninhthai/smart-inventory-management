package com.daoninhthai.inventory.dto;

import com.daoninhthai.inventory.entity.TenantPlan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantResponse {

    private Long id;
    private String name;
    private String subdomain;
    private boolean active;
    private TenantPlan plan;
    private LocalDateTime createdAt;
}
