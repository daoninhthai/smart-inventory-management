package com.daoninhthai.inventory.repository;

import com.daoninhthai.inventory.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findByEntityTypeAndEntityIdOrderByTimestampDesc(String entityType, Long entityId);

    Page<AuditLog> findByUserIdOrderByTimestampDesc(String userId, Pageable pageable);

    @Query("SELECT a FROM AuditLog a WHERE a.timestamp BETWEEN :start AND :end ORDER BY a.timestamp DESC")
    Page<AuditLog> findByDateRange(@Param("start") LocalDateTime start,
                                   @Param("end") LocalDateTime end,
                                   Pageable pageable);

    @Query("SELECT a FROM AuditLog a WHERE " +
            "(:entityType IS NULL OR a.entityType = :entityType) AND " +
            "(:entityId IS NULL OR a.entityId = :entityId) AND " +
            "(:userId IS NULL OR a.userId = :userId) " +
            "ORDER BY a.timestamp DESC")
    Page<AuditLog> searchAuditLogs(@Param("entityType") String entityType,
                                   @Param("entityId") Long entityId,
                                   @Param("userId") String userId,
                                   Pageable pageable);
}
