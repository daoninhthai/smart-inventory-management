package com.daoninhthai.inventory.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchImportResult {

    private int totalRows;
    private int imported;
    private int failed;

    @Builder.Default
    private List<ImportError> errors = new ArrayList<>();

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ImportError {
        private int row;
        private String message;
    }

    public void addError(int row, String message) {
        if (errors == null) {
            errors = new ArrayList<>();
        }
        errors.add(ImportError.builder().row(row).message(message).build());
    }
}
