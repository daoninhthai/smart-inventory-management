package com.daoninhthai.inventory.dto;

import com.daoninhthai.inventory.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderResponse {

    private Long id;
    private String orderNumber;
    private Long supplierId;
    private String supplierName;
    private Long warehouseId;
    private String warehouseName;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime receivedAt;
    private List<PurchaseOrderItemResponse> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PurchaseOrderItemResponse {
        private Long id;
        private Long productId;
        private String productName;
        private String productSku;
        private Integer quantity;
        private BigDecimal unitPrice;
        private Integer receivedQuantity;
    }
}
