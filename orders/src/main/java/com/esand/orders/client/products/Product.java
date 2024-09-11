package com.esand.orders.client.products;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    private String title;
    private String description;
    private Double price;
    private List<Category> categories;
    private Integer quantity;
    private String sku;
    private Boolean status;
}
