package com.daoninhthai.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReorderSuggestionResponse {

    private Long productId;
    private Integer reorderPoint;
    private Integer reorderQuantity;
    private Integer safetyStock;
    private Integer economicOrderQuantity;
    private Double estimatedAnnualSavings;
}
