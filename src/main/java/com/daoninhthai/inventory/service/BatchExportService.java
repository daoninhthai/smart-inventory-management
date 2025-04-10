package com.daoninhthai.inventory.service;

import com.daoninhthai.inventory.entity.Product;
import com.daoninhthai.inventory.entity.StockLevel;
import com.daoninhthai.inventory.repository.ProductRepository;
import com.daoninhthai.inventory.repository.StockLevelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BatchExportService {

    private final ProductRepository productRepository;
    private final StockLevelRepository stockLevelRepository;

    @Transactional(readOnly = true)
    public String exportProductsToCsv() {
        List<Product> products = productRepository.findAll();
        StringBuilder csv = new StringBuilder();

        csv.append("sku,name,unit,unitPrice,description,reorderPoint,reorderQuantity,active\n");

        for (Product product : products) {
            csv.append(escapeCsv(product.getSku())).append(",");
            csv.append(escapeCsv(product.getName())).append(",");
            csv.append(escapeCsv(product.getUnit() != null ? product.getUnit() : "")).append(",");
            csv.append(product.getUnitPrice() != null ? product.getUnitPrice().toPlainString() : "").append(",");
            csv.append(escapeCsv(product.getDescription() != null ? product.getDescription() : "")).append(",");
            csv.append(product.getReorderPoint() != null ? product.getReorderPoint() : "").append(",");
            csv.append(product.getReorderQuantity() != null ? product.getReorderQuantity() : "").append(",");
            csv.append(product.getActive()).append("\n");
        }

        log.info("Exported {} products to CSV", products.size());
        return csv.toString();
    }

    @Transactional(readOnly = true)
    public String exportStockToCsv() {
        List<StockLevel> stockLevels = stockLevelRepository.findAll();
        StringBuilder csv = new StringBuilder();

        csv.append("sku,productName,warehouseCode,warehouseName,quantity,minQuantity,maxQuantity\n");

        for (StockLevel sl : stockLevels) {
            csv.append(escapeCsv(sl.getProduct().getSku())).append(",");
            csv.append(escapeCsv(sl.getProduct().getName())).append(",");
            csv.append(escapeCsv(sl.getWarehouse().getCode())).append(",");
            csv.append(escapeCsv(sl.getWarehouse().getName())).append(",");
            csv.append(sl.getQuantity()).append(",");
            csv.append(sl.getMinQuantity() != null ? sl.getMinQuantity() : "").append(",");
            csv.append(sl.getMaxQuantity() != null ? sl.getMaxQuantity() : "").append("\n");
        }

        log.info("Exported {} stock levels to CSV", stockLevels.size());
        return csv.toString();
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
