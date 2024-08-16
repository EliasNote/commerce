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
import java.util.List;

public class EntityMock {

    public static ProductCreateDto createDto() {
        return new ProductCreateDto(
                "Wireless MouseS",
                "A high precision wireless m",
                29.99,
                "MOUSES",
                10,
                "MOUSE-2024-WL-0010",
                0.1,
                10.0,
                5.0,
                3.0,
                "Mach Supplies Inc."
        );
    }

    public static Product product() {
        return new Product(
                1L,
                "Wireless MouseS",
                "A high precision wireless m",
                29.99, Product.Category.MOUSES,
                10,
                "MOUSE-2024-WL-0010",
                0.1, 10.0,
                5.0,
                3.0,
                "Mach Supplies Inc.",
                LocalDateTime.now(),
                true
        );
    }

    public static ProductResponseDto productResponseDto() {
        return new ProductResponseDto(
                "Wireless MouseS",
                "A high precision wireless m",
                29.99,
                "MOUSES",
                10,
                "MOUSE-2024-WL-0010",
                true
        );
    }

    public static Page<ProductDtoPagination> page() {
        Pageable pageable = PageRequest.of(0, 10);
        List<ProductDtoPagination> content = List.of(
                new ProductDtoPagination() {
                    public String getTitle() { return "Wireless MouseS"; }
                    public String getDescription() { return "A high precision wireless m"; }
                    public Double getPrice() { return 29.99; }
                    public String getCategory() { return "MOUSES"; }
                    public Integer getQuantity() { return 10; }
                    public String getSku() { return "MOUSE-2024-WL-0010"; }
                    public Boolean getStatus() { return true; }
                }
        );
        return new PageImpl<>(content, pageable, content.size());
    }

    public static PageableDto pageableDto() {
        List<ProductDtoPagination> content = List.of(
                new ProductDtoPagination() {
                    public String getTitle() { return "Wireless MouseS"; }
                    public String getDescription() { return "A high precision wireless m"; }
                    public Double getPrice() { return 29.99; }
                    public String getCategory() { return "MOUSES"; }
                    public Integer getQuantity() { return 10; }
                    public String getSku() { return "MOUSE-2024-WL-0010"; }
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
                "MOUSES",
                15,
                "MOUSE-2024-WL-0010",
                0.2,
                12.0,
                6.0,
                4.0,
                "Updated Mach Supplies Inc."
        );
    }
}