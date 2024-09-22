package com.daoninhthai.inventory.controller;

import com.daoninhthai.inventory.dto.*;
import com.daoninhthai.inventory.service.StockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stock")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @GetMapping
    public ResponseEntity<List<StockLevelResponse>> getAllStockLevels() {
        return ResponseEntity.ok(stockService.getAllStockLevels());
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<StockLevelResponse>> getStockByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(stockService.getStockLevelsByProduct(productId));
    }

    @GetMapping("/product/{productId}/warehouse/{warehouseId}")
    public ResponseEntity<StockLevelResponse> getStockLevel(
            @PathVariable Long productId, @PathVariable Long warehouseId) {
        return ResponseEntity.ok(stockService.getStockLevel(productId, warehouseId));
    }

    @GetMapping("/alerts")
    public ResponseEntity<List<LowStockAlert>> getLowStockAlerts() {
        return ResponseEntity.ok(stockService.getLowStockAlerts());
    }

    @PostMapping("/adjust")
    public ResponseEntity<StockLevelResponse> adjustStock(
            @Valid @RequestBody StockAdjustmentRequest request) {
        return ResponseEntity.ok(stockService.adjustStock(request));
    }

    @PostMapping("/transfer")
    public ResponseEntity<Void> transferStock(
            @Valid @RequestBody StockTransferRequest request) {
        stockService.transferStock(request);
        return ResponseEntity.ok().build();
    }
}
