package com.esand.products.repository;

import com.esand.products.entity.Product;
import com.esand.products.repository.pagination.ProductDtoPagination;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.util.Optional;


public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT p FROM Product p")
    Page<ProductDtoPagination> findAllPageable(Pageable pageable);

    Optional<Product> findBySku(String sku);

    Optional<Product> findByTitleIgnoreCase(String title);

    Page<ProductDtoPagination> findBySupplierIgnoreCaseContaining(Pageable pageable, String supplier);

    Page<ProductDtoPagination> findByCategory(Pageable pageable, Product.Category category);

    boolean existsByTitle(String title);

    boolean existsBySku(String sku);
}

