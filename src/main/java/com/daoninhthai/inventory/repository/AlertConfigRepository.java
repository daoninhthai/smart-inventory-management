package com.daoninhthai.inventory.repository;

import com.daoninhthai.inventory.entity.AlertConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertConfigRepository extends JpaRepository<AlertConfig, Long> {

    List<AlertConfig> findByEnabledTrue();

    List<AlertConfig> findByAlertTypeAndEnabledTrue(AlertConfig.AlertType alertType);

    List<AlertConfig> findByProductId(Long productId);

    List<AlertConfig> findByWarehouseId(Long warehouseId);
}
