package com.esand.products.service;

import com.esand.products.entity.Product;
import com.esand.products.repository.ProductRepository;
import com.esand.products.web.dto.PageableDto;
import com.esand.products.web.dto.ProductCreateDto;
import com.esand.products.web.dto.ProductResponseDto;
import com.esand.products.web.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductMapper productMapper;
    private final ProductRepository productRepository;

    @Transactional
    public ProductResponseDto save(ProductCreateDto dto) {
        Product product = productRepository.save(productMapper.toProduct(dto));
        updateProductStatus(product);
        return productMapper.toDto(product);
    }

    @Transactional(readOnly = true)
    public PageableDto findAll(Pageable pageable) {
        return productMapper.toPageableDto(productRepository.findAllPageable(pageable));
    }

    @Transactional(readOnly = true)
    public ProductResponseDto findByTitle(String title) {
        return productMapper.toDto(productRepository.findByTitleIgnoreCase(title).orElseThrow());
    }

    @Transactional(readOnly = true)
    public PageableDto findBySupplier(Pageable pageable, String supplier) {
        return productMapper.toPageableDto(productRepository.findBySupplierIgnoreCaseContaining(pageable, supplier).orElseThrow());
    }

    @Transactional(readOnly = true)
    public PageableDto findByCategory(Pageable pageable, String category) {
        return productMapper.toPageableDto(productRepository.findByCategory(pageable, Product.Category.valueOf(category.toUpperCase())).orElseThrow());
    }

    @Transactional(readOnly = true)
    public ProductResponseDto findBySku(String sku) {
        return productMapper.toDto(findProductBySku(sku));
    }

    @Transactional
    public void update(String sku, ProductCreateDto dto) {
        Product product = findProductBySku(sku);
        productMapper.updateProduct(dto, product);
        updateProductStatus(product);
    }

    @Transactional
    public String alter(String sku) {
        Product product = findProductBySku(sku);
        product.setStatus(!product.getStatus());
        return product.getStatus().toString();
    }

    @Transactional
    public String add(String sku, Integer quantity) {
        Product product = findProductBySku(sku);
        product.setQuantity(product.getQuantity() + quantity);
        if (product.getQuantity() - quantity == 0) {
            updateProductStatus(product);
        }
        return product.getQuantity().toString();
    }

    @Transactional
    public String sub(String sku, Integer quantity) {
        Product product = findProductBySku(sku);
        if (quantity == null || quantity == 0) {
            throw new RuntimeException("No quantity informed");
        }
        if (product.getQuantity() < quantity) {
            throw new RuntimeException("The quantity of available products is " + product.getQuantity());
        }
        product.setQuantity(product.getQuantity() - quantity);
        updateProductStatus(product);
        return product.getQuantity().toString();
    }

    @Transactional
    private Product findProductBySku(String sku) {
        return productRepository.findBySku(sku).orElseThrow(
                () -> new RuntimeException("Produto não encontrado para o sku: " + sku)
        );
    }

    private void updateProductStatus(Product product) {
        product.setStatus(product.getQuantity() == 0 ? false : true);
    }
}
