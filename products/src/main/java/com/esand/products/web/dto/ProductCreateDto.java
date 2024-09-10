package com.esand.products.web.dto;

import com.esand.products.entity.Category;
import com.esand.products.entity.Product;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.UniqueElements;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreateDto {

    @NotBlank
    @Size(min = 5, max = 200)
    private String title;

    @Size(min = 5, max = 500)
    private String description;

    @NotNull
    @Min(0)
    private Double price;

    @NotBlank
    @Size(min = 5, max = 50)
    private String category;

    @NotNull
    @Min(0)
    private Integer quantity;

    @NotBlank
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

    @JsonIgnore
    private List<Category> categories = new ArrayList<>();
}
