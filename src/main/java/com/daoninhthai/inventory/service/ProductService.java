package com.daoninhthai.inventory.service;

import com.daoninhthai.inventory.dto.CreateProductRequest;
import com.daoninhthai.inventory.dto.ProductResponse;
import com.daoninhthai.inventory.dto.UpdateProductRequest;
import com.daoninhthai.inventory.entity.Category;
import com.daoninhthai.inventory.entity.Product;
import com.daoninhthai.inventory.exception.DuplicateResourceException;
import com.daoninhthai.inventory.exception.ResourceNotFoundException;
import com.daoninhthai.inventory.mapper.ProductMapper;
import com.daoninhthai.inventory.repository.CategoryRepository;
import com.daoninhthai.inventory.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Cacheable(value = "products", key = "#id")
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        return ProductMapper.toResponse(product);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        return productRepository.findByActiveTrue(pageable)
                .map(ProductMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> searchProducts(String name, String sku, Long categoryId, Pageable pageable) {
        return productRepository.searchProducts(name, sku, categoryId, pageable)
                .map(ProductMapper::toResponse);
    }

    @CacheEvict(value = "products", allEntries = true)
    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {
        if (productRepository.existsBySku(request.getSku())) {
            throw new DuplicateResourceException("Product", "sku", request.getSku());
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));

        Product product = ProductMapper.toEntity(request, category);
        Product saved = productRepository.save(product);

        log.info("Created product: {} (SKU: {})", saved.getName(), saved.getSku());
        return ProductMapper.toResponse(saved);
    }

    @CacheEvict(value = "products", allEntries = true)
    @Transactional
    public ProductResponse updateProduct(Long id, UpdateProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        if (request.getName() != null) {
            product.setName(request.getName());
        }
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));
            product.setCategory(category);
        }
        if (request.getUnit() != null) {
            product.setUnit(request.getUnit());
        }
        if (request.getUnitPrice() != null) {
            product.setUnitPrice(request.getUnitPrice());
        }
        if (request.getReorderPoint() != null) {
            product.setReorderPoint(request.getReorderPoint());
        }
        if (request.getReorderQuantity() != null) {
            product.setReorderQuantity(request.getReorderQuantity());
        }
        if (request.getActive() != null) {
            product.setActive(request.getActive());
        }

        Product updated = productRepository.save(product);
        log.info("Updated product: {} (ID: {})", updated.getName(), updated.getId());
        return ProductMapper.toResponse(updated);
    }

    @CacheEvict(value = "products", allEntries = true)
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        product.setActive(false);
        productRepository.save(product);
        log.info("Soft deleted product: {} (ID: {})", product.getName(), product.getId());
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductBySku(String sku) {
        Product product = productRepository.findBySku(sku)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "sku", sku));
        return ProductMapper.toResponse(product);
    }
}
