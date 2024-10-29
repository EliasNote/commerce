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
    public ResponseEntity<PageableDto> findByTitle(@Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable,
                                                   @RequestParam(value = "afterDate", required = false) String afterDate,
                                                   @RequestParam(value = "beforeDate", required = false) String beforeDate,
                                                   @PathVariable String title) {
        return ResponseEntity.ok(productService.findByTitle(title, afterDate, beforeDate, pageable));
    }

    @GetMapping("/supplier/{supplier}")
    public ResponseEntity<PageableDto> findBySupplier(@Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable,
                                                      @RequestParam(value = "afterDate", required = false) String afterDate,
                                                      @RequestParam(value = "beforeDate", required = false) String beforeDate,
                                                      @PathVariable String supplier) {
        return ResponseEntity.ok(productService.findBySupplier(supplier, afterDate, beforeDate, pageable));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<PageableDto> findByCategory(@Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable,
                                                      @RequestParam(value = "afterDate", required = false) String afterDate,
                                                      @RequestParam(value = "beforeDate", required = false) String beforeDate,
                                                      @PathVariable @Valid String category) {
        return ResponseEntity.ok(productService.findByCategory(category, afterDate, beforeDate, pageable));
    }

    @GetMapping("/sku/{sku}")
    public ResponseEntity<ProductResponseDto> findBySku(@PathVariable String sku) {
        return ResponseEntity.ok(productService.findBySku(sku));
    }

    @GetMapping("/actived")
    public ResponseEntity<PageableDto> findAllActived(@Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable,
                                                      @RequestParam(value = "afterDate", required = false) String afterDate,
                                                      @RequestParam(value = "beforeDate", required = false) String beforeDate) {
        return ResponseEntity.ok(productService.findAllActived(afterDate, beforeDate, pageable));
    }

    @GetMapping("/disabled")
    public ResponseEntity<PageableDto> findAllDisabled(@Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable,
                                                       @RequestParam(value = "afterDate", required = false) String afterDate,
                                                       @RequestParam(value = "beforeDate", required = false) String beforeDate) {
        return ResponseEntity.ok(productService.findAllDisabled(afterDate, beforeDate, pageable));
    }

    @PatchMapping("/edit/{sku}")
    public ResponseEntity<ProductResponseDto> update(@PathVariable String sku,
                                       @RequestBody(required = false) @Valid ProductUpdateDto dto,
                                       @RequestParam(required = false) Boolean status,
                                       @RequestParam(required = false) Integer addQuantity,
                                       @RequestParam(required = false) Integer subQuantity
                                       ) {
        return ResponseEntity.ok(productService.update(sku, dto, status, addQuantity, subQuantity));
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
    public ResponseEntity<PageableDto> getCategories(@Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable,
                                                     @RequestParam(value = "afterDate", required = false) String afterDate,
                                                     @RequestParam(value = "beforeDate", required = false) String beforeDate) {
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
