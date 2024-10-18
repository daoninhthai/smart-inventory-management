package com.daoninhthai.inventory.service;

import com.daoninhthai.inventory.entity.StockLevel;
import com.daoninhthai.inventory.entity.StockMovement;
import com.daoninhthai.inventory.repository.StockLevelRepository;
import com.daoninhthai.inventory.repository.StockMovementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

    private final StockLevelRepository stockLevelRepository;
    private final StockMovementRepository stockMovementRepository;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Transactional(readOnly = true)
    public String generateStockReport() {
        List<StockLevel> stockLevels = stockLevelRepository.findAll();

        StringBuilder csv = new StringBuilder();
        csv.append("SKU,Product,Warehouse,Quantity,MinQty,MaxQty,Value\n");

        for (StockLevel sl : stockLevels) {
            String sku = sl.getProduct().getSku();
            String productName = escapeCsv(sl.getProduct().getName());
            String warehouseName = escapeCsv(sl.getWarehouse().getName());
            int quantity = sl.getQuantity();
            Integer minQty = sl.getMinQuantity();
            Integer maxQty = sl.getMaxQuantity();

            BigDecimal unitPrice = sl.getProduct().getUnitPrice();
            BigDecimal value = unitPrice != null
                    ? unitPrice.multiply(BigDecimal.valueOf(quantity))
                    : BigDecimal.ZERO;

            csv.append(String.format("%s,%s,%s,%d,%s,%s,%.2f\n",
                    sku, productName, warehouseName, quantity,
                    minQty != null ? minQty : "",
                    maxQty != null ? maxQty : "",
                    value));
        }

        log.info("Generated stock report with {} entries", stockLevels.size());
        return csv.toString();
    }

    @Transactional(readOnly = true)
    public String generateMovementReport() {
        Page<StockMovement> movements = stockMovementRepository.findAll(
                PageRequest.of(0, 10000, Sort.by(Sort.Direction.DESC, "createdAt")));

        StringBuilder csv = new StringBuilder();
        csv.append("Date,SKU,Product,Warehouse,Type,Quantity,Reference,Notes\n");

        for (StockMovement sm : movements.getContent()) {
            String date = sm.getCreatedAt() != null ? sm.getCreatedAt().format(DATE_FORMAT) : "";
            String sku = sm.getProduct().getSku();
            String productName = escapeCsv(sm.getProduct().getName());
            String warehouseName = escapeCsv(sm.getWarehouse().getName());
            String type = sm.getType().name();
            int quantity = sm.getQuantity();
            String reference = sm.getReference() != null ? escapeCsv(sm.getReference()) : "";
            String notes = sm.getNotes() != null ? escapeCsv(sm.getNotes()) : "";

            csv.append(String.format("%s,%s,%s,%s,%s,%d,%s,%s\n",
                    date, sku, productName, warehouseName,
                    type, quantity, reference, notes));
        }

        log.info("Generated movement report with {} entries", movements.getContent().size());
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
