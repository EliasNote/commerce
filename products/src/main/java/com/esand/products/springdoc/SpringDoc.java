package com.esand.products.springdoc;

import com.esand.products.web.dto.ProductCreateDto;
import com.esand.products.web.dto.ProductResponseDto;
import com.esand.products.web.dto.ProductUpdateDto;
import com.esand.products.web.dto.PageableDto;
import com.esand.products.web.exception.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Products", description = "Contains all operations related to resources for registration, searching, correcting data, and updating status")
public interface SpringDoc {
    @Operation(summary = "Register a new product",
            description = "Endpoint that registers a new product.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product registered successfully!",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "409", description = "This product has already been registered",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)))
    })
    ResponseEntity<ProductResponseDto> create(@RequestBody @Valid ProductCreateDto dto);

    @Operation(summary = "Search for all products",
            description = "Endpoint to search for all products.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All products found successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PageableDto.class))),
            @ApiResponse(responseCode = "404", description = "No products found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)))
    })
    ResponseEntity<PageableDto> findAll(@Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable,
                                        @RequestParam(value = "afterDate", required = false) String afterDate,
                                        @RequestParam(value = "beforeDate", required = false) String beforeDate);

    @Operation(summary = "Search for product by title",
            description = "Endpoint to search for a product by title.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product found successfully by title",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Product not found by title",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)))
    })
    ResponseEntity<PageableDto> findByTitle(@Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable,
                                            @RequestParam(value = "afterDate", required = false) String afterDate,
                                            @RequestParam(value = "beforeDate", required = false) String beforeDate,
                                            @PathVariable String title);

    @Operation(summary = "Search for products by supplier",
            description = "Endpoint to search for products by supplier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products found successfully by supplier",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PageableDto.class))),
            @ApiResponse(responseCode = "404", description = "No products found by supplier",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)))
    })
    ResponseEntity<PageableDto> findBySupplier(@Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable,
                                               @RequestParam(value = "afterDate", required = false) String afterDate,
                                               @RequestParam(value = "beforeDate", required = false) String beforeDate,
                                               @PathVariable String supplier);

    @Operation(summary = "Search for products by category",
            description = "Endpoint to search for products by category.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products found successfully by category",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PageableDto.class))),
            @ApiResponse(responseCode = "404", description = "No products found by category",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)))
    })
    ResponseEntity<PageableDto> findByCategory(@Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable,
                                               @RequestParam(value = "afterDate", required = false) String afterDate,
                                               @RequestParam(value = "beforeDate", required = false) String beforeDate,
                                               @PathVariable @Valid String category);

    @Operation(summary = "Search for product by SKU",
            description = "Endpoint to search for a product by SKU.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product found successfully by SKU",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Product not found by SKU",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)))
    })
    ResponseEntity<ProductResponseDto> findBySku(@PathVariable String sku);

    @Operation(summary = "Search for active products",
            description = "Endpoint to search for active products.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Active products found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PageableDto.class))),
            @ApiResponse(responseCode = "404", description = "Active products not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)))
    })
    ResponseEntity<PageableDto> findAllActived(@Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable,
                                               @RequestParam(value = "afterDate", required = false) String afterDate,
                                               @RequestParam(value = "beforeDate", required = false) String beforeDate);

    @Operation(summary = "Search for disabled products",
            description = "Endpoint to search for disabled products.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Disabled products found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PageableDto.class))),
            @ApiResponse(responseCode = "404", description = "Disabled products not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)))
    })
    ResponseEntity<PageableDto> findAllDisabled(@Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable,
                                                @RequestParam(value = "afterDate", required = false) String afterDate,
                                                @RequestParam(value = "beforeDate", required = false) String beforeDate);

    @Operation(summary = "Update a product's data",
            description = "Endpoint to update a product's data by SKU. It's possible to update by specifying only the attribute you want to modify.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product data updated successfully",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "404", description = "Product not found by SKU",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)))
    })
    ResponseEntity<ProductResponseDto> update(@PathVariable String sku,
                                @RequestBody @Valid ProductUpdateDto dto,
                                @RequestParam(required = false) Boolean status,
                                @RequestParam(required = false) Integer addQuantity,
                                @RequestParam(required = false) Integer subQuantity
    );

    @Operation(summary = "Delete a product by SKU",
            description = "Endpoint to delete a product by SKU.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found by SKU",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)))
    })
    ResponseEntity<Void> deleteProductBySku(@PathVariable String sku);
}
