package com.daoninhthai.inventory.service;

import com.daoninhthai.inventory.dto.ForecastResponse;
import com.daoninhthai.inventory.dto.InsightResponse;
import com.daoninhthai.inventory.dto.ReorderSuggestionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiForecastService {

    private final WebClient aiServiceWebClient;

    private static final Duration TIMEOUT = Duration.ofSeconds(30);

    public ForecastResponse getDemandForecast(Map<String, Object> requestBody) {
        try {
            log.info("Requesting demand forecast from AI service");
            return aiServiceWebClient.post()
                    .uri("/api/forecast/demand")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(ForecastResponse.class)
                    .timeout(TIMEOUT)
                    .onErrorResume(WebClientResponseException.class, e -> {
                        log.error("AI service error for demand forecast: {} - {}",
                                e.getStatusCode(), e.getResponseBodyAsString());
                        return Mono.error(new RuntimeException(
                                "AI forecast service error: " + e.getMessage()));
                    })
                    .block();
        } catch (Exception e) {
            log.error("Failed to get demand forecast from AI service", e);
            throw new RuntimeException("Unable to reach AI forecast service: " + e.getMessage(), e);
        }
    }

    public ReorderSuggestionResponse getReorderSuggestion(Map<String, Object> requestBody) {
        try {
            log.info("Requesting reorder suggestion from AI service");
            return aiServiceWebClient.post()
                    .uri("/api/forecast/reorder-suggestion")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(ReorderSuggestionResponse.class)
                    .timeout(TIMEOUT)
                    .onErrorResume(WebClientResponseException.class, e -> {
                        log.error("AI service error for reorder suggestion: {} - {}",
                                e.getStatusCode(), e.getResponseBodyAsString());
                        return Mono.error(new RuntimeException(
                                "AI reorder service error: " + e.getMessage()));
                    })
                    .block();
        } catch (Exception e) {
            log.error("Failed to get reorder suggestion from AI service", e);
            throw new RuntimeException("Unable to reach AI reorder service: " + e.getMessage(), e);
        }
    }

    public InsightResponse getInsights(Map<String, Object> requestBody) {
        try {
            log.info("Requesting AI insights analysis");
            return aiServiceWebClient.post()
                    .uri("/api/insights/analyze")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(InsightResponse.class)
                    .timeout(TIMEOUT)
                    .onErrorResume(WebClientResponseException.class, e -> {
                        log.error("AI service error for insights: {} - {}",
                                e.getStatusCode(), e.getResponseBodyAsString());
                        return Mono.error(new RuntimeException(
                                "AI insights service error: " + e.getMessage()));
                    })
                    .block();
        } catch (Exception e) {
            log.error("Failed to get insights from AI service", e);
            throw new RuntimeException("Unable to reach AI insights service: " + e.getMessage(), e);
        }
    }
}
