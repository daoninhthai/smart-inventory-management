package com.daoninhthai.inventory.controller;

import com.daoninhthai.inventory.dto.BatchImportResult;
import com.daoninhthai.inventory.service.BatchExportService;
import com.daoninhthai.inventory.service.BatchImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/batch")
@RequiredArgsConstructor
public class BatchController {

    private final BatchImportService batchImportService;
    private final BatchExportService batchExportService;

    @PostMapping("/import/products")
    public ResponseEntity<BatchImportResult> importProducts(@RequestParam("file") MultipartFile file) {
        BatchImportResult result = batchImportService.importProductsFromCsv(file);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/import/stock")
    public ResponseEntity<BatchImportResult> importStock(@RequestParam("file") MultipartFile file) {
        BatchImportResult result = batchImportService.importStockFromCsv(file);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/export/products")
    public ResponseEntity<byte[]> exportProducts() {
        String csv = batchExportService.exportProductsToCsv();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=products-export.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv.getBytes());
    }

    @GetMapping("/export/stock")
    public ResponseEntity<byte[]> exportStock() {
        String csv = batchExportService.exportStockToCsv();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=stock-export.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv.getBytes());
    }
}
