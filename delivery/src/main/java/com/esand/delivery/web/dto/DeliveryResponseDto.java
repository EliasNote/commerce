package com.esand.delivery.web.dto;

import com.esand.delivery.entity.Delivery;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryResponseDto implements Serializable {
    @JsonProperty("order nº")
    private Long id;

    @JsonProperty("purchaser")
    private String name;

    @JsonProperty("CPF")
    private String cpf;

    @JsonProperty("product name")
    private String title;

    @JsonProperty("SKU")
    private String sku;

    @JsonProperty("unit price")
    private Double price;
    
    private Integer quantity;

    @JsonProperty("total price")
    private Double total;

    private String status;

    @JsonProperty("purchase date")
    private LocalDateTime date;
}