package com.daoninhthai.inventory.controller;

import com.daoninhthai.inventory.entity.AuditLog;
import com.daoninhthai.inventory.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditService auditService;

    @GetMapping
    public ResponseEntity<Page<AuditLog>> getAuditLogs(
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) Long entityId,
            @RequestParam(required = false) String userId,
            Pageable pageable) {
        return ResponseEntity.ok(auditService.searchAuditLogs(entityType, entityId, userId, pageable));
    }

    @GetMapping("/entity/{entityType}/{entityId}")
    public ResponseEntity<List<AuditLog>> getEntityAuditTrail(
            @PathVariable String entityType,
            @PathVariable Long entityId) {
        return ResponseEntity.ok(auditService.getAuditTrailByEntity(entityType, entityId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<AuditLog>> getUserAuditTrail(
            @PathVariable String userId,
            Pageable pageable) {
        return ResponseEntity.ok(auditService.getAuditTrailByUser(userId, pageable));
    }
}
