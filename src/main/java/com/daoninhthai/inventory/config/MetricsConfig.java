package com.daoninhthai.inventory.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.atomic.AtomicLong;

@Configuration
public class MetricsConfig {

    private final AtomicLong currentStockLevel = new AtomicLong(0);

    @Bean
    public AtomicLong stockLevelGaugeValue(MeterRegistry registry) {
        Gauge.builder("inventory_stock_level", currentStockLevel, AtomicLong::get)
                .description("Current total stock level across all products")
                .register(registry);
        return currentStockLevel;
    }

    @Bean
    public Counter ordersCounter(MeterRegistry registry) {
        return Counter.builder("inventory_orders_total")
                .description("Total number of orders created")
                .register(registry);
    }

    @Bean
    public Counter lowStockAlertsCounter(MeterRegistry registry) {
        return Counter.builder("inventory_low_stock_alerts")
                .description("Total number of low stock alerts triggered")
                .register(registry);
    }

    @Bean
    public Timer apiLatencyTimer(MeterRegistry registry) {
        return Timer.builder("inventory_api_latency")
                .description("API request latency")
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(registry);
    }
}
