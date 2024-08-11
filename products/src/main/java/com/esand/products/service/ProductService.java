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

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductMapper productMapper;
    private final ProductRepository productRepository;

    @Transactional
    public ProductResponseDto save(ProductCreateDto dto) {
        if (productRepository.existsByTitle(dto.getTitle())){
            throw new TitleUniqueViolationException("There is already a product registered with this title");
        }

        if (productRepository.existsBySku(dto.getSku())) {
            throw new SkuUniqueViolationException("There is already a product registered with this sku");
        }

        Product product = productRepository.save(productMapper.toProduct(dto));
        updateProductStatus(product);
        return productMapper.toDto(product);
    }

    @Transactional(readOnly = true)
    public PageableDto findAll(Pageable pageable) {
        PageableDto dto = productMapper.toPageableDto(productRepository.findAllPageable(pageable));
        if (dto.getContent().isEmpty()) {
            throw new EntityNotFoundException("No products found");
        }
        return dto;
    }

    @Transactional(readOnly = true)
    public ProductResponseDto findByTitle(String title) {
        return productMapper.toDto(productRepository.findByTitleIgnoreCase(title).orElseThrow(
                () -> new EntityNotFoundException("Product not found by title")
        ));
    }

    @Transactional(readOnly = true)
    public PageableDto findBySupplier(Pageable pageable, String supplier) {
        PageableDto dto = productMapper.toPageableDto(productRepository.findBySupplierIgnoreCaseContaining(pageable, supplier));
        if (dto.getContent().isEmpty()) {
            throw new EntityNotFoundException("No products found by supplier");
        }
        return dto;
    }

    @Transactional(readOnly = true)
    public PageableDto findByCategory(Pageable pageable, String category) {
        try {
            PageableDto dto = productMapper.toPageableDto(productRepository.findByCategory(pageable, Product.Category.valueOf(category.toUpperCase())));
            if (dto.getContent().isEmpty()) {
                throw new EntityNotFoundException("No products found by category");
            }
            return dto;
        } catch(IllegalArgumentException e) {
            throw new InvalidCategoryException("Category does not exist");
        }
    }

    @Transactional(readOnly = true)
    public ProductResponseDto findBySku(String sku) {
        return productMapper.toDto(findProductBySku(sku));
    }

    @Transactional(readOnly = true)
    public PageableDto findAllActived(Pageable pageable) {
        PageableDto dto = productMapper.toPageableDto(productRepository.findAllByStatus(pageable, true));
        if (dto.getContent().isEmpty()) {
            throw new EntityNotFoundException("No active product found");
        }
        return dto;
    }

    @Transactional(readOnly = true)
    public PageableDto findAllDisabled(Pageable pageable) {
        PageableDto dto = productMapper.toPageableDto(productRepository.findAllByStatus(pageable, false));
        if (dto.getContent().isEmpty()) {
            throw new EntityNotFoundException("No disabled product found");
        }
        return dto;
    }

    @Transactional(readOnly = true)
    public PageableDto findProductsByDate(String afterDate, String beforeDate, Pageable pageable) {
        PageableDto dto;
        if (afterDate != null && beforeDate != null) {
            dto = productMapper.toPageableDto(productRepository.findByCreateDateBetween(LocalDate.parse(afterDate).atStartOfDay(), LocalDate.parse(beforeDate).atStartOfDay().plusDays(1), pageable));
        } else if (afterDate != null) {
            dto = productMapper.toPageableDto(productRepository.findByCreateDateAfter(LocalDate.parse(afterDate).atStartOfDay(), pageable));
        } else if (beforeDate != null) {
            dto = productMapper.toPageableDto(productRepository.findByCreateDateBefore(LocalDate.parse(beforeDate).atStartOfDay().plusDays(1), pageable));
        } else {
            throw new EntityNotFoundException("No date parameters provided");
        }

        if (dto.getContent().isEmpty()) {
            throw new EntityNotFoundException("No products found by date(s)");
        }

        return dto;
    }

    @Transactional
    public void update(String sku, ProductUpdateDto dto) {
        Product product = findProductBySku(sku);
        productMapper.updateProduct(dto, product);
        updateProductStatus(product);
    }

    @Transactional
    public String alter(String sku) {
        Product product = findProductBySku(sku);
        if (product.getQuantity() == 0) {
            throw new InvalidProductStatusException("Cannot alter status when quantity is 0");
        }
        product.setStatus(!product.getStatus());
        return product.getStatus().toString();
    }

    @Transactional
    public String add(String sku, Integer quantity) {
        Product product = findProductBySku(sku);
        if (quantity == null || quantity <= 0) {
            throw new InvalidQuantityException("No quantity stated");
        }
        product.setQuantity(product.getQuantity() + quantity);
        if (product.getQuantity() - quantity == 0 || product.getQuantity() > 0 && product.getStatus()) {
            updateProductStatus(product);
        }
        return product.getQuantity().toString();
    }

    @Transactional
    public String sub(String sku, Integer quantity) {
        Product product = findProductBySku(sku);
        if (quantity == null || quantity <= 0) {
            throw new InvalidQuantityException("No quantity stated");
        }
        if (product.getQuantity() < quantity) {
            throw new InvalidQuantityException("The quantity of available products is " + product.getQuantity());
        }
        product.setQuantity(product.getQuantity() - quantity);
        updateProductStatus(product);
        return product.getQuantity().toString();
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
}
