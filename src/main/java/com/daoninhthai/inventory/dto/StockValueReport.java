package com.daoninhthai.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockValueReport {

    private Long warehouseId;
    private String warehouseName;
    private String warehouseCode;
    private BigDecimal totalValue;
}
