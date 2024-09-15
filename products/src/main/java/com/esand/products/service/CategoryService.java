package com.esand.products.service;

import com.esand.products.entity.Category;
import com.esand.products.exception.CategoryUniqueViolationException;
import com.esand.products.exception.EntityNotFoundException;
import com.esand.products.exception.ReferentialIntegrityException;
import com.esand.products.repository.CategoryRepository;
import com.esand.products.repository.ProductRepository;
import com.esand.products.web.dto.PageableDto;
import com.esand.products.web.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    @Transactional
    public String create(String category) {
        if (categoryRepository.existsByName(category.toUpperCase())) {
            throw new CategoryUniqueViolationException("Category already exists");
        }
        categoryRepository.save(new Category(null, category.toUpperCase()));
        return "Category created successfully!";
    }

    @Transactional
    public void deleteCategory(String category) {
        if (!categoryRepository.existsByName(category.toUpperCase())) {
            throw new EntityNotFoundException("Category not found");
        }

        if (productRepository.existsByCategoriesName(category.toUpperCase())) {
            throw new ReferentialIntegrityException("Cannot delete category as it is referenced by products");
        }

        categoryRepository.deleteByName(category.toUpperCase());
    }

    @Transactional
    public String editCategory(String name, String newName) {
        if (!categoryRepository.existsByName(name.toUpperCase())) {
            throw new EntityNotFoundException("Category not found");
        }
        Category category = categoryRepository.findByName(name.toUpperCase());
        category.setName(newName.toUpperCase());
        return "Category updated successfully";
    }

    @Transactional(readOnly = true)
    public PageableDto findAll(Pageable pageable) {
        PageableDto dto = productMapper.toPageableDto(categoryRepository.findAllPageable(pageable));
        if (dto.getContent().isEmpty()) {
            throw new EntityNotFoundException("No products found");
        }
        return dto;
    }
}
