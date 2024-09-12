package com.daoninhthai.inventory.mapper;

import com.daoninhthai.inventory.dto.CreateProductRequest;
import com.daoninhthai.inventory.dto.ProductResponse;
import com.daoninhthai.inventory.entity.Category;
import com.daoninhthai.inventory.entity.Product;

public class ProductMapper {

    private ProductMapper() {
        // Utility class
    }

    public static ProductResponse toResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .sku(product.getSku())
                .name(product.getName())
                .description(product.getDescription())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .unit(product.getUnit())
                .unitPrice(product.getUnitPrice())
                .reorderPoint(product.getReorderPoint())
                .reorderQuantity(product.getReorderQuantity())
                .active(product.getActive())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    public static Product toEntity(CreateProductRequest request, Category category) {
        return Product.builder()
                .sku(request.getSku())
                .name(request.getName())
                .description(request.getDescription())
                .category(category)
                .unit(request.getUnit())
                .unitPrice(request.getUnitPrice())
                .reorderPoint(request.getReorderPoint())
                .reorderQuantity(request.getReorderQuantity())
                .active(true)
                .build();
    }
}
