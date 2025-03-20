package com.daoninhthai.inventory.controller;

import com.daoninhthai.inventory.entity.AlertConfig;
import com.daoninhthai.inventory.service.AlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;

    @GetMapping("/config")
    public ResponseEntity<List<AlertConfig>> getAllConfigs() {
        return ResponseEntity.ok(alertService.getAllAlertConfigs());
    }

    @GetMapping("/config/{id}")
    public ResponseEntity<AlertConfig> getConfigById(@PathVariable Long id) {
        return ResponseEntity.ok(alertService.getAlertConfigById(id));
    }

    @GetMapping("/active")
    public ResponseEntity<List<AlertConfig>> getActiveAlerts() {
        return ResponseEntity.ok(alertService.getActiveAlerts());
    }

    @PostMapping("/config")
    public ResponseEntity<AlertConfig> createConfig(@RequestBody AlertConfig config) {
        return ResponseEntity.status(HttpStatus.CREATED).body(alertService.createAlertConfig(config));
    }

    @PutMapping("/config/{id}")
    public ResponseEntity<AlertConfig> updateConfig(@PathVariable Long id, @RequestBody AlertConfig config) {
        return ResponseEntity.ok(alertService.updateAlertConfig(id, config));
    }

    @DeleteMapping("/config/{id}")
    public ResponseEntity<Void> deleteConfig(@PathVariable Long id) {
        alertService.deleteAlertConfig(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/test/{configId}")
    public ResponseEntity<Map<String, String>> sendTestAlert(@PathVariable Long configId) {
        alertService.sendTestAlert(configId);
        return ResponseEntity.ok(Map.of("message", "Test alert sent successfully"));
    }
}
