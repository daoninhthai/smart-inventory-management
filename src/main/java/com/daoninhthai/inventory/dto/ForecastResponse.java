package com.daoninhthai.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForecastResponse {

    private Long productId;
    private List<PredictionPoint> predictions;
    private Double modelAccuracy;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PredictionPoint {
        private String date;
        private Double predictedQuantity;
        private Double confidenceLower;
        private Double confidenceUpper;
    }
}
