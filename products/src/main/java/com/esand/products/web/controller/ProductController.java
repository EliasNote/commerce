package com.esand.products.web.controller;

import com.esand.products.entity.Product;
import com.esand.products.service.ProductService;
import com.esand.products.springdoc.SpringDoc;
import com.esand.products.web.dto.PageableDto;
import com.esand.products.web.dto.ProductCreateDto;
import com.esand.products.web.dto.ProductResponseDto;
import com.esand.products.web.dto.ProductUpdateDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/products")
public class ProductController implements SpringDoc {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponseDto> create(@RequestBody @Valid ProductCreateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.save(dto));
    }

    @GetMapping
    public ResponseEntity<PageableDto> findAll(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(productService.findAll(pageable));
    }

    @GetMapping("/title/{title}")
    public ResponseEntity<ProductResponseDto> findByTitle(@PathVariable String title) {
        return ResponseEntity.ok(productService.findByTitle(title));
    }

    @GetMapping("/supplier/{supplier}")
    public ResponseEntity<PageableDto> findBySupplier(@PageableDefault(size = 10) Pageable pageable, @PathVariable String supplier) {
        return ResponseEntity.ok(productService.findBySupplier(pageable, supplier));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<PageableDto> findByCategory(@PageableDefault(size = 10) Pageable pageable, @PathVariable @Valid String category) {
        return ResponseEntity.ok(productService.findByCategory(pageable, category));
    }

    @GetMapping("/sku/{sku}")
    public ResponseEntity<ProductResponseDto> findBySku(@PathVariable String sku) {
        return ResponseEntity.ok(productService.findBySku(sku));
    }

    @PutMapping("/edit/{sku}")
    public ResponseEntity<Void> update(@PathVariable String sku, @RequestBody @Valid ProductUpdateDto dto) {
        productService.update(sku, dto);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/status/{sku}")
    public ResponseEntity<String> toggleStatus(@PathVariable String sku) {
        return ResponseEntity.ok("updated status for: " + productService.alter(sku));
    }

    @PatchMapping("/sku/{sku}/add/{quantity}")
    public ResponseEntity<String> increaseQuantity(@PathVariable String sku, @PathVariable Integer quantity) {
        return ResponseEntity.ok("updated quantity for: " + productService.add(sku, quantity));
    }

    @PatchMapping("/sku/{sku}/sub/{quantity}")
    public ResponseEntity<String> decreaseQuantity(@PathVariable String sku, @PathVariable Integer quantity) {
        return ResponseEntity.ok("updated quantity for: " + productService.sub(sku, quantity));
    }
}
