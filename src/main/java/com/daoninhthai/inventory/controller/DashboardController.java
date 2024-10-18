package com.daoninhthai.inventory.controller;

import com.daoninhthai.inventory.dto.DashboardSummary;
import com.daoninhthai.inventory.dto.ProductMovementSummary;
import com.daoninhthai.inventory.dto.StockValueReport;
import com.daoninhthai.inventory.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    public ResponseEntity<DashboardSummary> getSummary() {
        return ResponseEntity.ok(dashboardService.getSummary());
    }

    @GetMapping("/stock-value")
    public ResponseEntity<List<StockValueReport>> getStockValue() {
        return ResponseEntity.ok(dashboardService.getStockValueByWarehouse());
    }

    @GetMapping("/top-products")
    public ResponseEntity<List<ProductMovementSummary>> getTopProducts(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(dashboardService.getTopMovingProducts(limit));
    }

    @GetMapping("/trends")
    public ResponseEntity<List<Map<String, Object>>> getStockTrends(
            @RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(dashboardService.getStockTrends(days));
    }
}
