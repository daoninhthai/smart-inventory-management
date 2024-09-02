package com.daoninhthai.inventory.repository;

import com.daoninhthai.inventory.entity.OrderStatus;
import com.daoninhthai.inventory.entity.PurchaseOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {

    Optional<PurchaseOrder> findByOrderNumber(String orderNumber);

    Page<PurchaseOrder> findByStatus(OrderStatus status, Pageable pageable);

    Page<PurchaseOrder> findBySupplierId(Long supplierId, Pageable pageable);

    @Query("SELECT COUNT(po) FROM PurchaseOrder po WHERE po.status IN :statuses")
    long countByStatusIn(@Param("statuses") java.util.List<OrderStatus> statuses);

    @Query("SELECT po FROM PurchaseOrder po LEFT JOIN FETCH po.items WHERE po.id = :id")
    Optional<PurchaseOrder> findByIdWithItems(@Param("id") Long id);
}
