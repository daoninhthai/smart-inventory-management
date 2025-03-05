package com.daoninhthai.inventory.controller;

import com.daoninhthai.inventory.service.BarcodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/barcodes")
@RequiredArgsConstructor
public class BarcodeController {

    private final BarcodeService barcodeService;

    @GetMapping(value = "/product/{id}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> generateProductBarcode(@PathVariable Long id) {
        byte[] barcode = barcodeService.generateBarcodeForProduct(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=barcode-product-" + id + ".png")
                .contentType(MediaType.IMAGE_PNG)
                .body(barcode);
    }

    @GetMapping(value = "/qr/{data}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> generateQRCode(@PathVariable String data) {
        byte[] qrCode = barcodeService.generateQRCode(data);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=qrcode.png")
                .contentType(MediaType.IMAGE_PNG)
                .body(qrCode);
    }

    @PostMapping("/decode")
    public ResponseEntity<Map<String, String>> decodeBarcode(@RequestParam("file") MultipartFile file) throws IOException {
        String result = barcodeService.decodeBarcode(file.getBytes());
        return ResponseEntity.ok(Map.of("content", result));
    }
}
