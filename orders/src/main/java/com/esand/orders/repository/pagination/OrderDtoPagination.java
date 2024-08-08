package com.esand.orders.repository.pagination;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public interface OrderDtoPagination {
    @JsonProperty("order")
    Long getId();
    String getName();
    String getCpf();
    String getTitle();
    String getSku();
    Double getPrice();
    Integer getQuantity();
    Double getTotal();
    Boolean getProcessing();
    LocalDateTime getDate();
}
