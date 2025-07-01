package com.daoninhthai.inventory.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class InventoryMetricsService {

    private final MeterRegistry meterRegistry;
    private final AtomicLong stockLevelGaugeValue;
    private final Counter ordersCounter;
    private final Counter lowStockAlertsCounter;
    private final Timer apiLatencyTimer;

    public void recordStockChange(long newTotalStock) {
        stockLevelGaugeValue.set(newTotalStock);
    }

    public void recordOrderCreated() {
        ordersCounter.increment();
    }

    public void recordLowStockAlert() {
        lowStockAlertsCounter.increment();
    }

    public void recordApiLatency(Runnable operation) {
        apiLatencyTimer.record(operation);
    }

    public Timer.Sample startTimer() {
        return Timer.start(meterRegistry);
    }

    public void stopTimer(Timer.Sample sample) {
        sample.stop(apiLatencyTimer);
    }
}
