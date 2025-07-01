package com.daoninhthai.inventory.config;

import org.springframework.boot.actuate.autoconfigure.endpoint.web.CorsEndpointProperties;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * Actuator configuration to expose Prometheus, health, info, and metrics endpoints.
 *
 * Endpoints are configured via application properties:
 *   management.endpoints.web.exposure.include=prometheus,health,info,metrics
 *   management.endpoint.health.show-details=when-authorized
 *   management.endpoint.prometheus.enabled=true
 *   management.metrics.export.prometheus.enabled=true
 */
@Configuration
@EnableConfigurationProperties({WebEndpointProperties.class, CorsEndpointProperties.class})
public class ActuatorConfig {

    // Actuator endpoint exposure is configured via application properties.
    // This configuration class enables web endpoint properties and
    // ensures that Prometheus, health, info, and metrics endpoints are available.
}
