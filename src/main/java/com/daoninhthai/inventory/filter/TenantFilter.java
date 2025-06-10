package com.daoninhthai.inventory.filter;

import com.daoninhthai.inventory.config.TenantContext;
import com.daoninhthai.inventory.entity.Tenant;
import com.daoninhthai.inventory.repository.TenantRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
@Order(1)
@RequiredArgsConstructor
public class TenantFilter extends OncePerRequestFilter {

    private static final String TENANT_HEADER = "X-Tenant-ID";
    private static final String TENANT_SUBDOMAIN_HEADER = "X-Tenant-Subdomain";

    private final TenantRepository tenantRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain filterChain) throws ServletException, IOException {
        try {
            resolveTenant(request);
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }

    private void resolveTenant(HttpServletRequest request) {
        // Try header-based tenant resolution
        String tenantId = request.getHeader(TENANT_HEADER);
        if (tenantId != null && !tenantId.isEmpty()) {
            try {
                Long id = Long.parseLong(tenantId);
                Optional<Tenant> tenant = tenantRepository.findById(id);
                tenant.ifPresent(t -> {
                    if (t.getActive()) {
                        TenantContext.setCurrentTenant(t);
                        log.debug("Tenant resolved from header: id={}", t.getId());
                    }
                });
                return;
            } catch (NumberFormatException e) {
                log.warn("Invalid tenant ID in header: {}", tenantId);
            }
        }

        // Try subdomain header
        String subdomain = request.getHeader(TENANT_SUBDOMAIN_HEADER);
        if (subdomain != null && !subdomain.isEmpty()) {
            Optional<Tenant> tenant = tenantRepository.findBySubdomain(subdomain);
            tenant.ifPresent(t -> {
                if (t.getActive()) {
                    TenantContext.setCurrentTenant(t);
                    log.debug("Tenant resolved from subdomain header: {}", subdomain);
                }
            });
            return;
        }

        // Try to extract from Host header (subdomain-based routing)
        String host = request.getServerName();
        if (host != null && host.contains(".")) {
            String potentialSubdomain = host.split("\\.")[0];
            if (!potentialSubdomain.equals("www") && !potentialSubdomain.equals("api")) {
                Optional<Tenant> tenant = tenantRepository.findBySubdomain(potentialSubdomain);
                tenant.ifPresent(t -> {
                    if (t.getActive()) {
                        TenantContext.setCurrentTenant(t);
                        log.debug("Tenant resolved from host subdomain: {}", potentialSubdomain);
                    }
                });
            }
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/api/auth/") ||
               path.startsWith("/api/admin/tenants") ||
               path.startsWith("/ws") ||
               path.startsWith("/api-docs") ||
               path.startsWith("/swagger-ui");
    }
}
