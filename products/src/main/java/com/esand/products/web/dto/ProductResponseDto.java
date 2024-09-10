package com.esand.products.web.dto;

import com.esand.products.entity.Category;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductResponseDto {
    private String title;
    private String description;
    private Double price;
    private List<Category> categories;
    private Integer quantity;
    private String sku;
    private Boolean status;
}
