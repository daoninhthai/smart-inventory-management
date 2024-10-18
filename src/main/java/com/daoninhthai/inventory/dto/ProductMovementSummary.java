package com.daoninhthai.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductMovementSummary {

    private Long productId;
    private String productName;
    private String sku;
    private Long totalIn;
    private Long totalOut;
    private Long netChange;
}
