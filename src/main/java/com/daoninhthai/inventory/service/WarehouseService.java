package com.daoninhthai.inventory.service;

import com.daoninhthai.inventory.dto.StockLevelResponse;
import com.daoninhthai.inventory.dto.WarehouseResponse;
import com.daoninhthai.inventory.entity.Warehouse;
import com.daoninhthai.inventory.exception.DuplicateResourceException;
import com.daoninhthai.inventory.exception.ResourceNotFoundException;
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
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final StockLevelRepository stockLevelRepository;

    @Transactional(readOnly = true)
    public List<WarehouseResponse> getAllWarehouses() {
        return warehouseRepository.findByActiveTrue().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public WarehouseResponse getWarehouseById(Long id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "id", id));
        return toResponse(warehouse);
    }

    @Transactional
    public WarehouseResponse createWarehouse(WarehouseResponse request) {
        if (warehouseRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Warehouse", "code", request.getCode());
        }

        Warehouse warehouse = Warehouse.builder()
                .name(request.getName())
                .code(request.getCode())
                .address(request.getAddress())
                .capacity(request.getCapacity())
                .active(true)
                .build();

        Warehouse saved = warehouseRepository.save(warehouse);
        log.info("Created warehouse: {} ({})", saved.getName(), saved.getCode());
        return toResponse(saved);
    }

    @Transactional
    public WarehouseResponse updateWarehouse(Long id, WarehouseResponse request) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "id", id));

        if (request.getName() != null) warehouse.setName(request.getName());
        if (request.getAddress() != null) warehouse.setAddress(request.getAddress());
        if (request.getCapacity() != null) warehouse.setCapacity(request.getCapacity());

        Warehouse updated = warehouseRepository.save(warehouse);
        log.info("Updated warehouse: {} (ID: {})", updated.getName(), updated.getId());
        return toResponse(updated);
    }

    @Transactional
    public void deleteWarehouse(Long id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "id", id));
        warehouse.setActive(false);
        warehouseRepository.save(warehouse);
        log.info("Soft deleted warehouse: {} (ID: {})", warehouse.getName(), warehouse.getId());
    }

    @Transactional(readOnly = true)
    public List<StockLevelResponse> getStockLevels(Long warehouseId) {
        warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "id", warehouseId));

        return stockLevelRepository.findByWarehouseId(warehouseId).stream()
                .map(sl -> StockLevelResponse.builder()
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
                        .build())
                .collect(Collectors.toList());
    }

    private WarehouseResponse toResponse(Warehouse warehouse) {
        return WarehouseResponse.builder()
                .id(warehouse.getId())
                .name(warehouse.getName())
                .code(warehouse.getCode())
                .address(warehouse.getAddress())
                .capacity(warehouse.getCapacity())
                .active(warehouse.getActive())
                .build();
    }
}
