package com.daoninhthai.inventory.service;

import com.daoninhthai.inventory.dto.DashboardSummary;
import com.daoninhthai.inventory.dto.ProductMovementSummary;
import com.daoninhthai.inventory.dto.StockValueReport;
import com.daoninhthai.inventory.entity.OrderStatus;
import com.daoninhthai.inventory.entity.Warehouse;
import com.daoninhthai.inventory.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final StockLevelRepository stockLevelRepository;
    private final StockMovementRepository stockMovementRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;

    @Transactional(readOnly = true)
    public DashboardSummary getSummary() {
        long totalProducts = productRepository.countActiveProducts();
        long totalWarehouses = warehouseRepository.findByActiveTrue().size();
        long lowStockCount = stockLevelRepository.countLowStockLevels();
        long pendingOrdersCount = purchaseOrderRepository.countByStatusIn(
                List.of(OrderStatus.SUBMITTED, OrderStatus.APPROVED));

        return DashboardSummary.builder()
                .totalProducts(totalProducts)
                .totalWarehouses(totalWarehouses)
                .lowStockCount(lowStockCount)
                .pendingOrdersCount(pendingOrdersCount)
                .build();
    }

    @Transactional(readOnly = true)
    public List<StockValueReport> getStockValueByWarehouse() {
        List<Warehouse> warehouses = warehouseRepository.findByActiveTrue();
        List<StockValueReport> reports = new ArrayList<>();

        for (Warehouse warehouse : warehouses) {
            Double value = stockLevelRepository.calculateStockValueByWarehouse(warehouse.getId());
            reports.add(StockValueReport.builder()
                    .warehouseId(warehouse.getId())
                    .warehouseName(warehouse.getName())
                    .warehouseCode(warehouse.getCode())
                    .totalValue(value != null ? BigDecimal.valueOf(value) : BigDecimal.ZERO)
                    .build());
        }

        return reports;
    }

    @Transactional(readOnly = true)
    public List<ProductMovementSummary> getTopMovingProducts(int limit) {
        LocalDateTime since = LocalDateTime.now().minusDays(30);
        List<Object[]> results = stockMovementRepository.findTopMovingProducts(
                since, PageRequest.of(0, limit));

        return results.stream()
                .map(row -> ProductMovementSummary.builder()
                        .productId((Long) row[0])
                        .productName((String) row[1])
                        .totalIn(((Number) row[2]).longValue())
                        .totalOut(0L)
                        .netChange(((Number) row[2]).longValue())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getStockTrends(int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        List<Object[]> results = stockMovementRepository.findStockTrends(since);

        Map<String, Map<String, Object>> trendMap = new LinkedHashMap<>();

        for (Object[] row : results) {
            String date = row[0].toString();
            String type = row[1].toString();
            Long quantity = ((Number) row[2]).longValue();

            trendMap.computeIfAbsent(date, k -> {
                Map<String, Object> entry = new LinkedHashMap<>();
                entry.put("date", date);
                entry.put("totalIn", 0L);
                entry.put("totalOut", 0L);
                return entry;
            });

            Map<String, Object> entry = trendMap.get(date);
            if ("IN".equals(type)) {
                entry.put("totalIn", quantity);
            } else if ("OUT".equals(type)) {
                entry.put("totalOut", quantity);
            }
        }

        return new ArrayList<>(trendMap.values());
    }
}
