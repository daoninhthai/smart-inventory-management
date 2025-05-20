package com.daoninhthai.inventory.service;

import com.daoninhthai.inventory.entity.AuditLog;
import com.daoninhthai.inventory.repository.AuditLogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class AuditService {

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public AuditLog log(String entityType, Long entityId, String action,
                        Object oldValue, Object newValue,
                        String userId, String ipAddress) {
        AuditLog auditLog = AuditLog.builder()
                .entityType(entityType)
                .entityId(entityId)
                .action(action)
                .oldValue(toJson(oldValue))
                .newValue(toJson(newValue))
                .userId(userId)
                .ipAddress(ipAddress)
                .build();

        AuditLog saved = auditLogRepository.save(auditLog);
        log.debug("Audit log created: entity={}/{}, action={}, user={}",
                entityType, entityId, action, userId);
        return saved;
    }

    @Transactional(readOnly = true)
    public List<AuditLog> getAuditTrailByEntity(String entityType, Long entityId) {
        return auditLogRepository.findByEntityTypeAndEntityIdOrderByTimestampDesc(entityType, entityId);
    }

    @Transactional(readOnly = true)
    public Page<AuditLog> getAuditTrailByUser(String userId, Pageable pageable) {
        return auditLogRepository.findByUserIdOrderByTimestampDesc(userId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<AuditLog> getAuditTrailByDateRange(LocalDateTime start, LocalDateTime end, Pageable pageable) {
        return auditLogRepository.findByDateRange(start, end, pageable);
    }

    @Transactional(readOnly = true)
    public Page<AuditLog> searchAuditLogs(String entityType, Long entityId, String userId, Pageable pageable) {
        return auditLogRepository.searchAuditLogs(entityType, entityId, userId, pageable);
    }

    private String toJson(Object obj) {
        if (obj == null) return null;
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.warn("Failed to serialize object to JSON for audit log", e);
            return obj.toString();
        }
    }
}
