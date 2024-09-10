package com.esand.products.service;

import com.esand.products.entity.Category;
import com.esand.products.exception.ReferentialIntegrityException;
import com.esand.products.repository.CategoryRepository;
import com.esand.products.web.dto.PageableDto;
import com.esand.products.web.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    @Transactional
    public String create(String category) {
        if (categoryRepository.existsByName(category.toUpperCase())) {
            throw new RuntimeException("Category already exists");
        }
        categoryRepository.save(new Category(null, category.toUpperCase()));
        return "Category created successfully!";
    }

    @Transactional
    public void deleteCategory(String category) {
        if (!categoryRepository.existsByName(category.toUpperCase())) {
            throw new RuntimeException("Category not found");
        }

        try {
            categoryRepository.deleteByName(category.toUpperCase());
        } catch (DataIntegrityViolationException e) {
            throw new ReferentialIntegrityException("Cannot delete category as it is referenced by products");
        }
    }

    @Transactional
    public String editCategory(String name, String newName) {
        if (!categoryRepository.existsByName(name.toUpperCase())) {
            throw new RuntimeException("Category not found");
        }
        Category category = categoryRepository.findByName(name.toUpperCase());
        category.setName(newName.toUpperCase());
        return "Category updated successfully";
    }

    @Transactional(readOnly = true)
    public PageableDto findAll(Pageable pageable) {
        return productMapper.toPageableDto(categoryRepository.findAllPageable(pageable));
    }
}
