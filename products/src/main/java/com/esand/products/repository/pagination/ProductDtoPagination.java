package com.esand.products.repository.pagination;

public interface ProductDtoPagination {
    String getTitle();
    String getDescription();
    Double getPrice();
    String getCategory();
    Integer getQuantity();
    String getSku();
    Boolean getStatus();
}
