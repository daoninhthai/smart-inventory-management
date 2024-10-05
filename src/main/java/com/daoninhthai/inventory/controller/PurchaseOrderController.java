package com.daoninhthai.inventory.controller;

import com.daoninhthai.inventory.dto.CreatePurchaseOrderRequest;
import com.daoninhthai.inventory.dto.PurchaseOrderResponse;
import com.daoninhthai.inventory.dto.ReceiveOrderRequest;
import com.daoninhthai.inventory.entity.OrderStatus;
import com.daoninhthai.inventory.service.PurchaseOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/purchase-orders")
@RequiredArgsConstructor
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    @GetMapping
    public ResponseEntity<Page<PurchaseOrderResponse>> getAllOrders(
            @RequestParam(required = false) OrderStatus status,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        if (status != null) {
            return ResponseEntity.ok(purchaseOrderService.getOrdersByStatus(status, pageable));
        }
        return ResponseEntity.ok(purchaseOrderService.getAllOrders(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseOrderResponse> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(purchaseOrderService.getOrderById(id));
    }

    @PostMapping
    public ResponseEntity<PurchaseOrderResponse> createOrder(
            @Valid @RequestBody CreatePurchaseOrderRequest request) {
        PurchaseOrderResponse created = purchaseOrderService.createDraft(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<PurchaseOrderResponse> submitOrder(@PathVariable Long id) {
        return ResponseEntity.ok(purchaseOrderService.submitOrder(id));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<PurchaseOrderResponse> approveOrder(@PathVariable Long id) {
        return ResponseEntity.ok(purchaseOrderService.approveOrder(id));
    }

    @PostMapping("/{id}/receive")
    public ResponseEntity<PurchaseOrderResponse> receiveOrder(
            @PathVariable Long id,
            @Valid @RequestBody(required = false) ReceiveOrderRequest request) {
        return ResponseEntity.ok(purchaseOrderService.receiveOrder(id, request));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<PurchaseOrderResponse> cancelOrder(@PathVariable Long id) {
        return ResponseEntity.ok(purchaseOrderService.cancelOrder(id));
    }
}
