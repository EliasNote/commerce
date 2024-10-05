package com.esand.products.web.controller;

import com.esand.products.service.CategoryService;
import com.esand.products.service.ProductService;
import com.esand.products.springdoc.SpringDoc;
import com.esand.products.web.dto.PageableDto;
import com.esand.products.web.dto.ProductCreateDto;
import com.esand.products.web.dto.ProductResponseDto;
import com.esand.products.web.dto.ProductUpdateDto;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/products")
public class Controller implements SpringDoc {

    private final ProductService productService;
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<ProductResponseDto> create(@RequestBody @Valid ProductCreateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.save(dto));
    }

    @GetMapping
    public ResponseEntity<PageableDto> findAll(@Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable,
                                               @RequestParam(value = "afterDate", required = false) String afterDate,
                                               @RequestParam(value = "beforeDate", required = false) String beforeDate) {
        return ResponseEntity.ok(productService.findAll(afterDate, beforeDate, pageable));
    }

    @GetMapping("/title/{title}")
    public ResponseEntity<PageableDto> findByTitle(@PageableDefault(size = 10) Pageable pageable, @PathVariable String title) {
        return ResponseEntity.ok(productService.findByTitle(pageable, title));
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

    @GetMapping("/actived")
    public ResponseEntity<PageableDto> findAllActived(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(productService.findAllActived(pageable));
    }

    @GetMapping("/disabled")
    public ResponseEntity<PageableDto> findAllDisabled(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(productService.findAllDisabled(pageable));
    }

    @PatchMapping("/edit/{sku}")
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

    @DeleteMapping("/delete/{sku}")
    public ResponseEntity<Void> deleteProductBySku(@PathVariable String sku) {
        productService.deleteBySku(sku);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/category/{category}")
    public ResponseEntity<String> addCategory(@PathVariable String category) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.create(category));
    }

    @GetMapping("/category")
    public ResponseEntity<PageableDto> getCategories(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(categoryService.findAll(pageable));
    }

    @DeleteMapping("/category/{category}")
    public ResponseEntity<Void> removeCategory(@PathVariable String category) {
        categoryService.deleteCategory(category);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/category/{category}/name/{newName}")
    public ResponseEntity<String> editCategory(@PathVariable String category, @PathVariable String newName) {
        return ResponseEntity.ok(categoryService.editCategory(category, newName));
    }
}
