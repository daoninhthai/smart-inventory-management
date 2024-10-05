package com.daoninhthai.inventory.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceiveOrderRequest {

    @NotEmpty(message = "At least one item is required")
    @Valid
    private List<ReceiveItemRequest> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReceiveItemRequest {
        private Long productId;
        private Integer receivedQuantity;
    }
}
