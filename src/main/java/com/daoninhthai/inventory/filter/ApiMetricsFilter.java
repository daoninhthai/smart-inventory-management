package com.daoninhthai.inventory.filter;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
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

@Slf4j
@Component
@Order(2)
@RequiredArgsConstructor
public class ApiMetricsFilter extends OncePerRequestFilter {

    private final MeterRegistry meterRegistry;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain filterChain) throws ServletException, IOException {
        Timer.Sample sample = Timer.start(meterRegistry);

        try {
            filterChain.doFilter(request, response);
        } finally {
            String method = request.getMethod();
            String uri = normalizeUri(request.getRequestURI());
            int status = response.getStatus();

            Timer timer = Timer.builder("http_server_requests_duration")
                    .tag("method", method)
                    .tag("uri", uri)
                    .tag("status", String.valueOf(status))
                    .description("HTTP request duration")
                    .register(meterRegistry);
            sample.stop(timer);

            Counter.builder("http_server_requests_total")
                    .tag("method", method)
                    .tag("uri", uri)
                    .tag("status", String.valueOf(status))
                    .description("Total HTTP requests")
                    .register(meterRegistry)
                    .increment();
        }
    }

    private String normalizeUri(String uri) {
        // Replace numeric path segments with {id} for better metric grouping
        return uri.replaceAll("/\\d+", "/{id}");
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/actuator") ||
               path.startsWith("/swagger-ui") ||
               path.startsWith("/api-docs");
    }
}
