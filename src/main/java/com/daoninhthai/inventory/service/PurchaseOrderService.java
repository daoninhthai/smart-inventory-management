package com.daoninhthai.inventory.service;

import com.daoninhthai.inventory.dto.*;
import com.daoninhthai.inventory.entity.*;
import com.daoninhthai.inventory.exception.ResourceNotFoundException;
import com.daoninhthai.inventory.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final SupplierRepository supplierRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;
    private final StockLevelRepository stockLevelRepository;
    private final StockMovementService stockMovementService;
    private final StockWebSocketService stockWebSocketService;

    private static final AtomicLong orderSequence = new AtomicLong(1000);

    @Transactional(readOnly = true)
    public Page<PurchaseOrderResponse> getAllOrders(Pageable pageable) {
        return purchaseOrderRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<PurchaseOrderResponse> getOrdersByStatus(OrderStatus status, Pageable pageable) {
        return purchaseOrderRepository.findByStatus(status, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public PurchaseOrderResponse getOrderById(Long id) {
        PurchaseOrder order = purchaseOrderRepository.findByIdWithItems(id)
                .orElseThrow(() -> new ResourceNotFoundException("PurchaseOrder", "id", id));
        return toResponse(order);
    }

    @Transactional
    public PurchaseOrderResponse createDraft(CreatePurchaseOrderRequest request) {
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", request.getSupplierId()));
        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "id", request.getWarehouseId()));

        String orderNumber = "PO-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                + "-" + orderSequence.incrementAndGet();

        PurchaseOrder order = PurchaseOrder.builder()
                .orderNumber(orderNumber)
                .supplier(supplier)
                .warehouse(warehouse)
                .status(OrderStatus.DRAFT)
                .build();

        for (PurchaseOrderItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", itemReq.getProductId()));

            PurchaseOrderItem item = PurchaseOrderItem.builder()
                    .product(product)
                    .quantity(itemReq.getQuantity())
                    .unitPrice(itemReq.getUnitPrice())
                    .receivedQuantity(0)
                    .build();

            order.addItem(item);
        }

        order.recalculateTotal();
        PurchaseOrder saved = purchaseOrderRepository.save(order);
        log.info("Created draft purchase order: {}", saved.getOrderNumber());
        return toResponse(saved);
    }

    @Transactional
    public PurchaseOrderResponse submitOrder(Long id) {
        PurchaseOrder order = purchaseOrderRepository.findByIdWithItems(id)
                .orElseThrow(() -> new ResourceNotFoundException("PurchaseOrder", "id", id));

        if (order.getStatus() != OrderStatus.DRAFT) {
            throw new IllegalStateException("Only DRAFT orders can be submitted. Current status: " + order.getStatus());
        }

        order.setStatus(OrderStatus.SUBMITTED);
        PurchaseOrder saved = purchaseOrderRepository.save(order);
        stockWebSocketService.broadcastOrderStatus(saved.getOrderNumber(), "DRAFT", "SUBMITTED");
        log.info("Submitted purchase order: {}", saved.getOrderNumber());
        return toResponse(saved);
    }

    @Transactional
    public PurchaseOrderResponse approveOrder(Long id) {
        PurchaseOrder order = purchaseOrderRepository.findByIdWithItems(id)
                .orElseThrow(() -> new ResourceNotFoundException("PurchaseOrder", "id", id));

        if (order.getStatus() != OrderStatus.SUBMITTED) {
            throw new IllegalStateException("Only SUBMITTED orders can be approved. Current status: " + order.getStatus());
        }

        order.setStatus(OrderStatus.APPROVED);
        PurchaseOrder saved = purchaseOrderRepository.save(order);
        stockWebSocketService.broadcastOrderStatus(saved.getOrderNumber(), "SUBMITTED", "APPROVED");
        log.info("Approved purchase order: {}", saved.getOrderNumber());
        return toResponse(saved);
    }

    @Transactional
    public PurchaseOrderResponse receiveOrder(Long id, ReceiveOrderRequest receiveRequest) {
        PurchaseOrder order = purchaseOrderRepository.findByIdWithItems(id)
                .orElseThrow(() -> new ResourceNotFoundException("PurchaseOrder", "id", id));

        if (order.getStatus() != OrderStatus.APPROVED) {
            throw new IllegalStateException("Only APPROVED orders can be received. Current status: " + order.getStatus());
        }

        Warehouse warehouse = order.getWarehouse();

        for (PurchaseOrderItem item : order.getItems()) {
            Integer receivedQty = item.getQuantity();

            if (receiveRequest != null && receiveRequest.getItems() != null) {
                receivedQty = receiveRequest.getItems().stream()
                        .filter(ri -> ri.getProductId().equals(item.getProduct().getId()))
                        .map(ReceiveOrderRequest.ReceiveItemRequest::getReceivedQuantity)
                        .findFirst()
                        .orElse(item.getQuantity());
            }

            item.setReceivedQuantity(receivedQty);

            StockLevel stockLevel = stockLevelRepository
                    .findByProductIdAndWarehouseId(item.getProduct().getId(), warehouse.getId())
                    .orElseGet(() -> StockLevel.builder()
                            .product(item.getProduct())
                            .warehouse(warehouse)
                            .quantity(0)
                            .build());

            stockLevel.setQuantity(stockLevel.getQuantity() + receivedQty);
            stockLevelRepository.save(stockLevel);

            stockMovementService.recordMovement(
                    item.getProduct(), warehouse, MovementType.IN,
                    receivedQty, order.getOrderNumber(),
                    "Received from PO: " + order.getOrderNumber(), null);
        }

        order.setStatus(OrderStatus.RECEIVED);
        order.setReceivedAt(LocalDateTime.now());
        PurchaseOrder saved = purchaseOrderRepository.save(order);
        stockWebSocketService.broadcastOrderStatus(saved.getOrderNumber(), "APPROVED", "RECEIVED");
        log.info("Received purchase order: {} ({} items)", saved.getOrderNumber(), order.getItems().size());
        return toResponse(saved);
    }

    @Transactional
    public PurchaseOrderResponse cancelOrder(Long id) {
        PurchaseOrder order = purchaseOrderRepository.findByIdWithItems(id)
                .orElseThrow(() -> new ResourceNotFoundException("PurchaseOrder", "id", id));

        if (order.getStatus() == OrderStatus.RECEIVED || order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Cannot cancel an order that is " + order.getStatus());
        }

        String previousStatus = order.getStatus().name();
        order.setStatus(OrderStatus.CANCELLED);
        PurchaseOrder saved = purchaseOrderRepository.save(order);
        stockWebSocketService.broadcastOrderStatus(saved.getOrderNumber(), previousStatus, "CANCELLED");
        log.info("Cancelled purchase order: {}", saved.getOrderNumber());
        return toResponse(saved);
    }

    private PurchaseOrderResponse toResponse(PurchaseOrder order) {
        List<PurchaseOrderResponse.PurchaseOrderItemResponse> itemResponses =
                order.getItems().stream()
                        .map(item -> PurchaseOrderResponse.PurchaseOrderItemResponse.builder()
                                .id(item.getId())
                                .productId(item.getProduct().getId())
                                .productName(item.getProduct().getName())
                                .productSku(item.getProduct().getSku())
                                .quantity(item.getQuantity())
                                .unitPrice(item.getUnitPrice())
                                .receivedQuantity(item.getReceivedQuantity())
                                .build())
                        .collect(Collectors.toList());

        return PurchaseOrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .supplierId(order.getSupplier().getId())
                .supplierName(order.getSupplier().getName())
                .warehouseId(order.getWarehouse().getId())
                .warehouseName(order.getWarehouse().getName())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .createdBy(order.getCreatedBy())
                .createdAt(order.getCreatedAt())
                .receivedAt(order.getReceivedAt())
                .items(itemResponses)
                .build();
    }
}
