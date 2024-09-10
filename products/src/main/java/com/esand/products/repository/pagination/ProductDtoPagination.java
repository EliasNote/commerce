package com.esand.products.repository.pagination;

import com.esand.products.entity.Category;

import java.util.List;

public interface ProductDtoPagination {
    String getTitle();
    String getDescription();
    Double getPrice();
    List<Category> getCategories();
    Integer getQuantity();
    String getSku();
    Boolean getStatus();
}
