package com.esand.products.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Product implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "title", nullable = false, length = 200, unique = true)
    private String title;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "price", nullable = false)
    private Double price = 0.00;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 50)
    private Category category;

    @Column(name = "quantity", nullable = false)
    private Integer quantity = 0;

    @Column(name = "sku", nullable = false, unique = true)
    private String sku;

    private Double weight;

    private Double length;

    private Double width;

    private Double height;

    private String supplier;

    @CreatedDate
    @Column(name = "create_date", nullable = false)
    private LocalDateTime createDate;

    @Column(name = "active", nullable = false)
    private Boolean status = true;

    public enum Category {
        COMPUTERS,
        SMARTPHONES,
        HEADPHONES,
        MOUSES,
        KEYBOARDS,
        SCREENS
    }
}
