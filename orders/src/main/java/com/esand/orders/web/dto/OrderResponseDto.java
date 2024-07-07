package com.esand.orders.web.dto;

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
public class OrderResponseDto implements Serializable {
    private Long id;
    private String name;
    private String cpf;
    private String title;
    private String sku;
    private Double price;
    private Integer quantity;
    private Double total;
    private Boolean processed;
    private LocalDateTime date;
}
