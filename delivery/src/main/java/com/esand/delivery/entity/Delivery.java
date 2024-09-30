package com.esand.delivery.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Delivery {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "product_name")
    private String title;

    @Column(name = "product_sku")
    private String sku;

    @Column(name = "client_name")
    private String name;

    @Column(name = "client_cpf")
    private String cpf;

    private Double price;

    private Integer quantity;

    private Double total;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PROCESSING;

    @CreatedDate
    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    public enum Status {
        PROCESSING,
        SHIPPED,
        CANCELED
    }


}