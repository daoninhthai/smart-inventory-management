package com.daoninhthai.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

    private Long id;
    private String sku;
    private String name;
    private String description;
    private Long categoryId;
    private String categoryName;
    private String unit;
    private BigDecimal unitPrice;
    private Integer reorderPoint;
    private Integer reorderQuantity;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
