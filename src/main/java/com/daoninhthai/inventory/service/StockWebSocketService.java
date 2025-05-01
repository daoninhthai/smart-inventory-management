package com.daoninhthai.inventory.service;

import com.daoninhthai.inventory.dto.StockUpdateEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockWebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public void broadcastStockUpdate(Long productId, String productSku,
                                     Long warehouseId, String warehouseCode,
                                     Integer oldQuantity, Integer newQuantity,
                                     String changeType) {
        StockUpdateEvent event = StockUpdateEvent.builder()
                .productId(productId)
                .productSku(productSku)
                .warehouseId(warehouseId)
                .warehouseCode(warehouseCode)
                .oldQuantity(oldQuantity)
                .newQuantity(newQuantity)
                .changeType(changeType)
                .timestamp(LocalDateTime.now())
                .build();

        messagingTemplate.convertAndSend("/topic/stock-updates", event);
        log.debug("Broadcast stock update: product={}, warehouse={}, {} -> {}",
                productSku, warehouseCode, oldQuantity, newQuantity);
    }

    public void broadcastAlert(String alertType, String message) {
        Map<String, Object> alert = Map.of(
                "type", alertType,
                "message", message,
                "timestamp", LocalDateTime.now().toString()
        );
        messagingTemplate.convertAndSend("/topic/alerts", alert);
        log.debug("Broadcast alert: type={}", alertType);
    }

    public void broadcastOrderStatus(String orderNumber, String oldStatus, String newStatus) {
        Map<String, Object> statusUpdate = Map.of(
                "orderNumber", orderNumber,
                "oldStatus", oldStatus,
                "newStatus", newStatus,
                "timestamp", LocalDateTime.now().toString()
        );
        messagingTemplate.convertAndSend("/topic/order-updates", statusUpdate);
        log.debug("Broadcast order status: order={}, {} -> {}", orderNumber, oldStatus, newStatus);
    }
}
