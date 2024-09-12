package com.daoninhthai.inventory.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductRequest {

    @Size(max = 255, message = "Product name must be at most 255 characters")
    private String name;

    private String description;

    private Long categoryId;

    @Size(max = 20, message = "Unit must be at most 20 characters")
    private String unit;

    @Positive(message = "Unit price must be positive")
    private BigDecimal unitPrice;

    @Positive(message = "Reorder point must be positive")
    private Integer reorderPoint;

    @Positive(message = "Reorder quantity must be positive")
    private Integer reorderQuantity;

    private Boolean active;
}
