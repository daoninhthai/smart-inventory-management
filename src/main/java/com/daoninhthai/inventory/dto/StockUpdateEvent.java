package com.daoninhthai.inventory.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockUpdateEvent {

    private Long productId;
    private String productSku;
    private Long warehouseId;
    private String warehouseCode;
    private Integer oldQuantity;
    private Integer newQuantity;
    private String changeType;
    private LocalDateTime timestamp;
}
