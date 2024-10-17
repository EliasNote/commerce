package com.esand.products.repository;

import com.esand.products.entity.Product;
import com.esand.products.repository.pagination.ProductDtoPagination;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySku(String sku);

    boolean existsByCategoriesName(String upperCase);

    boolean existsByTitle(String title);

    boolean existsBySku(String sku);

    void deleteBySku(String sku);

    Page<ProductDtoPagination> findByTitleIgnoreCaseContainingAndCreateDateBetween(String title, LocalDateTime after, LocalDateTime before, Pageable pageable);

    Page<ProductDtoPagination> findByTitleIgnoreCaseContainingAndCreateDateAfter(String title, LocalDateTime after, Pageable pageable);

    Page<ProductDtoPagination> findByTitleIgnoreCaseContainingAndCreateDateBefore(String title, LocalDateTime before, Pageable pageable);

    Page<ProductDtoPagination> findByTitleIgnoreCaseContaining(String title, Pageable pageable);

    Page<ProductDtoPagination> findBySupplierIgnoreCaseContainingAndCreateDateBetween(String supplier, LocalDateTime after, LocalDateTime before, Pageable pageable);

    Page<ProductDtoPagination> findBySupplierIgnoreCaseContainingAndCreateDateAfter(String supplier, LocalDateTime after, Pageable pageable);

    Page<ProductDtoPagination> findBySupplierIgnoreCaseContainingAndCreateDateBefore(String supplier, LocalDateTime before, Pageable pageable);

    Page<ProductDtoPagination> findBySupplierIgnoreCaseContaining(String supplier, Pageable pageable);

    Page<ProductDtoPagination> findByCategoriesNameAndCreateDateBetween(String category, LocalDateTime after, LocalDateTime before, Pageable pageable);

    Page<ProductDtoPagination> findByCategoriesNameAndCreateDateAfter(String category, LocalDateTime after, Pageable pageable);

    Page<ProductDtoPagination> findByCategoriesNameAndCreateDateBefore(String category, LocalDateTime before, Pageable pageable);

    Page<ProductDtoPagination> findByCategoriesName(String category, Pageable pageable);

    Page<ProductDtoPagination> findAllByStatusAndCreateDateBetween(Boolean status, LocalDateTime after, LocalDateTime before, Pageable pageable);

    Page<ProductDtoPagination> findAllByStatusAndCreateDateAfter(Boolean status, LocalDateTime after, Pageable pageable);

    Page<ProductDtoPagination> findAllByStatusAndCreateDateBefore(Boolean status, LocalDateTime before, Pageable pageable);

    Page<ProductDtoPagination> findAllByStatus(boolean b, Pageable pageable);

    Page<ProductDtoPagination> findByCreateDateBetween(LocalDateTime afterDate, LocalDateTime beforeDate, Pageable pageable);

    Page<ProductDtoPagination> findByCreateDateAfter(LocalDateTime date, Pageable pageable);

    Page<ProductDtoPagination> findByCreateDateBefore(LocalDateTime date, Pageable pageable);

    @Query("SELECT p FROM Product p")
    Page<ProductDtoPagination> findAllPageable(Pageable pageable);
}

