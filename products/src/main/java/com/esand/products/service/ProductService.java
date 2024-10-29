package com.esand.products.service;

import com.esand.products.entity.Product;
import com.esand.products.exception.*;
import com.esand.products.repository.ProductRepository;
import com.esand.products.web.dto.PageableDto;
import com.esand.products.web.dto.ProductCreateDto;
import com.esand.products.web.dto.ProductResponseDto;
import com.esand.products.web.dto.ProductUpdateDto;
import com.esand.products.web.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductMapper productMapper;
    private final ProductRepository productRepository;
    private final CategoryService categoryService;

    @Transactional
    public ProductResponseDto save(ProductCreateDto dto) {
        if (productRepository.existsByTitle(dto.getTitle())){
            throw new TitleUniqueViolationException("There is already a product registered with this title");
        }

        if (productRepository.existsBySku(dto.getSku())) {
            throw new SkuUniqueViolationException("There is already a product registered with this sku");
        }

        dto.setCategories(List.of(categoryService.findByName(dto.getCategory())));
        Product product = productRepository.save(productMapper.toProduct(dto));
        updateProductStatus(product);
        return productMapper.toDto(product);
    }

    @Transactional
    public PageableDto findAll(String afterDate, String beforeDate, Pageable pageable) {
        return findByCriteria(null, null, null, null, afterDate, beforeDate, pageable);
    }

    @Transactional
    public PageableDto findByTitle(String title, String afterDate, String beforeDate, Pageable pageable) {
        return findByCriteria(title, null, null, null, afterDate, beforeDate, pageable);
    }

    @Transactional
    public PageableDto findBySupplier(String supplier, String afterDate, String beforeDate, Pageable pageable) {
        return findByCriteria(null, supplier, null, null, afterDate, beforeDate, pageable);
    }

    @Transactional
    public PageableDto findByCategory(String category, String afterDate, String beforeDate, Pageable pageable) {
        try {
            return findByCriteria(null, null, category, null, afterDate, beforeDate, pageable);
        } catch(IllegalArgumentException e) {
            throw new InvalidCategoryException("Category does not exist");
        }
    }

    @Transactional(readOnly = true)
    public ProductResponseDto findBySku(String sku) {
        return productMapper.toDto(findProductBySku(sku));
    }

    @Transactional
    public PageableDto findAllActived(String afterDate, String beforeDate, Pageable pageable) {
        return findByCriteria(null, null, null, true, afterDate, beforeDate, pageable);
    }

    @Transactional
    public PageableDto findAllDisabled(String afterDate, String beforeDate, Pageable pageable) {
        return findByCriteria(null, null, null, false, afterDate, beforeDate, pageable);
    }

    @Transactional
    public ProductResponseDto update(String sku, ProductUpdateDto dto, Boolean status, Integer addQuantity, Integer subQuantity) {
        Product product = findProductBySku(sku);

        if (dto != null) {
            productMapper.updateProduct(dto, product);
            updateProductStatus(product);
        }

        if (status != null) {
            if (product.getQuantity() == 0) {
                throw new InvalidProductStatusException("Cannot alter status when quantity is 0");
            }
            product.setStatus(status);
        }

        if (addQuantity != null) {
            if (addQuantity == null || addQuantity <= 0) {
                throw new InvalidQuantityException("No quantity stated");
            }
            product.setQuantity(product.getQuantity() + addQuantity);
            if (product.getQuantity() - addQuantity == 0 || product.getQuantity() > 0 && product.getStatus()) {
                updateProductStatus(product);
            }
        }

        if (subQuantity != null) {
            if (subQuantity == null || subQuantity <= 0) {
                throw new InvalidQuantityException("No quantity stated");
            }
            if (product.getQuantity() < subQuantity) {
                throw new InvalidQuantityException("The quantity of available products is " + product.getQuantity());
            }
            product.setQuantity(product.getQuantity() - subQuantity);
            updateProductStatus(product);
        }

        return productMapper.toDto(product);
    }

    @Transactional
    public void deleteBySku(String sku) {
        if (!productRepository.existsBySku(sku)) {
            throw new EntityNotFoundException("Product not found by sku");
        }
        productRepository.deleteBySku(sku);
    }

    @Transactional(readOnly = true)
    private Product findProductBySku(String sku) {
        return productRepository.findBySku(sku).orElseThrow(
                () -> new EntityNotFoundException("Product not found by sku")
        );
    }

    private void updateProductStatus(Product product) {
        product.setStatus(product.getQuantity() == 0 ? false : true);
    }

    @Transactional
    private PageableDto findByCriteria(String title, String supplier, String category, Boolean status, String afterDate, String beforeDate, Pageable pageable) {
        LocalDateTime after = null;
        LocalDateTime before = null;
        PageableDto dto;

        if (afterDate != null) {
            after = LocalDate.parse(afterDate).atStartOfDay();
        }
        if (beforeDate != null) {
            before = LocalDate.parse(beforeDate).atTime(LocalTime.MAX);
        }

        if (title != null) {
            if (after != null && before != null) {
                dto = productMapper.toPageableDto(productRepository.findByTitleIgnoreCaseContainingAndCreateDateBetween(title, after, before, pageable));
            } else if (after != null) {
                dto = productMapper.toPageableDto(productRepository.findByTitleIgnoreCaseContainingAndCreateDateAfter(title, after, pageable));
            } else if (before != null) {
                dto = productMapper.toPageableDto(productRepository.findByTitleIgnoreCaseContainingAndCreateDateBefore(title, before, pageable));
            } else {
                dto = productMapper.toPageableDto(productRepository.findByTitleIgnoreCaseContaining(title, pageable));
            }
        } else if (supplier != null) {
            if (after != null && before != null) {
                dto = productMapper.toPageableDto(productRepository.findBySupplierIgnoreCaseContainingAndCreateDateBetween(supplier, after, before, pageable));
            } else if (after != null) {
                dto = productMapper.toPageableDto(productRepository.findBySupplierIgnoreCaseContainingAndCreateDateAfter(supplier, after, pageable));
            } else if (before != null) {
                dto = productMapper.toPageableDto(productRepository.findBySupplierIgnoreCaseContainingAndCreateDateBefore(supplier, before, pageable));
            } else {
                dto = productMapper.toPageableDto(productRepository.findBySupplierIgnoreCaseContaining(supplier, pageable));
            }
        } else if (category != null) {
            if (after != null && before != null) {
                dto = productMapper.toPageableDto(productRepository.findByCategoriesNameAndCreateDateBetween(category, after, before, pageable));
            } else if (after != null) {
                dto = productMapper.toPageableDto(productRepository.findByCategoriesNameAndCreateDateAfter(category, after, pageable));
            } else if (before != null) {
                dto = productMapper.toPageableDto(productRepository.findByCategoriesNameAndCreateDateBefore(category, before, pageable));
            } else {
                dto = productMapper.toPageableDto(productRepository.findByCategoriesName(category, pageable));
            }
        } else if (status != null) {
            if (after != null && before != null) {
                dto = productMapper.toPageableDto(productRepository.findAllByStatusAndCreateDateBetween(status, after, before, pageable));
            } else if (after != null) {
                dto = productMapper.toPageableDto(productRepository.findAllByStatusAndCreateDateAfter(status, after, pageable));
            } else if (before != null) {
                dto = productMapper.toPageableDto(productRepository.findAllByStatusAndCreateDateBefore(status, before, pageable));
            } else {
                dto = productMapper.toPageableDto(productRepository.findAllByStatus(status, pageable));
            }
        } else {
            if (after != null && before != null) {
                dto = productMapper.toPageableDto(productRepository.findByCreateDateBetween(after, before, pageable));
            } else if (after != null) {
                dto = productMapper.toPageableDto(productRepository.findByCreateDateAfter(after, pageable));
            } else if (before != null) {
                dto = productMapper.toPageableDto(productRepository.findByCreateDateBefore(before, pageable));
            } else {
                dto = productMapper.toPageableDto(productRepository.findAllPageable(pageable));
            }
        }

        if (dto.getContent().isEmpty()) {
            throw new EntityNotFoundException("No products found");
        }

        return dto;
    }
}
