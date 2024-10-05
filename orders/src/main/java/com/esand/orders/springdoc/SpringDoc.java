package com.esand.orders.springdoc;

import com.esand.orders.web.dto.OrderCreateDto;
import com.esand.orders.web.dto.OrderResponseDto;
import com.esand.orders.web.dto.PageableDto;
import com.esand.orders.web.exception.ErrorMessage;
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

@Tag(name = "Orders", description = "Contains all operations related to order management, including creation, searching, updating, and deletion")
public interface SpringDoc {

    @Operation(summary = "Create a new order",
            description = "Endpoint that creates a new order.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order created successfully!",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrderResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "404", description = "Client or product not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "503", description = "Service unavailable",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)))
    })
    ResponseEntity<OrderResponseDto> create(@RequestBody @Valid OrderCreateDto dto);

    @Operation(summary = "Search for all orders",
            description = "Endpoint to search for all orders.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All orders found successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PageableDto.class))),
            @ApiResponse(responseCode = "404", description = "No orders found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)))
    })
    ResponseEntity<PageableDto> findAll(@Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable,
                                        @RequestParam(value = "afterDate", required = false) String afterDate,
                                        @RequestParam(value = "beforeDate", required = false) String beforeDate);

    @Operation(summary = "Search for orders by SKU",
            description = "Endpoint to search for orders by product SKU.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orders found successfully by SKU",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PageableDto.class))),
            @ApiResponse(responseCode = "404", description = "No orders found by SKU",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)))
    })
    ResponseEntity<PageableDto> findBySku(@Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable, @PathVariable String sku);

    @Operation(summary = "Search for orders by customer CPF",
            description = "Endpoint to search for orders by customer CPF.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orders found successfully by CPF",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PageableDto.class))),
            @ApiResponse(responseCode = "404", description = "No orders found by CPF",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)))
    })
    ResponseEntity<PageableDto> findByCpf(@Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable, @PathVariable String cpf);

    @Operation(summary = "Send an order",
            description = "Endpoint to send an order by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order sent successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "Order not found by ID",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "409", description = "Order already processing",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)))
    })
    ResponseEntity<String> sendOrder(@PathVariable Long id);

    @Operation(summary = "Delete an order",
            description = "Endpoint to delete an order by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Order deleted successfully",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Order not found by ID",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)))
    })
    ResponseEntity<Void> deleteById(@PathVariable Long id);

    @Operation(summary = "Delete all processing orders",
            description = "Endpoint to delete all orders that have been processing.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "All processing orders deleted successfully",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "No processing orders found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)))
    })
    ResponseEntity<Void> deleteAllProcessing();
}
