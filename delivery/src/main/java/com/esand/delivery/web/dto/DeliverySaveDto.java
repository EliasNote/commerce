package com.esand.delivery.web.dto;

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
public class DeliverySaveDto implements Serializable {
    private Long id;
    private String name;
    private String cpf;
    private String title;
    private String sku;
    private Double price;
    private Integer quantity;
    private Double total;
    private LocalDateTime date;
}