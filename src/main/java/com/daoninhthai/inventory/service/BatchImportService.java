package com.daoninhthai.inventory.service;

import com.daoninhthai.inventory.dto.BatchImportResult;
import com.daoninhthai.inventory.entity.Product;
import com.daoninhthai.inventory.entity.StockLevel;
import com.daoninhthai.inventory.entity.Warehouse;
import com.daoninhthai.inventory.repository.ProductRepository;
import com.daoninhthai.inventory.repository.StockLevelRepository;
import com.daoninhthai.inventory.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BatchImportService {

    private final ProductRepository productRepository;
    private final StockLevelRepository stockLevelRepository;
    private final WarehouseRepository warehouseRepository;

    @Transactional
    public BatchImportResult importProductsFromCsv(MultipartFile file) {
        BatchImportResult result = BatchImportResult.builder()
                .totalRows(0).imported(0).failed(0).errors(new ArrayList<>()).build();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            String headerLine = reader.readLine(); // skip header
            if (headerLine == null) {
                result.addError(0, "Empty CSV file");
                return result;
            }

            String line;
            int rowNum = 1;
            List<Product> batchProducts = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                rowNum++;
                result.setTotalRows(result.getTotalRows() + 1);

                try {
                    String[] fields = parseCsvLine(line);
                    if (fields.length < 4) {
                        result.addError(rowNum, "Insufficient columns. Expected: sku,name,unit,unitPrice");
                        result.setFailed(result.getFailed() + 1);
                        continue;
                    }

                    String sku = fields[0].trim();
                    String name = fields[1].trim();
                    String unit = fields[2].trim();
                    BigDecimal unitPrice = new BigDecimal(fields[3].trim());

                    if (sku.isEmpty() || name.isEmpty()) {
                        result.addError(rowNum, "SKU and name are required");
                        result.setFailed(result.getFailed() + 1);
                        continue;
                    }

                    if (productRepository.existsBySku(sku)) {
                        result.addError(rowNum, "Duplicate SKU: " + sku);
                        result.setFailed(result.getFailed() + 1);
                        continue;
                    }

                    Product product = Product.builder()
                            .sku(sku)
                            .name(name)
                            .unit(unit)
                            .unitPrice(unitPrice)
                            .active(true)
                            .build();

                    if (fields.length > 4 && !fields[4].trim().isEmpty()) {
                        product.setDescription(fields[4].trim());
                    }
                    if (fields.length > 5 && !fields[5].trim().isEmpty()) {
                        product.setReorderPoint(Integer.parseInt(fields[5].trim()));
                    }
                    if (fields.length > 6 && !fields[6].trim().isEmpty()) {
                        product.setReorderQuantity(Integer.parseInt(fields[6].trim()));
                    }

                    batchProducts.add(product);
                    result.setImported(result.getImported() + 1);

                    if (batchProducts.size() >= 100) {
                        productRepository.saveAll(batchProducts);
                        batchProducts.clear();
                    }
                } catch (NumberFormatException e) {
                    result.addError(rowNum, "Invalid number format: " + e.getMessage());
                    result.setFailed(result.getFailed() + 1);
                } catch (Exception e) {
                    result.addError(rowNum, "Error processing row: " + e.getMessage());
                    result.setFailed(result.getFailed() + 1);
                }
            }

            if (!batchProducts.isEmpty()) {
                productRepository.saveAll(batchProducts);
            }

            log.info("Product import completed: total={}, imported={}, failed={}",
                    result.getTotalRows(), result.getImported(), result.getFailed());
        } catch (Exception e) {
            log.error("Failed to import products from CSV", e);
            result.addError(0, "File processing error: " + e.getMessage());
        }

        return result;
    }

    @Transactional
    public BatchImportResult importStockFromCsv(MultipartFile file) {
        BatchImportResult result = BatchImportResult.builder()
                .totalRows(0).imported(0).failed(0).errors(new ArrayList<>()).build();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            String headerLine = reader.readLine();
            if (headerLine == null) {
                result.addError(0, "Empty CSV file");
                return result;
            }

            String line;
            int rowNum = 1;

            while ((line = reader.readLine()) != null) {
                rowNum++;
                result.setTotalRows(result.getTotalRows() + 1);

                try {
                    String[] fields = parseCsvLine(line);
                    if (fields.length < 3) {
                        result.addError(rowNum, "Insufficient columns. Expected: sku,warehouseCode,quantity");
                        result.setFailed(result.getFailed() + 1);
                        continue;
                    }

                    String sku = fields[0].trim();
                    String warehouseCode = fields[1].trim();
                    int quantity = Integer.parseInt(fields[2].trim());

                    Product product = productRepository.findBySku(sku).orElse(null);
                    if (product == null) {
                        result.addError(rowNum, "Product not found: " + sku);
                        result.setFailed(result.getFailed() + 1);
                        continue;
                    }

                    Warehouse warehouse = warehouseRepository.findByCode(warehouseCode).orElse(null);
                    if (warehouse == null) {
                        result.addError(rowNum, "Warehouse not found: " + warehouseCode);
                        result.setFailed(result.getFailed() + 1);
                        continue;
                    }

                    StockLevel stockLevel = stockLevelRepository
                            .findByProductIdAndWarehouseId(product.getId(), warehouse.getId())
                            .orElseGet(() -> StockLevel.builder()
                                    .product(product)
                                    .warehouse(warehouse)
                                    .quantity(0)
                                    .build());

                    stockLevel.setQuantity(quantity);

                    if (fields.length > 3 && !fields[3].trim().isEmpty()) {
                        stockLevel.setMinQuantity(Integer.parseInt(fields[3].trim()));
                    }
                    if (fields.length > 4 && !fields[4].trim().isEmpty()) {
                        stockLevel.setMaxQuantity(Integer.parseInt(fields[4].trim()));
                    }

                    stockLevelRepository.save(stockLevel);
                    result.setImported(result.getImported() + 1);
                } catch (NumberFormatException e) {
                    result.addError(rowNum, "Invalid number format: " + e.getMessage());
                    result.setFailed(result.getFailed() + 1);
                } catch (Exception e) {
                    result.addError(rowNum, "Error processing row: " + e.getMessage());
                    result.setFailed(result.getFailed() + 1);
                }
            }

            log.info("Stock import completed: total={}, imported={}, failed={}",
                    result.getTotalRows(), result.getImported(), result.getFailed());
        } catch (Exception e) {
            log.error("Failed to import stock from CSV", e);
            result.addError(0, "File processing error: " + e.getMessage());
        }

        return result;
    }

    private String[] parseCsvLine(String line) {
        return line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
    }
}
