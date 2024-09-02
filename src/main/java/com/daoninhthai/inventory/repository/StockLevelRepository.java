package com.daoninhthai.inventory.repository;

import com.daoninhthai.inventory.entity.StockLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockLevelRepository extends JpaRepository<StockLevel, Long> {

    Optional<StockLevel> findByProductIdAndWarehouseId(Long productId, Long warehouseId);

    List<StockLevel> findByWarehouseId(Long warehouseId);

    List<StockLevel> findByProductId(Long productId);

    @Query("SELECT sl FROM StockLevel sl WHERE sl.quantity <= sl.minQuantity AND sl.minQuantity IS NOT NULL")
    List<StockLevel> findLowStockLevels();

    @Query("SELECT COUNT(sl) FROM StockLevel sl WHERE sl.quantity <= sl.minQuantity AND sl.minQuantity IS NOT NULL")
    long countLowStockLevels();

    @Query("SELECT SUM(sl.quantity * sl.product.unitPrice) FROM StockLevel sl WHERE sl.warehouse.id = :warehouseId")
    Double calculateStockValueByWarehouse(@Param("warehouseId") Long warehouseId);
}
