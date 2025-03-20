package com.daoninhthai.inventory.scheduler;

import com.daoninhthai.inventory.service.AlertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class AlertScheduler {

    private final AlertService alertService;

    @Scheduled(fixedRate = 1800000) // every 30 minutes
    public void checkLowStockAlerts() {
        log.info("Running scheduled low stock alert check");
        try {
            alertService.checkLowStockAlerts();
            log.info("Completed low stock alert check");
        } catch (Exception e) {
            log.error("Error during low stock alert check", e);
        }
    }
}
