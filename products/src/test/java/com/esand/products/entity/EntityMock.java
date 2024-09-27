package com.esand.products.entity;

import com.esand.products.repository.pagination.ProductDtoPagination;
import com.esand.products.web.dto.PageableDto;
import com.esand.products.web.dto.ProductCreateDto;
import com.esand.products.web.dto.ProductResponseDto;
import com.esand.products.web.dto.ProductUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EntityMock {

    public static final String CATEGORY = "MOUSES";
    public static final String PRODUCT_TITLE = "Wireless MouseS";
    public static final String PRODUCT_DESCRIPTION = "A high precision wireless m";
    public static final String SKU = "MOUSE-2024-WL-0010";
    public static final Double PRICE = 29.99;
    public static final Integer QUANTITY = 10;
    public static final Double WEIGHT = 0.1;
    public static final Double LENGTH = 10.0;
    public static final Double WIDTH = 5.0;
    public static final Double HEIGHT = 3.0;
    public static final String SUPPLIER = "Mach Supplies Inc.";

    public static Category category(){
        return new Category(1L, CATEGORY);
    }

    public static ProductCreateDto createDto() {
        return new ProductCreateDto(
                PRODUCT_TITLE,
                PRODUCT_DESCRIPTION,
                PRICE,
                CATEGORY,
                QUANTITY,
                SKU,
                WEIGHT,
                LENGTH,
                WIDTH,
                HEIGHT,
                SUPPLIER,
                new ArrayList<>()
        );
    }

    public static Product product() {
        return new Product(
                null,
                PRODUCT_TITLE,
                PRODUCT_DESCRIPTION,
                PRICE,
                List.of(new Category(1L, CATEGORY)),
                QUANTITY,
                SKU,
                WEIGHT, LENGTH,
                WIDTH,
                HEIGHT,
                SUPPLIER,
                LocalDateTime.now(),
                true
        );
    }

    public static ProductResponseDto productResponseDto() {
        return new ProductResponseDto(
                PRODUCT_TITLE,
                PRODUCT_DESCRIPTION,
                PRICE,
                List.of(new Category(1L, CATEGORY)),
                QUANTITY,
                SKU,
                true
        );
    }

    public static Page<ProductDtoPagination> page() {
        Pageable pageable = PageRequest.of(0, 10);
        List<ProductDtoPagination> content = List.of(
                new ProductDtoPagination() {
                    public String getTitle() { return PRODUCT_TITLE; }
                    public String getDescription() { return PRODUCT_DESCRIPTION; }
                    public Double getPrice() { return PRICE; }
                    public List<Category> getCategories() { return List.of(new Category(1L, CATEGORY)); }
                    public Integer getQuantity() { return QUANTITY; }
                    public String getSku() { return SKU; }
                    public Boolean getStatus() { return true; }
                }
        );
        return new PageImpl<>(content, pageable, content.size());
    }

    public static PageableDto pageableDto() {
        List<ProductDtoPagination> content = List.of(
                new ProductDtoPagination() {
                    public String getTitle() { return PRODUCT_TITLE; }
                    public String getDescription() { return PRODUCT_DESCRIPTION; }
                    public Double getPrice() { return PRICE; }
                    public List<Category> getCategories() { return List.of(new Category(1L, CATEGORY)); }
                    public Integer getQuantity() { return QUANTITY; }
                    public String getSku() { return SKU; }
                    public Boolean getStatus() { return true; }
                }
        );
        PageableDto pageableDto = new PageableDto();
        pageableDto.setContent(content);
        return pageableDto;
    }

    public static Page<ProductDtoPagination> pageEmpty() {
        Pageable pageable = PageRequest.of(0, 10);
        List<ProductDtoPagination> content = List.of();
        return new PageImpl<>(content, pageable, content.size());
    }

    public static PageableDto pageableDtoEmpty() {
        List<ProductDtoPagination> content = List.of();
        PageableDto pageableDto = new PageableDto();
        pageableDto.setContent(content);
        return pageableDto;
    }

    public static ProductUpdateDto productUpdateDto() {
        return new ProductUpdateDto(
                "Updated Wireless MouseS",
                "An updated high precision wireless mouse",
                39.99,
                CATEGORY,
                QUANTITY,
                SKU,
                0.2,
                12.0,
                6.0,
                4.0,
                "Updated Mach Supplies Inc."
        );
    }
}