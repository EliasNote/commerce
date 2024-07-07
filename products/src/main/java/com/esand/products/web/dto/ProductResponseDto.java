package com.esand.products.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDto {
    private String title;
    private String description;
    private Double price;
    private String category;
    private Integer quantity;
    private String sku;
    private Boolean status;
}
