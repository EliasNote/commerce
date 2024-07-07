package com.esand.products.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreateDto {
    private String title;
    private String description;
    private Double price;
    private String category;
    private Integer quantity;
    private String sku;
    private Double weight;
    private Double length;
    private Double width;
    private Double height;
    private String supplier;
}
