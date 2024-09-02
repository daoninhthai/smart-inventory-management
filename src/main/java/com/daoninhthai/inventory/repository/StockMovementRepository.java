package com.daoninhthai.inventory.repository;

import com.daoninhthai.inventory.entity.MovementType;
import com.daoninhthai.inventory.entity.StockMovement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {

    Page<StockMovement> findByProductId(Long productId, Pageable pageable);

    Page<StockMovement> findByWarehouseId(Long warehouseId, Pageable pageable);

    List<StockMovement> findByProductIdAndCreatedAtBetween(
            Long productId, LocalDateTime start, LocalDateTime end);

    @Query("SELECT sm FROM StockMovement sm WHERE sm.type = :type ORDER BY sm.createdAt DESC")
    Page<StockMovement> findByType(@Param("type") MovementType type, Pageable pageable);

    @Query("SELECT sm.product.id, sm.product.name, SUM(sm.quantity) as totalQty " +
            "FROM StockMovement sm " +
            "WHERE sm.createdAt >= :since " +
            "GROUP BY sm.product.id, sm.product.name " +
            "ORDER BY totalQty DESC")
    List<Object[]> findTopMovingProducts(@Param("since") LocalDateTime since, Pageable pageable);

    @Query("SELECT FUNCTION('DATE', sm.createdAt), sm.type, SUM(sm.quantity) " +
            "FROM StockMovement sm " +
            "WHERE sm.createdAt >= :since " +
            "GROUP BY FUNCTION('DATE', sm.createdAt), sm.type " +
            "ORDER BY FUNCTION('DATE', sm.createdAt)")
    List<Object[]> findStockTrends(@Param("since") LocalDateTime since);
}
