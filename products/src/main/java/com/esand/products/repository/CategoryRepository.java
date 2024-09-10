package com.esand.products.repository;

import com.esand.products.entity.Category;
import com.esand.products.repository.pagination.CategoryDtoPagination;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByName(String category);

    Category findByName(String upperCase);

    void deleteByName(String category);

    @Query("SELECT c FROM Category c")
    Page<CategoryDtoPagination> findAllPageable(Pageable pageable);
}
