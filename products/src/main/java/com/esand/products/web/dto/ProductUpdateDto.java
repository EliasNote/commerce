package com.esand.products.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdateDto {
    @Size(min = 5, max = 200)
    private String title;

    @Size(min = 5, max = 500)
    private String description;

    @Min(0)
    private Double price;

    @Size(min = 5, max = 50)
    private String category;

    @Min(0)
    private Integer quantity;

    private String sku;

    @Min(0)
    private Double weight;

    @Min(0)
    private Double length;

    @Min(0)
    private Double width;

    @Min(0)
    private Double height;

    @Size(min = 5, max = 100)
    private String supplier;
}
