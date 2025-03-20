package com.daoninhthai.inventory.service;

import com.daoninhthai.inventory.entity.AlertConfig;
import com.daoninhthai.inventory.entity.StockLevel;
import com.daoninhthai.inventory.exception.ResourceNotFoundException;
import com.daoninhthai.inventory.repository.AlertConfigRepository;
import com.daoninhthai.inventory.repository.StockLevelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlertService {

    private final AlertConfigRepository alertConfigRepository;
    private final StockLevelRepository stockLevelRepository;
    private final AlertEmailService alertEmailService;

    @Transactional(readOnly = true)
    public List<AlertConfig> getAllAlertConfigs() {
        return alertConfigRepository.findAll();
    }

    @Transactional(readOnly = true)
    public AlertConfig getAlertConfigById(Long id) {
        return alertConfigRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AlertConfig", "id", id));
    }

    @Transactional(readOnly = true)
    public List<AlertConfig> getActiveAlerts() {
        return alertConfigRepository.findByEnabledTrue();
    }

    @Transactional
    public AlertConfig createAlertConfig(AlertConfig config) {
        AlertConfig saved = alertConfigRepository.save(config);
        log.info("Created alert config: type={}, productId={}", config.getAlertType(), config.getProductId());
        return saved;
    }

    @Transactional
    public AlertConfig updateAlertConfig(Long id, AlertConfig update) {
        AlertConfig existing = alertConfigRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AlertConfig", "id", id));

        existing.setProductId(update.getProductId());
        existing.setWarehouseId(update.getWarehouseId());
        existing.setAlertType(update.getAlertType());
        existing.setThreshold(update.getThreshold());
        existing.setEmailRecipients(update.getEmailRecipients());
        existing.setEnabled(update.getEnabled());

        return alertConfigRepository.save(existing);
    }

    @Transactional
    public void deleteAlertConfig(Long id) {
        if (!alertConfigRepository.existsById(id)) {
            throw new ResourceNotFoundException("AlertConfig", "id", id);
        }
        alertConfigRepository.deleteById(id);
        log.info("Deleted alert config: id={}", id);
    }

    public void checkLowStockAlerts() {
        List<AlertConfig> configs = alertConfigRepository.findByAlertTypeAndEnabledTrue(AlertConfig.AlertType.LOW_STOCK);
        log.info("Checking {} low stock alert configurations", configs.size());

        for (AlertConfig config : configs) {
            if (config.getProductId() != null && config.getWarehouseId() != null) {
                stockLevelRepository.findByProductIdAndWarehouseId(config.getProductId(), config.getWarehouseId())
                        .ifPresent(stockLevel -> evaluateLowStock(stockLevel, config));
            } else if (config.getProductId() != null) {
                List<StockLevel> levels = stockLevelRepository.findByProductId(config.getProductId());
                levels.forEach(sl -> evaluateLowStock(sl, config));
            }
        }
    }

    private void evaluateLowStock(StockLevel stockLevel, AlertConfig config) {
        if (stockLevel.getQuantity() <= config.getThreshold()) {
            String[] recipients = config.getEmailRecipients().split(",");
            alertEmailService.sendStockAlert(
                    recipients,
                    stockLevel.getProduct().getName(),
                    stockLevel.getProduct().getSku(),
                    stockLevel.getWarehouse().getName(),
                    stockLevel.getQuantity(),
                    config.getThreshold()
            );
            log.warn("Low stock alert triggered: product={}, warehouse={}, qty={}, threshold={}",
                    stockLevel.getProduct().getSku(),
                    stockLevel.getWarehouse().getCode(),
                    stockLevel.getQuantity(),
                    config.getThreshold());
        }
    }

    public void sendOrderStatusAlert(String orderNumber, String status) {
        List<AlertConfig> configs = alertConfigRepository.findByEnabledTrue();
        for (AlertConfig config : configs) {
            String[] recipients = config.getEmailRecipients().split(",");
            alertEmailService.sendOrderStatusAlert(recipients, orderNumber, status);
        }
    }

    public void sendExpiryAlert(String productName, String sku, String warehouseName, int daysUntilExpiry) {
        List<AlertConfig> configs = alertConfigRepository.findByAlertTypeAndEnabledTrue(AlertConfig.AlertType.EXPIRY);
        for (AlertConfig config : configs) {
            String[] recipients = config.getEmailRecipients().split(",");
            alertEmailService.sendExpiryAlert(recipients, productName, sku, warehouseName, daysUntilExpiry);
        }
    }

    public void sendTestAlert(Long configId) {
        AlertConfig config = alertConfigRepository.findById(configId)
                .orElseThrow(() -> new ResourceNotFoundException("AlertConfig", "id", configId));
        String[] recipients = config.getEmailRecipients().split(",");
        alertEmailService.sendStockAlert(recipients, "Test Product", "TEST-SKU", "Test Warehouse", 5, 10);
        log.info("Test alert sent for config: id={}", configId);
    }
}
