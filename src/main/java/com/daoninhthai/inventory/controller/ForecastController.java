package com.daoninhthai.inventory.controller;

import com.daoninhthai.inventory.dto.ForecastResponse;
import com.daoninhthai.inventory.dto.InsightResponse;
import com.daoninhthai.inventory.dto.ReorderSuggestionResponse;
import com.daoninhthai.inventory.entity.Product;
import com.daoninhthai.inventory.entity.StockMovement;
import com.daoninhthai.inventory.exception.ResourceNotFoundException;
import com.daoninhthai.inventory.repository.ProductRepository;
import com.daoninhthai.inventory.repository.StockLevelRepository;
import com.daoninhthai.inventory.service.AiForecastService;
import com.daoninhthai.inventory.service.DashboardService;
import com.daoninhthai.inventory.service.StockMovementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/forecast")
@RequiredArgsConstructor
public class ForecastController {

    private final AiForecastService aiForecastService;
    private final StockMovementService stockMovementService;
    private final ProductRepository productRepository;
    private final StockLevelRepository stockLevelRepository;
    private final DashboardService dashboardService;

    @GetMapping("/demand/{productId}")
    public ResponseEntity<ForecastResponse> getDemandForecast(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "30") int periodsAhead) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        List<StockMovement> movements = stockMovementService.getMovementHistory(
                productId,
                LocalDateTime.now().minusDays(180),
                LocalDateTime.now()
        );

        List<Map<String, Object>> historicalData = movements.stream()
                .map(m -> {
                    Map<String, Object> point = new HashMap<>();
                    point.put("date", m.getCreatedAt().toLocalDate().toString());
                    point.put("quantity", m.getQuantity());
                    return point;
                })
                .collect(Collectors.toList());

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("product_id", productId);
        requestBody.put("historical_data", historicalData);
        requestBody.put("periods_ahead", periodsAhead);

        ForecastResponse response = aiForecastService.getDemandForecast(requestBody);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/reorder/{productId}")
    public ResponseEntity<ReorderSuggestionResponse> getReorderSuggestion(
            @PathVariable Long productId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        List<StockMovement> movements = stockMovementService.getMovementHistory(
                productId,
                LocalDateTime.now().minusDays(90),
                LocalDateTime.now()
        );

        double avgDailyDemand = movements.isEmpty() ? 0 :
                movements.stream().mapToInt(StockMovement::getQuantity).sum() / 90.0;

        double stdDev = 0;
        if (!movements.isEmpty()) {
            double mean = movements.stream().mapToInt(StockMovement::getQuantity).average().orElse(0);
            stdDev = Math.sqrt(movements.stream()
                    .mapToDouble(m -> Math.pow(m.getQuantity() - mean, 2))
                    .average().orElse(0));
        }

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("product_id", productId);
        requestBody.put("average_daily_demand", avgDailyDemand);
        requestBody.put("demand_std_dev", stdDev);
        requestBody.put("lead_time_days", 7);
        requestBody.put("ordering_cost", 50.0);
        requestBody.put("holding_cost_per_unit",
                product.getUnitPrice() != null ? product.getUnitPrice().doubleValue() * 0.2 : 1.0);
        requestBody.put("unit_price",
                product.getUnitPrice() != null ? product.getUnitPrice().doubleValue() : 10.0);
        requestBody.put("service_level", 0.95);

        ReorderSuggestionResponse response = aiForecastService.getReorderSuggestion(requestBody);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/insights")
    public ResponseEntity<InsightResponse> getInsights(@RequestBody(required = false) Map<String, Object> body) {
        Map<String, Object> stockData = new HashMap<>();

        var summary = dashboardService.getSummary();
        stockData.put("total_products", summary.getTotalProducts());
        stockData.put("total_warehouses", summary.getTotalWarehouses());
        stockData.put("low_stock_count", summary.getLowStockCount());
        stockData.put("pending_orders", summary.getPendingOrdersCount());
        stockData.put("stock_value", dashboardService.getStockValueByWarehouse());
        stockData.put("top_products", dashboardService.getTopMovingProducts(5));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("stock_data", stockData);

        if (body != null && body.containsKey("question")) {
            requestBody.put("question", body.get("question"));
        }

        InsightResponse response = aiForecastService.getInsights(requestBody);
        return ResponseEntity.ok(response);
    }
}
