package com.daoninhthai.inventory.service;

import com.daoninhthai.inventory.entity.MovementType;
import com.daoninhthai.inventory.entity.Product;
import com.daoninhthai.inventory.entity.StockMovement;
import com.daoninhthai.inventory.entity.Warehouse;
import com.daoninhthai.inventory.repository.StockMovementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockMovementService {

    private final StockMovementRepository stockMovementRepository;

    @Transactional
    public StockMovement recordMovement(Product product, Warehouse warehouse,
                                         MovementType type, Integer quantity,
                                         String reference, String notes, String createdBy) {
        StockMovement movement = StockMovement.builder()
                .product(product)
                .warehouse(warehouse)
                .type(type)
                .quantity(quantity)
                .reference(reference)
                .notes(notes)
                .createdBy(createdBy)
                .build();

        StockMovement saved = stockMovementRepository.save(movement);
        log.info("Recorded {} movement: product={}, warehouse={}, qty={}",
                type, product.getSku(), warehouse.getCode(), quantity);
        return saved;
    }

    @Transactional(readOnly = true)
    public Page<StockMovement> getMovementsByProduct(Long productId, Pageable pageable) {
        return stockMovementRepository.findByProductId(productId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<StockMovement> getMovementsByWarehouse(Long warehouseId, Pageable pageable) {
        return stockMovementRepository.findByWarehouseId(warehouseId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<StockMovement> getMovementsByType(MovementType type, Pageable pageable) {
        return stockMovementRepository.findByType(type, pageable);
    }

    @Transactional(readOnly = true)
    public List<StockMovement> getMovementHistory(Long productId, LocalDateTime start, LocalDateTime end) {
        return stockMovementRepository.findByProductIdAndCreatedAtBetween(productId, start, end);
    }
}
