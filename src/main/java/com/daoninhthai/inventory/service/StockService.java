package com.daoninhthai.inventory.service;

import com.daoninhthai.inventory.dto.*;
import com.daoninhthai.inventory.entity.*;
import com.daoninhthai.inventory.exception.ResourceNotFoundException;
import com.daoninhthai.inventory.repository.ProductRepository;
import com.daoninhthai.inventory.repository.StockLevelRepository;
import com.daoninhthai.inventory.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockService {

    private final StockLevelRepository stockLevelRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final StockMovementService stockMovementService;

    @Transactional(readOnly = true)
    public StockLevelResponse getStockLevel(Long productId, Long warehouseId) {
        StockLevel sl = stockLevelRepository.findByProductIdAndWarehouseId(productId, warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "StockLevel", "productId/warehouseId", productId + "/" + warehouseId));
        return toResponse(sl);
    }

    @Transactional(readOnly = true)
    public List<StockLevelResponse> getAllStockLevels() {
        return stockLevelRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<StockLevelResponse> getStockLevelsByProduct(Long productId) {
        return stockLevelRepository.findByProductId(productId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public StockLevelResponse adjustStock(StockAdjustmentRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", request.getProductId()));
        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "id", request.getWarehouseId()));

        StockLevel stockLevel = stockLevelRepository
                .findByProductIdAndWarehouseId(request.getProductId(), request.getWarehouseId())
                .orElseGet(() -> StockLevel.builder()
                        .product(product)
                        .warehouse(warehouse)
                        .quantity(0)
                        .build());

        switch (request.getType()) {
            case IN -> stockLevel.setQuantity(stockLevel.getQuantity() + request.getQuantity());
            case OUT -> {
                if (stockLevel.getQuantity() < request.getQuantity()) {
                    throw new IllegalStateException("Insufficient stock. Available: " + stockLevel.getQuantity());
                }
                stockLevel.setQuantity(stockLevel.getQuantity() - request.getQuantity());
            }
            case ADJUSTMENT -> stockLevel.setQuantity(request.getQuantity());
            default -> throw new IllegalStateException("Invalid movement type: " + request.getType());
        }

        StockLevel saved = stockLevelRepository.save(stockLevel);

        stockMovementService.recordMovement(product, warehouse, request.getType(),
                request.getQuantity(), null, request.getNotes(), null);

        log.info("Stock adjusted: product={}, warehouse={}, type={}, qty={}",
                product.getSku(), warehouse.getCode(), request.getType(), request.getQuantity());

        return toResponse(saved);
    }

    @Transactional
    public void transferStock(StockTransferRequest request) {
        if (request.getFromWarehouseId().equals(request.getToWarehouseId())) {
            throw new IllegalStateException("Source and destination warehouses must be different");
        }

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", request.getProductId()));
        Warehouse fromWarehouse = warehouseRepository.findById(request.getFromWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "id", request.getFromWarehouseId()));
        Warehouse toWarehouse = warehouseRepository.findById(request.getToWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "id", request.getToWarehouseId()));

        StockLevel sourceStock = stockLevelRepository
                .findByProductIdAndWarehouseId(request.getProductId(), request.getFromWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("StockLevel", "warehouse", request.getFromWarehouseId()));

        if (sourceStock.getQuantity() < request.getQuantity()) {
            throw new IllegalStateException("Insufficient stock at source warehouse. Available: " + sourceStock.getQuantity());
        }

        sourceStock.setQuantity(sourceStock.getQuantity() - request.getQuantity());
        stockLevelRepository.save(sourceStock);

        StockLevel destStock = stockLevelRepository
                .findByProductIdAndWarehouseId(request.getProductId(), request.getToWarehouseId())
                .orElseGet(() -> StockLevel.builder()
                        .product(product)
                        .warehouse(toWarehouse)
                        .quantity(0)
                        .build());

        destStock.setQuantity(destStock.getQuantity() + request.getQuantity());
        stockLevelRepository.save(destStock);

        String transferRef = "TRANSFER-" + System.currentTimeMillis();

        stockMovementService.recordMovement(product, fromWarehouse, MovementType.OUT,
                request.getQuantity(), transferRef,
                "Transfer to " + toWarehouse.getCode() + ": " + request.getNotes(), null);

        stockMovementService.recordMovement(product, toWarehouse, MovementType.IN,
                request.getQuantity(), transferRef,
                "Transfer from " + fromWarehouse.getCode() + ": " + request.getNotes(), null);

        log.info("Stock transferred: product={}, from={}, to={}, qty={}",
                product.getSku(), fromWarehouse.getCode(), toWarehouse.getCode(), request.getQuantity());
    }

    @Transactional(readOnly = true)
    public List<LowStockAlert> getLowStockAlerts() {
        return stockLevelRepository.findLowStockLevels().stream()
                .map(sl -> LowStockAlert.builder()
                        .productId(sl.getProduct().getId())
                        .productName(sl.getProduct().getName())
                        .sku(sl.getProduct().getSku())
                        .warehouseId(sl.getWarehouse().getId())
                        .warehouseName(sl.getWarehouse().getName())
                        .currentQuantity(sl.getQuantity())
                        .minQuantity(sl.getMinQuantity())
                        .build())
                .collect(Collectors.toList());
    }

    private StockLevelResponse toResponse(StockLevel sl) {
        return StockLevelResponse.builder()
                .id(sl.getId())
                .productId(sl.getProduct().getId())
                .productName(sl.getProduct().getName())
                .productSku(sl.getProduct().getSku())
                .warehouseId(sl.getWarehouse().getId())
                .warehouseName(sl.getWarehouse().getName())
                .quantity(sl.getQuantity())
                .minQuantity(sl.getMinQuantity())
                .maxQuantity(sl.getMaxQuantity())
                .lastUpdated(sl.getLastUpdated())
                .build();
    }
}
