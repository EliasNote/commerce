package com.esand.orders.entity;

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
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    private Double price = 0.00;

    private Integer quantity = 0;

    private Double total = 0.00;

    private Boolean processing = false;

    @CreatedDate
    @Column(name = "date", nullable = false)
    private LocalDateTime date;
}
