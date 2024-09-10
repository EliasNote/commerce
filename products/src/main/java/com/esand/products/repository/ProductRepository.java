package com.esand.products.repository;

import com.esand.products.entity.Product;
import com.esand.products.repository.pagination.ProductDtoPagination;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.time.LocalDateTime;
import java.util.Optional;


public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT p FROM Product p")
    Page<ProductDtoPagination> findAllPageable(Pageable pageable);

    Optional<Product> findBySku(String sku);

    Page<ProductDtoPagination> findByTitleIgnoreCaseContaining(Pageable pageable, String title);

    Page<ProductDtoPagination> findBySupplierIgnoreCaseContaining(Pageable pageable, String supplier);

    Page<Product> findByCategoriesName(Pageable pageable, String category);

    boolean existsByTitle(String title);

    boolean existsBySku(String sku);

    Page<ProductDtoPagination> findAllByStatus(Pageable pageable, boolean b);

    Page<ProductDtoPagination> findByCreateDateAfter(LocalDateTime date, Pageable pageable);

    Page<ProductDtoPagination> findByCreateDateBefore(LocalDateTime date, Pageable pageable);

    Page<ProductDtoPagination> findByCreateDateBetween(LocalDateTime afterDate, LocalDateTime beforeDate, Pageable pageable);

    void deleteBySku(String sku);
}

