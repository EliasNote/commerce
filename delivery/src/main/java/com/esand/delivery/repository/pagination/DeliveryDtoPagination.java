package com.esand.delivery.repository.pagination;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public interface DeliveryDtoPagination {
    @JsonProperty("order")
    Long getId();

    @JsonProperty("purchaser")
    String getName();

    @JsonProperty("CPF")
    String getCpf();

    @JsonProperty("product name")
    String getTitle();

    @JsonProperty("SKU")
    String getSku();

    @JsonProperty("unit price")
    Double getPrice();

    Integer getQuantity();

    @JsonProperty("total price")
    Double getTotal();

    String getStatus();

    @JsonProperty("purchase date")
    LocalDateTime getDate();
}
