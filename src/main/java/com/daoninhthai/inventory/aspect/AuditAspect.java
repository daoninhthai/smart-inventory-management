package com.daoninhthai.inventory.aspect;

import com.daoninhthai.inventory.annotation.Auditable;
import com.daoninhthai.inventory.service.AuditService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {

    private final AuditService auditService;

    @Around("@annotation(auditable)")
    public Object auditMethod(ProceedingJoinPoint joinPoint, Auditable auditable) throws Throwable {
        String entityType = auditable.entityType();
        String action = auditable.action().isEmpty() ? joinPoint.getSignature().getName() : auditable.action();

        Object[] args = joinPoint.getArgs();
        Long entityId = extractEntityId(args);

        String userId = getCurrentUserId();
        String ipAddress = getClientIpAddress();

        Object oldState = null;
        if (args.length > 0) {
            oldState = args[0];
        }

        Object result = joinPoint.proceed();

        try {
            auditService.log(entityType, entityId, action, oldState, result, userId, ipAddress);
        } catch (Exception e) {
            log.error("Failed to create audit log for {}.{}", entityType, action, e);
        }

        return result;
    }

    private Long extractEntityId(Object[] args) {
        for (Object arg : args) {
            if (arg instanceof Long) {
                return (Long) arg;
            }
        }
        return null;
    }

    private String getCurrentUserId() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated()) {
                return auth.getName();
            }
        } catch (Exception e) {
            log.debug("Could not get current user for audit log");
        }
        return "system";
    }

    private String getClientIpAddress() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String xForwardedFor = request.getHeader("X-Forwarded-For");
                if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
                    return xForwardedFor.split(",")[0].trim();
                }
                return request.getRemoteAddr();
            }
        } catch (Exception e) {
            log.debug("Could not get client IP for audit log");
        }
        return "unknown";
    }
}
